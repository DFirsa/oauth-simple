package org.scribe.oauth;

import java.io.IOException;

import org.scribe.model.OAuthRequest;
import org.scribe.model.OAuthToken;
import org.scribe.model.Verifier;

/**
 * The main Scribe object.
 * 
 * A facade responsible for the retrieval of request and access tokens and for
 * the signing of HTTP requests.
 * 
 * @author Pablo Fernandez
 */
public interface OAuthService {
	/**
	 * Retrieve the request token.
	 * 
	 * @return request token
	 */
	public OAuthToken getRequestToken() throws IOException;

	/**
	 * Retrieve the access token by username/password, xauth and oauth 2.0
	 * support it.
	 * 
	 * @param userName
	 * @param password
	 * @return
	 * @throws IOException
	 */
	public OAuthToken getAccessToken(String userName, String password)
			throws IOException;

	/**
	 * Retrieve the access token
	 * 
	 * @param requestToken
	 *            request token (obtained previously)
	 * @param verifier
	 *            verifier code
	 * @return access token
	 */
	public OAuthToken getAccessToken(OAuthToken requestToken, Verifier verifier)
			throws IOException;

	/**
	 * Signs am OAuth request
	 * 
	 * @param accessToken
	 *            access token (obtained previously)
	 * @param request
	 *            request to sign
	 */
	public void signRequest(OAuthToken accessToken, OAuthRequest request);

	/**
	 * Returns the OAuth version of the service.
	 * 
	 * @return oauth version as string
	 */
	public String getVersion();

	/**
	 * Returns the URL where you should redirect your users to authenticate your
	 * application.
	 * 
	 * @param requestToken
	 *            the request token you need to authorize
	 * @return the URL where you should redirect your users
	 */
	public String getAuthorizationUrl(OAuthToken requestToken);

	public void setProxy(java.net.Proxy proxy);
}
