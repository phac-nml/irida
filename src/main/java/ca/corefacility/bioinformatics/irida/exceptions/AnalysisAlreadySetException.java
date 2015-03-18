package ca.corefacility.bioinformatics.irida.exceptions;

import ca.corefacility.bioinformatics.irida.model.workflow.analysis.Analysis;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;

/**
 * This exception is thrown when
 * {@link AnalysisSubmission#setAnalysis(Analysis)} is called, but the instance
 * of {@link AnalysisSubmission} already has a non-{@code null} instance of an
 * {@link Analysis} assigned to it.
 *
 */
public class AnalysisAlreadySetException extends Exception {

	/**
	 * Create a new instance of {@link AnalysisAlreadySetException}.
	 * 
	 * @param message the message explaining the exception.
	 */
	public AnalysisAlreadySetException(final String message) {
		super(message);
	}
}
