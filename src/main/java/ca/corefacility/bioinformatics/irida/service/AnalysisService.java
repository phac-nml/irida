package ca.corefacility.bioinformatics.irida.service;

import java.util.Set;

import ca.corefacility.bioinformatics.irida.model.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.Analysis;

/**
 * Service for managing objects of type {@link Analysis}.
 * 
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 *
 */
public interface AnalysisService extends CRUDService<Long, Analysis> {

	/**
	 * Get all types of {@link Analysis} generated for a specific
	 * {@link SequenceFile}.
	 * 
	 * @param file
	 *            the {@link SequenceFile} to get {@link Analysis} objects for.
	 * @return the {@link Analysis} objects created for the {@link SequenceFile}
	 *         .
	 */
	public Set<Analysis> getAnalysesForSequenceFile(SequenceFile file);

	/**
	 * Get all types of {@link Analysis} generated for a specific
	 * {@link SequenceFile} with a specific subtype of {@link Analysis}.
	 * 
	 * @param file
	 *            the {@link SequenceFile} to get {@link Analysis} objects for.
	 * @param analysisType
	 *            the specific subtype of {@link Analysis} to load.
	 * @return the {@link Analysis} objects created for the {@link SequenceFile}
	 *         .
	 */
	public <T extends Analysis> Set<T> getAnalysesForSequenceFile(SequenceFile file, Class<T> analysisType);
}
