package ca.corefacility.bioinformatics.irida.exceptions.galaxy;

import ca.corefacility.bioinformatics.irida.exceptions.ExecutionManagerObjectNotFoundException;

/**
 * When no data library could be found.
 *
 */
public class NoLibraryFoundException extends ExecutionManagerObjectNotFoundException {
	private static final long serialVersionUID = -2968750017497563652L;

	/**
	 * Constructs a new NoLibraryFoundException with no information.
	 */
	public NoLibraryFoundException() {
		super();
	}

	/**
	 * Constructs a new NoLibraryFoundException with the given message and cause.
	 * @param message  The message explaining the error.
	 * @param cause  The cause of this message.
	 */
	public NoLibraryFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Constructs a new NoLibraryFoundException with the given message.
	 * @param message  The message explaining the error.
	 */
	public NoLibraryFoundException(String message) {
		super(message);
	}

	/**
	 * Constructs a new NoLibraryFoundException with the given cause.
	 * @param cause  The cause of this error.
	 */
	public NoLibraryFoundException(Throwable cause) {
		super(cause);
	}
}
