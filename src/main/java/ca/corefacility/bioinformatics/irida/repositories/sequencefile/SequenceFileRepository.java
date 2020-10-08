package ca.corefacility.bioinformatics.irida.repositories.sequencefile;

import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.repositories.IridaJpaRepository;
import ca.corefacility.bioinformatics.irida.repositories.filesystem.FilesystemSupplementedRepository;

/**
 * A repository to store information about sequence files. This repository will
 * not directly store the file, just metadata
 * 
 */
public interface SequenceFileRepository extends FilesystemSupplementedRepository<SequenceFile>,
		IridaJpaRepository<SequenceFile, Long> {
	/**
	 * {@inheritDoc}
	 *
	 * Save is overridden here instead of in FilesystemSupplementedRepository as it would throw a
	 * compilation error
	 */
	<S extends SequenceFile> S save(S entity);
}
