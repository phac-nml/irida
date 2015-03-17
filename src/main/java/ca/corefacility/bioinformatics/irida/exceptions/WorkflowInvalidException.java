package ca.corefacility.bioinformatics.irida.exceptions;


/**
 * An exception thrown if a workflow is invalid.
 *
 */
public class WorkflowInvalidException extends WorkflowException {

	private static final long serialVersionUID = -3946466932639734867L;

	/**
	 * Constructs a new WorkflowInvalidException with no information.
	 */
	public WorkflowInvalidException() {
		super();
	}

	/**
	 * Constructs a new WorkflowInvalidException with the given message and cause.
	 * @param message  The message explaining the error.
	 * @param cause  The cause of this message.
	 */
	public WorkflowInvalidException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Constructs a new WorkflowInvalidException with the given message.
	 * @param message  The message explaining the error.
	 */
	public WorkflowInvalidException(String message) {
		super(message);
	}

	/**
	 * Constructs a new WorkflowInvalidException with the given cause.
	 * @param cause  The cause of this error.
	 */
	public WorkflowInvalidException(Throwable cause) {
		super(cause);
	}
}
