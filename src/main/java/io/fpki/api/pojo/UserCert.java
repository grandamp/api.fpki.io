package io.fpki.api.pojo;

import java.io.IOException;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.fpki.api.constants.POJOObjectMapper;

/**
 * This class is a Java representation of the UserCert JSON object.
 * 
 * <pre>
 * {
 *     "caSKI": "FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF",
 *     "urlAIA": [
 *         "http://foo.foo/foo.p7c"
 *     ],
 *     "urlCRL": [
 *         "http://foo.foo/foo.crl"
 *     ],
 *     "urlOCSP": [
 *         "http://foo.foo" <TODO: must determine issuerNameHash and issuerKeyHash with userCert serial to form OCSP get request
 *     ],
 *     "userCert": "MIIE9TCC=="
 * }
 * </pre>
 */
@XmlRootElement
@XmlType(propOrder = { "userCert", "caSKI", "urlOCSP", "urlCRL", "urlAIA" })
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserCert {

	/**
	 * Static creator for de-serialization
	 * 
	 * @throws IOException
	 * @throws JsonMappingException
	 * @throws JsonParseException
	 */
	@JsonCreator
	public static UserCert getInstance(String jsonString) throws JsonParseException, JsonMappingException, IOException {
		ObjectMapper mapper = POJOObjectMapper.instance().getMapper();
		UserCert req = null;
		req = mapper.readValue(jsonString, UserCert.class);
		return req;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		ObjectMapper mapper = POJOObjectMapper.instance().getMapper();
		try {
			return mapper.writeValueAsString(this);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Field userCert.
	 */
	@JsonProperty("userCert")
	public String userCert;

	/**
	 * Field caSKI.
	 */
	@JsonProperty("caSKI")
	public String caSKI;

	/**
	 * Field urlOCSP.
	 */
	@JsonProperty("urlOCSP")
	public String[] urlOCSP;

	/**
	 * Field urlCRL.
	 */
	@JsonProperty("urlCRL")
	public String[] urlCRL;

	/**
	 * Field urlAIA.
	 */
	@JsonProperty("urlAIA")
	public String[] urlAIA;

}