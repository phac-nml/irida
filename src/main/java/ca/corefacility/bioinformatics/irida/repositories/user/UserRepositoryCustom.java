package ca.corefacility.bioinformatics.irida.repositories.user;

import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.model.user.User;

import java.util.Date;

/**
 * Custom repository methods for {@link UserRepository}
 */
public interface UserRepositoryCustom {

	/**
	 * Update the last login date for the given {@link User}
	 *
	 * @param user the {@link User} to update
	 * @param date the {@link Date} to set
	 */
	public void updateLogin(User user, Date date);

	/**
	 * Get a user from the database with the supplied email address
	 *
	 * @param email The email address to look up
	 * @return The user with the given email address
	 * @throws EntityNotFoundException if no user can be found with the given email address
	 */
	public User loadUserByEmail(String email) throws EntityNotFoundException;
}
