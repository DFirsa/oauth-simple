package org.oauthsimple.builder.api;

import org.oauthsimple.model.OAuthToken;

public class TrelloApi extends DefaultApi10a {
	private static final String AUTHORIZE_URL = "https://trello.com/1/OAuthAuthorizeToken?oauth_token=%s";

	@Override
	public String getAccessTokenEndpoint() {
		return "https://trello.com/1/OAuthGetAccessToken";
	}

	@Override
	public String getRequestTokenEndpoint() {
		return "https://trello.com/1/OAuthGetRequestToken";
	}

	@Override
	public String getAuthorizationUrl(OAuthToken requestToken) {
		return String.format(AUTHORIZE_URL, requestToken.getToken());
	}

}
