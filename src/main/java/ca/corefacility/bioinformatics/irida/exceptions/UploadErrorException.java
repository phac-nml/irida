package ca.corefacility.bioinformatics.irida.exceptions;

/**
 * Exception that gets thrown when there is an error while uploading data to a
 * separate data management system (e.g. Galaxy).
 * 
 * 
 */
public class UploadErrorException extends UploadException {

	/**
	 * Constructs a new UploadErrorException with no information.
	 */
	public UploadErrorException() {
		super();
	}

	/**
	 * Constructs a new UploadErrorException with the given message and cause.
	 * 
	 * @param message
	 *            The message explaining the error.
	 * @param cause
	 *            The cause of this message.
	 */
	public UploadErrorException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Constructs a new UploadErrorException with the given message.
	 * 
	 * @param message
	 *            The message explaining the error.
	 */
	public UploadErrorException(String message) {
		super(message);
	}

	/**
	 * Constructs a new UploadErrorException with the given cause.
	 * 
	 * @param cause
	 *            The cause of this error.
	 */
	public UploadErrorException(Throwable cause) {
		super(cause);
	}
}
