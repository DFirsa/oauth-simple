package org.oauthsimple.builder.api;

import org.oauthsimple.extractors.AccessTokenExtractor;
import org.oauthsimple.extractors.JsonTokenExtractor;
import org.oauthsimple.model.OAuthConfig;
import org.oauthsimple.http.Verb;
import org.oauthsimple.utils.OAuthEncoder;

/**
 * SinaWeibo OAuth 2.0 api.
 */
public class SinaWeiboApi20 extends DefaultApi20 {
	private static final String AUTHORIZE_URL = "https://api.weibo.com/oauth2/authorize?client_id=%s&redirect_uri=%s&response_type=%s";
	private static final String SCOPED_AUTHORIZE_URL = AUTHORIZE_URL
			+ "&scope=%s";

	@Override
	public Verb getAccessTokenVerb() {
		return Verb.POST;
	}

	@Override
	public AccessTokenExtractor getAccessTokenExtractor() {
		return new JsonTokenExtractor();
	}

	@Override
	public String getAccessTokenEndpoint(OAuthConfig config) {
		return "https://api.weibo.com/oauth2/access_token?grant_type="
				+ config.getGrantType().getTypeValue();
	}

	@Override
	public String getAuthorizationUrl(OAuthConfig config) {
		// Append scope if present
		if (config.hasScope()) {
			return String.format(SCOPED_AUTHORIZE_URL, config.getApiKey(),
					OAuthEncoder.encode(config.getCallback()), config
							.getResponseType().getTypeValue(), OAuthEncoder
							.encode(config.getScope()));
		} else {
			return String.format(AUTHORIZE_URL, config.getApiKey(),
					OAuthEncoder.encode(config.getCallback()), config
							.getResponseType().getTypeValue());
		}
	}
}
