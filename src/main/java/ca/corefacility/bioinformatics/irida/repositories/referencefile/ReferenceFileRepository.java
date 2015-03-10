package ca.corefacility.bioinformatics.irida.repositories.referencefile;

import ca.corefacility.bioinformatics.irida.model.project.ReferenceFile;
import ca.corefacility.bioinformatics.irida.repositories.IridaJpaRepository;
import ca.corefacility.bioinformatics.irida.repositories.filesystem.FilesystemSupplementedRepository;

/**
 * Repository for interacting with {@link ReferenceFile}.
 * 
 *
 */
public interface ReferenceFileRepository extends IridaJpaRepository<ReferenceFile, Long>,
		FilesystemSupplementedRepository<ReferenceFile> {

}
