package ca.corefacility.bioinformatics.irida.exceptions;

/**
 * Exception thrown when attempting to access a value that does not currently exist.
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 */
public class NoSuchValueException extends Exception {

	private static final long serialVersionUID = -1741854317815951619L;

	/**
	 * Constructs a new NoSuchValueException with no information.
	 */
	public NoSuchValueException() {
		super();
	}

	/**
	 * Constructs a new NoSuchValueException with the given message and cause.
	 * @param message  The message explaining the error.
	 * @param cause  The cause of this message.
	 */
	public NoSuchValueException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Constructs a new NoSuchValueException with the given message.
	 * @param message  The message explaining the error.
	 */
	public NoSuchValueException(String message) {
		super(message);
	}

	/**
	 * Constructs a new NoSuchValueException with the given cause.
	 * @param cause  The cause of this error.
	 */
	public NoSuchValueException(Throwable cause) {
		super(cause);
	}
}
