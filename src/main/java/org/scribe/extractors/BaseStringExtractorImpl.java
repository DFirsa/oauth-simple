package org.scribe.extractors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.scribe.exceptions.OAuthParametersMissingException;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Parameter;
import org.scribe.utils.OAuthEncoder;
import org.scribe.utils.Preconditions;
import org.scribe.utils.Utils;

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
		if (request.isFormEncodedContent()) {
			params.addAll(request.getBodyParams());
		}
		params.addAll(request.getQueryStringParams());
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
