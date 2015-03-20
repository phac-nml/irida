package ca.corefacility.bioinformatics.irida.exceptions.galaxy;

import ca.corefacility.bioinformatics.irida.exceptions.ExecutionManagerException;

/**
 * Exception when there is a failure to delete a Galaxy library.
 */
public class DeleteLibraryFailedException extends ExecutionManagerException {
	
	private static final long serialVersionUID = -737246883706607391L;

	/**
	 * Constructs a new CreateLibraryException with the given message.
	 * @param message  The message explaining the error.
	 */
	public DeleteLibraryFailedException(String message) {
		super(message);
	}
}
