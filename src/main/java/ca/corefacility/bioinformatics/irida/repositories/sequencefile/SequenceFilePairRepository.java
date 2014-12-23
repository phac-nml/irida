package ca.corefacility.bioinformatics.irida.repositories.sequencefile;

import org.springframework.data.jpa.repository.Query;

import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFilePair;
import ca.corefacility.bioinformatics.irida.repositories.IridaJpaRepository;

/**
 * Repository for storing {@link SequenceFilePair}s
 * 
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 *
 */
public interface SequenceFilePairRepository extends IridaJpaRepository<SequenceFilePair, Long> {
	@Query("FROM SequenceFilePair p WHERE ?1 in elements(p.files)")
	public SequenceFilePair getPairForSequenceFile(SequenceFile file);
}
