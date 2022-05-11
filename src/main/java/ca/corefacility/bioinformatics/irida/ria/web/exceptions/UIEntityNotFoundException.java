package ca.corefacility.bioinformatics.irida.ria.web.exceptions;

import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;

/**
 * Exception to be thrown by the UI when the entity does not exist.
 */
public class UIEntityNotFoundException extends EntityNotFoundException {
	public UIEntityNotFoundException(String errorMessage) {
		super(errorMessage);
	}
}
