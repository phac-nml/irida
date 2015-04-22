package ca.corefacility.bioinformatics.irida.exceptions;

/**
 * Exception that gets thrown when there is an issue running a workflow.
 *
 */
public class WorkflowException extends ExecutionManagerException {

	private static final long serialVersionUID = -5418939764042687991L;

	/**
	 * Constructs a new WorkflowException with no information.
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
