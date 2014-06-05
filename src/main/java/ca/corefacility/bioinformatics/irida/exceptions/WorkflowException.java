package ca.corefacility.bioinformatics.irida.exceptions;

public class WorkflowException extends Exception {

	private static final long serialVersionUID = -5418939764042687991L;

	/**
	 * Constructs a new UploadException with no information.
	 */
	public WorkflowException() {
		super();
	}

	/**
	 * Constructs a new WorkflowException with the given message and cause.
	 * @param message  The message explaining the error.
	 * @param cause  The cause of this message.
	 */
	public WorkflowException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Constructs a new WorkflowException with the given message.
	 * @param message  The message explaining the error.
	 */
	public WorkflowException(String message) {
		super(message);
	}

	/**
	 * Constructs a new WorkflowException with the given cause.
	 * @param cause  The cause of this error.
	 */
	public WorkflowException(Throwable cause) {
		super(cause);
	}
}
