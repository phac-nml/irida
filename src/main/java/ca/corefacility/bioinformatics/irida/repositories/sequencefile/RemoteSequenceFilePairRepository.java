package ca.corefacility.bioinformatics.irida.repositories.sequencefile;

import ca.corefacility.bioinformatics.irida.model.sequenceFile.RemoteSequenceFilePair;
import ca.corefacility.bioinformatics.irida.repositories.IridaJpaRepository;

/**
 * JPA repository for storing and retrieving {@link RemoteSequenceFilePair}s
 */
public interface RemoteSequenceFilePairRepository extends IridaJpaRepository<RemoteSequenceFilePair, Long> {

}
