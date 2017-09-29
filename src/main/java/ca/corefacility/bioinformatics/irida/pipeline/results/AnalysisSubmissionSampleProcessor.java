package ca.corefacility.bioinformatics.irida.pipeline.results;

import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;

/**
 * Updates samples from an {@link AnalysisSubmission} with results from the analysis.
 */
public interface AnalysisSubmissionSampleProcessor {
	/**
	 * Updates the samples associated with an {@link AnalysisSubmission} to
	 * contain information from the {@link Analysis}.
	 * 
	 * @param analysisSubmission
	 *            The submission to update.
	 */
	public void updateSamples(AnalysisSubmission analysisSubmission);
}
