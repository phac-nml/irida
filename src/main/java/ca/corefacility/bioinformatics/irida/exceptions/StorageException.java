package ca.corefacility.bioinformatics.irida.exceptions;

/**
 * When a database error occurs
 *
 */
public class StorageException extends RuntimeException {

	private static final long serialVersionUID = 1981775136339995070L;

	/**
	 * Construct a new {@link DatabaseException} with the specified message.
	 *
	 * @param message
	 *            the message explaining the exception.
	 */
	public StorageException(String message) {
		super(message);
	}

	/**
	 * Construct a new {@link DatabaseException} with the specified message and
	 * original cause.
	 * 
	 * @param message
	 * @param cause
	 */
	public StorageException(String message, Throwable cause) {
		super(message, cause);
	}
}
