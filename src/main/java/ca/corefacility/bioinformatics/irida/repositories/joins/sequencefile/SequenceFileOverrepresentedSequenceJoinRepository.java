package ca.corefacility.bioinformatics.irida.repositories.joins.sequencefile;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import ca.corefacility.bioinformatics.irida.model.OverrepresentedSequence;
import ca.corefacility.bioinformatics.irida.model.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.joins.impl.SequenceFileOverrepresentedSequenceJoin;

/**
 * Repository for managing {@link SequenceFileOverrepresentedSequenceJoin}.
 * 
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 * 
 */
public interface SequenceFileOverrepresentedSequenceJoinRepository extends
		CrudRepository<SequenceFileOverrepresentedSequenceJoin, Long> {
	/**
	 * Get the {@link OverrepresentedSequence} objects associated with a
	 * {@link SequenceFile}.
	 * 
	 * @param sequenceFile
	 *            the file to get overrepresented sequences for.
	 * @return the collection of overrepresented sequences for the sequence
	 *         file.
	 */
	@Query("select j from SequenceFileOverrepresentedSequenceJoin j where j.sequenceFile = ?1")
	public List<Join<SequenceFile, OverrepresentedSequence>> getOverrepresentedSequencesForSequenceFile(
			SequenceFile sequenceFile);
}
