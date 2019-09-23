package io.fpki.api.apigateway;

import java.util.HashMap;

public class ProxyResponseOk extends ProxyResponse {

	public ProxyResponseOk(String body, String contentType) {
		this.statusCode = 200;
		this.headers = new HashMap<String, String>();
		this.headers.put("Content-Type", contentType);
		this.body = body;
	}

}