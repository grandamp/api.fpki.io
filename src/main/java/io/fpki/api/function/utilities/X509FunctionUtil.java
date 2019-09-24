package io.fpki.api.function.utilities;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Locale;

import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.x509.AuthorityKeyIdentifier;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.SubjectKeyIdentifier;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemWriter;

import com.amazonaws.util.Base64;

import io.fpki.api.dynamodb.DynamoDBCAEntryPOJO;
import io.fpki.api.pojo.CAEntry;
import io.fpki.api.pojo.CAEntryWithSubs;

public class X509FunctionUtil {

	/**
	 * Hidden Constructor
	 */
	private X509FunctionUtil() {
	}

	/**
	 * 
	 * @param entry
	 * @return String
	 * @throws IOException 
	 */
	public static String toPEM(CAEntryWithSubs entry) {
		StringBuffer sb = new StringBuffer();
		sb.append("Subject=" + entry.caSubject + "\n");
		sb.append("Issuer=" + entry.caIssuer + "\n");
		sb.append("CRL=" + entry.caCrl + "\n");
		sb.append("NotBefore=" + entry.caNotBefore + "\n");
		sb.append("NotAfter=" + entry.caNotAfter + "\n");
		byte[] certData = Base64.decode(entry.caCert);
		PemObject certPem = new PemObject("CERTIFICATE", certData);
		StringWriter sw = new StringWriter();
		PemWriter writer = new PemWriter(sw);
		try {
			writer.writeObject(certPem);
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		sb.append(sw.getBuffer());
		return sb.toString();
	}

	public static String getCAEntryWithSubsAsPEM(CAEntryWithSubs entry) {
		StringBuffer sb = new StringBuffer();
		/*
		 * Get the root entry
		 */
		sb.append(toPEM(entry));
		/*
		 * Get all of the root Entries subordinates by calling ourselves.
		 */
		if (null != entry.caSubordinates) {
			for (CAEntryWithSubs currentSub : entry.caSubordinates) {
				sb.append(getCAEntryWithSubsAsPEM(currentSub));
			}
		}
		return sb.toString();
	}

	/**
	 * 
	 * @param entry
	 * @return X509Certificate
	 * @throws CertificateException
	 *             If there is a problem initializing the X509Certificate object
	 * @throws IllegalArgumentException
	 *             If there is a problem with the input base64
	 */
	public static X509Certificate getCertificate(CAEntry entry) throws CertificateException, IllegalArgumentException {
		byte[] certBytes = null;
		CertificateFactory cf;
		ByteArrayInputStream bais;
		certBytes = Base64.decode(entry.caCert);
		if (null != certBytes) {
			cf = CertificateFactory.getInstance("X509");
			bais = new ByteArrayInputStream(certBytes);
			return (X509Certificate) cf.generateCertificate(bais);
		}
		return null;
	}

	/**
	 * 
	 * @param entry
	 * @return X509Certificate
	 */
	public static X509Certificate getCertificate(DynamoDBCAEntryPOJO entry) {
		byte[] certBytes = null;
		CertificateFactory cf;
		ByteArrayInputStream bais;
		X509Certificate cert = null;
		certBytes = entry.getCaCert().array();
		try {
			cf = CertificateFactory.getInstance("X509");
			bais = new ByteArrayInputStream(certBytes);
			cert = (X509Certificate) cf.generateCertificate(bais);
		} catch (CertificateException e) {
			e.printStackTrace();
		}
		return cert;
	}

	/**
	 * 
	 * @param cert
	 * @return
	 */
	public static boolean isCA(X509Certificate cert) {
		byte[] bcExtValue = cert.getExtensionValue(Extension.basicConstraints.getId());
		if (null != bcExtValue) {
			byte[] bcValue = ASN1OctetString.getInstance(bcExtValue).getOctets();
			BasicConstraints bc = BasicConstraints.getInstance(bcValue);
			return bc.isCA();
		}
		/*
		 * basicConstraints extension not present, so, not a CA
		 */
		return false;
	}

	/**
	 * 
	 * @param cert
	 * @return String representation of keyIdentifier value in Hex
	 */
	public static String getAuthorityKeyIdentifier(X509Certificate cert) {
		byte[] akiExtValue = cert.getExtensionValue(Extension.authorityKeyIdentifier.getId());
		if (null != akiExtValue) {
			byte[] akiValue = ASN1OctetString.getInstance(akiExtValue).getOctets();
			AuthorityKeyIdentifier akid = AuthorityKeyIdentifier.getInstance(akiValue);
			byte[] subjectKeyID = akid.getKeyIdentifier();
			return byteArrayToString(subjectKeyID);
		} else {
			if (cert.getIssuerX500Principal().getName().equalsIgnoreCase(cert.getSubjectX500Principal().getName())) {
				return new String("TRUST_ANCHOR_NOT_APPLICABLE");
			} else {
				throw new IllegalArgumentException("Certificate does not include an authorityKeyIdentifier");
			}
		}
	}

	public static String getSubjectKeyIdentifier(X509Certificate cert) {
		byte[] skiExtValue = cert.getExtensionValue(Extension.subjectKeyIdentifier.getId());
		if (null != skiExtValue) {
			byte[] skiValue = ASN1OctetString.getInstance(skiExtValue).getOctets();
			SubjectKeyIdentifier skid = SubjectKeyIdentifier.getInstance(skiValue);
			byte[] authorityKeyID = skid.getKeyIdentifier();
			return byteArrayToString(authorityKeyID);
		} else {
			return getKeyHash(cert.getPublicKey());
		}
	}

	/**
	 * 
	 * @param cert
	 * @return ByteBuffer containing the X509Certificate bytes with the buffer
	 *         position at 0
	 * @throws CertificateEncodingException
	 */
	public static ByteBuffer certificateEncodedToByteBuffer(X509Certificate cert) throws CertificateEncodingException {
		ByteBuffer certByteBuffer = null;
		byte[] certBytes = cert.getEncoded();
		certByteBuffer = ByteBuffer.allocate(certBytes.length);
		certByteBuffer.put(certBytes, 0, certBytes.length);
		certByteBuffer.position(0);
		return certByteBuffer;
	}

	/**
	 * Return Hex String of SHA-256 digest of the certificate
	 * 
	 * @param cert
	 * @return String
	 */
	public static String getCAHash(X509Certificate cert) {
		byte[] digest = null;
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("SHA-256");
			md.update(cert.getEncoded());
			digest = md.digest();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (CertificateEncodingException e) {
			e.printStackTrace();
		}
		return byteArrayToString(digest);
	}

	/**
	 * Return Hex String of SHA-1 digest of the Public Key
	 * 
	 * @param key
	 * @return String
	 */
	public static String getKeyHash(PublicKey spk) {
		byte[] digest = null;
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("SHA-1");
			md.update(spk.getEncoded());
			digest = md.digest();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return byteArrayToString(digest);
	}

	/**
	 * Convert a byte array to a Hex String
	 * 
	 * The following method converts a byte[] object to a String object, where
	 * the only output characters are "0123456789ABCDEF".
	 * 
	 * @param ba
	 *            A byte array
	 * 
	 * @return String Hexidecimal String object which represents the contents of
	 *         the byte array
	 */
	public static String byteArrayToString(byte[] ba) {
		if (ba == null) {
			return "";
		}
		StringBuffer hex = new StringBuffer(ba.length * 2);
		for (int i = 0; i < ba.length; i++) {
			hex.append(Integer.toString((ba[i] & 0xff) + 0x100, 16).substring(1));
		}
		return hex.toString().toUpperCase(Locale.US);
	}

}
