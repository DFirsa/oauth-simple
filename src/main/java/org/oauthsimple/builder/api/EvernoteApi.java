package org.oauthsimple.builder.api;

import org.oauthsimple.model.OAuthToken;

public class EvernoteApi extends DefaultApi10a {
	private static final String AUTHORIZATION_URL = "https://www.evernote.com/OAuth.action?oauth_token=%s";

	@Override
	public String getRequestTokenEndpoint() {
		return "https://www.evernote.com/oauth";
	}

	@Override
	public String getAccessTokenEndpoint() {
		return "https://www.evernote.com/oauth";
	}

	@Override
	public String getAuthorizationUrl(OAuthToken requestToken) {
		return String.format(AUTHORIZATION_URL, requestToken.getToken());
	}

	public static class Sandbox extends EvernoteApi {
		private static final String SANDBOX_URL = "https://sandbox.evernote.com";

		@Override
		public String getRequestTokenEndpoint() {
			return SANDBOX_URL + "/oauth";
		}

		@Override
		public String getAccessTokenEndpoint() {
			return SANDBOX_URL + "/oauth";
		}

		@Override
		public String getAuthorizationUrl(OAuthToken requestToken) {
			return String.format(SANDBOX_URL + "/OAuth.action?oauth_token=%s",
					requestToken.getToken());
		}
	}
}
