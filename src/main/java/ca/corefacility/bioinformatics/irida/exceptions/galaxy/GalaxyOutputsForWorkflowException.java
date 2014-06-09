package ca.corefacility.bioinformatics.irida.exceptions.galaxy;

import ca.corefacility.bioinformatics.irida.exceptions.WorkflowException;

/**
 * An exception in getting outputs from Galaxy for a workflow.
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 */
public class GalaxyOutputsForWorkflowException extends WorkflowException {

	private static final long serialVersionUID = 5281884851300317788L;

	/**
	 * Constructs a new NoGalaxyOutputsForWorkflowException with no information.
	 */
	public GalaxyOutputsForWorkflowException() {
		super();
	}

	/**
	 * Constructs a new NoGalaxyOutputsForWorkflowException with the given message and cause.
	 * @param message  The message explaining the error.
	 * @param cause  The cause of this message.
	 */
	public GalaxyOutputsForWorkflowException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Constructs a new NoGalaxyOutputsForWorkflowException with the given message.
	 * @param message  The message explaining the error.
	 */
	public GalaxyOutputsForWorkflowException(String message) {
		super(message);
	}

	/**
	 * Constructs a new NoGalaxyOutputsForWorkflowException with the given cause.
	 * @param cause  The cause of this error.
	 */
	public GalaxyOutputsForWorkflowException(Throwable cause) {
		super(cause);
	}
}
