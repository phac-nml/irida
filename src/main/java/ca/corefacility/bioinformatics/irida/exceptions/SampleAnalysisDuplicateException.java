package ca.corefacility.bioinformatics.irida.exceptions;

import ca.corefacility.bioinformatics.irida.model.sample.Sample;

/**
 * If there is a duplicate {@link Sample} which is being sent for analysis.
 *
 */
public class SampleAnalysisDuplicateException extends WorkflowPreprationException {
	
	private static final long serialVersionUID = -432490135272603894L;

	/**
	 * Construct a new {@link SampleAnalysisDuplicateException} with the specified message.
	 *
	 * @param message
	 *            the message explaining the exception.
	 */
	public SampleAnalysisDuplicateException(String message) {
		super(message);
	}

	/**
	 * Construct a new {@link SampleAnalysisDuplicateException} with the
	 * specified message and original cause.
	 * 
	 * @param message
	 *            the message explaining the exception.
	 * @param cause
	 *            the original cause of the exception.
	 */
	public SampleAnalysisDuplicateException(String message, Throwable cause) {
		super(message, cause);
	}
}
