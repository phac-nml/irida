package ca.corefacility.bioinformatics.irida.repositories.joins.sample;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sample.SampleSequenceFileJoin;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFilePair;

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

	/**
	 * Remove a {@link SequenceFile} from a {@link Sample}
	 * 
	 * @param sample
	 *            The sample to remove the file from
	 * @param file
	 *            The file to remove
	 */
	@Modifying
	@Query("delete from SampleSequenceFileJoin j where j.sample = ?1 and j.sequenceFile = ?2")
	public void removeFileFromSample(Sample sample, SequenceFile file);

	/**
	 * Get {@link SequenceFile}s for a {@link Sample} that do not have a
	 * {@link SequenceFilePair}
	 * 
	 * @param sample
	 *            The Sample to get files for
	 * @return a List of Join<Sample,SequenceFile>
	 */
	@Query("FROM SampleSequenceFileJoin j WHERE j.sample=?1 AND not exists (FROM SequenceFilePair p WHERE j.sequenceFile in elements(p.files))")
	public List<Join<Sample, SequenceFile>> getUnpairedSequenceFilesForSample(Sample sample);
}
