package ca.corefacility.bioinformatics.irida.repositories.user;

import ca.corefacility.bioinformatics.irida.model.user.Organization;
import ca.corefacility.bioinformatics.irida.repositories.IridaJpaRepository;

/**
 * Repository interface for {@link Organization}.
 * 
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 *
 */
public interface OrganizationRepository extends IridaJpaRepository<Organization, Long> {

}
