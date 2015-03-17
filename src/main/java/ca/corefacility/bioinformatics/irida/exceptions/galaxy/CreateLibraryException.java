package ca.corefacility.bioinformatics.irida.exceptions.galaxy;

import ca.corefacility.bioinformatics.irida.exceptions.UploadException;

/**
 * When there is an issue building a new data library.
 *
 */
public class CreateLibraryException extends UploadException {
	private static final long serialVersionUID = -5461414386915764417L;

	/**
	 * Constructs a new CreateLibraryException with the given message and cause.
	 * @param message  The message explaining the error.
	 * @param cause  The cause of this message.
	 */
	public CreateLibraryException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Constructs a new CreateLibraryException with the given message.
	 * @param message  The message explaining the error.
	 */
	public CreateLibraryException(String message) {
		super(message);
	}

	/**
	 * Constructs a new CreateLibraryException with the given cause.
	 * @param cause  The cause of this error.
	 */
	public CreateLibraryException(Throwable cause) {
		super(cause);
	}

	/**
	 * Constructs a new CreateLibraryException with no information.
	 */
	public CreateLibraryException() {
		super();
	}
}
