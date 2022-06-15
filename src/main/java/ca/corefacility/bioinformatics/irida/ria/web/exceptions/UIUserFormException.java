package ca.corefacility.bioinformatics.irida.ria.web.exceptions;

import java.util.Map;

/**
 * Exception to be thrown by the UI when a user cannot be created or updated.
 */
public class UIUserFormException extends Exception {
	private final Map<String, String> errors;

	public UIUserFormException(Map<String, String> errors) {
		super("User Form Exception");
		this.errors = errors;
	}

	public Map<String, String> getErrors() {
		return this.errors;
	}
}
