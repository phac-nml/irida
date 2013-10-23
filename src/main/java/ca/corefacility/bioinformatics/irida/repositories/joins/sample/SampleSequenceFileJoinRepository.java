package ca.corefacility.bioinformatics.irida.repositories.joins.sample;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import ca.corefacility.bioinformatics.irida.model.Sample;
import ca.corefacility.bioinformatics.irida.model.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.joins.impl.SampleSequenceFileJoin;

/**
 * Repository for managing {@link SampleSequenceFileJoin}.
 * 
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 * 
 */
public interface SampleSequenceFileJoinRepository extends CrudRepository<SampleSequenceFileJoin, Long> {
	/**
	 * Get the {@link Sample} that owns the {@link SequenceFile}.
	 * 
	 * @param sequenceFile
	 *            the file to find the {@link Sample} for.
	 * @return the {@link Sample} that owns the file.
	 */
	@Query("select j from SampleSequenceFileJoin j where j.sequenceFile = ?1")
	public Join<Sample, SequenceFile> getSampleForSequenceFile(SequenceFile sequenceFile);
	

	/**
	 * Get the {@link SequenceFile}s associated with a sample
	 * 
	 * @param sample
	 *            The sample to get the files for
	 * @return a list of {@link SampleSequenceFileJoin} objects
	 */
	@Query("select j from SampleSequenceFileJoin j where j.sample = ?1")
	public List<Join<Sample, SequenceFile>> getFilesForSample(Sample sample);
}
