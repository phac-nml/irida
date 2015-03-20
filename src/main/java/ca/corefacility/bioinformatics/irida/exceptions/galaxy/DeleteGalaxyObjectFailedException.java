package ca.corefacility.bioinformatics.irida.exceptions.galaxy;

import ca.corefacility.bioinformatics.irida.exceptions.ExecutionManagerException;

/**
 * Exception when there is a failure to delete a Galaxy object.
 */
public class DeleteGalaxyObjectFailedException extends ExecutionManagerException {
	
	private static final long serialVersionUID = -737246883706607391L;

	/**
	 * Constructs a new CreateLibraryException with the given message.
	 * @param message  The message explaining the error.
	 */
	public DeleteGalaxyObjectFailedException(String message) {
		super(message);
	}
	
	/**
	 * Constructs a new DeleteGalaxyObjectFailedException with the given cause.
	 * @param cause  The cause of this error.
	 */
	public DeleteGalaxyObjectFailedException(Throwable cause) {
		super(cause);
	}
}
