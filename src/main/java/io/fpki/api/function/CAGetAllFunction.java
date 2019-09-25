package io.fpki.api.function;

import java.util.List;

import org.apache.log4j.Logger;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import io.fpki.api.apigateway.ProxyRequest;
import io.fpki.api.apigateway.ProxyResponse;
import io.fpki.api.apigateway.ProxyResponseJSONOk;
import io.fpki.api.dynamodb.DynamoDBCAEntry;
import io.fpki.api.dynamodb.DynamoDBCAEntryPOJO;
import io.fpki.api.function.utilities.POJOFunctionUtil;

public class CAGetAllFunction implements RequestHandler<ProxyRequest, ProxyResponse> {

	private static final Logger log = Logger.getLogger(CAGetAllFunction.class);

	private static final DynamoDBCAEntry ddbEntry = DynamoDBCAEntry.instance();

	@Override
	public ProxyResponse handleRequest(ProxyRequest request, Context arg1) {
		/*
		 * Request from API Gateway has no real use in this method. The fact
		 * that we were called will cause us to return all entries.
		 */
		log.info(POJOFunctionUtil.pojoToString(request));
		List<DynamoDBCAEntryPOJO> ddbEntries = ddbEntry.getEveryCA();
		log.info("Found " + ddbEntries.size() + " total entries.");
		return new ProxyResponseJSONOk(POJOFunctionUtil.pojoToString(ddbEntries));
	}

}
