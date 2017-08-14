package ca.corefacility.bioinformatics.irida.service.analysis.storage;

import java.util.Collection;

import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.Analysis;

/**
 * A service for updating samples with results from a particular analysis.
 */
public interface AnalysisSampleUpdatorService {
	/**
	 * Updates a collection of samples with a particular analysis result.
	 * 
	 * @param samples
	 *            The samples to update.
	 * @param analysis
	 *            The analysis to use for updating.
	 */
	public void update(Collection<Sample> samples, Analysis analysis);
}