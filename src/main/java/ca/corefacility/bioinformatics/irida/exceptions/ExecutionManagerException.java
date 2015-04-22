package ca.corefacility.bioinformatics.irida.exceptions;

/**
 * Exception that gets thrown when dealing with data or workflows from an external
 * execution manager (e.g. Galaxy).
 *
 */
public class ExecutionManagerException extends Exception {
	
	private static final long serialVersionUID = 333234286899354317L;

	/**
	 * Constructs a new ExecutionManagerException with no information.
	 */
	public ExecutionManagerException() {
		super();
	}

	/**
	 * Constructs a new ExecutionManagerException with the given message and cause.
	 * @param message  The message explaining the error.
	 * @param cause  The cause of this message.
	 */
	public ExecutionManagerException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Constructs a new ExecutionManagerException with the given message.
	 * @param message  The message explaining the error.
	 */
	public ExecutionManagerException(String message) {
		super(message);
	}

	/**
	 * Constructs a new ExecutionManagerException with the given cause.
	 * @param cause  The cause of this error.
	 */
	public ExecutionManagerException(Throwable cause) {
		super(cause);
	}
}
