package ca.corefacility.bioinformatics.irida.ria.web.exceptions;

import java.util.Map;

/**
 * Used by UI to contain internationalized constraint violations.
 */
public class UIConstraintViolationException extends Exception {
	private final Map<String, String> errors;

	public UIConstraintViolationException(Map<String, String> errors) {
		this.errors = errors;
	}

	public Map<String, String> getErrors() {
		return errors;
	}
}
