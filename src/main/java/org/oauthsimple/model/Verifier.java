package org.oauthsimple.model;

import org.oauthsimple.utils.Preconditions;

/**
 * Represents an OAuth verifier code.
 * 
 * @author Pablo Fernandez
 */
public class Verifier {

	private final String value;

	/**
	 * Default constructor.
	 * 
	 * @param value
	 *            verifier value
	 */
	public Verifier(String value) {
		Preconditions.checkNotNull(value,
				"Must provide a valid string as verifier");
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Verifier [");
		builder.append(value);
		builder.append("]");
		return builder.toString();
	}
}
