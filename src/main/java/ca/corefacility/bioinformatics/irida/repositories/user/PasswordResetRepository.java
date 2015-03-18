package ca.corefacility.bioinformatics.irida.repositories.user;

import ca.corefacility.bioinformatics.irida.model.user.PasswordReset;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.repositories.IridaJpaRepository;

/**
 * A repository to store password resets for a user.
 * 
 */
public interface PasswordResetRepository extends IridaJpaRepository<PasswordReset, String> {

	/**
	 * Find any existing PasswordResets for the specified user.
	 *
	 * @param user
	 *            The {@link User} to find existing PasswordReset for.
	 * @return the {@link PasswordReset} for the {@link User}.
	 */
	public PasswordReset findByUser(User user);
}
