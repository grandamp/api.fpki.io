package io.fpki.api.apigateway;

/*
 * Per: https://willhamill.com/2016/12/12/aws-api-gateway-lambda-proxy-request-and-response-objects
 */
public class RequestContext {

	private String accountId;
	private String resourceId;
	private String stage;
	private String requestId;
	private Identity identity;
	private String resourcePath;
	private String httpMethod;
	private String apiId;

	public String getAccountId() {
		return accountId;
	}

	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}

	public String getResourceId() {
		return resourceId;
	}

	public void setResourceId(String resourceId) {
		this.resourceId = resourceId;
	}

	public String getStage() {
		return stage;
	}

	public void setStage(String stage) {
		this.stage = stage;
	}

	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	public Identity getIdentity() {
		return identity;
	}

	public void setIdentity(Identity identity) {
		this.identity = identity;
	}

	public String getResourcePath() {
		return resourcePath;
	}

	public void setResourcePath(String resourcePath) {
		this.resourcePath = resourcePath;
	}

	public String getHttpMethod() {
		return httpMethod;
	}

	public void setHttpMethod(String httpMethod) {
		this.httpMethod = httpMethod;
	}

	public String getApiId() {
		return apiId;
	}

	public void setApiId(String apiId) {
		this.apiId = apiId;
	}
}
