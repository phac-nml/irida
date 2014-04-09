package ca.corefacility.bioinformatics.irida.repositories;

import org.springframework.data.repository.PagingAndSortingRepository;

import ca.corefacility.bioinformatics.irida.model.PasswordReset;

/**
 * A repository to store password resets for a user.
 * 
 * @author Josh Adam <josh.adam@phac-aspc.gc.ca>
 */
public interface PasswordResetRepository extends PagingAndSortingRepository<PasswordReset, Long> {
}
