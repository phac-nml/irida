package ca.corefacility.bioinformatics.irida.exceptions.galaxy;

import ca.corefacility.bioinformatics.irida.exceptions.ExecutionManagerException;

/**
 * This exception is thrown when there is an error obtaining a Galaxy dataset.
 *
 */
public class GalaxyDatasetException extends ExecutionManagerException {

	private static final long serialVersionUID = -5320234853643948588L;

	/**
	 * Constructs a new GalaxyDatasetException with no information.
	 */
	public GalaxyDatasetException() {
		super();
	}

	/**
	 * Constructs a new GalaxyDatasetException with the given message and cause.
	 * @param message  The message explaining the error.
	 * @param cause  The cause of this message.
	 */
	public GalaxyDatasetException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Constructs a new GalaxyDatasetException with the given message.
	 * @param message  The message explaining the error.
	 */
	public GalaxyDatasetException(String message) {
		super(message);
	}

	/**
	 * Constructs a new GalaxyDatasetException with the given cause.
	 * @param cause  The cause of this error.
	 */
	public GalaxyDatasetException(Throwable cause) {
		super(cause);
	}
}
