package ca.corefacility.bioinformatics.irida.ria.web.exceptions;

import java.util.Map;

public class UIConstraintViolationException extends Exception {
	private Map<String, String> errors;

	public UIConstraintViolationException(Map<String, String> errors) {
		this.errors = errors;
	}

	public Map<String, String> getErrors() {
		return errors;
	}
}
