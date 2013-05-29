package org.oauthsimple.utils;

import java.util.Collections;
import java.util.List;
import java.util.Random;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.oauthsimple.http.Parameter;

public final class OAuthUtils {
	private static final String UTF8 = "UTF-8";
	private static final String HMAC_SHA1 = "HmacSHA1";
	private static final String OAUTH_BASE_STRING_FORMAT = "%s&%s&%s";
	private static final String EMPTY_STRING = "";
	// private static final String PARAM_SEPARATOR = ", ";
	private static final String PREAMBLE = "OAuth ";
	private static final String CARRIAGE_RETURN = "\r\n";
	private static final Random RAND = new Random();

	// static String generateXAuthHeader(String userName, String password,
	// OAuthProvider provider) {
	// List<Parameter> oauthHeaderParams = new ArrayList<Parameter>();
	// oauthHeaderParams.add(new Parameter(OAuthConstants.CONSUMER_KEY,
	// provider.getConsumerKey()));
	// oauthHeaderParams.add(new Parameter(OAuthConstants.SIGN_METHOD,
	// OAuthConstants.OAUTH_SIGN_METHOD));
	// oauthHeaderParams.add(new Parameter(OAuthConstants.TIMESTAMP,
	// getTimestamp()));
	// oauthHeaderParams.add(new Parameter(OAuthConstants.NONCE, getNonce()));
	// oauthHeaderParams.add(new Parameter(OAuthConstants.VERSION,
	// OAuthConstants.OAUTH_VERSION));
	// oauthHeaderParams.add(new Parameter(OAuthConstants.X_AUTH_USERNAME,
	// userName));
	// oauthHeaderParams.add(new Parameter(OAuthConstants.X_AUTH_PASSWORD,
	// password));
	// oauthHeaderParams.add(new Parameter(OAuthConstants.X_AUTH_MODE,
	// "client_auth"));
	//
	// String baseString = getSortedAndEncodedParams(oauthHeaderParams);
	// String signature = generateSignature(baseString,
	// provider.getConsumerSecret());
	// oauthHeaderParams
	// .add(new Parameter(OAuthConstants.SIGNATURE, signature));
	// String oauthHeaderString = generateOAuthHeader(oauthHeaderParams);
	// return oauthHeaderString;
	// }

	public static String getBaseString(String method, String url,
			List<Parameter> params) {
		String methodStr = OAuthEncoder.encode(method);
		String urlStr = OAuthEncoder.encode(url);
		String paramsStr = getSortedAndEncodedParams(params);
		return String.format(OAUTH_BASE_STRING_FORMAT, methodStr, urlStr,
				paramsStr);
	}

	public static String getSortedAndEncodedParams(List<Parameter> params) {
		Collections.sort(params);
		return asOauthBaseString(params);
	}

	public static String generateOAuthHeader(List<Parameter> oauthParameters) {
		StringBuilder header = new StringBuilder(PREAMBLE);
		for (Parameter param : oauthParameters) {
			append(header, param.getName(), param.getValue());
		}
		// hack: we have to remove the extra ',' at the end
		return header.substring(0, header.length() - 1);
	}

	public static void append(StringBuilder builder, String name, String value) {
		if (value != null) {
			builder.append(' ').append(OAuthEncoder.encode(name)).append("=\"")
					.append(OAuthEncoder.encode(value)).append("\",");
		}
	}

	public static String generateSignature(String baseString, String apiSecret) {
		return generateSignature(baseString, apiSecret, null);
	}

	public static String generateSignature(String baseString, String apiSecret,
			String tokenSecret) {
		try {
			String keyString = OAuthEncoder.encode(apiSecret) + '&';
			if (tokenSecret != null) {
				keyString += OAuthEncoder.encode(tokenSecret);
			}
			return doSign(baseString, keyString);
		} catch (Exception e) {
			return "";
		}
	}

	public static String doSign(String toSign, String keyString)
			throws Exception {
		SecretKeySpec key = new SecretKeySpec((keyString).getBytes(UTF8),
				HMAC_SHA1);
		Mac mac = Mac.getInstance(HMAC_SHA1);
		mac.init(key);
		byte[] bytes = mac.doFinal(toSign.getBytes(UTF8));
		return bytesToBase64String(bytes)
				.replace(CARRIAGE_RETURN, EMPTY_STRING);
	}

	public static String bytesToBase64String(byte[] bytes) {
		return Base64.encodeBytes(bytes);
	}

	public static String getNonce() {
		long ts = getTs();
		return String.valueOf(ts + RAND.nextInt());
	}

	public static String getTimestamp() {
		return String.valueOf(getTs());
	}

	public static long getTs() {
		return System.currentTimeMillis() / 1000;
	}

	public static String asOauthBaseString(List<Parameter> params) {
		return OAuthEncoder.encode(Utils.asFormUrlEncodedString(params));
	}
}
