package ca.corefacility.bioinformatics.irida.exceptions;

/**
 * Defines an exception in cases of no parameters within an IRIDA workflow.
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 */
public class IridaWorkflowNoParameterException extends IridaWorkflowParameterException {
	
	private static final long serialVersionUID = -8828296188042330090L;

	/**
	 * Constructs a new {@link IridaWorkflowNoParameterException} with the given message
	 * and cause.
	 * 
	 * @param message
	 *            The message explaining the error.
	 * @param cause
	 *            The cause of this message.
	 */
	public IridaWorkflowNoParameterException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Constructs a new {@link IridaWorkflowNoParameterException} with the given message.
	 * 
	 * @param message
	 *            The message explaining the error.
	 */
	public IridaWorkflowNoParameterException(String message) {
		super(message);
	}
}
