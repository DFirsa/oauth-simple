package org.oauthsimple.oauth;

import java.io.IOException;
import java.net.Proxy;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.oauthsimple.builder.api.DefaultApi10a;
import org.oauthsimple.model.OAuthConfig;
import org.oauthsimple.model.OAuthConstants;
import org.oauthsimple.model.OAuthRequest;
import org.oauthsimple.model.OAuthToken;
import org.oauthsimple.model.Parameter;
import org.oauthsimple.model.Request;
import org.oauthsimple.model.RequestTuner;
import org.oauthsimple.model.Response;
import org.oauthsimple.model.Verb;
import org.oauthsimple.model.Verifier;
import org.oauthsimple.utils.MapUtils;
import org.oauthsimple.utils.Utils;

/**
 * OAuth 1.0a implementation of {@link OAuthService}
 * 
 * @author Pablo Fernandez
 */
public class OAuth10aServiceImpl implements OAuthService {
	private static final String VERSION = "1.0";

	private OAuthConfig config;
	private DefaultApi10a api;
	private java.net.Proxy proxy;

	/**
	 * Default constructor
	 * 
	 * @param api
	 *            OAuth1.0a api information
	 * @param config
	 *            OAuth 1.0a configuration param object
	 */
	public OAuth10aServiceImpl(DefaultApi10a api, OAuthConfig config) {
		this.api = api;
		this.config = config;
	}

	/**
	 * {@inheritDoc}
	 */
	public OAuthToken getRequestToken(int timeout, TimeUnit unit)
			throws IOException {
		return getRequestToken(new TimeoutTuner(timeout, unit));
	}

	public OAuthToken getRequestToken() throws IOException {
		return getRequestToken(2, TimeUnit.SECONDS);
	}

	public OAuthToken getRequestToken(RequestTuner tuner) throws IOException {
		config.log("obtaining request token from "
				+ api.getRequestTokenEndpoint());
		OAuthRequest request = new OAuthRequest(api.getRequestTokenVerb(),
				api.getRequestTokenEndpoint());

		config.log("setting oauth_callback to " + config.getCallback());
		request.addOAuthParameter(OAuthConstants.CALLBACK, config.getCallback());
		addOAuthParams(request, OAuthConstants.EMPTY_TOKEN);
		appendSignature(request);

		config.log("setting proxy to " + proxy);
		if (proxy != null) {
			request.setProxy(proxy);
		}
		config.log("sending request...");
		Response response;
		response = request.send();
		String body = response.getBody();
		config.log("response status code: " + response.getCode());
		config.log("response body: " + body);
		return api.getRequestTokenExtractor().extract(body);
	}

	private void addXAuthParams(OAuthRequest request, String userName,
			String password) {
		request.addOAuthParameter(OAuthConstants.X_AUTH_USERNAME, userName);
		request.addOAuthParameter(OAuthConstants.X_AUTH_PASSWORD, password);
		request.addOAuthParameter(OAuthConstants.X_AUTH_MODE, "client_auth");
		request.addOAuthParameter(OAuthConstants.TIMESTAMP, api
				.getTimestampService().getTimestampInSeconds());
		request.addOAuthParameter(OAuthConstants.NONCE, api
				.getTimestampService().getNonce());
		request.addOAuthParameter(OAuthConstants.CONSUMER_KEY,
				config.getApiKey());
		request.addOAuthParameter(OAuthConstants.SIGN_METHOD, api
				.getSignatureService().getSignatureMethod());
		request.addOAuthParameter(OAuthConstants.VERSION, getVersion());
		if (config.hasScope())
			request.addOAuthParameter(OAuthConstants.SCOPE, config.getScope());
		request.addOAuthParameter(OAuthConstants.SIGNATURE,
				getSignature(request, null));

		config.log("appended additional OAuth parameters: "
				+ Utils.convertToString(request.getOauthParameters()));
	}

	private void addOAuthParams(OAuthRequest request, OAuthToken token) {
		request.addOAuthParameter(OAuthConstants.TIMESTAMP, api
				.getTimestampService().getTimestampInSeconds());
		request.addOAuthParameter(OAuthConstants.NONCE, api
				.getTimestampService().getNonce());
		request.addOAuthParameter(OAuthConstants.CONSUMER_KEY,
				config.getApiKey());
		request.addOAuthParameter(OAuthConstants.SIGN_METHOD, api
				.getSignatureService().getSignatureMethod());
		request.addOAuthParameter(OAuthConstants.VERSION, getVersion());
		if (config.hasScope())
			request.addOAuthParameter(OAuthConstants.SCOPE, config.getScope());
		request.addOAuthParameter(OAuthConstants.SIGNATURE,
				getSignature(request, token));

		config.log("appended additional OAuth parameters: "
				+ Utils.convertToString(request.getOauthParameters()));
	}

	@Override
	public OAuthToken getAccessToken(String userName, String password)
			throws IOException {
		config.log("obtaining access token use xauth from "
				+ api.getAccessTokenEndpoint());
		OAuthRequest request = new OAuthRequest(api.getAccessTokenVerb(),
				api.getAccessTokenEndpoint());

		config.log("setting userName to: " + userName + " and password to: "
				+ password);
		addXAuthParams(request, userName, password);
		appendSignature(request);
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

	public OAuthToken getAccessToken(OAuthToken requestToken,
			Verifier verifier, int timeout, TimeUnit unit) throws IOException {
		return getAccessToken(requestToken, verifier, new TimeoutTuner(timeout,
				unit));
	}

	public OAuthToken getAccessToken(OAuthToken requestToken, Verifier verifier)
			throws IOException {
		return getAccessToken(requestToken, verifier, 2, TimeUnit.SECONDS);
	}

	public OAuthToken getAccessToken(OAuthToken requestToken,
			Verifier verifier, RequestTuner tuner) throws IOException {
		config.log("obtaining access token from "
				+ api.getAccessTokenEndpoint());
		OAuthRequest request = new OAuthRequest(api.getAccessTokenVerb(),
				api.getAccessTokenEndpoint());
		request.addOAuthParameter(OAuthConstants.TOKEN, requestToken.getToken());
		request.addOAuthParameter(OAuthConstants.VERIFIER, verifier.getValue());

		config.log("setting token to: " + requestToken + " and verifier to: "
				+ verifier);
		addOAuthParams(request, requestToken);
		appendSignature(request);
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
	public void signRequest(OAuthToken token, OAuthRequest request) {
		config.log("signing request: " + request.getCompleteUrl());

		// Do not append the token if empty. This is for two legged OAuth calls.
		if (!token.isEmpty()) {
			request.addOAuthParameter(OAuthConstants.TOKEN, token.getToken());
		}
		config.log("setting token to: " + token);
		addOAuthParams(request, token);
		appendSignature(request);
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
	public String getAuthorizationUrl(OAuthToken requestToken) {
		return api.getAuthorizationUrl(requestToken);
	}

	private String getSignature(OAuthRequest request, OAuthToken token) {
		config.log("generating signature...");
		String baseString = api.getBaseStringExtractor().extract(request);
		String signature = api.getSignatureService()
				.getSignature(baseString, config.getApiSecret(),
						token == null ? null : token.getSecret());

		config.log("base string is: " + baseString);
		config.log("signature is: " + signature);
		return signature;
	}

	private void appendSignature(OAuthRequest request) {
		switch (config.getSignatureType()) {
		case QUERY_STRING:
			config.log("using Querystring signature");
			if (Verb.POST == request.getVerb() || Verb.PUT == request.getVerb()) {
				for (Parameter param : request.getOauthParameters()) {
					request.addBodyParameter(param);
				}
			} else {
				for (Parameter param : request.getOauthParameters()) {
					request.addQueryStringParameter(param);
				}
			}
			break;
		default:
			config.log("using Http Header signature");
			String oauthHeader = api.getHeaderExtractor().extract(request);
			request.addHeader(OAuthConstants.HEADER, oauthHeader);
			break;
		}
	}

	private static class TimeoutTuner extends RequestTuner {
		private final int duration;
		private final TimeUnit unit;

		public TimeoutTuner(int duration, TimeUnit unit) {
			this.duration = duration;
			this.unit = unit;
		}

		@Override
		public void tune(Request request) {
			request.setReadTimeout(duration, unit);
		}
	}

	@Override
	public void setProxy(Proxy proxy) {
		this.proxy = proxy;
	}

}
