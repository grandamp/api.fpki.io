package io.fpki.api.function;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.fpki.api.dynamodb.DynamoDBCAEntry;
import io.fpki.api.dynamodb.DynamoDBCAEntryPOJO;
import io.fpki.api.pojo.ApiGatewayProxyRequest;
import io.fpki.api.pojo.ApiGatewayProxyResponse;

public class CAGetBySKIFunction implements RequestHandler<ApiGatewayProxyRequest, ApiGatewayProxyResponse> {

	private static final Logger log = Logger.getLogger(CAGetBySKIFunction.class);

	private static final DynamoDBCAEntry ddbEntry = DynamoDBCAEntry.instance();

	@Override
	public ApiGatewayProxyResponse handleRequest(ApiGatewayProxyRequest request, Context arg1) {
		/*
		 * This request is received with PathParameters rather than a body.
		 */
		ObjectMapper mapper = new ObjectMapper();
		String jsonString = null;
		try {
			jsonString = mapper.writeValueAsString(request);
			log.info(jsonString);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		Map<String, String> pathParams = request.getPathParameters();
		String querySKI = null;
		if (null != pathParams.get("caSKI")) {
			querySKI = pathParams.get("caSKI");
		}
		if (null != querySKI) {
			DynamoDBCAEntryPOJO ski = new DynamoDBCAEntryPOJO();
			ski.setCaSKI(querySKI);
			log.info("getCAsBySKIHandler invoked with caSKI = " + ski.getCaSKI());
			List<DynamoDBCAEntryPOJO> ddbEntries = ddbEntry.getCA(ski.getCaSKI());
			log.info("Found " + ddbEntries.size() + " entries for caSKI = " + ski.getCaSKI());
			jsonString = null;
			try {
				jsonString = mapper.writeValueAsString(ddbEntries);
				log.info(jsonString);
			} catch (JsonProcessingException e) {
				log.error("Error converting DynamoDBCAEntryPOJO to JSON", e);
			}
			return new ApiGatewayProxyResponse(200, jsonString);
		} else {
			return new ApiGatewayProxyResponse("caSKI must be the Hex value representing the SHA-1 digest of the CA's subjectPublicKeyInfo");
		}
	}

}
