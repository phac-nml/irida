package ca.corefacility.bioinformatics.irida.exceptions;

/**
 * Exception thrown if an object is not found by the execution manager
 */
public class ExecutionManagerObjectNotFoundException extends UploadException {

	private static final long serialVersionUID = -8631240265058889252L;

	/**
	 * Constructs a new ExecutionManagerObjectNotFoundException with no information.
	 */
	public ExecutionManagerObjectNotFoundException() {
		super();
	}

	/**
	 * Constructs a new ExecutionManagerObjectNotFoundException with the given message and cause.
	 * @param message  The message explaining the error.
	 * @param cause  The cause of this message.
	 */
	public ExecutionManagerObjectNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Constructs a new ExecutionManagerObjectNotFoundException with the given message.
	 * @param message  The message explaining the error.
	 */
	public ExecutionManagerObjectNotFoundException(String message) {
		super(message);
	}

	/**
	 * Constructs a new ExecutionManagerObjectNotFoundException with the given cause.
	 * @param cause  The cause of this error.
	 */
	public ExecutionManagerObjectNotFoundException(Throwable cause) {
		super(cause);
	}
}
