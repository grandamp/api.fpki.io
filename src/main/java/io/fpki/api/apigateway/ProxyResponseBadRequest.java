package io.fpki.api.apigateway;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.fpki.api.constants.APISettings;
import io.fpki.api.constants.POJOObjectMapper;

public class ProxyResponseBadRequest extends ProxyResponse {

	public ProxyResponseBadRequest(String errorMessage) {
		this.statusCode = 400;
		this.headers = APISettings.instance().getJSONHeaders();
		Map<String, String> error = new HashMap<>();
		error.put("message", errorMessage);
		ObjectMapper mapper = POJOObjectMapper.instance().getMapper();
		try {
			this.body = mapper.writeValueAsString(error);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
	}

}