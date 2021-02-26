package ca.corefacility.bioinformatics.irida.ria.web.ajax.projects.settings.exceptions;

/**
 * Thrown if there was an error during the update.
 */
public class UpdateException extends Exception{
	public UpdateException(String message) {
		super(message);
	}
}
