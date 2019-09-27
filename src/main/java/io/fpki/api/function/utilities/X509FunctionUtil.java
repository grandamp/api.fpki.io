package io.fpki.api.function.utilities;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.x509.AccessDescription;
import org.bouncycastle.asn1.x509.AuthorityInformationAccess;
import org.bouncycastle.asn1.x509.AuthorityKeyIdentifier;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.CRLDistPoint;
import org.bouncycastle.asn1.x509.DistributionPoint;
import org.bouncycastle.asn1.x509.DistributionPointName;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.asn1.x509.SubjectKeyIdentifier;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemWriter;

import com.amazonaws.util.Base64;

import io.fpki.api.dynamodb.DynamoDBCAEntryPOJO;
import io.fpki.api.function.utilities.asn1.SubjectInformationAccess;
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
	public static X509Certificate getCertificate(String b64String)
			throws CertificateException, IllegalArgumentException {
		byte[] certBytes = null;
		CertificateFactory cf;
		ByteArrayInputStream bais;
		certBytes = Base64.decode(b64String);
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
	 * Returns the extensions from the provided certificate
	 * 
	 * @param cert
	 * @return
	 */
	private static Extensions getExtensions(X509Certificate cert) {
		Set<String> critExt = cert.getCriticalExtensionOIDs();
		Set<String> nonCritExt = cert.getNonCriticalExtensionOIDs();
		Set<Extension> extensions = new HashSet<Extension>();
		for (String oidStr : critExt) {
			ASN1ObjectIdentifier extnId = new ASN1ObjectIdentifier(oidStr);
			byte[] extBytes = cert.getExtensionValue(oidStr);
			extensions.add(new Extension(extnId, true, ASN1OctetString.getInstance(extBytes)));
		}
		for (String oidStr : nonCritExt) {
			ASN1ObjectIdentifier extnId = new ASN1ObjectIdentifier(oidStr);
			byte[] extBytes = cert.getExtensionValue(oidStr);
			extensions.add(new Extension(extnId, false, ASN1OctetString.getInstance(extBytes)));
		}
		Extension[] extArr = new Extension[critExt.size() + nonCritExt.size()];
		return new Extensions(extensions.toArray(extArr));
	}

	/**
	 * Returns a boolean answer to the question: "Is this a CA certificate?"
	 * 
	 * @param cert
	 * @return
	 */
	public static boolean isCA(X509Certificate cert) {
		Extensions ext = getExtensions(cert);
		Extension bc = ext.getExtension(Extension.basicConstraints);
		if (null != bc) {
			BasicConstraints basicConstraints = BasicConstraints.getInstance(bc.getExtnValue().getOctets());
			return basicConstraints.isCA();
		}
		/*
		 * basicConstraints extension not present, so, not a CA
		 */
		return false;
	}

	/**
	 * Returns the Hex-String representation of the authorityKeyIdentifer
	 * keyIdentifier value
	 * 
	 * @param cert
	 * @return String representation of keyIdentifier value in Hex
	 */
	public static String getAuthorityKeyIdentifier(X509Certificate cert) {
		Extensions ext = getExtensions(cert);
		Extension aki = ext.getExtension(Extension.authorityKeyIdentifier);
		if (null != aki) {
			AuthorityKeyIdentifier authorityKeyIdentifier = AuthorityKeyIdentifier
					.getInstance(aki.getExtnValue().getOctets());
			return byteArrayToString(authorityKeyIdentifier.getKeyIdentifier());
		} else {
			if (cert.getIssuerX500Principal().getName().equalsIgnoreCase(cert.getSubjectX500Principal().getName())) {
				return new String("TRUST_ANCHOR_NOT_APPLICABLE");
			} else {
				throw new IllegalArgumentException("Certificate does not include an authorityKeyIdentifier");
			}
		}
	}

	/**
	 * Returns the Hex-String representation of the subjectKeyIdentifer
	 * keyIdentifier value
	 * 
	 * @param cert
	 * @return
	 */
	public static String getSubjectKeyIdentifier(X509Certificate cert) {
		Extensions ext = getExtensions(cert);
		Extension ski = ext.getExtension(Extension.subjectKeyIdentifier);
		if (null != ski) {
			SubjectKeyIdentifier subjectKeyIdentifier = SubjectKeyIdentifier
					.getInstance(ski.getExtnValue().getOctets());
			return byteArrayToString(subjectKeyIdentifier.getKeyIdentifier());
		} else {
			return getKeyHash(cert.getPublicKey());
		}
	}

	/**
	 * Returns URL[] containing all HTTP URLs in the cRLDistributionPoints
	 * extension.
	 * 
	 * @param cert
	 * @return
	 */
	public static URL[] getCRLDistributionPointURLs(X509Certificate cert) {
		List<URL> urls = new ArrayList<URL>();
		Extensions ext = getExtensions(cert);
		Extension cdp = ext.getExtension(Extension.cRLDistributionPoints);
		if (null != cdp) {
			CRLDistPoint crlDistPoint = CRLDistPoint.getInstance(cdp.getExtnValue().getOctets());
			DistributionPoint[] dps = crlDistPoint.getDistributionPoints();
			if (dps != null) {
				for (DistributionPoint currentDp : dps) {
					GeneralNames generalNames = currentDp.getCRLIssuer();
					if (null != generalNames) {
						GeneralName[] gnArr = generalNames.getNames();
						if (null != gnArr) {
							for (GeneralName currentGn : gnArr) {
								if (currentGn.getTagNo() == GeneralName.uniformResourceIdentifier) {
									URL url = null;
									if (currentGn.getName().toString().toLowerCase().startsWith("http")) {
										try {
											url = new URL(currentGn.getName().toString());
											urls.add(url);
										} catch (MalformedURLException e) {
											e.printStackTrace();
										}
									}
								}
							}
						}
					}
					DistributionPointName distPointName = currentDp.getDistributionPoint();
					if (null != distPointName) {
						if (distPointName.getType() == DistributionPointName.FULL_NAME) {
							GeneralName[] gnArr = GeneralNames.getInstance(distPointName.getName()).getNames();
							if (null != gnArr) {
								for (GeneralName currentGn : gnArr) {
									if (currentGn.getTagNo() == GeneralName.uniformResourceIdentifier) {
										URL url = null;
										if (currentGn.getName().toString().toLowerCase().startsWith("http")) {
											try {
												url = new URL(currentGn.getName().toString());
												urls.add(url);
											} catch (MalformedURLException e) {
												e.printStackTrace();
											}
										}
									}
								}
							}
						}
					}
				}
				return urls.toArray(new URL[urls.size()]);
			} else {
				return null;
			}
		} else {
			return null;
		}
	}

	/**
	 * Returns URL[] containing all HTTP URLs in the subjectInfoAccess
	 * extension.
	 * 
	 * @param cert
	 * @return
	 */
	public static URL[] getSubjectInfoAccessURLs(X509Certificate cert) {
		List<URL> urls = new ArrayList<URL>();
		Extensions ext = getExtensions(cert);
		Extension sia = ext.getExtension(Extension.subjectInfoAccess);
		if (null != sia) {
			SubjectInformationAccess subjectInformationAccess = SubjectInformationAccess
					.getInstance(sia.getExtnValue().getOctets());
			AccessDescription[] ad = subjectInformationAccess.getAccessDescriptions();
			if (null != ad) {
				for (AccessDescription currentAd : ad) {
					if (currentAd.getAccessMethod().equals(SubjectInformationAccess.id_ad_caRepository)) {
						GeneralName al = currentAd.getAccessLocation();
						if (al.getTagNo() == GeneralName.uniformResourceIdentifier) {
							URL url = null;
							if (al.getName().toString().toLowerCase().startsWith("http")) {
								try {
									url = new URL(al.getName().toString());
									urls.add(url);
								} catch (MalformedURLException e) {
									e.printStackTrace();
								}
							}
						}
					}
				}
			} else {
				return null;
			}
			return urls.toArray(new URL[urls.size()]);
		} else {
			return null;
		}
	}

	/**
	 * Returns URL[] containing all caIssuers HTTP URLs in the
	 * authorityInfoAccess extension.
	 * 
	 * @param cert
	 * @return
	 */
	public static URL[] getAuthorityInformationAccessURLs(X509Certificate cert) {
		List<URL> urls = new ArrayList<URL>();
		Extensions ext = getExtensions(cert);
		Extension aia = ext.getExtension(Extension.authorityInfoAccess);
		if (null != aia) {
			AuthorityInformationAccess authorityInformationAccess = AuthorityInformationAccess
					.getInstance(aia.getExtnValue().getOctets());
			AccessDescription[] ad = authorityInformationAccess.getAccessDescriptions();
			if (null != ad) {
				for (AccessDescription currentAd : ad) {
					if (currentAd.getAccessMethod().equals(AccessDescription.id_ad_caIssuers)) {
						GeneralName al = currentAd.getAccessLocation();
						if (al.getTagNo() == GeneralName.uniformResourceIdentifier) {
							URL url = null;
							if (al.getName().toString().toLowerCase().startsWith("http")) {
								try {
									url = new URL(al.getName().toString());
									urls.add(url);
								} catch (MalformedURLException e) {
									e.printStackTrace();
								}
							}
						}
					}
				}
			} else {
				return null;
			}
			return urls.toArray(new URL[urls.size()]);
		} else {
			return null;
		}
	}

	/**
	 * Returns URL[] containing all OCSP HTTP URLs in the authorityInfoAccess
	 * extension.
	 * 
	 * @param cert
	 * @return
	 */
	public static URL[] getAuthorityInformationAccessOCSPURLs(X509Certificate cert) {
		List<URL> urls = new ArrayList<URL>();
		Extensions ext = getExtensions(cert);
		Extension aia = ext.getExtension(Extension.authorityInfoAccess);
		if (null != aia) {
			AuthorityInformationAccess authorityInformationAccess = AuthorityInformationAccess
					.getInstance(aia.getExtnValue().getOctets());
			AccessDescription[] ad = authorityInformationAccess.getAccessDescriptions();
			if (null != ad) {
				for (AccessDescription currentAd : ad) {
					if (currentAd.getAccessMethod().equals(AccessDescription.id_ad_ocsp)) {
						GeneralName al = currentAd.getAccessLocation();
						if (al.getTagNo() == GeneralName.uniformResourceIdentifier) {
							URL url = null;
							if (al.getName().toString().toLowerCase().startsWith("http")) {
								try {
									url = new URL(al.getName().toString());
									urls.add(url);
								} catch (MalformedURLException e) {
									e.printStackTrace();
								}
							}
						}
					}
				}
			} else {
				return null;
			}
			return urls.toArray(new URL[urls.size()]);
		} else {
			return null;
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
