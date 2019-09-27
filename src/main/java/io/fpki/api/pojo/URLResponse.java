package io.fpki.api.pojo;

import java.io.IOException;
import java.util.Map;

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
 * This class is a Java representation of the URLResponse JSON object.
 * 
 * <pre>
 * {
 * 	"url": "http://foo",
 * 	"urlResponseTime": "long",
 * 	"urlCode": "int",
 * 	"urlType": "CMS||CRL||OCSP",
 * 	"urlHeaders: []
 * }
 * </pre>
 */
@XmlRootElement
@XmlType(propOrder = { "url", "urlResponseTime", "urlCode", "urlType", "urlHeaders" })
@JsonIgnoreProperties(ignoreUnknown = true)
public class URLResponse {

	/**
	 * Static creator for de-serialization
	 * 
	 * @throws IOException
	 * @throws JsonMappingException
	 * @throws JsonParseException
	 */
	@JsonCreator
	public static URLResponse getInstance(String jsonString)
			throws JsonParseException, JsonMappingException, IOException {
		ObjectMapper mapper = POJOObjectMapper.instance().getMapper();
		URLResponse req = null;
		req = mapper.readValue(jsonString, URLResponse.class);
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
	 * Field url.
	 */
	@JsonProperty("url")
	public String url;

	/**
	 * Field urlResponseTime.
	 */
	@JsonProperty("urlResponseTime")
	public long urlResponseTime;

	/**
	 * Field urlCode.
	 */
	@JsonProperty("urlCode")
	public int urlCode;

	/**
	 * Field urlType.
	 */
	@JsonProperty("urlType")
	public String urlType;

	/**
	 * Field urlHeaders.
	 */
	@JsonProperty("urlHeaders")
	public Map<String, String> urlHeaders;

}