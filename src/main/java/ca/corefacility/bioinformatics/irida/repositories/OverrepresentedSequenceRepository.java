package ca.corefacility.bioinformatics.irida.repositories;

import java.util.Set;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import ca.corefacility.bioinformatics.irida.model.OverrepresentedSequence;
import ca.corefacility.bioinformatics.irida.model.SequenceFile;

/**
 * 
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 */
public interface OverrepresentedSequenceRepository extends PagingAndSortingRepository<OverrepresentedSequence, Long> {

	/**
	 * Load the set of {@link OverrepresentedSequence} for a
	 * {@link SequenceFile}.
	 * 
	 * @param sf
	 *            the file to load {@link OverrepresentedSequence} records for.
	 * @return the set of {@link OverrepresentedSequence} records.
	 */
	@Query("select os from OverrepresentedSequence os where os.sequenceFile = ?1")
	public Set<OverrepresentedSequence> findOverrepresentedSequencesForSequenceFile(SequenceFile sf);

}
