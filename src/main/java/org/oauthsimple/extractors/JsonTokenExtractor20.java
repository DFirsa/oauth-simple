package org.oauthsimple.extractors;

import org.json.JSONException;
import org.json.JSONObject;
import org.oauthsimple.exceptions.OAuthException;
import org.oauthsimple.model.OAuthToken;
import org.oauthsimple.utils.Preconditions;

public class JsonTokenExtractor20 implements AccessTokenExtractor {

	public OAuthToken extract(String response) {
		Preconditions.checkEmptyString(response,
				"Cannot extract a token from a null or empty String");
		String rawResponse = response;
		try {
			JSONObject json = new JSONObject(rawResponse);
			String accessToken = json.getString("access_token");
			String refreshToken = json.getString("refresh_token");
			long expiresAt = json.getLong("expires_in")
					+ System.currentTimeMillis();
			return new OAuthToken(accessToken, refreshToken, expiresAt,
					rawResponse);
		} catch (JSONException e) {
			throw new OAuthException(
					"Cannot extract an acces token. Response was: " + response);
		}
	}

}