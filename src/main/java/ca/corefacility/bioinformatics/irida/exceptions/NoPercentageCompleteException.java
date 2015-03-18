package ca.corefacility.bioinformatics.irida.exceptions;

import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;

/**
 * An exception that gets thrown when no percentage complete exists for an
 * {@link AnalysisSubmission}.
 */
public class NoPercentageCompleteException extends WorkflowException {

	private static final long serialVersionUID = -5738673935417424230L;

	/**
	 * Constructs a new NoPercentageComplete with the given message.
	 * 
	 * @param message
	 *            The message explaining the error.
	 */
	public NoPercentageCompleteException(String message) {
		super(message);
	}
}
