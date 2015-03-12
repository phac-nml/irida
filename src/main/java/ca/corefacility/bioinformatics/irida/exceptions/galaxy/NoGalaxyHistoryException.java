package ca.corefacility.bioinformatics.irida.exceptions.galaxy;

import ca.corefacility.bioinformatics.irida.exceptions.ExecutionManagerObjectNotFoundException;

/**
 * An exception that gets thrown when there is no history in Galaxy that can be found.
 *
 */
public class NoGalaxyHistoryException extends ExecutionManagerObjectNotFoundException {

	private static final long serialVersionUID = 6482869317425193110L;

	/**
	 * Constructs a new NoGalaxyHistoryException with no information.
	 */
	public NoGalaxyHistoryException() {
		super();
	}

	/**
	 * Constructs a new NoGalaxyHistoryException with the given message and cause.
	 * @param message  The message explaining the error.
	 * @param cause  The cause of this message.
	 */
	public NoGalaxyHistoryException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Constructs a new NoGalaxyHistoryException with the given message.
	 * @param message  The message explaining the error.
	 */
	public NoGalaxyHistoryException(String message) {
		super(message);
	}

	/**
	 * Constructs a new NoGalaxyHistoryException with the given cause.
	 * @param cause  The cause of this error.
	 */
	public NoGalaxyHistoryException(Throwable cause) {
		super(cause);
	}
}
