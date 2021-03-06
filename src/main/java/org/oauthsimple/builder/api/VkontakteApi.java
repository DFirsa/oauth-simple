package org.oauthsimple.builder.api;

import org.oauthsimple.extractors.AccessTokenExtractor;
import org.oauthsimple.extractors.JsonTokenExtractor;
import org.oauthsimple.model.OAuthConfig;
import org.oauthsimple.utils.OAuthEncoder;
import org.oauthsimple.utils.Preconditions;

/**
 * @author Boris G. Tsirkin <mail@dotbg.name>
 * @since 20.4.2011
 */
public class VkontakteApi extends DefaultApi20 {
	private static final String AUTHORIZE_URL = "https://oauth.vk.com/authorize?client_id=%s&redirect_uri=%s&response_type=%s";
	private static final String SCOPED_AUTHORIZE_URL = String.format(
			"%s&scope=%%s", AUTHORIZE_URL);

	@Override
	public String getAccessTokenEndpoint(OAuthConfig config) {
		return "https://api.vkontakte.ru/oauth/access_token";
	}

	@Override
	public String getAuthorizationUrl(OAuthConfig config) {
		Preconditions
				.checkValidUrl(config.getCallback(),
						"Valid url is required for a callback. Vkontakte does not support OOB");
		if (config.hasScope())// Appending scope if present
		{
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

	@Override
	public AccessTokenExtractor getAccessTokenExtractor() {
		return new JsonTokenExtractor();
	}
}
