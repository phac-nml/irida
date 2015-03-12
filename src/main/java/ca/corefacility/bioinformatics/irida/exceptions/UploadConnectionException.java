package ca.corefacility.bioinformatics.irida.exceptions;

/**
 * Thrown when a connection to a remote site for uploading data fails.
 *
 */
public class UploadConnectionException extends UploadException {
	private static final long serialVersionUID = 736574425314133873L;

	/**
	 * Constructs a new UploadConnectionException with no information.
	 */
	public UploadConnectionException() {
		super();
	}

	/**
	 * Constructs a new UploadConnectionException with the given message and cause.
	 * @param message  The message explaining the error.
	 * @param cause  The cause of this message.
	 */
	public UploadConnectionException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Constructs a new UploadConnectionException with the given message.
	 * @param message  The message explaining the error.
	 */
	public UploadConnectionException(String message) {
		super(message);
	}

	/**
	 * Constructs a new UploadConnectionException with the given cause.
	 * @param cause  The cause of this error.
	 */
	public UploadConnectionException(Throwable cause) {
		super(cause);
	}
}
