package ca.corefacility.bioinformatics.irida.exceptions;

/**
 * An exception that gets thrown during the preparation of a workflow.
 *
 */
public class WorkflowPreprationException extends WorkflowException {

	private static final long serialVersionUID = 6727124691365346057L;

	/**
	 * Constructs a new WorkflowPreprationException with no information.
	 */
	public WorkflowPreprationException() {
		super();
	}

	/**
	 * Constructs a new WorkflowPreprationException with the given message and cause.
	 * @param message  The message explaining the error.
	 * @param cause  The cause of this message.
	 */
	public WorkflowPreprationException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Constructs a new WorkflowPreprationException with the given message.
	 * @param message  The message explaining the error.
	 */
	public WorkflowPreprationException(String message) {
		super(message);
	}

	/**
	 * Constructs a new WorkflowPreprationException with the given cause.
	 * @param cause  The cause of this error.
	 */
	public WorkflowPreprationException(Throwable cause) {
		super(cause);
	}
}
