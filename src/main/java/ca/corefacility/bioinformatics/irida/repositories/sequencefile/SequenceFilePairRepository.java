package ca.corefacility.bioinformatics.irida.repositories.sequencefile;

import java.util.List;

import org.springframework.data.jpa.repository.Query;

import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFilePair;
import ca.corefacility.bioinformatics.irida.repositories.IridaJpaRepository;

/**
 * Repository for storing {@link SequenceFilePair}s
 * 
 *
 */
public interface SequenceFilePairRepository extends IridaJpaRepository<SequenceFilePair, Long> {

	/**
	 * Get the pair for a {@link SequenceFile}
	 * 
	 * @param file
	 *            The file that should be one half of a pair
	 * @return The {@link SequenceFilePair} for the object
	 */
	@Query("FROM SequenceFilePair p WHERE ?1 in elements(p.files)")
	public SequenceFilePair getPairForSequenceFile(SequenceFile file);

	/**
	 * Get the {@link SequenceFilePair}s for a given {@link Sample}
	 * 
	 * @param sample
	 *            the {@link Sample} to get {@link SequenceFilePair}s for
	 * @return the {@link SequenceFilePair}s associated with the {@link Sample}
	 */
	@Query("SELECT distinct p FROM SequenceFilePair p, SampleSequenceFileJoin j WHERE j.sample=?1 AND j.sequenceFile in elements(p.files)")
	public List<SequenceFilePair> getSequenceFilePairsForSample(Sample sample);
}
