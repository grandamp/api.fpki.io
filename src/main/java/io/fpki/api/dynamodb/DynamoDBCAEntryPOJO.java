package io.fpki.api.dynamodb;

import java.nio.ByteBuffer;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.amazonaws.util.Base64;
import com.fasterxml.jackson.core.io.JsonStringEncoder;

@DynamoDBTable(tableName = "fpki_list")
public class DynamoDBCAEntryPOJO {

	private String caAKI;

	/**
	 * @return the caAKI
	 */
	@DynamoDBAttribute(attributeName = "caAKI")
	public String getCaAKI() {
		return caAKI;
	}

	/**
	 * @param caAKI
	 *            the caAKI to set
	 */
	public void setCaAKI(String caAKI) {
		this.caAKI = caAKI;
	}

	/**
	 * It's important to note that AWS DynamoDB only serializes the ByteBuffer
	 * to Base64 during a DynamoDBMapper.save(<DynamoDBCAEntryPOJO>) operation.
	 * When fetching records from DynamoDB, the caCert ByteBuffer will be filled
	 * with the byte[] encoding of the Base64 String value.
	 */
	private ByteBuffer caCert;

	/**
	 * @return the caCert
	 */
	@DynamoDBAttribute(attributeName = "caCert")
	public ByteBuffer getCaCert() {
		return caCert;
	}

	/**
	 * @param caCert
	 *            the caCert to set
	 */
	public void setCaCert(ByteBuffer caCert) {
		this.caCert = caCert;
	}

	private String caCrl;

	/**
	 * @return the caCrl
	 */
	@DynamoDBAttribute(attributeName = "caCrl")
	public String getCaCrl() {
		return caCrl;
	}

	/**
	 * @param caCrl
	 *            the caCrl to set
	 */
	public void setCaCrl(String caCrl) {
		this.caCrl = caCrl;
	}

	private String caHash;

	/**
	 * @return the caHash
	 */
	@DynamoDBAttribute(attributeName = "caHash")
	public String getCaHash() {
		return caHash;
	}

	/**
	 * @param caHash
	 *            the caHash to set
	 */
	public void setCaHash(String caHash) {
		this.caHash = caHash;
	}

	private String caIssuer;

	/**
	 * @return the caIssuer
	 */
	@DynamoDBAttribute(attributeName = "caIssuer")
	public String getCaIssuer() {
		return caIssuer;
	}

	/**
	 * @param caIssuer
	 *            the caIssuer to set
	 */
	public void setCaIssuer(String caIssuer) {
		this.caIssuer = caIssuer;
	}

	private String caNotAfter;

	/**
	 * @return the caNotAfter
	 */
	@DynamoDBAttribute(attributeName = "caNotAfter")
	public String getCaNotAfter() {
		return caNotAfter;
	}

	/**
	 * @param caNotAfter
	 *            the caNotAfter to set
	 */
	public void setCaNotAfter(String caNotAfter) {
		this.caNotAfter = caNotAfter;
	}

	private String caNotBefore;

	/**
	 * @return the caNotBefore
	 */
	@DynamoDBAttribute(attributeName = "caNotBefore")
	public String getCaNotBefore() {
		return caNotBefore;
	}

	/**
	 * @param caNotBefore
	 *            the caNotBefore to set
	 */
	public void setCaNotBefore(String caNotBefore) {
		this.caNotBefore = caNotBefore;
	}

	private String caSerial;

	/**
	 * @return the caSerial
	 */
	@DynamoDBAttribute(attributeName = "caSerial")
	public String getCaSerial() {
		return caSerial;
	}

	/**
	 * @param caSerial
	 *            the caSerial to set
	 */
	public void setCaSerial(String caSerial) {
		this.caSerial = caSerial;
	}

	private String caSKI;

	/**
	 * @return the caSKI
	 */
	@DynamoDBHashKey(attributeName = "caSKI")
	public String getCaSKI() {
		return caSKI;
	}

	/**
	 * @param caSKI
	 *            the caSKI to set
	 */
	public void setCaSKI(String caSKI) {
		this.caSKI = caSKI;
	}

	private String caSubject;

	/**
	 * @return the caSubject
	 */
	@DynamoDBAttribute(attributeName = "caSubject")
	public String getCaSubject() {
		return caSubject;
	}

	/**
	 * @param caSubject
	 *            the caSubject to set
	 */
	public void setCaSubject(String caSubject) {
		this.caSubject = caSubject;
	}

	@Override
	public String toString() {
		String caCertBase64 = Base64.encodeAsString(caCert.array());
		JsonStringEncoder e = JsonStringEncoder.getInstance();
		StringBuffer sb = new StringBuffer();
		sb.append("{");
		sb.append("\"caAKI\":\"" + new String(e.quoteAsUTF8(caAKI)) + "\",");
		sb.append("\"caCert\":\"" + new String(e.quoteAsUTF8(caCertBase64)) + "\",");
		sb.append("\"caCrl\":\"" + new String(e.quoteAsUTF8(caCrl)) + "\",");
		sb.append("\"caHash\":\"" + new String(e.quoteAsUTF8(caHash)) + "\",");
		sb.append("\"caIssuer\":\"" + new String(e.quoteAsUTF8(caIssuer)) + "\",");
		sb.append("\"caNotAfter\":\"" + new String(e.quoteAsUTF8(caNotAfter)) + "\",");
		sb.append("\"caNotBefore\":\"" + new String(e.quoteAsUTF8(caNotBefore)) + "\",");
		sb.append("\"caSerial\":\"" + new String(e.quoteAsUTF8(caSerial)) + "\",");
		sb.append("\"caSKI\":\"" + new String(e.quoteAsUTF8(caSKI)) + "\",");
		sb.append("\"caSubject\":\"" + new String(e.quoteAsUTF8(caSubject)) + "\"");
		sb.append("}");
		return sb.toString();
	}

}
