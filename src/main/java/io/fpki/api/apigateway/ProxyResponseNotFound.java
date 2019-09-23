package io.fpki.api.apigateway;

import java.util.HashMap;

public class ProxyResponseNotFound extends ProxyResponse {

	public ProxyResponseNotFound() {
		this.statusCode = 404;
		this.headers = new HashMap<String, String>();
		this.headers.put("Content-Type", "application/json");
		this.body = "{\"message\":\"item not found\"}";
	}

}