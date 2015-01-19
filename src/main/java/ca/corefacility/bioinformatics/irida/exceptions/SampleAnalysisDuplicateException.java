package ca.corefacility.bioinformatics.irida.exceptions;

import ca.corefacility.bioinformatics.irida.exceptions.galaxy.WorkflowUploadException;

/**
 * If there is a duplicate {@link Sample} which is being sent for analysis.
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 */
public class SampleAnalysisDuplicateException extends WorkflowUploadException {
	
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
	 * Construct a new {@link SampleAnalysisDuplicateException} with the specified message and
	 * original cause.
	 * 
	 * @param message
	 * @param cause
	 */
	public SampleAnalysisDuplicateException(String message, Throwable cause) {
		super(message, cause);
	}
}
