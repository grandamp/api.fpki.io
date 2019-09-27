package io.fpki.api.function.utilities;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.HttpHeaders;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.log4j.Logger;

import io.fpki.api.constants.APISettings;
import io.fpki.api.pojo.URLResponse;

public class HttpClient {

	private static final Logger log = Logger.getLogger(HttpClient.class);

	private static HttpClient instance = null;
	private HttpClientContext context = null;
	private CloseableHttpClient httpClient = null;
	private PoolingHttpClientConnectionManager cm = null;

	private int timeout = APISettings.instance().getHTTPTimeout();

	public static synchronized HttpClient getInstance() {
		if (instance == null) {
			instance = new HttpClient();
		}
		return instance;
	}

	private HttpClient() {
		cm = new PoolingHttpClientConnectionManager();
		cm.setMaxTotal(200);
		cm.setDefaultMaxPerRoute(20);
		context = HttpClientContext.create();
		RequestConfig config = RequestConfig.custom().setConnectTimeout(timeout * 1000)
				.setConnectionRequestTimeout(timeout * 1000).setSocketTimeout(timeout * 1000).build();

		httpClient = HttpClients.custom().setConnectionManager(cm).setDefaultRequestConfig(config).build();
	}

	public byte[] getRequest(final String url, String type) throws HttpClientException {
		CloseableHttpResponse response = null;
		long startTime = 0;
		long responseTime = 0;
		try {
			final HttpGet httpget = new HttpGet(url);
			httpget.setHeader(HttpHeaders.USER_AGENT, "api.fpki.io-HTTP-client");
			log.info("Executing request " + httpget.getRequestLine());
			startTime = System.currentTimeMillis();
			response = httpClient.execute(httpget, context);
			responseTime = System.currentTimeMillis() - startTime;
			final int statusCode = response.getStatusLine().getStatusCode();
			URLResponse responseData = new URLResponse();
			responseData.url = url;
			responseData.urlResponseTime = responseTime;
			responseData.urlCode = statusCode;
			responseData.urlType = type;
			Header[] headerArr = response.getAllHeaders();
			Map<String, String> headers = new HashMap<String, String>();
			for (Header currentHeader : headerArr) {
				headers.put(currentHeader.getName(), currentHeader.getValue());
			}
			responseData.urlHeaders = headers;
			log.info(responseData.toString());
			/*
			 * Any redirects should be automatically followed. Anything other
			 * than a 200 will be considered a fail.
			 */
			if (statusCode != 200) {
				response.close();
				return null;
			} else {
				if (null != headers.get("Content-Length")) {
					Integer length = new Integer(headers.get("Content-Length"));
					int contentLength = length.intValue();
					if (contentLength <= APISettings.instance().getHTTPMaxResponseSize()) {
						ByteArrayOutputStream baos = new ByteArrayOutputStream();
						response.getEntity().writeTo(baos);
						response.close();
						return baos.toByteArray();
					}
					throw new HttpClientException("Exception while requesting [" + url + "]: Max size exceeded: " + contentLength);
				}
				throw new HttpClientException("Exception while requesting [" + url + "]: No Content-Length header");
			}
		} catch (final UnknownHostException e) {
			log.fatal("DNS or Connectivity error?:");
			throw new HttpClientException("Exception while requesting [" + url + "]", e);
		} catch (final ConnectTimeoutException e) {
			log.fatal("Timeout Reached: Current Timeout: " + timeout + " seconds: ");
			throw new HttpClientException("Exception while requesting [" + url + "]", e);
		} catch (final SocketTimeoutException e) {
			log.fatal("Timeout Reached: Current Timeout: " + timeout + " seconds: ");
			throw new HttpClientException("Exception while requesting [" + url + "]", e);
		} catch (final ConnectException e) {
			log.fatal("Timeout Reached: Current Timeout: " + timeout + " seconds: ");
			throw new HttpClientException("Exception while requesting [" + url + "]", e);
		} catch (final Exception e) {
			log.fatal("Common Error? Catch and re-throw explicitly!:", e);
			throw new HttpClientException("Exception while requesting [" + url + "]", e);
		} finally {
			try {
				if (response != null) {
					response.close();
				}
			} catch (final IOException e) {
				log.fatal("Exception when closing response in catch block:", e);
				throw new HttpClientException("Exception while closing response for [" + url + "]", e);
			}
		}
	}

}