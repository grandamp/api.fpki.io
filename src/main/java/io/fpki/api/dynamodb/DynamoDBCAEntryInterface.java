package io.fpki.api.dynamodb;

import java.util.List;

public interface DynamoDBCAEntryInterface {

	List<DynamoDBCAEntryPOJO> getEveryCA();

	List<DynamoDBCAEntryPOJO> getCA(String caSKI);

	void createCA(DynamoDBCAEntryPOJO entry);

}
