package ca.corefacility.bioinformatics.irida.service.analysis.sample;

import java.util.Collection;

import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.Analysis;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;

/**
 * A service for updating samples with results from a particular {@link Analysis} type.
 */
public interface AnalysisSampleUpdatorService {
	/**
	 * Updates a collection of samples with a particular analysis result.
	 * 
	 * @param samples
	 *            The samples to update.
	 * @param analysis
	 *            The {@link AnalysisSubmission} to use for updating.
	 */
	public void update(Collection<Sample> samples, AnalysisSubmission analysis);
}