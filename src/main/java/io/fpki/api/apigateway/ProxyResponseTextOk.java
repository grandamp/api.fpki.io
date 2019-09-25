package io.fpki.api.apigateway;

import io.fpki.api.constants.APISettings;

public class ProxyResponseTextOk extends ProxyResponse {

	public ProxyResponseTextOk(String body) {
		this.statusCode = 200;
		this.headers = APISettings.instance().getPlaintextHeaders();
		this.body = body;
	}

}