package io.fpki.api.constants;

import java.security.Provider;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import io.fpki.api.pojo.CAEntry;

public class APISettings {

	private static final Logger log = Logger.getLogger(APISettings.class);

	private static volatile APISettings instance;

	private final static String caAKI = "TRUST_ANCHOR_NOT_APPLICABLE";
	private final static String caCert = "MIIEYDCCA0igAwIBAgICATAwDQYJKoZIhvcNAQELBQAwWTELMAkGA1UEBhMCVVMxGDAWBgNVBAoTD1UuUy4gR292ZXJubWVudDENMAsGA1UECxMERlBLSTEhMB8GA1UEAxMYRmVkZXJhbCBDb21tb24gUG9saWN5IENBMB4XDTEwMTIwMTE2NDUyN1oXDTMwMTIwMTE2NDUyN1owWTELMAkGA1UEBhMCVVMxGDAWBgNVBAoTD1UuUy4gR292ZXJubWVudDENMAsGA1UECxMERlBLSTEhMB8GA1UEAxMYRmVkZXJhbCBDb21tb24gUG9saWN5IENBMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA2HX7NRY0WkG/Wq9cMAQUHK14RLXqJup1YcfNNnn4fNi9KVFmWSHjeavUeL6wLbCh1bI1FiPQzB6+Duir3MPJ1hLXp3JoGDG4FyKyPn66CG3G/dFYLGmgA/Aqo/Y/ISU937cyxY4nsyOl4FKzXZbpsLjFxZ+7xaBugkC7xScFNknWJidpDDSPzyd6KgqjQV+NHQOGgxXgVcHFmCye7Bpy3EjBPvmE0oSCwRvDdDa3ucc2Mnr4MrbQNq4iGDGMUHMhnv6DOzCIJOPpwX7e7ZjHH5IQip9bYi+dpLzVhW86/clTpyBLqtsgqyFOHQ1O5piF5asRR12dP8QjwOMUBm7+nQIDAQABo4IBMDCCASwwDwYDVR0TAQH/BAUwAwEB/zCB6QYIKwYBBQUHAQsEgdwwgdkwPwYIKwYBBQUHMAWGM2h0dHA6Ly9odHRwLmZwa2kuZ292L2ZjcGNhL2NhQ2VydHNJc3N1ZWRCeWZjcGNhLnA3YzCBlQYIKwYBBQUHMAWGgYhsZGFwOi8vbGRhcC5mcGtpLmdvdi9jbj1GZWRlcmFsJTIwQ29tbW9uJTIwUG9saWN5JTIwQ0Esb3U9RlBLSSxvPVUuUy4lMjBHb3Zlcm5tZW50LGM9VVM/Y0FDZXJ0aWZpY2F0ZTtiaW5hcnksY3Jvc3NDZXJ0aWZpY2F0ZVBhaXI7YmluYXJ5MA4GA1UdDwEB/wQEAwIBBjAdBgNVHQ4EFgQUrQx6dVzl85jEeZgOrCj9l/TnAvwwDQYJKoZIhvcNAQELBQADggEBAI9z2uF/gLGH9uwsz9GEYx728Yi3mvIRte9UrYpuGDco71wb5O9Qt2wmGCMiTR0mRyDpCZzicGJxqxHPkYnos/UqoEfAFMtOQsHdDA4b8Idb7OV316rgVNdF9IU+7LQd3nyKf1tNnJaK0KIyn9psMQz4pO9+c+iR3Ah6cFqgr2KBWfgAdKLI3VTKQVZHvenAT+0g3eOlCd+uKML80cgX2BLHb94u6b2akfI8WpQukSKAiaGMWMyDeiYZdQKlDn0KJnNR6obLB6jI/WNaNZvSr79PMUjBhHDbNXuaGQ/lj/RqDG8z2esccKIN47lQA2EC/0rskqTcLe4qNJMHtyznGI8=";
	private final static String caCrl = "http://http.fpki.gov/fcpca/fcpca.crl";
	private final static String caHash = "894EBC0B23DA2A50C0186B7F8F25EF1F6B2935AF32A94584EF80AAF877A3A06E";
	private final static String caIssuer = "CN=Federal Common Policy CA,OU=FPKI,O=U.S. Government,C=US";
	private final static String caNotAfter = "2030-12-01T16:45:27.000+0000";
	private final static String caNotBefore = "2010-12-01T16:45:27.000+0000";
	private final static String caSerial = "0130";
	private final static String caSKI = "AD0C7A755CE5F398C479980EAC28FD97F4E702FC";
	private final static String caSubject = "CN=Federal Common Policy CA,OU=FPKI,O=U.S. Government,C=US";
	private final static CAEntry TRUST_ANCHOR = new CAEntry(caAKI, caCert, caCrl, caHash, caIssuer, caNotAfter,
			caNotBefore, caSerial, caSKI, caSubject);

	private final static BouncyCastleProvider JCE_PROVIDER = new BouncyCastleProvider();

	private final static int PEM_SIZE_LIMIT = 8192;

	private final static int HTTP_TIMEOUT = 15;

	private final static int HTTP_RESPONSE_MAX_BYTES = 31457280;

	private static Map<String, String> CORS_HEADERS;

	private static Map<String, String> HEADERS_PLAINTEXT;

	private static Map<String, String> HEADERS_JSON;

	private APISettings() {
		log.info("Creating APISettings Instance");
		/*
		 * CORS headers: Access-Control-Allow-Origin: *
		 * Access-Control-Allow-Methods: GET, POST Access-Control-Allow-Headers:
		 * Content-Type
		 */
		CORS_HEADERS = new HashMap<String, String>();
		CORS_HEADERS.put("Access-Control-Allow-Origin", "*");
		CORS_HEADERS.put("Access-Control-Allow-Methods", "*");
		CORS_HEADERS.put("Access-Control-Allow-Headers", "Content-Type");
		/*
		 * Header Values:
		 */
		/*
		 * Content-Type: application/json
		 */
		HEADERS_JSON = new HashMap<String, String>();
		HEADERS_JSON.putAll(CORS_HEADERS);
		HEADERS_JSON.put("Content-Type", "application/json");
		/*
		 * Content-Type: text/plain
		 */
		HEADERS_PLAINTEXT = new HashMap<String, String>();
		HEADERS_PLAINTEXT.putAll(CORS_HEADERS);
		HEADERS_PLAINTEXT.put("Content-Type", "text/plain");
	}

	public static APISettings instance() {
		if (instance == null) {
			synchronized (APISettings.class) {
				if (instance == null)
					instance = new APISettings();
			}
		}
		return instance;
	}

	public Map<String, String> getJSONHeaders() {
		return HEADERS_JSON;
	}

	public Map<String, String> getPlaintextHeaders() {
		return HEADERS_PLAINTEXT;
	}

	public int getPEMSizeLimit() {
		return PEM_SIZE_LIMIT;
	}

	public Provider getJCEProvider() {
		return JCE_PROVIDER;
	}

	public CAEntry getTrustAnchor() {
		return TRUST_ANCHOR;
	}

	public int getHTTPTimeout() {
		return HTTP_TIMEOUT;
	}

	public int getHTTPMaxResponseSize() {
		return HTTP_RESPONSE_MAX_BYTES;
	}
}
