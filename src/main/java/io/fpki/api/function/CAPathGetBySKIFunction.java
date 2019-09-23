package io.fpki.api.function;

import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import io.fpki.api.apigateway.ProxyRequest;
import io.fpki.api.apigateway.ProxyResponse;
import io.fpki.api.apigateway.ProxyResponseOk;
import io.fpki.api.apigateway.ProxyResponseServerError;
import io.fpki.api.constants.TrustAnchor;
import io.fpki.api.dynamodb.DynamoDBCAEntry;
import io.fpki.api.dynamodb.DynamoDBCAEntryPOJO;
import io.fpki.api.function.utilities.POJOFunctionUtil;
import io.fpki.api.pojo.CAEntryWithSubs;

public class CAPathGetBySKIFunction implements RequestHandler<ProxyRequest, ProxyResponse> {

	private static final Logger log = Logger.getLogger(CAPathGetBySKIFunction.class);

	private static final DynamoDBCAEntry ddbEntry = DynamoDBCAEntry.instance();

	@Override
	public ProxyResponse handleRequest(ProxyRequest request, Context arg1) {
		/*
		 * This request is received with PathParameters rather than a body.
		 * 
		 * 
		 * This handler makes use of simple recursion to obtain a specific path,
		 * performing an AKI chase, like an AIA chase.
		 * 
		 * - First, get the subject certificate identified from the
		 * "/ca/{caSKI}" handler
		 */
		log.info(POJOFunctionUtil.pojoToString(request));
		CAGetBySKIFunction getBySki = new CAGetBySKIFunction();
		ProxyResponse skiResponse = getBySki.handleRequest(request, arg1);
		if (skiResponse.getStatusCode() == 200) {
			String jsonBody = skiResponse.getBody();
			CAEntryWithSubs currentEntry = null;
			try {
				currentEntry = CAEntryWithSubs.getInstance(jsonBody);
			} catch (IOException e) {
				log.error(e);
				return new ProxyResponseServerError("Inconsistency or encoding error in data storage");
			}
			if (currentEntry.caAKI.equalsIgnoreCase(TrustAnchor.getTrustAnchor().caAKI)) {
				return new ProxyResponseOk(currentEntry.toString(), "application/json");
			} else {
				/*
				 * - Next, find the issuing CA from this entries AKI, and set it
				 * as our primary entry, with the current entry nested as a
				 * caSubordinate value.
				 */
				while (!currentEntry.caAKI.equalsIgnoreCase(TrustAnchor.getTrustAnchor().caAKI)) {
					currentEntry = getIssuerAndAppendAsSub(currentEntry);
					if (null == currentEntry) {
						return new ProxyResponseServerError("Inconsistency or encoding error in data storage");
					}
				}
				return new ProxyResponseOk(currentEntry.toString(), "application/json");
			}
		} else {
			return skiResponse;
		}
	}

	private CAEntryWithSubs getIssuerAndAppendAsSub(CAEntryWithSubs currentEntry) {
		List<DynamoDBCAEntryPOJO> entries = ddbEntry.getCA(currentEntry.caAKI);
		if (entries.isEmpty()) {
			/*
			 * Should never get here, as the Issuer must have been present for
			 * this entry to have been added.
			 */
			log.error("DynamoDB Table containing CAEntry records is not consistent.  Issuer does not exist!");
			log.error("{\"BAD_ENTRY\":" + currentEntry.toString() + "}");
			return null;
		} else {
			CAEntryWithSubs primaryEntry = null;
			try {
				primaryEntry = CAEntryWithSubs.getInstance(entries.get(0).toString());
			} catch (IOException e) {
				log.error(e);
				return null;
			}
			CAEntryWithSubs[] subs = new CAEntryWithSubs[1];
			subs[0] = currentEntry;
			primaryEntry.caSubordinates = subs;
			log.info("{\"RETURNING_ENTRY\":" + currentEntry.toString() + "}");
			return primaryEntry;
		}
	}
}
