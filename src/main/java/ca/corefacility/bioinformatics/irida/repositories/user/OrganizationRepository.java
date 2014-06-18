package ca.corefacility.bioinformatics.irida.repositories.user;

import org.springframework.data.repository.PagingAndSortingRepository;

import ca.corefacility.bioinformatics.irida.model.user.Organization;

/**
 * Repository interface for {@link Organization}.
 * 
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 *
 */
public interface OrganizationRepository extends PagingAndSortingRepository<Organization, Long> {

}
