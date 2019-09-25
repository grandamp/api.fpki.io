package io.fpki.api.apigateway;

import io.fpki.api.constants.APISettings;

public class ProxyResponseJSONOk extends ProxyResponse {

	public ProxyResponseJSONOk(String body) {
		this.statusCode = 200;
		this.headers = APISettings.instance().getJSONHeaders();
		this.body = body;
	}

}