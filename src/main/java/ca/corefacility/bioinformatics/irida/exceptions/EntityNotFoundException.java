package ca.corefacility.bioinformatics.irida.exceptions;

/**
 * When an entity cannot be found in the database.
 */
public class EntityNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 2074593569749610287L;

	/**
	 * Construct a new {@link EntityNotFoundException} with the specified message.
	 *
	 * @param message the message explaining the exception.
	 */
	public EntityNotFoundException(String message) {
		super(message);
	}

	/**
	 * Construct a new {@link EntityNotFoundException} with the specified message and cause.
	 *
	 * @param message the message explaining the exception.
	 * @param cause   the original cause of the exception
	 */
	public EntityNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}
}
