package ca.corefacility.bioinformatics.irida.exceptions;

/**
 * An exception that gets thrown when attempting to load a workflow.
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 */
public class IridaWorkflowLoadException extends IridaWorkflowException {

	private static final long serialVersionUID = -2054557407529157457L;

	/**
	 * Constructs a new {@link IridaWorkflowLoadException} with the given message and cause.
	 * @param message  The message explaining the error.
	 * @param cause  The cause of this message.
	 */
	public IridaWorkflowLoadException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Constructs a new {@link IridaWorkflowLoadException} with the given message.
	 * @param message  The message explaining the error.
	 */
	public IridaWorkflowLoadException(String message) {
		super(message);
	}
}
