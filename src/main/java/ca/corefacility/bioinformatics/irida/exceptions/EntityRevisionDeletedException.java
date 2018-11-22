package ca.corefacility.bioinformatics.irida.exceptions;

/**
 * Spring data envers barfs when revisions are attempted to be loaded for a
 * deleted resource. Please see
 * https://github.com/spring-projects/spring-data-envers/issues/13 for more
 * information.
 * 
 *
 */
public class EntityRevisionDeletedException extends RuntimeException {
	private static final long serialVersionUID = 2074593569749610287L;

	/**
	 * Construct a new {@link EntityRevisionDeletedException} with the specified
	 * message.
	 *
	 * @param message
	 *            the message explaining the exception.
	 */
	public EntityRevisionDeletedException(String message) {
		super(message);
	}

	/**
	 * Construct a new {@link EntityRevisionDeletedException} with the specified
	 * message and cause.
	 *
	 * @param message
	 *            the message explaining the exception.
	 * @param cause
	 *            the original cause of the exception
	 */
	public EntityRevisionDeletedException(String message, Throwable cause) {
		super(message, cause);
	}
}
