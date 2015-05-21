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
	 * Read a {@link SequenceFile} for a {@link Sample}. Used to get the join
	 * object for a file and sample.
	 * 
	 * @param sample
	 *            The sample to get the join for
	 * @param file
	 *            The file to get the join for
	 * @return a {@link SampleSequenceFileJoin}
	 */
	@Query("from SampleSequenceFileJoin j where j.sample = ?1 and j.sequenceFile = ?2")
	public SampleSequenceFileJoin getJoinForSampleAndFile(Sample sample, SequenceFile file);

	/**
	 * Get {@link SequenceFile}s for a {@link Sample} that do not have a
	 * {@link SequenceFilePair}
	 * 
	 * @param sample
	 *            The Sample to get files for
	 * @return a List of {@link SampleSequenceFileJoin}
	 */
	@Query("FROM SampleSequenceFileJoin j WHERE j.sample=?1 AND not exists (FROM SequenceFilePair p WHERE j.sequenceFile in elements(p.files))")
	public List<Join<Sample, SequenceFile>> getUnpairedSequenceFilesForSample(Sample sample);
}
