package ca.corefacility.bioinformatics.irida.pipeline.results;

import ca.corefacility.bioinformatics.irida.exceptions.PostProcessingException;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.Analysis;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.type.AnalysisType;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;
import ca.corefacility.bioinformatics.irida.pipeline.results.updater.AnalysisSampleUpdater;

/**
 * Updates samples from an {@link AnalysisSubmission} with results from the
 * analysis.
 */
public interface AnalysisSubmissionSampleProcessor {
	/**
	 * Updates the samples associated with an {@link AnalysisSubmission} to
	 * contain information from the {@link Analysis}.
	 *
	 * @param analysisSubmission The submission to update.
	 * @throws PostProcessingException if a post processing job fails
	 */
	public void updateSamples(AnalysisSubmission analysisSubmission) throws PostProcessingException;

	/**
	 * Whether or not there exists a registered {@link AnalysisSampleUpdater}
	 * for the corresponding {@link AnalysisType}.
	 *
	 * @param analysisType The {@link AnalysisType}.
	 * @return True if there is a registered class for the {@link AnalysisType},
	 * false otherwise.
	 */
	boolean hasRegisteredAnalysisSampleUpdater(AnalysisType analysisType);
}
