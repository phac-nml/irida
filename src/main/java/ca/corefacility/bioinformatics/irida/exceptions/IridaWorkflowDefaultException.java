package ca.corefacility.bioinformatics.irida.exceptions;

/**
 * Exception that gets thrown when attempting to set a default workflow for an
 * analysis type that already has a default workflow set.
 * 
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 */
public class IridaWorkflowDefaultException extends IridaWorkflowException {

	private static final long serialVersionUID = -4902782284860573991L;

	/**
	 * Constructs a new {@link IridaWorkflowDefaultException} with the given
	 * message and cause.
	 * 
	 * @param message
	 *            The message explaining the error.
	 * @param cause
	 *            The cause of this message.
	 */
	public IridaWorkflowDefaultException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Constructs a new {@link IridaWorkflowDefaultException} with the given
	 * message.
	 * 
	 * @param message
	 *            The message explaining the error.
	 */
	public IridaWorkflowDefaultException(String message) {
		super(message);
	}
}
