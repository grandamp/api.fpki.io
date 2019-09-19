package io.fpki.api.pojo;

import java.io.IOException;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * This class is a Java representation of the DynamoDBCAEntryPOJO JSON object.
 * 
 * { "caAKI": "TRUST_ANCHOR_NOT_APPLICABLE", "caCert":
 * "MIIEYDCCA0igAwIBAgICATAwDQYJKoZIhvcNAQELBQAwWTELMAkGA1UEBhMCVVMxGDAWBgNVBAoTD1UuUy4gR292ZXJubWVudDENMAsGA1UECxMERlBLSTEhMB8GA1UEAxMYRmVkZXJhbCBDb21tb24gUG9saWN5IENBMB4XDTEwMTIwMTE2NDUyN1oXDTMwMTIwMTE2NDUyN1owWTELMAkGA1UEBhMCVVMxGDAWBgNVBAoTD1UuUy4gR292ZXJubWVudDENMAsGA1UECxMERlBLSTEhMB8GA1UEAxMYRmVkZXJhbCBDb21tb24gUG9saWN5IENBMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA2HX7NRY0WkG/Wq9cMAQUHK14RLXqJup1YcfNNnn4fNi9KVFmWSHjeavUeL6wLbCh1bI1FiPQzB6+Duir3MPJ1hLXp3JoGDG4FyKyPn66CG3G/dFYLGmgA/Aqo/Y/ISU937cyxY4nsyOl4FKzXZbpsLjFxZ+7xaBugkC7xScFNknWJidpDDSPzyd6KgqjQV+NHQOGgxXgVcHFmCye7Bpy3EjBPvmE0oSCwRvDdDa3ucc2Mnr4MrbQNq4iGDGMUHMhnv6DOzCIJOPpwX7e7ZjHH5IQip9bYi+dpLzVhW86/clTpyBLqtsgqyFOHQ1O5piF5asRR12dP8QjwOMUBm7+nQIDAQABo4IBMDCCASwwDwYDVR0TAQH/BAUwAwEB/zCB6QYIKwYBBQUHAQsEgdwwgdkwPwYIKwYBBQUHMAWGM2h0dHA6Ly9odHRwLmZwa2kuZ292L2ZjcGNhL2NhQ2VydHNJc3N1ZWRCeWZjcGNhLnA3YzCBlQYIKwYBBQUHMAWGgYhsZGFwOi8vbGRhcC5mcGtpLmdvdi9jbj1GZWRlcmFsJTIwQ29tbW9uJTIwUG9saWN5JTIwQ0Esb3U9RlBLSSxvPVUuUy4lMjBHb3Zlcm5tZW50LGM9VVM/Y0FDZXJ0aWZpY2F0ZTtiaW5hcnksY3Jvc3NDZXJ0aWZpY2F0ZVBhaXI7YmluYXJ5MA4GA1UdDwEB/wQEAwIBBjAdBgNVHQ4EFgQUrQx6dVzl85jEeZgOrCj9l/TnAvwwDQYJKoZIhvcNAQELBQADggEBAI9z2uF/gLGH9uwsz9GEYx728Yi3mvIRte9UrYpuGDco71wb5O9Qt2wmGCMiTR0mRyDpCZzicGJxqxHPkYnos/UqoEfAFMtOQsHdDA4b8Idb7OV316rgVNdF9IU+7LQd3nyKf1tNnJaK0KIyn9psMQz4pO9+c+iR3Ah6cFqgr2KBWfgAdKLI3VTKQVZHvenAT+0g3eOlCd+uKML80cgX2BLHb94u6b2akfI8WpQukSKAiaGMWMyDeiYZdQKlDn0KJnNR6obLB6jI/WNaNZvSr79PMUjBhHDbNXuaGQ/lj/RqDG8z2esccKIN47lQA2EC/0rskqTcLe4qNJMHtyznGI8=",
 * "caCrl": "http://http.fpki.gov/fcpca/fcpca.crl", "caHash":
 * "905F942FD9F28F679B378180FD4F846347F645C1", "caIssuer":
 * "CN=Federal Common Policy CA, OU=FPKI, O=U.S. Government, C=US",
 * "caNotAfter": "2030-12-01T16:45:27.000+0000", "caNotBefore":
 * "2010-12-01T16:45:27.000+0000", "caSerial": "0130", "caSKI":
 * "AD0C7A755CE5F398C479980EAC28FD97F4E702FC", "caSubject":
 * "CN=Federal Common Policy CA, OU=FPKI, O=U.S. Government, C=US"
 * 
 * }
 */
@XmlRootElement
@XmlType(propOrder = { "caAKI", "caCert", "caCrl", "caHash", "caIssuer", "caNotAfter", "caNotBefore", "caSerial",
		"caSKI", "caSubject" })
@JsonIgnoreProperties(ignoreUnknown = true)
public class CAEntry {

	/**
	 * Static creator for de-serialization
	 * 
	 * @throws IOException
	 * @throws JsonMappingException
	 * @throws JsonParseException
	 */
	@JsonCreator
	public static CAEntry getInstance(String jsonString) throws JsonParseException, JsonMappingException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		CAEntry req = null;
		req = mapper.readValue(jsonString, CAEntry.class);
		return req;
	}

	/**
	 * Default Constructor
	 * 
	 * @param caAKI
	 * @param caCert
	 * @param caCrl
	 * @param caHash
	 * @param caIssuer
	 * @param caNotAfter
	 * @param caNotBefore
	 * @param caSerial
	 * @param caSKI
	 * @param caSubject
	 */
	@JsonCreator
	public CAEntry(@JsonProperty("caAKI") String caAKI, @JsonProperty("caCert") String caCert,
			@JsonProperty("caCrl") String caCrl, @JsonProperty("caHash") String caHash,
			@JsonProperty("caIssuer") String caIssuer, @JsonProperty("caNotAfter") String caNotAfter,
			@JsonProperty("caNotBefore") String caNotBefore, @JsonProperty("caSerial") String caSerial,
			@JsonProperty("caSKI") String caSKI, @JsonProperty("caSubject") String caSubject) {
		this.caAKI = caAKI;
		this.caCert = caCert;
		this.caCrl = caCrl;
		this.caHash = caHash;
		this.caIssuer = caIssuer;
		this.caNotAfter = caNotAfter;
		this.caNotBefore = caNotBefore;
		this.caSerial = caSerial;
		this.caSKI = caSKI;
		this.caSubject = caSubject;
	}

	/**
	 * Field caAKI.
	 */
	@JsonProperty("caAKI")
	public String caAKI;

	/**
	 * Field caCert.
	 */
	@JsonProperty("caCert")
	public String caCert;

	/**
	 * Field caCrl.
	 */
	@JsonProperty("caCrl")
	public String caCrl;

	/**
	 * Field caHash.
	 */
	@JsonProperty("caHash")
	public String caHash;

	/**
	 * Field caIssuer.
	 */
	@JsonProperty("caIssuer")
	public String caIssuer;

	/**
	 * Field caNotAfter.
	 */
	@JsonProperty("caNotAfter")
	public String caNotAfter;

	/**
	 * Field caNotBefore.
	 */
	@JsonProperty("caNotBefore")
	public String caNotBefore;

	/**
	 * Field caSerial.
	 */
	@JsonProperty("caSerial")
	public String caSerial;

	/**
	 * Field caSKI.
	 */
	@JsonProperty("caSKI")
	public String caSKI;

	/**
	 * Field caSubject.
	 */
	@JsonProperty("caSubject")
	public String caSubject;

}