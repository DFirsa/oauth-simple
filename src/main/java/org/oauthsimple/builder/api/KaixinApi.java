package org.oauthsimple.builder.api;

import org.oauthsimple.model.OAuthToken;
import org.oauthsimple.http.Verb;

public class KaixinApi extends DefaultApi10a {
	private static final String REQUEST_TOKEN_URL = "http://api.kaixin001.com/oauth/request_token";
	private static final String ACCESS_TOKEN_URL = "http://api.kaixin001.com/oauth/access_token";
	private static final String AUTHORIZE_URL = "http://api.kaixin001.com/oauth/authorize?oauth_token=%s";

	@Override
	public String getRequestTokenEndpoint() {
		return REQUEST_TOKEN_URL;
	}

	@Override
	public String getAccessTokenEndpoint() {
		return ACCESS_TOKEN_URL;
	}

	@Override
	public String getAuthorizationUrl(OAuthToken requestToken) {
		return String.format(AUTHORIZE_URL, requestToken.getToken());
	}

	@Override
	public Verb getRequestTokenVerb() {
		return Verb.GET;
	}

	@Override
	public Verb getAccessTokenVerb() {
		return Verb.GET;
	}
}
