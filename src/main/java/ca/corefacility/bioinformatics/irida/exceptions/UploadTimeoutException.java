package ca.corefacility.bioinformatics.irida.exceptions;

/**
 * Exception that gets thrown when there is a timeout while uploading data to a
 * separate data management system (e.g. Galaxy).
 * 
 * 
 */
public class UploadTimeoutException extends UploadException {

	/**
	 * Constructs a new UploadTimeoutException with no information.
	 */
	public UploadTimeoutException() {
		super();
	}

	/**
	 * Constructs a new UploadTimeoutException with the given message and cause.
	 * 
	 * @param message
	 *            The message explaining the error.
	 * @param cause
	 *            The cause of this message.
	 */
	public UploadTimeoutException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Constructs a new UploadTimeoutException with the given message.
	 * 
	 * @param message
	 *            The message explaining the error.
	 */
	public UploadTimeoutException(String message) {
		super(message);
	}

	/**
	 * Constructs a new UploadTimeoutException with the given cause.
	 * 
	 * @param cause
	 *            The cause of this error.
	 */
	public UploadTimeoutException(Throwable cause) {
		super(cause);
	}
}
