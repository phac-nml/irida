package ca.corefacility.bioinformatics.irida.ria.web.exceptions;

import ca.corefacility.bioinformatics.irida.exceptions.ProjectWithoutOwnerException;

/**
 * Exception to be thrown by the UI when the member being removed from the project,
 * would leave the project without a manager.
 */
public class UIProjectWithoutOwnerException extends ProjectWithoutOwnerException {
	public UIProjectWithoutOwnerException(String errorMessage) {
		super(errorMessage);
	}
}
