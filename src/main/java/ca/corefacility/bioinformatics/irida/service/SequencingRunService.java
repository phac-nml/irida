package ca.corefacility.bioinformatics.irida.service;

import java.util.Map;

import javax.validation.ConstraintViolationException;
import javax.validation.Valid;

import org.springframework.security.access.prepost.PreAuthorize;

import ca.corefacility.bioinformatics.irida.exceptions.EntityExistsException;
import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.exceptions.InvalidPropertyException;
import ca.corefacility.bioinformatics.irida.model.run.SequencingRun;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;

/**
 * Service layer for SequencingRun objects
 * 
 */
public interface SequencingRunService extends CRUDService<Long, SequencingRun> {

	/**
	 * {@inheritDoc}
	 */
	// TODO: ROLE_SEQUENCER should **not** have access to read sequencing run
	// after they have been created. Revoke this access when sequencing data is
	// uploaded as a single package.
	@PreAuthorize("hasAnyRole('ROLE_SEQUENCER', 'ROLE_USER')")
	public SequencingRun read(Long id) throws EntityNotFoundException;

	/**
	 * {@inheritDoc}
	 */
	@Override
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public Iterable<SequencingRun> findAll();

	/**
	 * {@inheritDoc}
	 */
	@PreAuthorize("hasRole('ROLE_SEQUENCER')")
	public SequencingRun create(@Valid SequencingRun object) throws EntityExistsException, ConstraintViolationException;

	/**
	 * Create a join between a {@link SequenceFile} to a {@link SequencingRun}
	 * 
	 * @param run
	 *            The {@link SequencingRun}
	 * @param file
	 *            The {@link SequenceFile}
	 */
	@PreAuthorize("hasRole('ROLE_SEQUENCER')")
	public void addSequenceFileToSequencingRun(SequencingRun run, SequenceFile file);

	/**
	 * Get the {@link SequencingRun} for the given {@link SequenceFile}
	 * 
	 * @param file
	 *            The {@link SequenceFile} for to get the run for
	 * @return A SequencingRun for the file
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasPermission(#file, 'canReadSequenceFile')")
	public SequencingRun getSequencingRunForSequenceFile(SequenceFile file);

	/**
	 * Deletes a {@link SequencingRun} and cascades the delete to any empty
	 * {@link Sample}s. If a Sample is empty after the delete, it will also be
	 * deleted.
	 * 
	 * @param id
	 *            The ID of the {@link SequencingRun} to delete.
	 * @throws EntityNotFoundException
	 *             If a {@link SequencingRun} with this ID doesn't exist
	 */
	@Override
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public void delete(Long id) throws EntityNotFoundException;

	/**
	 * {@inheritDoc}
	 */
	@PreAuthorize("hasRole('ROLE_SEQUENCER')")
	@Override
	public SequencingRun update(Long id, Map<String, Object> updatedProperties) throws EntityExistsException,
			EntityNotFoundException, ConstraintViolationException, InvalidPropertyException;
}
