package io.fpki.api.apigateway;

import io.fpki.api.constants.APISettings;

public class ProxyResponseNotFound extends ProxyResponse {

	public ProxyResponseNotFound() {
		this.statusCode = 404;
		this.headers = APISettings.instance().getJSONHeaders();
		this.body = "{\"message\":\"item not found\"}";
	}

}