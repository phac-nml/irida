package ca.corefacility.bioinformatics.irida.exceptions;

/**
 * Thrown when there is an issue downloading data from an execution manager.
 *
 */
public class ExecutionManagerDownloadException extends ExecutionManagerException {

	private static final long serialVersionUID = 6602102710037152420L;

	/**
	 * Constructs a new ExecutionManagerDownloadException with no information.
	 */
	public ExecutionManagerDownloadException() {
		super();
	}

	/**
	 * Constructs a new ExecutionManagerDownloadException with the given message and cause.
	 * @param message  The message explaining the error.
	 * @param cause  The cause of this message.
	 */
	public ExecutionManagerDownloadException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Constructs a new ExecutionManagerDownloadException with the given message.
	 * @param message  The message explaining the error.
	 */
	public ExecutionManagerDownloadException(String message) {
		super(message);
	}

	/**
	 * Constructs a new ExecutionManagerDownloadException with the given cause.
	 * @param cause  The cause of this error.
	 */
	public ExecutionManagerDownloadException(Throwable cause) {
		super(cause);
	}
}
