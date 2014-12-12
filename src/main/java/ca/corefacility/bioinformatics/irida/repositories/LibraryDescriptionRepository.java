package ca.corefacility.bioinformatics.irida.repositories;

import ca.corefacility.bioinformatics.irida.model.project.library.LibraryDescription;

/**
 * Repository for managing {@link LibraryDescription}.
 * 
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 *
 */
public interface LibraryDescriptionRepository extends IridaJpaRepository<LibraryDescription, Long> {

}
