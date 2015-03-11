package ca.corefacility.bioinformatics.irida.service;

import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.security.access.prepost.PreAuthorize;

import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.Analysis;

/**
 * Service for managing objects of type {@link Analysis}.
 * 
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
	 * @param <T>
	 *            the type of analysis to get.
	 * @return the {@link Analysis} objects created for the {@link SequenceFile}
	 * 
	 */
	public <T extends Analysis> Set<T> getAnalysesForSequenceFile(SequenceFile file, Class<T> analysisType);

	/**
	 * {@inheritDoc}
	 */
	// TODO: remove this permission when analysis has user permissions.
	@PreAuthorize("hasRole('ROLE_USER')")
	public Page<Analysis> list(int page, int size, Direction order, String... sortProperty)
			throws IllegalArgumentException;

	/**
	 * {@inheritDoc}
	 */
	// TODO: remove this permission when analysis has user permissions.
	@PreAuthorize("hasRole('ROLE_USER')")
	public Page<Analysis> list(int page, int size, Direction order);
}
