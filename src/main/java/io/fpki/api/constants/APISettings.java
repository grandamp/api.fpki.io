package io.fpki.api.constants;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class APISettings {

	/**
	 * Field JCE_PROVIDER
	 */
	public final static BouncyCastleProvider JCE_PROVIDER = new BouncyCastleProvider();

	/**
	 * Field PEM_SIZE_LIMIT
	 */
	public final static int PEM_SIZE_LIMIT = 8192;

	/*
	 * Hidden constructor
	 */
	private APISettings() {}

}
