package io.fpki.api.function.utilities.asn1;

import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x509.AccessDescription;
import org.bouncycastle.asn1.x509.GeneralName;

/**
 * The SubjectInformationAccess object.
 * 
 * This class is based off of the BouncyCastle AuthorityInfoAccess object.
 * 
 * <pre>
 * id-pe-subjectInfoAccess OBJECT IDENTIFIER ::= { id-pe 11 }
 * 
 * SubjectInfoAccessSyntax  ::=
 *     SEQUENCE SIZE (1..MAX) OF AccessDescription
 * 
 * AccessDescription  ::=  SEQUENCE {
 *     accessMethod          OBJECT IDENTIFIER,
 *     accessLocation        GeneralName  }
 * 
 * id-ad OBJECT IDENTIFIER ::= { id-pkix 48 }
 * id-ad-caRepository OBJECT IDENTIFIER ::= { id-ad 5 }
 * id-ad-timeStamping OBJECT IDENTIFIER ::= { id-ad 3 }
 * </pre>
 */
public class SubjectInformationAccess extends ASN1Object {

	private AccessDescription[] descriptions;
	private static final ASN1ObjectIdentifier id_pkix = new ASN1ObjectIdentifier("1.3.6.1.5.5.7.48");
	public static final ASN1ObjectIdentifier id_ad_timeStamping = id_pkix.branch("3");
	public static final ASN1ObjectIdentifier id_ad_caRepository = id_pkix.branch("5");

	/**
	 * 
	 * @param obj
	 * @return
	 */
	public static SubjectInformationAccess getInstance(Object obj) {
		if (obj instanceof SubjectInformationAccess) {
			return (SubjectInformationAccess) obj;
		}
		if (obj != null) {
			return new SubjectInformationAccess(ASN1Sequence.getInstance(obj));
		}
		return null;
	}

	private SubjectInformationAccess(ASN1Sequence seq) {
		if (seq.size() < 1) {
			throw new IllegalArgumentException("sequence may not be empty");
		}
		descriptions = new AccessDescription[seq.size()];
		for (int i = 0; i != seq.size(); i++) {
			descriptions[i] = AccessDescription.getInstance(seq.getObjectAt(i));
		}
	}

	/**
	 * Create a SubjectInformationAccess with the oid and location provided.
	 * 
	 * @param oid
	 * @param location
	 */
	public SubjectInformationAccess(ASN1ObjectIdentifier oid, GeneralName location) {
		descriptions = new AccessDescription[1];
		descriptions[0] = new AccessDescription(oid, location);
	}

	/**
	 * 
	 * @return the access descriptions contained in this object.
	 */
	public AccessDescription[] getAccessDescriptions() {
		return descriptions;
	}

	public ASN1Primitive toASN1Primitive() {
		ASN1EncodableVector vec = new ASN1EncodableVector();
		for (int i = 0; i != descriptions.length; i++) {
			vec.add(descriptions[i]);
		}
		return new DERSequence(vec);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return ("SubjectInformationAccess: Oid(" + this.descriptions[0].getAccessMethod().getId() + ")");
	}
}