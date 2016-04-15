package ca.corefacility.bioinformatics.irida.repositories.sequencefile;

import ca.corefacility.bioinformatics.irida.model.sequenceFile.SingleEndSequenceFileSnapshot;
import ca.corefacility.bioinformatics.irida.repositories.IridaJpaRepository;

/**
 * Repository for storing and retrieving {@link SingleEndSequenceFileSnapshot}s
 */
public interface SingleEndSequenceFileSnapshotRepository extends
		IridaJpaRepository<SingleEndSequenceFileSnapshot, Long> {

}
