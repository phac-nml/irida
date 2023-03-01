package ca.corefacility.bioinformatics.irida.exceptions.galaxy;

import ca.corefacility.bioinformatics.irida.exceptions.ExecutionManagerException;

/**
 * An exception that gets thrown when there a problem accessing a Galaxy Tool Data Table.
 *
 */
public class GalaxyToolDataTableException extends ExecutionManagerException {

	private static final long serialVersionUID = 5892958304576147418L;
	
	/**
	 * Constructs a new GalaxyToolDataTableException with no information.
	 */
	public GalaxyToolDataTableException() {
		super();
	}

	/**
	 * Constructs a new GalaxyToolDataTableException with the given message and cause.
	 * @param message  The message explaining the error.
	 * @param cause  The cause of this message.
	 */
	public GalaxyToolDataTableException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Constructs a new GalaxyToolDataTableException with the given message.
	 * @param message  The message explaining the error.
	 */
	public GalaxyToolDataTableException(String message) {
		super(message);
	}

	/**
	 * Constructs a new GalaxyToolDataTableException with the given cause.
	 * @param cause  The cause of this error.
	 */
	public GalaxyToolDataTableException(Throwable cause) {
		super(cause);
	}
}
