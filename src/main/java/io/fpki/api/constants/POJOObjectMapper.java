package io.fpki.api.constants;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;

public class POJOObjectMapper {

	private static final Logger log = Logger.getLogger(POJOObjectMapper.class);

	private static volatile POJOObjectMapper instance;

	private static ObjectMapper mapper;

	private POJOObjectMapper() {
		log.info("Creating DynamoDBManager Instance");
		mapper = new ObjectMapper();
	}

	public static POJOObjectMapper instance() {
		if (instance == null) {
			synchronized (POJOObjectMapper.class) {
				if (instance == null)
					instance = new POJOObjectMapper();
			}
		}
		return instance;
	}

	public ObjectMapper mapper() {
		return mapper;
	}

}
