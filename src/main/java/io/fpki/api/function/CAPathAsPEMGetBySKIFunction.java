package io.fpki.api.function;

import java.io.IOException;

import org.apache.log4j.Logger;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import io.fpki.api.apigateway.ProxyRequest;
import io.fpki.api.apigateway.ProxyResponse;
import io.fpki.api.apigateway.ProxyResponseOk;
import io.fpki.api.apigateway.ProxyResponseServerError;
import io.fpki.api.function.utilities.POJOFunctionUtil;
import io.fpki.api.function.utilities.X509FunctionUtil;
import io.fpki.api.pojo.CAEntryWithSubs;

public class CAPathAsPEMGetBySKIFunction implements RequestHandler<ProxyRequest, ProxyResponse> {

	private static final Logger log = Logger.getLogger(CAPathAsPEMGetBySKIFunction.class);

	@Override
	public ProxyResponse handleRequest(ProxyRequest request, Context arg1) {
		/*
		 * This request is received with PathParameters rather than a body. This
		 * handler makes use of simple recursion to obtain a specific path,
		 * performing an AKI chase, like an AIA chase.
		 * 
		 * - First, get the subject certificate identified from the
		 * "/ca/{caSKI}" handler
		 */
		log.info(POJOFunctionUtil.pojoToString(request));
		CAPathGetBySKIFunction getBySki = new CAPathGetBySKIFunction();
		ProxyResponse skiResponse = getBySki.handleRequest(request, arg1);
		if (skiResponse.getStatusCode() == 200) {
			String jsonBody = skiResponse.getBody();
			CAEntryWithSubs currentEntry = null;
			try {
				currentEntry = CAEntryWithSubs.getInstance(jsonBody);
			} catch (IOException e) {
				log.error(e);
				return new ProxyResponseServerError("Inconsistency or encoding error in data storage");
			}
			return new ProxyResponseOk(X509FunctionUtil.getCAEntryWithSubsAsPEM(currentEntry), "text/plain");
		} else {
			return skiResponse;
		}
	}

}
