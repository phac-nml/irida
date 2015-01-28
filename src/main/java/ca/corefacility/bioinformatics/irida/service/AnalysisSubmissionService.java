package ca.corefacility.bioinformatics.irida.service;

import java.util.Map;

import javax.validation.ConstraintViolationException;
import javax.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.history.Revision;
import org.springframework.data.history.Revisions;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;

import ca.corefacility.bioinformatics.irida.exceptions.EntityExistsException;
import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.exceptions.EntityRevisionDeletedException;
import ca.corefacility.bioinformatics.irida.exceptions.InvalidPropertyException;
import ca.corefacility.bioinformatics.irida.model.enums.AnalysisState;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;

/**
 * A service for AnalysisSubmissions.
 * 
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 */
public interface AnalysisSubmissionService extends CRUDService<Long, AnalysisSubmission> {
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public AnalysisSubmission create(@Valid AnalysisSubmission analysisSubmission) throws EntityExistsException, ConstraintViolationException;

	/**
	 * {@inheritDoc}
	 */
	@Override
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasPermission(#id, 'canReadAnalysisSubmission')")
	public AnalysisSubmission read(Long id) throws EntityNotFoundException;

	/**
	 * {@inheritDoc}
	 */
	@Override
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasPermission(#idents, 'canReadAnalysisSubmission')")
	public Iterable<AnalysisSubmission> readMultiple(Iterable<Long> idents);

	/**
	 * {@inheritDoc}
	 */
	@Override
	@PreAuthorize("hasRole('ROLE_USER')")
	public AnalysisSubmission update(Long id, Map<String, Object> updatedProperties) throws EntityExistsException,
			EntityNotFoundException, ConstraintViolationException, InvalidPropertyException;

	/**
	 * {@inheritDoc}
	 */
	@Override
	@PreAuthorize("hasRole('ROLE_USER')")
	public void delete(Long id) throws EntityNotFoundException;

	/**
	 * {@inheritDoc}
	 */
	@Override
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public Iterable<AnalysisSubmission> findAll();

	/**
	 * {@inheritDoc}
	 */
	@Override
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public Page<AnalysisSubmission> list(int page, int size, Direction order, String... sortProperty) throws IllegalArgumentException;

	/**
	 * {@inheritDoc}
	 */
	@Override
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public Page<AnalysisSubmission> list(int page, int size, Direction order);

	/**
	 * {@inheritDoc}
	 */
	@Override
	@PreAuthorize("hasRole('ROLE_USER')")
	public Boolean exists(Long id);

	/**
	 * {@inheritDoc}
	 */
	@Override
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public long count();

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Page<AnalysisSubmission> search(Specification<AnalysisSubmission> specification, int page, int size, Direction order,
			String... sortProperties);

	/**
	 * {@inheritDoc}
	 */
	@Override
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasPermission(#id, 'canReadAnalysisSubmission')")
	public Revisions<Integer, AnalysisSubmission> findRevisions(Long id) throws EntityRevisionDeletedException;

	/**
	 * {@inheritDoc}
	 */
	@Override
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasPermission(#id, 'canReadAnalysisSubmission')")
	public Page<Revision<Integer, AnalysisSubmission>> findRevisions(Long id, Pageable pageable)
			throws EntityRevisionDeletedException;

	/**
	 * Given an analysis submission id, gets the state of this analysis.
	 * 
	 * @param analysisSubmissionId
	 *            The id of this analysis.
	 * @return The state of this analysis.
	 * @throws EntityNotFoundException
	 *             If the corresponding analysis cannot be found.
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasPermission(#analysisSubmissionId, 'canReadAnalysisSubmission')")
	public AnalysisState getStateForAnalysisSubmission(Long analysisSubmissionId) throws EntityNotFoundException;
}
