package org.oauthsimple.extractors;

import org.oauthsimple.exceptions.OAuthParametersMissingException;
import org.oauthsimple.http.OAuthRequest;
import org.oauthsimple.http.Parameter;
import org.oauthsimple.utils.OAuthEncoder;
import org.oauthsimple.utils.Preconditions;
import org.oauthsimple.utils.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Default implementation of {@link BaseStringExtractor}. Conforms to OAuth 1.0a
 * 
 * @author Pablo Fernandez
 * 
 */
public class BaseStringExtractorImpl implements BaseStringExtractor {

	private static final String AMPERSAND_SEPARATED_STRING = "%s&%s&%s";

	/**
	 * {@inheritDoc}
	 */
	public String extract(OAuthRequest request) {
		checkPreconditions(request);
		String verb = OAuthEncoder.encode(request.getVerb().name());
		String url = OAuthEncoder.encode(request.getSanitizedUrl());
		String params = getSortedAndEncodedParams(request);
		return String.format(AMPERSAND_SEPARATED_STRING, verb, url, params);
	}

	private String getSortedAndEncodedParams(OAuthRequest request) {
		List<Parameter> params = new ArrayList<Parameter>();
//		if (request.isFormEncodedContent()) {
			params.addAll(request.getParameters());
//		}
		params.addAll(request.getOauthParameters());
		Collections.sort(params);
		return OAuthEncoder.encode(Utils.asFormUrlEncodedString(params));
	}

	private void checkPreconditions(OAuthRequest request) {
		Preconditions.checkNotNull(request,
				"Cannot extract base string from null object");

		if (request.getOauthParameters() == null
				|| request.getOauthParameters().size() <= 0) {
			throw new OAuthParametersMissingException(request);
		}
	}
}
