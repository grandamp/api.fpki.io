package io.fpki.api.function;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.security.cert.CRLException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateFactory;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import io.fpki.api.apigateway.ProxyRequest;
import io.fpki.api.apigateway.ProxyResponse;
import io.fpki.api.apigateway.ProxyResponseBadRequest;
import io.fpki.api.apigateway.ProxyResponseJSONOk;
import io.fpki.api.constants.APISettings;
import io.fpki.api.constants.POJOObjectMapper;
import io.fpki.api.dynamodb.DynamoDBCAEntry;
import io.fpki.api.dynamodb.DynamoDBCAEntryPOJO;
import io.fpki.api.function.utilities.HttpClient;
import io.fpki.api.function.utilities.HttpClientException;
import io.fpki.api.function.utilities.X509FunctionUtil;
import io.fpki.api.pojo.UserCert;

public class CAPathFromUserCertFunction implements RequestHandler<ProxyRequest, ProxyResponse> {

	private static final Logger log = Logger.getLogger(CAPathFromUserCertFunction.class);

	private static final POJOObjectMapper mapper = POJOObjectMapper.instance();

	private static final DynamoDBCAEntry ddbEntry = DynamoDBCAEntry.instance();

	@Override
	public ProxyResponse handleRequest(ProxyRequest request, Context arg1) {
		log.info(request.toString());
		UserCert submittedCert = null;
		try {
			submittedCert = UserCert.getInstance(request.getBody());
		} catch (IOException e) {
			return new ProxyResponseBadRequest(e.getMessage());
		}
		if (null == submittedCert) {
			return new ProxyResponseBadRequest("Request must include userCert object");
		}
		if (submittedCert.userCert.length() >= APISettings.instance().getPEMSizeLimit()) {
			return new ProxyResponseBadRequest(
					"Size limit " + APISettings.instance().getPEMSizeLimit() + "(bytes) exceeded for userCert object");
		}
		X509Certificate userCertificate = null;
		try {
			userCertificate = X509FunctionUtil.getCertificate(submittedCert.userCert);
		} catch (CertificateException | IllegalArgumentException e) {
			return new ProxyResponseBadRequest("Error decoding userCert: " + e.getMessage());
		}
		try {
			userCertificate.checkValidity();
		} catch (CertificateExpiredException e) {
			return new ProxyResponseBadRequest("The certificate submitted is expired");
		} catch (CertificateNotYetValidException e) {
			return new ProxyResponseBadRequest("The certificate submitted is not yet valid");
		}
		if (X509FunctionUtil.isCA(userCertificate)) {
			return new ProxyResponseBadRequest("The certificate submitted is a CA certificate");
		}
		/*
		 * TODO: Check to make sure we have the issuing CA before we download the CRL for the submitted cert.
		 * 
		 * 	If we don't have the issuing CA, download the AIA artifact to discover and validate the issuing CA.
		 * 	We should also limit the number of AIA references we are willing to process, as well as the size of the CMS objects that are fetched.
		 */
		/*
		 * Create a new UserCert object based on our processing
		 */
		UserCert processedCert = new UserCert();
		/*
		 * Copy Base64 Cert
		 */
		processedCert.userCert = submittedCert.userCert;
		/*
		 * Set issuing CA's SKI
		 */
		processedCert.caSKI = X509FunctionUtil.getAuthorityKeyIdentifier(userCertificate);
		/*
		 * Set OCSP URLs
		 */
		URL[] ocspUrls = X509FunctionUtil.getAuthorityInformationAccessOCSPURLs(userCertificate);
		List<String> ocspUrlList = new ArrayList<String>();
		for (URL ocspUrl : ocspUrls) {
			ocspUrlList.add(ocspUrl.toString());
		}
		processedCert.urlOCSP = ocspUrlList.toArray(new String[ocspUrlList.size()]);
		/*
		 * Set CRL URLs
		 */
		URL[] crlUrls = X509FunctionUtil.getCRLDistributionPointURLs(userCertificate);
		List<String> crlUrlList = new ArrayList<String>();
		for (URL crlUrl : crlUrls) {
			crlUrlList.add(crlUrl.toString());
		}
		processedCert.urlCRL = crlUrlList.toArray(new String[crlUrlList.size()]);
		/*
		 * Set AIA URLs
		 */
		URL[] aiaUrls = X509FunctionUtil.getAuthorityInformationAccessURLs(userCertificate);
		List<String> aiaUrlList = new ArrayList<String>();
		for (URL aiaUrl : aiaUrls) {
			aiaUrlList.add(aiaUrl.toString());
		}
		processedCert.urlAIA = aiaUrlList.toArray(new String[aiaUrlList.size()]);
		/*
		 * Return the issuer path, if we have the issuer
		 */
		List<DynamoDBCAEntryPOJO> entries = ddbEntry.getCA(processedCert.caSKI);
		if (!entries.isEmpty()) {
			CAPathGetBySKIFunction getBySki = new CAPathGetBySKIFunction();
			Map<String, String> pathParams = new HashMap<String,String>();
			pathParams.put("caSKI", processedCert.caSKI);
			request.setPathParameters(pathParams);
			return getBySki.handleRequest(request, arg1);
		}

		byte[] crlBytes = null;
		try {
			crlBytes = HttpClient.getInstance().getRequest(processedCert.urlCRL[0], "CRL");
		} catch (HttpClientException e) {
			log.error(e);
			return new ProxyResponseBadRequest("error downloading crl: " + e.getMessage());
		}
		CertificateFactory cf = null;
		X509CRL crl = null;
		try {
			cf = CertificateFactory.getInstance("X.509");
			crl = (X509CRL) cf.generateCRL(new ByteArrayInputStream(crlBytes));
		} catch (CertificateException e) {
			return new ProxyResponseBadRequest("error downloading crl: " + e.getMessage());
		} catch (CRLException e) {
			return new ProxyResponseBadRequest("error downloading crl: " + e.getMessage());
		}
		if (crl.isRevoked(userCertificate)) {
			return new ProxyResponseBadRequest("Certificate Revoked");
		} else {
			/*
			 * TODO:  Write CRL to S3 bucket, it *may* be useful
			 */
			return new ProxyResponseJSONOk(processedCert.toString());
		}
	}

}
