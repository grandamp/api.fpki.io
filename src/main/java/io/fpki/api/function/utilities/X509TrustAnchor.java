package io.fpki.api.function.utilities;

import java.io.ByteArrayInputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateFactory;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import io.fpki.api.dynamodb.DynamoDBCAEntryPOJO;

public class X509TrustAnchor {

	/*
	 * Informal code to build the initial trust anchor entry.
	 * 
	 * For this implementation, there are only 2 trust anchors:
	 * 
	 * 	-Common Policy Root CA
	 * 	-Test Common Policy Root CA (CITE)
	 */

	/*
	 * Common Policy Root CA
	 */
	private static final String COMMON_CRL = "http://http.fpki.gov/fcpca/fcpca.crl";
	private static final String COMMON_CERT = 
			"-----BEGIN CERTIFICATE-----" +
			"MIIEYDCCA0igAwIBAgICATAwDQYJKoZIhvcNAQELBQAwWTELMAkGA1UEBhMCVVMx\n" +
			"GDAWBgNVBAoTD1UuUy4gR292ZXJubWVudDENMAsGA1UECxMERlBLSTEhMB8GA1UE\n" +
			"AxMYRmVkZXJhbCBDb21tb24gUG9saWN5IENBMB4XDTEwMTIwMTE2NDUyN1oXDTMw\n" +
			"MTIwMTE2NDUyN1owWTELMAkGA1UEBhMCVVMxGDAWBgNVBAoTD1UuUy4gR292ZXJu\n" +
			"bWVudDENMAsGA1UECxMERlBLSTEhMB8GA1UEAxMYRmVkZXJhbCBDb21tb24gUG9s\n" +
			"aWN5IENBMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA2HX7NRY0WkG/\n" +
			"Wq9cMAQUHK14RLXqJup1YcfNNnn4fNi9KVFmWSHjeavUeL6wLbCh1bI1FiPQzB6+\n" +
			"Duir3MPJ1hLXp3JoGDG4FyKyPn66CG3G/dFYLGmgA/Aqo/Y/ISU937cyxY4nsyOl\n" +
			"4FKzXZbpsLjFxZ+7xaBugkC7xScFNknWJidpDDSPzyd6KgqjQV+NHQOGgxXgVcHF\n" +
			"mCye7Bpy3EjBPvmE0oSCwRvDdDa3ucc2Mnr4MrbQNq4iGDGMUHMhnv6DOzCIJOPp\n" +
			"wX7e7ZjHH5IQip9bYi+dpLzVhW86/clTpyBLqtsgqyFOHQ1O5piF5asRR12dP8Qj\n" +
			"wOMUBm7+nQIDAQABo4IBMDCCASwwDwYDVR0TAQH/BAUwAwEB/zCB6QYIKwYBBQUH\n" +
			"AQsEgdwwgdkwPwYIKwYBBQUHMAWGM2h0dHA6Ly9odHRwLmZwa2kuZ292L2ZjcGNh\n" +
			"L2NhQ2VydHNJc3N1ZWRCeWZjcGNhLnA3YzCBlQYIKwYBBQUHMAWGgYhsZGFwOi8v\n" +
			"bGRhcC5mcGtpLmdvdi9jbj1GZWRlcmFsJTIwQ29tbW9uJTIwUG9saWN5JTIwQ0Es\n" +
			"b3U9RlBLSSxvPVUuUy4lMjBHb3Zlcm5tZW50LGM9VVM/Y0FDZXJ0aWZpY2F0ZTti\n" +
			"aW5hcnksY3Jvc3NDZXJ0aWZpY2F0ZVBhaXI7YmluYXJ5MA4GA1UdDwEB/wQEAwIB\n" +
			"BjAdBgNVHQ4EFgQUrQx6dVzl85jEeZgOrCj9l/TnAvwwDQYJKoZIhvcNAQELBQAD\n" +
			"ggEBAI9z2uF/gLGH9uwsz9GEYx728Yi3mvIRte9UrYpuGDco71wb5O9Qt2wmGCMi\n" +
			"TR0mRyDpCZzicGJxqxHPkYnos/UqoEfAFMtOQsHdDA4b8Idb7OV316rgVNdF9IU+\n" +
			"7LQd3nyKf1tNnJaK0KIyn9psMQz4pO9+c+iR3Ah6cFqgr2KBWfgAdKLI3VTKQVZH\n" +
			"venAT+0g3eOlCd+uKML80cgX2BLHb94u6b2akfI8WpQukSKAiaGMWMyDeiYZdQKl\n" +
			"Dn0KJnNR6obLB6jI/WNaNZvSr79PMUjBhHDbNXuaGQ/lj/RqDG8z2esccKIN47lQ\n" +
			"A2EC/0rskqTcLe4qNJMHtyznGI8=\n" +
			"-----END CERTIFICATE-----";

	/*
	 * Test Common Policy Root CA (CITE)
	 */
	private static final String CITE_CRL = "http://http.cite.fpki-lab.gov/common/TestCommon.crl";
	private static final String CITE_CERT = 
			"-----BEGIN CERTIFICATE-----\n" +
			"MIID9jCCAt6gAwIBAgIUZbKm4RGAmMMdO0I5fiLHPUWC4/4wDQYJKoZIhvcNAQEL\n" +
			"BQAwYjELMAkGA1UEBhMCVVMxGDAWBgNVBAoTD1UuUy4gR292ZXJubWVudDERMA8G\n" +
			"A1UECxMIVGVzdEZQS0kxJjAkBgNVBAMTHVRlc3QgRmVkZXJhbCBDb21tb24gUG9s\n" +
			"aWN5IENBMB4XDTE0MTAwNjE0MzYxMloXDTM0MTAwNjE0MzYxMlowYjELMAkGA1UE\n" +
			"BhMCVVMxGDAWBgNVBAoTD1UuUy4gR292ZXJubWVudDERMA8GA1UECxMIVGVzdEZQ\n" +
			"S0kxJjAkBgNVBAMTHVRlc3QgRmVkZXJhbCBDb21tb24gUG9saWN5IENBMIIBIjAN\n" +
			"BgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAw5jDCmZVQecMslcu4aD3VHShAePE\n" +
			"54cFLyiIqmRfdren207P3p1mNX9BZMJ+OdwNUdN4bHuXIpXr6PP5sVhRMCDtft0u\n" +
			"9WqpV7G5HsMUw4V/2Ejfmjk1EIbisyb4etMiEqS36DoOanWWVrnKBwdFxPof6+Mz\n" +
			"333Q5HANtx3eysBd6Sl0dnGGVy6JXl2mzpp43ShUVR8KTx/ZiMxG7gmFc1HGuaMk\n" +
			"cyYjHk42ZTjQJOVil88B8njGyZ/WbULjXofcLgWO0FGZHvHg7M+B5hgN2qjRSuiU\n" +
			"B1Iqykxto3BLOU7WuG8ed4DZzPFaaOf6Dds/tpajx3SfZWqTforSG6cCMQIDAQAB\n" +
			"o4GjMIGgMA8GA1UdEwEB/wQFMAMBAf8wXgYIKwYBBQUHAQsEUjBQME4GCCsGAQUF\n" +
			"BzAFhkJodHRwOi8vaHR0cC5jaXRlLmZwa2ktbGFiLmdvdi9jb21tb24vY2FDZXJ0\n" +
			"c0lzc3VlZEJ5dGVzdENvbW1vbi5wN2MwDgYDVR0PAQH/BAQDAgEGMB0GA1UdDgQW\n" +
			"BBTqBnGM05KgDc/nHfSJeXkp548PjTANBgkqhkiG9w0BAQsFAAOCAQEAjtoCkDgI\n" +
			"q1SR753iOOjdisOiCwcgYF//9/9w5gvJIvVJwioRYtxpasgEDQ+7v7hSVi7QJGBP\n" +
			"eZiadaimrbjM8Bz2yoORl6xXsbVgOlKDDUxzW3In2jz8QtzdRYmAaS5N8aBpsukw\n" +
			"PGXsB9s1uNEdxlyVhsVHCAoeXHYNZ3HunaVxjjPp6P95DL5EKuSn4EL2asPtQOOZ\n" +
			"4hbbLs2qUNWuhwsGs/n+lSvRtzgPqDNyomWth1p+fgwqCss8l+CfzO65bia9qws/\n" +
			"RhBqntL3OlyIXebK5SNV7Js4VBytdKHuOKvYABrnepzXNtJKPoQkyu1rZCooG2ZO\n" +
			"jXy1Lk7xEtwbBg==\n" +
			"-----END CERTIFICATE-----";

	/**
	 * Hidden Constructor
	 */
	private X509TrustAnchor() {
	}

	public static void main(String[] args) {
		String CRL = null;
		String CERT = null;
		String usage = "Usage: java io.fpki.api.function.utilities.X509TrustAnchor \"COMMON\"|\"CITE\"";
		if (args.length != 1) {
			System.out.println(usage);
			System.exit(0);
		}
		if (args[0].equals("COMMON")) {
			CRL = COMMON_CRL;
			CERT = COMMON_CERT;
		} else if (args[0].equals("CITE")) {
			CRL = CITE_CRL;
			CERT = CITE_CERT;
		} else {
			System.out.println(usage);
			System.exit(0);
		}
		System.out.println("Using Cert:");
		System.out.println(CERT);
		System.out.println("Using CRL:");
		System.out.println(CRL);
		/*
		 * Generate our X509Certificate object
		 */
		CertificateFactory cf;
		ByteArrayInputStream bais;
		X509Certificate newCertificate = null;
		try {
			cf = CertificateFactory.getInstance("X509");
			bais = new ByteArrayInputStream(CERT.getBytes());
			newCertificate = (X509Certificate) cf.generateCertificate(bais);
		} catch (CertificateException e) {
			e.printStackTrace();
		}
		/*
		 * From here, we will build our DynamoDB CAEntry POJO
		 */
		DynamoDBCAEntryPOJO newEntry = new DynamoDBCAEntryPOJO();
		/*
		 * @param caAKI
		 */
		String caAKI = null;
		try {
			caAKI = X509FunctionUtil.getAuthorityKeyIdentifier(newCertificate);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
		newEntry.setCaAKI(caAKI);
		/*
		 * @param caCert
		 */
		ByteBuffer caCert = null;
		try {
			caCert = X509FunctionUtil.certificateEncodedToByteBuffer(newCertificate);
		} catch (CertificateEncodingException e) {
			e.printStackTrace();
		}
		newEntry.setCaCert(caCert);
		/*
		 * Before we proceed, we should do the obvious: - check temporal
		 * validity,and; - see *if* this is really a CA certificate.
		 * 
		 * Basic logic would be to check out Basic Constraints, and check to see
		 * if CA=True
		 */
		try {
			newCertificate.checkValidity();
		} catch (CertificateExpiredException e) {
			System.out.println("The certificate submitted is expired");
			e.printStackTrace();
		} catch (CertificateNotYetValidException e) {
			System.out.println("The certificate submitted is not yet valid");
			e.printStackTrace();
		}
		if (!X509FunctionUtil.isCA(newCertificate)) {
			System.out.println("The certificate submitted is not a CA certificate");
			System.exit(0);
		}
		/*
		 * @param caCrl
		 */
		String caCrl = null;
		try {
			URL crlUrl = new URL(CRL);
			caCrl = crlUrl.toString();
		} catch (MalformedURLException e) {
			System.out.println("Malformed URL.");
			e.printStackTrace();
		}
		newEntry.setCaCrl(caCrl);
		/*
		 * @param caHash
		 */
		newEntry.setCaHash(X509FunctionUtil.getCAHash(newCertificate));
		/*
		 * @param caIssuer
		 */
		newEntry.setCaIssuer(newCertificate.getIssuerX500Principal().getName());
		/*
		 * Date formatter for caNotAfter & caNotBefore See:
		 * http://xkcd.com/1179/
		 */
		SimpleDateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
		dFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		/*
		 * @param caNotAfter
		 */
		Date notAfter = newCertificate.getNotAfter();
		newEntry.setCaNotAfter(dFormat.format(notAfter));
		/*
		 * @param caNotBefore
		 */
		Date notBefore = newCertificate.getNotBefore();
		newEntry.setCaNotBefore(dFormat.format(notBefore));
		/*
		 * @param caSerial
		 */
		newEntry.setCaSerial(X509FunctionUtil.byteArrayToString(newCertificate.getSerialNumber().toByteArray()));
		/*
		 * @param caSKI
		 */
		newEntry.setCaSKI(X509FunctionUtil.getSubjectKeyIdentifier(newCertificate));
		/*
		 * @param caSubject
		 */
		newEntry.setCaSubject(newCertificate.getSubjectX500Principal().getName());
		System.out.println(newEntry.toString());
	}
	
}