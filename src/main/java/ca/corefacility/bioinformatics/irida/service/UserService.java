package ca.corefacility.bioinformatics.irida.service;

import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.model.Project;
import ca.corefacility.bioinformatics.irida.model.User;
import ca.corefacility.bioinformatics.irida.model.enums.ProjectRole;
import ca.corefacility.bioinformatics.irida.model.joins.Join;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Collection;
import java.util.List;

/**
 * Customized service for accessing user objects.
 * 
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 */
public interface UserService extends CRUDService<Long, User>, UserDetailsService {

	/**
	 * Get a user from the database with the supplied username.
	 * 
	 * @param username
	 *            the user's username.
	 * @return the user corresponding to the username.
	 */
	public User getUserByUsername(String username) throws EntityNotFoundException;

	/**
	 * Get all users associated with a particular project.
	 * 
	 * @param project
	 *            the project to get users for.
	 * @return the users associated with the project.
	 */
	public Collection<Join<Project, User>> getUsersForProject(Project project);

	/**
	 * Get the list of {@link User}s that are not associated with the current
	 * project. This is a convenience method for the front end to see what users
	 * can be added to the project.
	 * 
	 * @param project
	 *            The project we want to list the available users for
	 * @return A List of {@link User}s that are not associated with the project.
	 */
	public List<User> getUsersAvailableForProject(Project project);

	/**
	 * Get {@link User}s for a {@link Project} that have a particular role
	 * 
	 * @param project
	 *            The project to find users for
	 * @param projectRole
	 *            The {@link ProjectRole} a user needs to have to be returned
	 * @return A Collection of {@link Join<Project,User>}s that have the given
	 *         role
	 */
	public Collection<Join<Project, User>> getUsersForProjectByRole(Project project, ProjectRole projectRole);

	/**
	 * Change the password on the {@link User} account. This method may be
	 * called by a fully-authenticated {@link User} (though they should just
	 * call {@link UserService#update(Long, java.util.Map)}), or by a
	 * {@link User} who is <i>NOT</i> fully-authenticated and
	 * {@link User#isCredentialsNonExpired()} returns <code>false</code>.
	 * 
	 * @param userId
	 *            the identifier of the account to change the password for.
	 * @param password
	 *            the new password for the user account.
	 * @return the user entity with the updated password.
	 */
	public User changePassword(Long userId, String password);
}
