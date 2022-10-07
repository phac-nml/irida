package ca.corefacility.bioinformatics.irida.ria.web.exceptions;

/**
 * Exception to throw if there is an error updating the user status
 */
public class UIUserStatusException extends Exception {
	public UIUserStatusException(String message) {
		super(message);
	}
}