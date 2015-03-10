package ca.corefacility.bioinformatics.irida.exceptions;

/**
 * Exception that gets thrown for any Irida workflows.
 * 
 *
 */
public class IridaWorkflowException extends Exception {

	private static final long serialVersionUID = 716423852630929108L;

	/**
	 * Constructs a new {@link IridaWorkflowException} with the given message
	 * and cause.
	 * 
	 * @param message
	 *            The message explaining the error.
	 * @param cause
	 *            The cause of this message.
	 */
	public IridaWorkflowException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Constructs a new {@link IridaWorkflowException} with the given message.
	 * 
	 * @param message
	 *            The message explaining the error.
	 */
	public IridaWorkflowException(String message) {
		super(message);
	}
}
