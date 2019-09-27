package io.fpki.api.function;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.apache.log4j.Logger;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.core.JsonProcessingException;

import io.fpki.api.apigateway.ProxyRequest;
import io.fpki.api.apigateway.ProxyResponse;
import io.fpki.api.apigateway.ProxyResponseBadRequest;
import io.fpki.api.apigateway.ProxyResponseJSONOk;
import io.fpki.api.apigateway.ProxyResponseServerError;
import io.fpki.api.constants.APISettings;
import io.fpki.api.constants.POJOObjectMapper;
import io.fpki.api.dynamodb.DynamoDBCAEntry;
import io.fpki.api.dynamodb.DynamoDBCAEntryPOJO;
import io.fpki.api.function.utilities.X509FunctionUtil;
import io.fpki.api.pojo.CAEntry;

public class CACreateEntryFunction implements RequestHandler<ProxyRequest, ProxyResponse> {

	private static final Logger log = Logger.getLogger(CACreateEntryFunction.class);

	private static final POJOObjectMapper mapper = POJOObjectMapper.instance();

	private static final DynamoDBCAEntry ddbEntry = DynamoDBCAEntry.instance();

	@Override
	public ProxyResponse handleRequest(ProxyRequest request, Context arg1) {
		log.info(request.toString());
		CAEntry submittedEntry = null;
		try {
			submittedEntry = CAEntry.getInstance(request.getBody());
		} catch (IOException e) {
			return new ProxyResponseBadRequest(e.getMessage());
		}
		if (null == submittedEntry || null == submittedEntry.caCrl || null == submittedEntry.caCert) {
			return new ProxyResponseBadRequest("Request must include caCrl & caCert objects");
		}
		if (submittedEntry.caCert.length() >= APISettings.instance().getPEMSizeLimit()) {
			return new ProxyResponseBadRequest(
					"Size limit " + APISettings.instance().getPEMSizeLimit() + "(bytes) exceeded for caCert object");
		}
		X509Certificate newCertificate = null;
		try {
			newCertificate = X509FunctionUtil.getCertificate(submittedEntry.caCert);
		} catch (CertificateException | IllegalArgumentException e) {
			return new ProxyResponseBadRequest("Error decoding caCert: " + e.getMessage());
		}
		/*
		 * From here, we will build our DynamoDB CAEntry POJO
		 */
		DynamoDBCAEntryPOJO newEntry = new DynamoDBCAEntryPOJO();
		/*
		 * @param caAKI
		 */
		String caAKI = null;
		try {
			caAKI = X509FunctionUtil.getAuthorityKeyIdentifier(newCertificate);
		} catch (IllegalArgumentException e) {
			return new ProxyResponseServerError(e.getMessage());
		}
		newEntry.setCaAKI(caAKI);
		/*
		 * @param caCert
		 */
		ByteBuffer caCert = null;
		try {
			caCert = X509FunctionUtil.certificateEncodedToByteBuffer(newCertificate);
		} catch (CertificateEncodingException e) {
			e.printStackTrace();
		}
		newEntry.setCaCert(caCert);
		/*
		 * Before we proceed, we should do the obvious: - check temporal
		 * validity,and; - see *if* this is really a CA certificate.
		 * 
		 * Basic logic would be to check out Basic Constraints, and check to see
		 * if CA=True
		 */
		try {
			newCertificate.checkValidity();
		} catch (CertificateExpiredException e) {
			return new ProxyResponseBadRequest("The certificate submitted is expired");
		} catch (CertificateNotYetValidException e) {
			return new ProxyResponseBadRequest("The certificate submitted is not yet valid");
		}
		if (!X509FunctionUtil.isCA(newCertificate)) {
			return new ProxyResponseBadRequest("The certificate submitted is not a CA certificate");
		}
		/*
		 * @param caCrl
		 */
		String caCrl = null;
		try {
			URL crlUrl = new URL(submittedEntry.caCrl);
			caCrl = crlUrl.toString();
		} catch (MalformedURLException e) {
			log.error("Malformed URL.", e);
			return new ProxyResponseBadRequest("Error processing caCrl URL: " + e.getMessage());
		}
		newEntry.setCaCrl(caCrl);
		/*
		 * @param caHash
		 */
		newEntry.setCaHash(X509FunctionUtil.getCAHash(newCertificate));
		/*
		 * @param caIssuer
		 */
		newEntry.setCaIssuer(newCertificate.getIssuerX500Principal().getName());
		/*
		 * Date formatter for caNotAfter & caNotBefore See:
		 * http://xkcd.com/1179/
		 */
		SimpleDateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
		dFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		/*
		 * @param caNotAfter
		 */
		Date notAfter = newCertificate.getNotAfter();
		newEntry.setCaNotAfter(dFormat.format(notAfter));
		/*
		 * @param caNotBefore
		 */
		Date notBefore = newCertificate.getNotBefore();
		newEntry.setCaNotBefore(dFormat.format(notBefore));
		/*
		 * @param caSerial
		 */
		newEntry.setCaSerial(X509FunctionUtil.byteArrayToString(newCertificate.getSerialNumber().toByteArray()));
		/*
		 * @param caSKI
		 */
		newEntry.setCaSKI(X509FunctionUtil.getSubjectKeyIdentifier(newCertificate));
		/*
		 * @param caSubject
		 */
		newEntry.setCaSubject(newCertificate.getSubjectX500Principal().getName());
		/*
		 * Now that we have a new entry from the submitted request, lets check
		 * and see if we should actually add the entry
		 */
		List<DynamoDBCAEntryPOJO> entries = ddbEntry.getCA(newEntry.getCaSKI());
		if (entries.isEmpty()) {
			/*
			 * Entry does not exist, checking for issuer of this certificate
			 */
			entries = ddbEntry.getCA(newEntry.getCaAKI());
			if (entries.isEmpty()) {
				/*
				 * Issuing CA not present, rejecting
				 */
				return new ProxyResponseBadRequest("Issuing CA does not exist for this CA certificate");
			} else {
				/*
				 * Issuing CA present, validating signature
				 */
				DynamoDBCAEntryPOJO issuingEntry = entries.get(0);
				X509Certificate issuingCA = X509FunctionUtil.getCertificate(issuingEntry);
				try {
					newCertificate.verify(issuingCA.getPublicKey());
				} catch (InvalidKeyException | CertificateException | NoSuchAlgorithmException | NoSuchProviderException
						| SignatureException e) {
					/*
					 * Invalid certificate, rejecting
					 */
					log.error("Validation using issuing CA failed", e);
					return new ProxyResponseBadRequest(
							"Error validating certificate using issuer Public Key: " + e.getMessage());
				}
				/*
				 * Adding new entry
				 */
				ddbEntry.createCA(newEntry);
				String jsonString = null;
				try {
					jsonString = mapper.getMapper().writeValueAsString(newEntry);
				} catch (JsonProcessingException e) {
					e.printStackTrace();
				}
				return new ProxyResponseJSONOk(jsonString);
			}
		} else {
			/*
			 * Entry does exist, check to see if this certificate is more recent
			 */
			DynamoDBCAEntryPOJO existingEntry = entries.get(0);
			X509Certificate existingCertificate = X509FunctionUtil.getCertificate(existingEntry);
			if (existingCertificate.equals(newCertificate)) {
				/*
				 * Certificate already exists, rejecting
				 */
				return new ProxyResponseBadRequest("Entry already exists");
			} else if (existingCertificate.getNotBefore().before(newCertificate.getNotBefore())) {
				/*
				 * Certificate has a newer issuance date, updating
				 */
				ddbEntry.createCA(newEntry);
				String jsonString = null;
				try {
					jsonString = mapper.getMapper().writeValueAsString(newEntry);
				} catch (JsonProcessingException e) {
					e.printStackTrace();
				}
				return new ProxyResponseJSONOk(jsonString);
			}
			/*
			 * If we get here, then we encountered a CA cert that is older, or
			 * possibly re-issued without altering notBefore and notAfter. Out
			 * of caution, reject.
			 */
			return new ProxyResponseBadRequest("Entry with more recent notBefore already exists");
		}
	}

}
