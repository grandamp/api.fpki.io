package io.fpki.api.function;

import java.util.List;

import org.apache.log4j.Logger;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.fpki.api.apigateway.ProxyRequest;
import io.fpki.api.apigateway.ProxyResponse;
import io.fpki.api.dynamodb.DynamoDBCAEntry;
import io.fpki.api.dynamodb.DynamoDBCAEntryPOJO;

public class CAGetAllFunction implements RequestHandler<ProxyRequest, ProxyResponse> {

	private static final Logger log = Logger.getLogger(CAGetAllFunction.class);

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
		List<DynamoDBCAEntryPOJO> ddbEntries = ddbEntry.getEveryCA();
		log.info("Found " + ddbEntries.size() + " total entries.");
		jsonString = null;
		try {
			jsonString = mapper.writeValueAsString(ddbEntries);
			log.info(jsonString);
		} catch (JsonProcessingException e) {
			log.error("Error converting DynamoDBCAEntryPOJO to JSON", e);
		}
		return new ProxyResponse(200, jsonString);
	}

}
