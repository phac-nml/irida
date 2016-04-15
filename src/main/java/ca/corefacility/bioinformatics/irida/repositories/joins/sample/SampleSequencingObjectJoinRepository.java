package ca.corefacility.bioinformatics.irida.repositories.joins.sample;

import java.util.List;

import org.springframework.data.jpa.repository.Query;

import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sample.SampleSequencingObjectJoin;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequencingObject;
import ca.corefacility.bioinformatics.irida.repositories.IridaJpaRepository;

/**
 * Repository for storing and retrieving {@link SampleSequencingObjectJoin}s
 */
public interface SampleSequencingObjectJoinRepository extends IridaJpaRepository<SampleSequencingObjectJoin, Long> {
	/**
	 * Get the {@link SequenceFile}s associated with a sample
	 * 
	 * @param sample
	 *            The sample to get the files for
	 * @return a list of {@link SampleSequencingObjectJoin} objects
	 */
	@Query("select j from SampleSequencingObjectJoin j where j.sample = ?1")
	public List<SampleSequencingObjectJoin> getSequencesForSample(Sample sample);

	/**
	 * Read a {@link SampleSequencingObjectJoin} with the given {@link Sample}
	 * and {@link SequencingObject} by id
	 * 
	 * @param sample
	 *            {@link Sample} to get the join for
	 * @param sequenceId
	 *            ID of the {@link SequencingObject} to get the join for
	 * @return A {@link SampleSequencingObjectJoin}
	 */
	@Query("from SampleSequencingObjectJoin j where j.sample = ?1 and j.sequencingObject.id = ?2")
	public SampleSequencingObjectJoin readObjectForSample(Sample sample, Long sequenceId);

	/**
	 * Get the {@link SampleSequencingObjectJoin} for a given
	 * {@link SequencingObject}
	 * 
	 * @param seqObject
	 *            the {@link SequencingObject} to get the join for
	 * @return a {@link SampleSequencingObjectJoin}
	 */
	@Query("from SampleSequencingObjectJoin j where j.sequencingObject = ?1")
	public SampleSequencingObjectJoin getSampleForSequencingObject(SequencingObject seqObject);
}
