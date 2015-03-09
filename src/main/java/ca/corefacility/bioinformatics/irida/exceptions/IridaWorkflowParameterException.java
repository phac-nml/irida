package ca.corefacility.bioinformatics.irida.exceptions;

/**
 * Defines an exception for parameters within an IRIDA workflow.
 *
 */
public class IridaWorkflowParameterException extends IridaWorkflowException {
	
	private static final long serialVersionUID = -8828296188042330090L;

	/**
	 * Constructs a new {@link IridaWorkflowParameterException} with the given message
	 * and cause.
	 * 
	 * @param message
	 *            The message explaining the error.
	 * @param cause
	 *            The cause of this message.
	 */
	public IridaWorkflowParameterException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Constructs a new {@link IridaWorkflowParameterException} with the given message.
	 * 
	 * @param message
	 *            The message explaining the error.
	 */
	public IridaWorkflowParameterException(String message) {
		super(message);
	}
}
