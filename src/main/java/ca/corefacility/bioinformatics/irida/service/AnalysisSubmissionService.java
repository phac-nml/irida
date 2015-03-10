package ca.corefacility.bioinformatics.irida.service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import ca.corefacility.bioinformatics.irida.model.project.ReferenceFile;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFilePair;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.model.workflow.IridaWorkflow;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.IridaWorkflowNamedParameters;

/**
 * A service for AnalysisSubmissions.
 * 
 *
 */
public interface AnalysisSubmissionService extends CRUDService<Long, AnalysisSubmission> {
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	@PreAuthorize("hasRole('ROLE_USER')")
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
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public AnalysisSubmission update(Long id, Map<String, Object> updatedProperties) throws EntityExistsException,
			EntityNotFoundException, ConstraintViolationException, InvalidPropertyException;

	/**
	 * {@inheritDoc}
	 */
	@Override
	@PreAuthorize("hasRole('ROLE_ADMIN')")
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
	@PreAuthorize("hasRole('ROLE_ADMIN')")
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
	
	/**
	 * Gets a {@link Set} of {@link AnalysisSubmission}s for a {@link User}.
	 * 
	 * @param user
	 *            The {@link User} to find all submissions for.
	 * @return A {@link Set} of {@link AnalysisSubmission}s for a user.
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN') or authentication.name == #user.username")
	public Set<AnalysisSubmission> getAnalysisSubmissionsForUser(User user);

	/**
	 * Gets a {@link Set} of {@link AnalysisSubmission}s for the current
	 * {@link User}.
	 * 
	 * @return A {@link Set} of {@link AnalysisSubmission}s for the current
	 *         user.
	 */
	@PreAuthorize("hasRole('ROLE_USER')")
	public Set<AnalysisSubmission> getAnalysisSubmissionsForCurrentUser();
	
	/**
	 * Submit {@link AnalysisSubmission} for workflows allowing multiple one
	 * {@link SequenceFile} or {@link SequenceFilePair}
	 *
	 * @param workflow
	 *            {@link IridaWorkflow} that the files will be run on
	 * @param ref
	 *            {@link Long} id for a {@link ReferenceFile}
	 * @param sequenceFiles
	 *            {@link List} of {@link SequenceFile} to run on the workflow
	 * @param sequenceFilePairs
	 *            {@link List} of {@link SequenceFilePair} to run on the
	 *            workflow
	 * @param unnamedParameters
	 *            {@link Map} of parameters specific for the pipeline
	 * @param namedParameters
	 *            the named parameters to use for the workflow.
	 * @param name
	 *            {@link String} the name for the analysis
	 */
	@PreAuthorize("hasRole('ROLE_USER')")
	public AnalysisSubmission createMultipleSampleSubmission(IridaWorkflow workflow, Long ref,
			List<SequenceFile> sequenceFiles, List<SequenceFilePair> sequenceFilePairs,
			Map<String, String> unnamedParameters, IridaWorkflowNamedParameters namedParameters, String name);

	/**
	 * Submit {@link AnalysisSubmission} for workflows requiring only one
	 * {@link SequenceFile} or {@link SequenceFilePair}
	 *
	 * @param workflow
	 *            {@link IridaWorkflow} that the files will be run on
	 * @param ref
	 *            {@link Long} id for a {@link ReferenceFile}
	 * @param sequenceFiles
	 *            {@link List} of {@link SequenceFile} to run on the workflow
	 * @param sequenceFilePairs
	 *            {@link List} of {@link SequenceFilePair} to run on the
	 *            workflow
	 * @param unnamedParameters
	 *            {@link Map} of parameters specific for the pipeline
	 * @param namedParameters
	 *            the named parameters to use for the workflow.
	 * @param name
	 *            {@link String} the name for the analysis
	 */
	@PreAuthorize("hasRole('ROLE_USER')")
	public Collection<AnalysisSubmission> createSingleSampleSubmission(IridaWorkflow workflow, Long ref,
			List<SequenceFile> sequenceFiles, List<SequenceFilePair> sequenceFilePairs,
			Map<String, String> unnamedParameters, IridaWorkflowNamedParameters namedParameters, String name);
}
