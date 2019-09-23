package io.fpki.api.function;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import io.fpki.api.apigateway.ProxyRequest;
import io.fpki.api.apigateway.ProxyResponse;
import io.fpki.api.apigateway.ProxyResponseNotFound;
import io.fpki.api.apigateway.ProxyResponseOk;
import io.fpki.api.apigateway.ProxyResponseServerError;
import io.fpki.api.dynamodb.DynamoDBCAEntry;
import io.fpki.api.dynamodb.DynamoDBCAEntryPOJO;
import io.fpki.api.function.utilities.POJOFunctionUtil;

public class CAGetBySKIFunction implements RequestHandler<ProxyRequest, ProxyResponse> {

	private static final Logger log = Logger.getLogger(CAGetBySKIFunction.class);

	private static final DynamoDBCAEntry ddbEntry = DynamoDBCAEntry.instance();

	@Override
	public ProxyResponse handleRequest(ProxyRequest request, Context arg1) {
		/*
		 * This request is received with PathParameters rather than a body.
		 */
		log.info(POJOFunctionUtil.pojoToString(request));
		Map<String, String> pathParams = request.getPathParameters();
		String querySKI = null;
		if (null != pathParams.get("caSKI")) {
			querySKI = pathParams.get("caSKI");
		}
		if (null != querySKI) {
			DynamoDBCAEntryPOJO ski = new DynamoDBCAEntryPOJO();
			ski.setCaSKI(querySKI);
			log.info("getCAsBySKIHandler invoked with caSKI = " + ski.getCaSKI());
			List<DynamoDBCAEntryPOJO> ddbEntries = ddbEntry.getCA(ski.getCaSKI());
			if (ddbEntries.size() == 0) {
				return new ProxyResponseNotFound();
			} else {
				return new ProxyResponseOk(ddbEntries.get(0).toString(), "application/json");
			}
		} else {
			return new ProxyResponseServerError(
					"caSKI must be the Hex value representing the SHA-1 digest of the CA's subjectPublicKeyInfo");
		}
	}
}
