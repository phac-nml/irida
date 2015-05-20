package ca.corefacility.bioinformatics.irida.repositories.sequencefile;

import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFileSnapshot;
import ca.corefacility.bioinformatics.irida.repositories.IridaJpaRepository;
import ca.corefacility.bioinformatics.irida.repositories.filesystem.FilesystemSupplementedRepository;

/**
 * Repository for storing and retrieving {@link SequenceFileSnapshot}s
 */
public interface SequenceFileSnapshotRepository extends FilesystemSupplementedRepository<SequenceFileSnapshot>,
		IridaJpaRepository<SequenceFileSnapshot, Long> {

}
