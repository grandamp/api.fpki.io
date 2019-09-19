package io.fpki.api.pojo;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.fpki.api.dynamodb.DynamoDBCAEntryPOJO;

/*
 * Per: https://willhamill.com/2016/12/12/aws-api-gateway-lambda-proxy-request-and-response-objects
 * 
 *   Response class for an API Gateway proxied lambda must conform to these three properties as per AWS docs
 *   https://docs.aws.amazon.com/apigateway/latest/developerguide/api-gateway-set-up-simple-proxy.html#api-gateway-simple-proxy-for-lambda-output-format
 *   Headers map will be merged with whichever header AWS sets, e.g. X-Amzn-Trace-Id
 */

public class ApiGatewayProxyResponse {

	private int statusCode;
	private Map<String, String> headers;
	private String body;
	private boolean isBase64Encoded = false;

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

	public String getBody() throws JsonParseException, JsonMappingException, IOException {
		return this.body;
	}

	public void setBody(List<DynamoDBCAEntryPOJO> body) throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		this.body = mapper.writeValueAsString(body);
	}

	public ApiGatewayProxyResponse(String errorMessage) {
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
		this.isBase64Encoded = false;
	}

	public ApiGatewayProxyResponse(int statusCode, String body) {
		this.statusCode = statusCode;
		headers = new HashMap<String, String>();
		headers.put("Content-Type", "application/json");
		this.body = body;
		this.isBase64Encoded = false;
	}

	/**
	 * @return the isBase64Encoded
	 */
	public boolean isBase64Encoded() {
		return isBase64Encoded;
	}

	/**
	 * @param isBase64Encoded
	 *            the isBase64Encoded to set
	 */
	public void setBase64Encoded(boolean isBase64Encoded) {
		this.isBase64Encoded = isBase64Encoded;
	}
}