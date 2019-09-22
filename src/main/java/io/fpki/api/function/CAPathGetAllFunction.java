package io.fpki.api.function;

import org.apache.log4j.Logger;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.fpki.api.apigateway.ProxyRequest;
import io.fpki.api.apigateway.ProxyResponse;
import io.fpki.api.dynamodb.DynamoDBCAEntry;

public class CAPathGetAllFunction implements RequestHandler<ProxyRequest, ProxyResponse> {

	private static final Logger log = Logger.getLogger(CAPathGetAllFunction.class);

	private static final DynamoDBCAEntry ddbEntry = DynamoDBCAEntry.instance();

	@Override
	public ProxyResponse handleRequest(ProxyRequest request, Context arg1) {
		/*
		 * Request from API Gateway has no real use in this method.
		 * The fact that we were called will cause us to return all entries.
		 */
		log.info("getAllEntriesHandler invoked to scan table for ALL CAs");
		ObjectMapper mapper = new ObjectMapper();
		String jsonString = null;
		try {
			jsonString = mapper.writeValueAsString(request);
			log.info(jsonString);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		/*
		 * Request from API Gateway has no real use in this method.
		 * The fact that we were called will cause us to return all entries.
		 */
		
		/*
		 * TODO:  Write handler logic for endpoint
		 * 
		 * Use recursion through list reduction (intial nested JSON concept code).
		 */
		return new ProxyResponse("Endpoint logic not yet implemented");
	}

}
