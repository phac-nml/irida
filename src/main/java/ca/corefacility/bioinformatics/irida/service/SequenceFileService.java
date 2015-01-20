package ca.corefacility.bioinformatics.irida.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.validation.ConstraintViolationException;
import javax.validation.Valid;

import org.springframework.security.access.prepost.PreAuthorize;

import ca.corefacility.bioinformatics.irida.exceptions.EntityExistsException;
import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.exceptions.InvalidPropertyException;
import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.run.SequencingRun;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sample.SampleSequenceFileJoin;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;

/**
 * Service for managing {@link SequenceFile} entities.
 * 
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 */
public interface SequenceFileService extends CRUDService<Long, SequenceFile> {

	/**
	 * {@inheritDoc}
	 */
	@PreAuthorize("hasAnyRole('ROLE_SEQUENCER', 'ROLE_USER')")
	public SequenceFile create(@Valid SequenceFile object) throws EntityExistsException, ConstraintViolationException;

	/**
	 * {@inheritDoc}
	 */
	@PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_SEQUENCER') or hasPermission(#id, 'canReadSequenceFile')")
	public SequenceFile read(Long id) throws EntityNotFoundException;

	/**
	 * Persist the {@link SequenceFile} to the database and create a new
	 * relationship between the {@link SequenceFile} and a {@link Sample}
	 * 
	 * @param sequenceFile
	 *            the {@link SequenceFile} to be persisted.
	 * @param sample
	 *            The sample to add the file to
	 * @return the {@link Join} between the {@link SequenceFile} and its
	 *         {@link Sample}.
	 */
	@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SEQUENCER') or hasPermission(#sample, 'canReadSample')")
	public Join<Sample, SequenceFile> createSequenceFileInSample(SequenceFile sequenceFile, Sample sample);

	/**
	 * Create a pair of {@link SequenceFile}s in a {@link Sample}
	 * 
	 * @param file1
	 *            First {@link SequenceFile}
	 * @param file2
	 *            Second {@link SequenceFile}
	 * @param sample
	 *            The {@link Sample} to add to
	 * @return The created {@link Join}s
	 */
	@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SEQUENCER') or hasPermission(#sample, 'canReadSample')")
	public List<Join<Sample, SequenceFile>> createSequenceFilePairInSample(SequenceFile file1, SequenceFile file2,
			Sample sample);

	/**
	 * Get a {@link List} of {@link SequenceFile} references for a specific
	 * {@link Sample}.
	 * 
	 * @param sample
	 *            the {@link Sample} to get the {@link SequenceFile} references
	 *            from.
	 * @return the references to {@link SequenceFile}.
	 */
	@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SEQUENCER') or hasPermission(#sample, 'canReadSample')")
	public List<Join<Sample, SequenceFile>> getSequenceFilesForSample(Sample sample);

	/**
	 * Read a {@link SequenceFile} that exists in a given {@link Sample}
	 * 
	 * @param sample
	 *            The {@link Sample} to read from
	 * @param identifier
	 *            The {@link SequenceFile} ID
	 * @return a {@link SequenceFile}
	 * @throws EntityNotFoundException
	 *             if the file doesn't exist in the sample
	 */
	@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SEQUENCER') or hasPermission(#sample, 'canReadSample')")
	public Join<Sample, SequenceFile> getSequenceFileForSample(Sample sample, Long identifier)
			throws EntityNotFoundException;

	/**
	 * Get a {@link List} of {@link SequenceFile} references for a specific
	 * {@link SequencingRun}.
	 * 
	 * @param sequencingRun
	 *            the {@link SequencingRun} to get the {@link SequenceFile}
	 *            references from.
	 * @return the references to {@link SequenceFile}.
	 */
	public Set<SequenceFile> getSequenceFilesForSequencingRun(SequencingRun sequencingRun);

	/**
	 * {@inheritDoc}
	 */
	@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SEQUENCER') or hasPermission(#id, 'canReadSequenceFile')")
	public SequenceFile update(Long id, Map<String, Object> updatedFields) throws InvalidPropertyException;

	/**
	 * Get the {@link SequenceFile}s that do not have pairs for a {@link Sample}
	 * 
	 * @param sample
	 * @return A List of {@link SampleSequenceFileJoin}s
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasPermission(#sample, 'canReadSample')")
	public List<Join<Sample, SequenceFile>> getUnpairedSequenceFilesForSample(Sample sample);
}
