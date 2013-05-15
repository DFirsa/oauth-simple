package org.oauthsimple.builder.api;

import org.oauthsimple.model.OAuthToken;

public class XingApi extends DefaultApi10a {
	private static final String AUTHORIZE_URL = "https://api.xing.com/v1/authorize?oauth_token=%s";

	@Override
	public String getAccessTokenEndpoint() {
		return "https://api.xing.com/v1/access_token";
	}

	@Override
	public String getRequestTokenEndpoint() {
		return "https://api.xing.com/v1/request_token";
	}

	@Override
	public String getAuthorizationUrl(OAuthToken requestToken) {
		return String.format(AUTHORIZE_URL, requestToken.getToken());
	}

}