package ca.corefacility.bioinformatics.irida.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.security.access.prepost.PreAuthorize;

import ca.corefacility.bioinformatics.irida.model.workflow.analysis.Analysis;

/**
 * Service for managing objects of type {@link Analysis}.
 * 
 *
 */
public interface AnalysisService extends CRUDService<Long, Analysis> {
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
