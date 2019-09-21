package io.fpki.api.function;

import org.apache.log4j.Logger;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import io.fpki.api.apigateway.ProxyRequest;
import io.fpki.api.apigateway.ProxyResponse;
import io.fpki.api.dynamodb.DynamoDBCAEntry;

public class CAPathGetBySKIFunction implements RequestHandler<ProxyRequest, ProxyResponse> {

	private static final Logger log = Logger.getLogger(CAPathGetBySKIFunction.class);

	private static final DynamoDBCAEntry ddbEntry = DynamoDBCAEntry.instance();

	@Override
	public ProxyResponse handleRequest(ProxyRequest request, Context arg1) {
		/*
		 * This request is received with PathParameters rather than a body.
		 */
		
		/*
		 * TODO:  Write handler logic for endpoint
		 */
		return new ProxyResponse("Endpoint logic not yet implemented");
	}

}
