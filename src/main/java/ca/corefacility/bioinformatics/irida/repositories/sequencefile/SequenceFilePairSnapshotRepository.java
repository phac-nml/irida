package ca.corefacility.bioinformatics.irida.repositories.sequencefile;

import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFilePairSnapshot;
import ca.corefacility.bioinformatics.irida.repositories.IridaJpaRepository;

/**
 * JPA repository for storing and retrieving {@link SequenceFilePairSnapshot}s
 */
public interface SequenceFilePairSnapshotRepository extends IridaJpaRepository<SequenceFilePairSnapshot, Long> {

}
