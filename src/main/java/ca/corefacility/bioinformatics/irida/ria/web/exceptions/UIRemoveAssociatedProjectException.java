package ca.corefacility.bioinformatics.irida.ria.web.exceptions;

/**
 * Exception to through if there is an error removing an associated project from a project
 */
public class UIRemoveAssociatedProjectException extends Exception {
	public UIRemoveAssociatedProjectException(String message) {
		super(message);
	}
}
