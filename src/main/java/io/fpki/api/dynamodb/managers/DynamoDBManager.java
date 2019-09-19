package io.fpki.api.dynamodb.managers;

import org.apache.log4j.Logger;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBAsyncClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;

public class DynamoDBManager {

	private static final Logger log = Logger.getLogger(DynamoDBManager.class);

	private static volatile DynamoDBManager instance;

	private static DynamoDBMapper mapper;

	private DynamoDBManager() {
		log.info("Creating DynamoDBManager Instance");
		AmazonDynamoDB client = AmazonDynamoDBAsyncClientBuilder
				.standard()
				.withRegion(Regions.US_EAST_1)
				.build();
		mapper = new DynamoDBMapper(client);
	}

	public static DynamoDBManager instance() {
		if (instance == null) {
			synchronized (DynamoDBManager.class) {
				if (instance == null)
					instance = new DynamoDBManager();
			}
		}
		return instance;
	}

	public DynamoDBMapper mapper() {
		return mapper;
	}

}
