package ca.corefacility.bioinformatics.irida.ria.web.exceptions;

import java.util.Map;

/**
 * Used by UI to contain internationalized constraint violations.
 */
public class UIConstraintViolationException extends Exception {
	private final Map<String, String> errors;
	private final String errorMessage;

	public UIConstraintViolationException(Map<String, String> errors) {
		this.errors = errors;
		this.errorMessage = null;
	}

	public UIConstraintViolationException(String errorMessage) {
		this.errors = null;
		this.errorMessage = errorMessage; }

	public Map<String, String> getErrors() {
		return errors;
	}

	public String getErrorMessage() { return errorMessage; }
}
