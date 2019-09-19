package io.fpki.api.dynamodb;

import java.util.List;

import org.apache.log4j.Logger;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;

import io.fpki.api.dynamodb.managers.DynamoDBManager;

public class DynamoDBCAEntry implements DynamoDBCAEntryInterface {

	private static final Logger log = Logger.getLogger(DynamoDBCAEntry.class);

	private static final DynamoDBMapper mapper = DynamoDBManager.instance().mapper();

	private static volatile DynamoDBCAEntry instance;

	private DynamoDBCAEntry() {
		log.info("Creating DynamoDBCAEntry Instance");
	}

	public static DynamoDBCAEntry instance() {
		if (instance == null) {
			synchronized (DynamoDBCAEntry.class) {
				if (instance == null)
					instance = new DynamoDBCAEntry();
			}
		}
		return instance;
	}

	@Override
	public List<DynamoDBCAEntryPOJO> getEveryCA() {
		return mapper.scan(DynamoDBCAEntryPOJO.class, new DynamoDBScanExpression());
	}

	@Override
	public List<DynamoDBCAEntryPOJO> getCA(String caSKI) {
		DynamoDBQueryExpression<DynamoDBCAEntryPOJO> caQuery = new DynamoDBQueryExpression<>();
		DynamoDBCAEntryPOJO caKey = new DynamoDBCAEntryPOJO();
		caKey.setCaSKI(caSKI);
		caQuery.setHashKeyValues(caKey);
		List<DynamoDBCAEntryPOJO> caEntries = mapper.query(DynamoDBCAEntryPOJO.class, caQuery);
		return caEntries;
	}

	@Override
	public void createCA(DynamoDBCAEntryPOJO entry) {
		mapper.save(entry);
	}

}
