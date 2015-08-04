package ca.corefacility.bioinformatics.irida.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

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
	public SequenceFilePair createSequenceFilePair(SequenceFile file1, SequenceFile file2);

	/**
	 * Get the {@link SequenceFilePair}s associated with a {@link Sample}
	 *
	 * @param sample
	 *            the sample to get pairs for.
	 * @return a List of {@link SequenceFilePair}s
	 */
	public List<SequenceFilePair> getSequenceFilePairsForSample(Sample sample);

	/**
	 * read an individual {@link SequenceFilePair} for a given {@link Sample}
	 * 
	 * @param sample
	 *            The {@link Sample} to read from
	 * @param id
	 *            ID of the pair to read
	 * @return {@link SequenceFilePair}
	 */
	public SequenceFilePair readSequenceFilePairForSample(Sample sample, Long id);

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
	public Map<Sample, SequenceFilePair> getUniqueSamplesForSequenceFilePairs(Set<SequenceFilePair> pairedInputFiles)
			throws DuplicateSampleException;
}
