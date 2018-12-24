package ca.corefacility.bioinformatics.irida.pipeline.results.updater;

import java.util.Collection;

import ca.corefacility.bioinformatics.irida.exceptions.PostProcessingException;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.Analysis;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.type.AnalysisType;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;

/**
 * A service for updating samples with results from a particular
 * {@link Analysis} type.
 */
public interface AnalysisSampleUpdater {
	/**
	 * Updates a collection of samples with a particular analysis result.
	 *
	 * @param samples  The samples to update.
	 * @param analysis The {@link AnalysisSubmission} to use for updating.
	 * @throws PostProcessingException if the updater could not complete its processing
	 */
	public void update(Collection<Sample> samples, AnalysisSubmission analysis) throws PostProcessingException;

	/**
	 * Gets the {@link AnalysisType} this updater service handles.
	 * 
	 * @return The {@link AnalysisType}.
	 */
	public AnalysisType getAnalysisType();
}