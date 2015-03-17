package ca.corefacility.bioinformatics.irida.exceptions;

/**
 * Exception that gets thrown when uploading data to a separate data management
 * system (e.g. Galaxy).
 * 
 * 
 */
public class UploadException extends ExecutionManagerException {
	private static final long serialVersionUID = 8934376035189966872L;

	/**
	 * Constructs a new UploadException with no information.
	 */
	public UploadException() {
		super();
	}

	/**
	 * Constructs a new UploadException with the given message and cause.
	 * @param message  The message explaining the error.
	 * @param cause  The cause of this message.
	 */
	public UploadException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Constructs a new UploadException with the given message.
	 * @param message  The message explaining the error.
	 */
	public UploadException(String message) {
		super(message);
	}

	/**
	 * Constructs a new UploadException with the given cause.
	 * @param cause  The cause of this error.
	 */
	public UploadException(Throwable cause) {
		super(cause);
	}
}
