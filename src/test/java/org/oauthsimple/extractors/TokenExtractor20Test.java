package org.oauthsimple.extractors;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.oauthsimple.exceptions.OAuthException;
import org.oauthsimple.model.OAuthToken;

public class TokenExtractor20Test {

	private TokenExtractor20Impl extractor;

	@Before
	public void setup() {
		extractor = new TokenExtractor20Impl();
	}

	@Test
	public void shouldExtractTokenFromOAuthStandardResponse() {
		String response = "access_token=166942940015970|2.2ltzWXYNDjCtg5ZDVVJJeg__.3600.1295816400-548517159|RsXNdKrpxg8L6QNLWcs2TVTmcaE";
		OAuthToken extracted = extractor.extract(response);
		assertEquals(
				"166942940015970|2.2ltzWXYNDjCtg5ZDVVJJeg__.3600.1295816400-548517159|RsXNdKrpxg8L6QNLWcs2TVTmcaE",
				extracted.getToken());
		assertEquals("", extracted.getSecret());
	}

	@Test
	public void shouldExtractTokenFromResponseWithExpiresParam() {
		String response = "access_token=166942940015970|2.2ltzWXYNDjCtg5ZDVVJJeg__.3600.1295816400-548517159|RsXNdKrpxg8L6QNLWcs2TVTmcaE&expires=5108";
		OAuthToken extracted = extractor.extract(response);
		assertEquals(
				"166942940015970|2.2ltzWXYNDjCtg5ZDVVJJeg__.3600.1295816400-548517159|RsXNdKrpxg8L6QNLWcs2TVTmcaE",
				extracted.getToken());
		assertEquals("", extracted.getSecret());
	}

	@Test
	public void shouldExtractTokenFromResponseWithManyParameters() {
		String response = "access_token=foo1234&other_stuff=yeah_we_have_this_too&number=42";
		OAuthToken extracted = extractor.extract(response);
		assertEquals("foo1234", extracted.getToken());
		assertEquals("", extracted.getSecret());
	}

	@Test(expected = OAuthException.class)
	public void shouldThrowExceptionIfTokenIsAbsent() {
		String response = "&expires=5108";
		extractor.extract(response);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldThrowExceptionIfResponseIsNull() {
		String response = null;
		extractor.extract(response);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldThrowExceptionIfResponseIsEmptyString() {
		String response = "";
		extractor.extract(response);
	}
}
