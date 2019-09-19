package io.fpki.api.dynamodb;

import java.util.List;

import io.fpki.api.dynamodb.DynamoDBCAEntryPOJO;

public class DynamoDBCAEntriesPOJO {

	private List<DynamoDBCAEntryPOJO> entries;

	/**
	 * @return the entries
	 */
	public List<DynamoDBCAEntryPOJO> getEntries() {
		return entries;
	}

	/**
	 * @param entries the entries to set
	 */
	public void setEntries(List<DynamoDBCAEntryPOJO> entries) {
		this.entries = entries;
	}

}
