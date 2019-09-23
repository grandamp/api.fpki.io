package io.fpki.api.apigateway;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ProxyResponseServerError extends ProxyResponse {

	public ProxyResponseServerError(String errorMessage) {
		this.statusCode = 500;
		this.headers = new HashMap<String, String>();
		this.headers.put("Content-Type", "application/json");
		Map<String, String> error = new HashMap<>();
		error.put("message", errorMessage);
		ObjectMapper mapper = new ObjectMapper();
		try {
			this.body = mapper.writeValueAsString(error);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
	}

}