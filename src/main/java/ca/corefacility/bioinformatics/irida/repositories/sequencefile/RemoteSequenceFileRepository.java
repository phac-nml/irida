package ca.corefacility.bioinformatics.irida.repositories.sequencefile;

import ca.corefacility.bioinformatics.irida.model.sequenceFile.RemoteSequenceFile;
import ca.corefacility.bioinformatics.irida.repositories.IridaJpaRepository;
import ca.corefacility.bioinformatics.irida.repositories.filesystem.FilesystemSupplementedRepository;

/**
 * Repository for storing and retrieving {@link RemoteSequenceFile}s
 */
public interface RemoteSequenceFileRepository extends FilesystemSupplementedRepository<RemoteSequenceFile>,
		IridaJpaRepository<RemoteSequenceFile, Long> {

}
