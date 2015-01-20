package ca.corefacility.bioinformatics.irida.service;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;

import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFilePair;

/**
 * Created by josh on 15-01-19.
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
	 * @param file2
	 * @return A new {@link SequenceFilePair} object
	 */
	@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SEQUENCER') or (hasPermission(#file1, 'canReadSequenceFile') and hasPermission(#file2, 'canReadSequenceFile'))")
	public SequenceFilePair createSequenceFilePair(SequenceFile file1, SequenceFile file2);

	/**
	 * Get the {@link SequenceFilePair}s associated with a {@link Sample}
	 *
	 * @param sample
	 * @return a List of {@link SequenceFilePair}s
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasPermission(#sample, 'canReadSample')")
	public List<SequenceFilePair> getSequenceFilePairsForSample(Sample sample);
}
