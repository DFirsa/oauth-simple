package org.oauthsimple.builder.api;

import org.oauthsimple.model.OAuthToken;

/**
 * OAuth access to the Meetup.com API. For more information visit
 * http://www.meetup.com/api
 */
public class MeetupApi extends DefaultApi10a {
	private static final String AUTHORIZE_URL = "http://www.meetup.com/authenticate?oauth_token=%s";

	@Override
	public String getRequestTokenEndpoint() {
		return "http://api.meetup.com/oauth/request/";
	}

	@Override
	public String getAccessTokenEndpoint() {
		return "http://api.meetup.com/oauth/access/";
	}

	@Override
	public String getAuthorizationUrl(OAuthToken requestToken) {
		return String.format(AUTHORIZE_URL, requestToken.getToken());
	}
}
