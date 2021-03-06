package org.oauthsimple.builder.api;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.oauthsimple.exceptions.OAuthException;
import org.oauthsimple.extractors.AccessTokenExtractor;
import org.oauthsimple.model.OAuthConfig;
import org.oauthsimple.model.OAuthToken;
import org.oauthsimple.http.Verb;
import org.oauthsimple.utils.OAuthEncoder;
import org.oauthsimple.utils.Preconditions;

public class ConstantContactApi2 extends DefaultApi20 {
	private static final String AUTHORIZE_URL = "https://oauth2.constantcontact.com/oauth2/oauth/siteowner/authorize?client_id=%s&response_type=%s&redirect_uri=%s";

	@Override
	public String getAccessTokenEndpoint(OAuthConfig config) {
		return "https://oauth2.constantcontact.com/oauth2/oauth/token?grant_type=authorization_code"
				+ config.getGrantType().getTypeValue();
	}

	@Override
	public String getAuthorizationUrl(OAuthConfig config) {
		return String.format(AUTHORIZE_URL, config.getApiKey(), config
				.getResponseType().getTypeValue(), OAuthEncoder.encode(config
				.getCallback()));
	}

	@Override
	public Verb getAccessTokenVerb() {
		return Verb.POST;
	}

	@Override
	public AccessTokenExtractor getAccessTokenExtractor() {
		return new AccessTokenExtractor() {

			public OAuthToken extract(String response) {
				Preconditions
						.checkEmptyString(response,
								"Response body is incorrect. Can't extract a token from an empty string");

				String regex = "\"access_token\"\\s*:\\s*\"([^&\"]+)\"";
				Matcher matcher = Pattern.compile(regex).matcher(response);
				if (matcher.find()) {
					String token = OAuthEncoder.decode(matcher.group(1));
					return new OAuthToken(token, "", response);
				} else {
					throw new OAuthException(
							"Response body is incorrect. Can't extract a token from this: '"
									+ response + "'", null);
				}
			}
		};
	}
}