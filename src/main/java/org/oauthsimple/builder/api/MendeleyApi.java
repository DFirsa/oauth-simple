package org.oauthsimple.builder.api;

import org.oauthsimple.model.OAuthToken;
import org.oauthsimple.http.Verb;

/**
 * @author Arieh "Vainolo" Bibliowicz
 * @see http://apidocs.mendeley.com/home/authentication
 */
public class MendeleyApi extends DefaultApi10a {

	private static final String AUTHORIZATION_URL = "http://api.mendeley.com/oauth/authorize?oauth_token=%s";

	@Override
	public String getRequestTokenEndpoint() {
		return "http://api.mendeley.com/oauth/request_token/";
	}

	@Override
	public String getAccessTokenEndpoint() {
		return "http://api.mendeley.com/oauth/access_token/";
	}

	@Override
	public String getAuthorizationUrl(OAuthToken requestToken) {
		return String.format(AUTHORIZATION_URL, requestToken.getToken());
	}

	@Override
	public Verb getAccessTokenVerb() {
		return Verb.GET;
	}

	@Override
	public Verb getRequestTokenVerb() {
		return Verb.GET;
	}
}
