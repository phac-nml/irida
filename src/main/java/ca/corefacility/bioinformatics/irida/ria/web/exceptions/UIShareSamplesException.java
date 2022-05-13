package ca.corefacility.bioinformatics.irida.ria.web.exceptions;

/**
 * Exception to through if there is an error sharing samples between projects
 */
public class UIShareSamplesException extends Exception {
	public UIShareSamplesException(String message) {
		super(message);
	}
}
