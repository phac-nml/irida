package ca.corefacility.bioinformatics.irida.exceptions.galaxy;

import ca.corefacility.bioinformatics.irida.exceptions.ExecutionManagerObjectNotFoundException;

/**
 * When no content for a data library could be found.
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 */
public class NoGalaxyContentFoundException extends ExecutionManagerObjectNotFoundException {
	private static final long serialVersionUID = -4971585560286283917L;

	/**
	 * Constructs a new NoGalaxyContentFoundException with no information.
	 */
	public NoGalaxyContentFoundException() {
		super();
	}

	/**
	 * Constructs a new NoGalaxyContentFoundException with the given message and cause.
	 * @param message  The message explaining the error.
	 * @param cause  The cause of this message.
	 */
	public NoGalaxyContentFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Constructs a new NoGalaxyContentFoundException with the given message.
	 * @param message  The message explaining the error.
	 */
	public NoGalaxyContentFoundException(String message) {
		super(message);
	}

	/**
	 * Constructs a new NoGalaxyContentFoundException with the given cause.
	 * @param cause  The cause of this error.
	 */
	public NoGalaxyContentFoundException(Throwable cause) {
		super(cause);
	}
}
