package ca.corefacility.bioinformatics.irida.service.analysis.storage;

import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;

public interface AnalysisSubmissionSampleService {
	/**
	 * Updates the samples associated with an {@link AnalysisSubmission} to
	 * contain information from the {@link Analysis}.
	 * 
	 * @param analysisSubmission
	 *            The submission to update.
	 */
	public void update(AnalysisSubmission analysisSubmission);
}
