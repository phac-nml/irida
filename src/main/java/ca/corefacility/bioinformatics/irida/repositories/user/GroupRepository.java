package ca.corefacility.bioinformatics.irida.repositories.user;

import ca.corefacility.bioinformatics.irida.model.user.Group;
import ca.corefacility.bioinformatics.irida.repositories.IridaJpaRepository;

/**
 * Repository for managing {@link Group} objects.
 * 
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 *
 */
public interface GroupRepository extends IridaJpaRepository<Group, Long> {

}
