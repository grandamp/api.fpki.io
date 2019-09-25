package io.fpki.api.function.utilities;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.fpki.api.constants.POJOObjectMapper;

public class POJOFunctionUtil {

	/*
	 * Hidden Constructor
	 */
	private POJOFunctionUtil() {}

	public static String pojoToString(Object obj) {
		ObjectMapper mapper = POJOObjectMapper.instance().getMapper();
		String jsonString = null;
		try {
			jsonString = mapper.writeValueAsString(obj);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return jsonString;
	}

}
