package ca.corefacility.bioinformatics.irida.repositories.user;

import org.springframework.data.repository.PagingAndSortingRepository;

import ca.corefacility.bioinformatics.irida.model.user.Group;

/**
 * Repository for managing {@link Group} objects.
 * 
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 *
 */
public interface GroupRepository extends PagingAndSortingRepository<Group, Long> {

}
