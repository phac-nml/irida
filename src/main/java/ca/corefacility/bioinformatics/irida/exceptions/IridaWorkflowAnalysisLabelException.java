package ca.corefacility.bioinformatics.irida.exceptions;

/**
 * Exception that gets thrown when there are issues defining a label for an analysis output file.
 *
 */
public class IridaWorkflowAnalysisLabelException extends IridaWorkflowException {

	/**
	 * Constructs a new {@link IridaWorkflowAnalysisLabelException} with the given message
	 * and cause.
	 * 
	 * @param message
	 *            The message explaining the error.
	 * @param cause
	 *            The cause of this message.
	 */
	public IridaWorkflowAnalysisLabelException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Constructs a new {@link IridaWorkflowAnalysisLabelException} with the given message.
	 * 
	 * @param message
	 *            The message explaining the error.
	 */
	public IridaWorkflowAnalysisLabelException(String message) {
		super(message);
	}

}
