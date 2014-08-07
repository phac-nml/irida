package ca.corefacility.bioinformatics.irida.repositories;

import ca.corefacility.bioinformatics.irida.model.project.ReferenceFile;

/**
 * Repository for interacting with {@link ReferenceFile}.
 * 
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 *
 */
public interface ReferenceFileRepository extends IridaJpaRepository<ReferenceFile, Long> {

}
