package ca.corefacility.bioinformatics.irida.ria.web.exceptions;

import java.util.Map;

/**
 * Exception to be thrown by the UI when there are errors during a metadata import.
 */
public class UIMetadataImportException extends Exception {
	private final Map<String, String> errors;

	public UIMetadataImportException(Map<String, String> errors) {
		super("Metadata Import Exception");
		this.errors = errors;
	}

	public Map<String, String> getErrors() {
		return this.errors;
	}
}
