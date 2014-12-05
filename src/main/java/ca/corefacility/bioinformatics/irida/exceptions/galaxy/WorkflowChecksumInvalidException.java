package ca.corefacility.bioinformatics.irida.exceptions.galaxy;

import ca.corefacility.bioinformatics.irida.exceptions.WorkflowInvalidException;

/**
 * An exception thrown in the case of a mismatch with a workflow checksum.
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 */
public class WorkflowChecksumInvalidException extends WorkflowInvalidException {

	private static final long serialVersionUID = -51034346873552179L;

	/**
	 * Constructs a new WorkflowChecksumInvalidException with no information.
	 */
	public WorkflowChecksumInvalidException() {
		super();
	}

	/**
	 * Constructs a new WorkflowChecksumInvalidException with the given message and cause.
	 * @param message  The message explaining the error.
	 * @param cause  The cause of this message.
	 */
	public WorkflowChecksumInvalidException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Constructs a new WorkflowChecksumInvalidException with the given message.
	 * @param message  The message explaining the error.
	 */
	public WorkflowChecksumInvalidException(String message) {
		super(message);
	}

	/**
	 * Constructs a new WorkflowChecksumInvalidException with the given cause.
	 * @param cause  The cause of this error.
	 */
	public WorkflowChecksumInvalidException(Throwable cause) {
		super(cause);
	}
}
