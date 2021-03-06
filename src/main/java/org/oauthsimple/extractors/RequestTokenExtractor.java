package org.oauthsimple.extractors;

import org.oauthsimple.model.OAuthToken;

/**
 * Simple command object that extracts a {@link OAuthToken} from a String
 * 
 * @author Pablo Fernandez
 */
public interface RequestTokenExtractor {
	/**
	 * Extracts the request token from the contents of an Http Response
	 * 
	 * @param response
	 *            the contents of the response
	 * @return OAuth access token
	 */
	public OAuthToken extract(String response);
}
