package io.fpki.api.apigateway;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.fpki.api.dynamodb.DynamoDBCAEntryPOJO;
import io.fpki.api.pojo.CAEntryWithSubs;

/*
 * Per: https://willhamill.com/2016/12/12/aws-api-gateway-lambda-proxy-request-and-response-objects
 * 
 *   Response class for an API Gateway proxied lambda must conform to these three properties as per AWS docs
 *   https://docs.aws.amazon.com/apigateway/latest/developerguide/api-gateway-set-up-simple-proxy.html#api-gateway-simple-proxy-for-lambda-output-format
 *   Headers map will be merged with whichever header AWS sets, e.g. X-Amzn-Trace-Id
 *   
 *   TEJ:  Removed isBase64Encoded, due to sam local sending an error:  Invalid API Gateway Response Keys: {'base64Encoded'} in {'statusCode': 200, 'headers': {'Content-Type': 'application/json'}, 'body': '[{"caAKI":"AD0C[TRUNCATED]
 */

public class ProxyResponse {

	private int statusCode;
	private Map<String, String> headers;
	private String body;

	public int getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}

	public Map<String, String> getHeaders() {
		return headers;
	}

	public void setHeaders(Map<String, String> headers) {
		this.headers = headers;
	}

	public String getBody() {
		return this.body;
	}

	public void setBody(List<DynamoDBCAEntryPOJO> body) throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		this.body = mapper.writeValueAsString(body);
	}

	public ProxyResponse(String errorMessage) {
		this.statusCode = 500;
		headers = new HashMap<String, String>();
		headers.put("Content-Type", "application/json");
		Map<String, String> error = new HashMap<>();
		error.put("message", errorMessage);
		ObjectMapper mapper = new ObjectMapper();
		try {
			this.body = mapper.writeValueAsString(error);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
	}

	public ProxyResponse(int statusCode, String body) {
		this.statusCode = statusCode;
		headers = new HashMap<String, String>();
		headers.put("Content-Type", "application/json");
		this.body = body;
	}

	public ProxyResponse(int i, CAEntryWithSubs entry) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			this.body = mapper.writeValueAsString(entry);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
	}

}