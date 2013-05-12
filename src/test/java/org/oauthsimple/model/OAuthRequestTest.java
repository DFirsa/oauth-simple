package org.oauthsimple.model;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.oauthsimple.model.OAuthConstants;
import org.oauthsimple.model.OAuthRequest;
import org.oauthsimple.model.Verb;

public class OAuthRequestTest {

	private OAuthRequest request;

	@Before
	public void setup() {
		request = new OAuthRequest(Verb.GET, "http://example.com");
	}

	@Test
	public void shouldAddOAuthParamters() {
		request.addOAuthParameter(OAuthConstants.TOKEN, "token");
		request.addOAuthParameter(OAuthConstants.NONCE, "nonce");
		request.addOAuthParameter(OAuthConstants.TIMESTAMP, "ts");
		request.addOAuthParameter(OAuthConstants.SCOPE, "feeds");

		assertEquals(4, request.getOauthParameters().size());
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldThrowExceptionIfParameterIsNotOAuth() {
		request.addOAuthParameter("otherParam", "value");
	}
}
