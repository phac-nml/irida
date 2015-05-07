package ca.corefacility.bioinformatics.irida.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.security.access.prepost.PreAuthorize;

import ca.corefacility.bioinformatics.irida.exceptions.DuplicateSampleException;
import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFilePair;

/**
 * Service for managing {@link SequenceFilePair} entities.
 *
 */
public interface SequenceFilePairService extends CRUDService<Long, SequenceFilePair> {

	/**
	 * Get the paired {@link SequenceFile} for the given {@link SequenceFile}
	 *
	 * @param file
	 *            One side of the file pair
	 * @return The other side of the file pair
	 * @throws EntityNotFoundException
	 *             If a pair cannot be found
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasPermission(#file, 'canReadSequenceFile')")
	public SequenceFile getPairedFileForSequenceFile(SequenceFile file) throws EntityNotFoundException;

	/**
	 * Create a new {@link SequenceFilePair} for the given files
	 *
	 * @param file1
	 *            the first file in the pair.
	 * @param file2
	 *            the second file in the pairl
	 * @return A new {@link SequenceFilePair} object
	 */
	@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SEQUENCER') or (hasPermission(#file1, 'canReadSequenceFile') and hasPermission(#file2, 'canReadSequenceFile'))")
	public SequenceFilePair createSequenceFilePair(SequenceFile file1, SequenceFile file2);

	/**
	 * Get the {@link SequenceFilePair}s associated with a {@link Sample}
	 *
	 * @param sample
	 *            the sample to get pairs for.
	 * @return a List of {@link SequenceFilePair}s
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasPermission(#sample, 'canReadSample')")
	public List<SequenceFilePair> getSequenceFilePairsForSample(Sample sample);

	/**
	 * Gets a map of {@link SequenceFilePair}s and corresponding {@link Sample}
	 * s.
	 *
	 * @param pairedInputFiles
	 *            A {@link Set} of {@link SequenceFilePair}s.
	 * @return A {@link Map} of between {@link Sample} and
	 *         {@link SequenceFilePair}.
	 * @throws DuplicateSampleException
	 *             If there is a duplicate sample.
	 */
	@PreAuthorize("hasAnyRole('ROLE_ADMIN') or hasPermission(#pairedInputFiles, 'canReadSequenceFile')")
	public Map<Sample, SequenceFilePair> getUniqueSamplesForSequenceFilePairs(Set<SequenceFilePair> pairedInputFiles)
			throws DuplicateSampleException;
}
