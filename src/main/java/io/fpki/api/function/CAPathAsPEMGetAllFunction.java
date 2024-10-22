package io.fpki.api.function;

import java.io.IOException;

import org.apache.log4j.Logger;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.core.JsonProcessingException;

import io.fpki.api.apigateway.ProxyRequest;
import io.fpki.api.apigateway.ProxyResponse;
import io.fpki.api.apigateway.ProxyResponseServerError;
import io.fpki.api.apigateway.ProxyResponseTextOk;
import io.fpki.api.constants.POJOObjectMapper;
import io.fpki.api.function.utilities.POJOFunctionUtil;
import io.fpki.api.function.utilities.X509FunctionUtil;
import io.fpki.api.pojo.CAEntryWithSubs;

public class CAPathAsPEMGetAllFunction implements RequestHandler<ProxyRequest, ProxyResponse> {

	private static final Logger log = Logger.getLogger(CAPathAsPEMGetAllFunction.class);

	private static final POJOObjectMapper mapper = POJOObjectMapper.instance();

	@Override
	public ProxyResponse handleRequest(ProxyRequest request, Context arg1) {
		/*
		 * Request from API Gateway has no real use in this method. The fact
		 * that we were called will cause us to return all entries.
		 */
		log.info(POJOFunctionUtil.pojoToString(request));
		CAPathGetAllFunction getAll = new CAPathGetAllFunction();
		ProxyResponse allResponse = getAll.handleRequest(request, arg1);
		String jsonBody = allResponse.getBody();
		CAEntryWithSubs entry = null;
		try {
			entry = CAEntryWithSubs.getInstance(jsonBody);
		} catch (IOException e) {
			log.error(e);
			return new ProxyResponseServerError(e.getMessage());
		}
		ProxyResponseTextOk ok = new ProxyResponseTextOk(X509FunctionUtil.getCAEntryWithSubsAsPEM(entry));
		try {
			log.info(mapper.getMapper().writeValueAsString(ok));
		} catch (JsonProcessingException e) {
			log.error(e);
		}
		return ok;
	}
	

}
