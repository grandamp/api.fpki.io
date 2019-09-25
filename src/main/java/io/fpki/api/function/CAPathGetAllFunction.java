package io.fpki.api.function;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.log4j.Logger;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.fpki.api.apigateway.ProxyRequest;
import io.fpki.api.apigateway.ProxyResponse;
import io.fpki.api.apigateway.ProxyResponseOk;
import io.fpki.api.apigateway.ProxyResponseServerError;
import io.fpki.api.constants.POJOObjectMapper;
import io.fpki.api.constants.TrustAnchor;
import io.fpki.api.function.utilities.POJOFunctionUtil;
import io.fpki.api.pojo.CAEntryWithSubs;

public class CAPathGetAllFunction implements RequestHandler<ProxyRequest, ProxyResponse> {

	private static final Logger log = Logger.getLogger(CAPathGetAllFunction.class);

	private static CAEntryWithSubs trustAnchor;

	@Override
	public ProxyResponse handleRequest(ProxyRequest request, Context arg1) {
		/*
		 * Request from API Gateway has no real use in this method. The fact
		 * that we were called will cause us to return all entries.
		 */
		log.info(POJOFunctionUtil.pojoToString(request));
		/*
		 * Call /ca endpoint handler, and get all entries in the DynamoDB table.
		 */
		CAGetAllFunction getAll = new CAGetAllFunction();
		ProxyResponse allResponse = getAll.handleRequest(request, arg1);
		if (allResponse.getStatusCode() == 200) {
			String jsonBody = allResponse.getBody();
			CAEntryWithSubs[] entries = null;
			ObjectMapper mapper = POJOObjectMapper.instance().mapper();
			try {
				entries = mapper.readValue(jsonBody, CAEntryWithSubs[].class);
			} catch (IOException e) {
				log.error(e);
				return new ProxyResponseServerError("error serializing data from /ca endpoint");
			}
			List<CAEntryWithSubs> entryList = new CopyOnWriteArrayList<CAEntryWithSubs>();
			for (CAEntryWithSubs entry : entries) {
				if (!entry.caSKI.equalsIgnoreCase(TrustAnchor.getTrustAnchor().caSKI)) {
					entryList.add(entry);
				} else {
					trustAnchor = entry;
				}
			}
			while (entryList.size() != 0) {
				entryList = findSubordinates(entryList);
			}
			return new ProxyResponseOk(trustAnchor.toString(), "application/json");
		} else {
			return allResponse;
		}
	}

	private synchronized static List<CAEntryWithSubs> findSubordinates(List<CAEntryWithSubs> entries) {
		List<CAEntryWithSubs> newEntryList = entries;
		for (CAEntryWithSubs entry : entries) {
			if (trustAnchor.addSubordinate(entry)) {
				newEntryList.remove(entry);
			}
		}
		return newEntryList;
	}

}
