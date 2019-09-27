package io.fpki.api.function;

import java.io.IOException;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import io.fpki.api.apigateway.ProxyRequest;
import io.fpki.api.apigateway.ProxyResponse;
import io.fpki.api.apigateway.ProxyResponseBadRequest;
import io.fpki.api.apigateway.ProxyResponseJSONOk;
import io.fpki.api.constants.APISettings;
import io.fpki.api.function.utilities.X509FunctionUtil;
import io.fpki.api.pojo.UserCert;

public class CAPathFromUserCertFunction implements RequestHandler<ProxyRequest, ProxyResponse> {

	private static final Logger log = Logger.getLogger(CAPathFromUserCertFunction.class);

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
		return new ProxyResponseJSONOk(processedCert.toString());
	}

}
