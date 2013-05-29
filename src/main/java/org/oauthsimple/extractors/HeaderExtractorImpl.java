package org.oauthsimple.extractors;

import org.oauthsimple.exceptions.OAuthParametersMissingException;
import org.oauthsimple.model.OAuthRequest;
import org.oauthsimple.model.Parameter;
import org.oauthsimple.utils.OAuthEncoder;
import org.oauthsimple.utils.Preconditions;

import java.util.List;

/**
 * Default implementation of {@link HeaderExtractor}. Conforms to OAuth 1.0a
 * 
 * @author Pablo Fernandez
 * 
 */
public class HeaderExtractorImpl implements HeaderExtractor {
	private static final String PARAM_SEPARATOR = ", ";
	private static final String PREAMBLE = "OAuth ";
	public static final int ESTIMATED_PARAM_LENGTH = 20;

	/**
	 * {@inheritDoc}
	 */
	public String extract(OAuthRequest request) {
		checkPreconditions(request);
		List<Parameter> parameters = request.getOauthParameters();
		// Map<String, String> parameters = request.getOauthParameters();
		StringBuilder header = new StringBuilder(parameters.size()
				* ESTIMATED_PARAM_LENGTH);
		header.append(PREAMBLE);
		for (Parameter param : parameters) {
			if (header.length() > PREAMBLE.length()) {
				header.append(PARAM_SEPARATOR);
			}
			header.append(String.format("%s=\"%s\"", param.getName(),
					OAuthEncoder.encode(param.getValue())));
		}

		return header.toString();
	}

	private void checkPreconditions(OAuthRequest request) {
		Preconditions.checkNotNull(request,
				"Cannot extract a header from a null object");

		if (request.getOauthParameters() == null
				|| request.getOauthParameters().size() <= 0) {
			throw new OAuthParametersMissingException(request);
		}
	}

}
