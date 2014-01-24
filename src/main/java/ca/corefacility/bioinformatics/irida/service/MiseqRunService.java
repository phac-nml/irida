package ca.corefacility.bioinformatics.irida.service;

import javax.validation.ConstraintViolationException;
import javax.validation.Valid;

import org.springframework.security.access.prepost.PreAuthorize;

import ca.corefacility.bioinformatics.irida.exceptions.EntityExistsException;
import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.model.MiseqRun;
import ca.corefacility.bioinformatics.irida.model.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.joins.Join;

/**
 * Service layer for MiseqRun objects
 * 
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 */
public interface MiseqRunService extends CRUDService<Long, MiseqRun> {

	/**
	 * {@inheritDoc}
	 */
	// TODO: ROLE_SEQUENCER should **not** have access to read miseq run
	// after they have been created. Revoke this access when sequencing data is
	// uploaded as a single package.
	@PreAuthorize("hasAnyRole('ROLE_SEQUENCER', 'ROLE_USER')")
	public MiseqRun read(Long id) throws EntityNotFoundException;

	/**
	 * {@inheritDoc}
	 */
	@PreAuthorize("hasRole('ROLE_SEQUENCER')")
	public MiseqRun create(@Valid MiseqRun object) throws EntityExistsException, ConstraintViolationException;

	/**
	 * Create a join between a {@link SequenceFile} to a {@link MiseqRun}
	 * 
	 * @param run
	 *            The {@link MiseqRun}
	 * @param file
	 *            The {@link SequenceFile}
	 * @return A {@link Join<MiseqRun, SequenceFile>} describing the
	 *         relationship
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasPermission(#file, 'canReadSequenceFile')")
	public Join<MiseqRun, SequenceFile> addSequenceFileToMiseqRun(MiseqRun run, SequenceFile file);

	/**
	 * Get the {@link MiseqRun} for the given {@link SequenceFile}
	 * 
	 * @param file
	 *            The {@link SequenceFile} for to get the run for
	 * @return A {@link Join<MiseqRun, SequenceFile>} describing the
	 *         relationship between the run and file
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasPermission(#file, 'canReadSequenceFile')")
	public Join<MiseqRun, SequenceFile> getMiseqRunForSequenceFile(SequenceFile file);
}
