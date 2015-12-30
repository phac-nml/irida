package ca.corefacility.bioinformatics.irida.repositories.joins.sample;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sample.SampleSequencingObjectJoin;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;

/**
 * Repository for storing and retrieving {@link SampleSequencingObjectJoin}s
 */
public interface SampleSequencingObjectJoinRepository extends CrudRepository<SampleSequencingObjectJoin, Long> {
	/**
	 * Get the {@link SequenceFile}s associated with a sample
	 * 
	 * @param sample
	 *            The sample to get the files for
	 * @return a list of {@link SampleSequencingObjectJoin} objects
	 */
	@Query("select j from SampleSequencingObjectJoin j where j.sample = ?1")
	public List<SampleSequencingObjectJoin> getSequencesForSample(Sample sample);
}
