package org.oauthsimple.services;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.oauthsimple.exceptions.OAuthSignatureException;
import org.oauthsimple.utils.Base64;
import org.oauthsimple.utils.OAuthEncoder;
import org.oauthsimple.utils.Preconditions;

/**
 * HMAC-SHA1 implementation of {@SignatureService}
 * 
 * @author Pablo Fernandez
 * 
 */
public class HMACSha1SignatureService implements SignatureService {
	private static final String EMPTY_STRING = "";
	private static final String CARRIAGE_RETURN = "\r\n";
	private static final String UTF8 = "UTF-8";
	private static final String HMAC_SHA1 = "HmacSHA1";
	private static final String METHOD = "HMAC-SHA1";

	/**
	 * {@inheritDoc}
	 */
	public String getSignature(String baseString, String apiSecret,
			String tokenSecret) {
		try {
			Preconditions.checkEmptyString(baseString,
					"Base string cant be null or empty string");
			Preconditions.checkEmptyString(apiSecret,
					"Api secret cant be null or empty string");
			String keyString = OAuthEncoder.encode(apiSecret) + '&';
			if (tokenSecret != null) {
				keyString += OAuthEncoder.encode(tokenSecret);
			}
			return doSign(baseString, keyString);
		} catch (Exception e) {
			throw new OAuthSignatureException(baseString, e);
		}
	}

	private String doSign(String toSign, String keyString) throws Exception {
		SecretKeySpec key = new SecretKeySpec((keyString).getBytes(UTF8),
				HMAC_SHA1);
		Mac mac = Mac.getInstance(HMAC_SHA1);
		mac.init(key);
		byte[] bytes = mac.doFinal(toSign.getBytes(UTF8));
		return bytesToBase64String(bytes)
				.replace(CARRIAGE_RETURN, EMPTY_STRING);
	}

	private String bytesToBase64String(byte[] bytes) {
		return Base64.encodeBytes(bytes);
	}

	/**
	 * {@inheritDoc}
	 */
	public String getSignatureMethod() {
		return METHOD;
	}
}
