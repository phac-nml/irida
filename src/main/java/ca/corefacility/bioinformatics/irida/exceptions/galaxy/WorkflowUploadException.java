package ca.corefacility.bioinformatics.irida.exceptions.galaxy;

import ca.corefacility.bioinformatics.irida.exceptions.ExecutionManagerException;

/**
 * Exception that gets thrown when there is an error uploading a workflow to Galaxy.
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 */
public class WorkflowUploadException extends ExecutionManagerException {

	private static final long serialVersionUID = -1966345755487182884L;

	/**
	 * Constructs a new {@link WorkflowUploadException} with the given message and cause.
	 * @param message  The message explaining the error.
	 * @param cause  The cause of this message.
	 */
	public WorkflowUploadException(String message, Throwable cause) {
		super(message, cause);
	}
}
