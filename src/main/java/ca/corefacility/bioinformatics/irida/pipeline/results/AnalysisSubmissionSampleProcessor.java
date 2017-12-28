package ca.corefacility.bioinformatics.irida.pipeline.results;

import ca.corefacility.bioinformatics.irida.model.enums.AnalysisType;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;

/**
 * Updates samples from an {@link AnalysisSubmission} with results from the
 * analysis.
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

	/**
	 * Whether or not there exists a registered {@link AnalysisSampleUpdater}
	 * for the corresponding {@link AnalysisType}.
	 * 
	 * @param analysisType
	 *            The {@link AnalysisType}.
	 * @return True if there is a registered class for the {@link AnalysisType},
	 *         false otherwise.
	 */
	boolean hasRegisteredAnalysisSampleUpdater(AnalysisType analysisType);
}
