package io.fpki.api.apigateway;

import java.util.Map;

public abstract class ProxyResponse {

	public int statusCode;
	public Map<String, String> headers;
	public String body;

	public int getStatusCode() {
		return statusCode;
	}

	public Map<String, String> getHeaders() {
		return headers;
	}

	public String getBody() {
		return this.body;
	}

}