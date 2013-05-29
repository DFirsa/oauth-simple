package org.oauthsimple.oauth;

import org.oauthsimple.builder.api.DefaultApi20;
import org.oauthsimple.model.*;

import java.io.IOException;
import java.net.Proxy;

public class OAuth20ServiceImpl implements OAuthService {
	private static final String VERSION = "2.0";
	private static final int CREDENTIALS_MIN_LENGTH = 3;
	private static final String CREDENTIALS_SEPARATOR = ":";

	private final DefaultApi20 api;
	private final OAuthConfig config;
	private java.net.Proxy proxy;

	/**
	 * Default constructor
	 * 
	 * @param api
	 *            OAuth2.0 api information
	 * @param config
	 *            OAuth 2.0 configuration param object
	 */
	public OAuth20ServiceImpl(DefaultApi20 api, OAuthConfig config) {
		this.api = api;
		this.config = config;
	}

	@Override
	public OAuthToken getAccessToken(String userName, String password)
			throws IOException {
		return getAccessToken(null, new Verifier(userName
				+ CREDENTIALS_SEPARATOR + password));
	}

	public OAuthToken getAccessToken(OAuthToken requestToken, Verifier verifier)
			throws IOException {
		config.log("get access token , verifier is  " + verifier);
		if (config.getResponseType() == ResponseType.TOKEN) {
			return new OAuthToken(verifier.getValue(), "");
		}
		OAuthRequest request = new OAuthRequest(api.getAccessTokenVerb(),
				api.getAccessTokenEndpoint(config));
		GrantType type = config.getGrantType();

		// verifier对于不同的GRANT_TYPE有不同的含义
		// 1. authorize_code
		// 2. refresh token
		// 3. username+password
		if (type == GrantType.AUTHORIZATION_CODE) {
			request.addParameter(OAuthConstants.CODE,
					verifier.getValue());
		} else if (type == GrantType.REFRESH_TOKEN) {
			request.addParameter(OAuthConstants.REFRESH_TOKEN,
					verifier.getValue());
		} else if (type == GrantType.RESOURCE_OWNER_PASSWORD_CREDENTIALS) {
			String resource = verifier.getValue();
			if (resource != null
					&& resource.trim().length() >= CREDENTIALS_MIN_LENGTH) {
				String[] credentials = resource.split(CREDENTIALS_SEPARATOR);
				if (credentials != null && credentials.length == 2) {
					String userName = credentials[0];
					String password = credentials[1];
					request.addParameter(OAuthConstants.USERNAME,
							userName);
					request.addParameter(OAuthConstants.PASSWORD,
							password);
				}
			}
		}
		request.addParameter(OAuthConstants.CLIENT_ID,
				config.getApiKey());
		request.addParameter(OAuthConstants.CLIENT_SECRET,
				config.getApiSecret());
		request.addParameter(OAuthConstants.REDIRECT_URI,
				config.getCallback());
		request.addParameter(OAuthConstants.GRANT_TYPE, config
				.getGrantType().getTypeValue());
		if (config.hasScope())
			request.addParameter(OAuthConstants.SCOPE,
					config.getScope());

		config.log("setting proxy to " + proxy);
		if (proxy != null) {
			request.setProxy(proxy);
		}
		config.log("sending request...");
		Response response = request.send();
		String body = response.getBody();
		config.log("response status code: " + response.getCode());
		config.log("response body: " + body);
		return api.getAccessTokenExtractor().extract(body);
	}

	/**
	 * {@inheritDoc}
	 */
	public OAuthToken getRequestToken() throws IOException {
		throw new UnsupportedOperationException(
				"Unsupported operation, please use 'getAuthorizationUrl' and redirect your users there");
	}

	/**
	 * {@inheritDoc}
	 */
	public String getVersion() {
		return VERSION;
	}

	/**
	 * {@inheritDoc}
	 */
	public void signRequest(OAuthToken token, OAuthRequest request) {
		config.log("signing request: " + request.getCompleteUrl());
		config.log("setting token to: " + token);
		if (!token.isEmpty()) {
			switch (config.getSignatureType()) {
			case HEADER_BEARER:
				config.log("using Http Header Bearer signature");
				request.addHeader(OAuthConstants.HEADER,
						SignatureType.HEADER_BEARER.getTypeValue() + " "
								+ token.getToken());
				break;
			case HEADER_OAUTH:
				config.log("using Http Header OAuth2 signature");
				request.addHeader(
						OAuthConstants.HEADER,
						SignatureType.HEADER_OAUTH.getTypeValue() + " "
								+ token.getToken());
			case QUERY_STRING:
				config.log("using Querystring signature");
				request.addParameter(OAuthConstants.ACCESS_TOKEN,
						token.getToken());
				break;
			default:
				break;
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public String getAuthorizationUrl(OAuthToken requestToken) {
		return api.getAuthorizationUrl(config);
	}

	@Override
	public void setProxy(Proxy proxy) {
		this.proxy = proxy;
	}

}
