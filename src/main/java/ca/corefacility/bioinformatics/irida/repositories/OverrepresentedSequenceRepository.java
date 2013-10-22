package ca.corefacility.bioinformatics.irida.repositories;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;

import ca.corefacility.bioinformatics.irida.model.OverrepresentedSequence;
import ca.corefacility.bioinformatics.irida.model.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.joins.Join;

/**
 * 
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 */
public interface OverrepresentedSequenceRepository extends PagingAndSortingRepository<OverrepresentedSequence, Long> {
	/**
	 * Get the {@link OverrepresentedSequence} objects associated with a
	 * {@link SequenceFile}.
	 * 
	 * @param sequenceFile
	 *            the file to get overrepresented sequences for.
	 * @return the collection of overrepresented sequences for the sequence
	 *         file.
	 */
	public List<Join<SequenceFile, OverrepresentedSequence>> getOverrepresentedSequencesForSequenceFile(
			SequenceFile sequenceFile);
}
