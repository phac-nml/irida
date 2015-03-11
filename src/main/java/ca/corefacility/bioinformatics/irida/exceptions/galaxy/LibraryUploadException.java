package ca.corefacility.bioinformatics.irida.exceptions.galaxy;

import ca.corefacility.bioinformatics.irida.exceptions.UploadException;

/**
 * When there is an issue uploading files to a data library.
 *
 */
public class LibraryUploadException extends UploadException {
	private static final long serialVersionUID = -5915057695904796185L;

	/**
	 * Constructs a new LibraryUploadException with no information.
	 */
	public LibraryUploadException() {
		super();
	}

	/**
	 * Constructs a new LibraryUploadException with the given message and cause.
	 * @param message  The message explaining the error.
	 * @param cause  The cause of this message.
	 */
	public LibraryUploadException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Constructs a new LibraryUploadException with the given message.
	 * @param message  The message explaining the error.
	 */
	public LibraryUploadException(String message) {
		super(message);
	}

	/**
	 * Constructs a new LibraryUploadException with the given cause.
	 * @param cause  The cause of this error.
	 */
	public LibraryUploadException(Throwable cause) {
		super(cause);
	}
}
