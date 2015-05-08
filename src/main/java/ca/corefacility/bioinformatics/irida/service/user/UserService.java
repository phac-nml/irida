package ca.corefacility.bioinformatics.irida.service.user;

import java.util.Collection;
import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.model.enums.ProjectRole;
import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.user.Group;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.service.CRUDService;

/**
 * Customized service for accessing user objects.
 * 
 */
public interface UserService extends CRUDService<Long, User>, UserDetailsService {

	/**
	 * A user is permitted to change their own password if they did not
	 * successfully log in, but the reason for the login failure is that their
	 * credentials are expired. This permission checks to see that the user is
	 * authenticated, or that the principle in the security context has an
	 * expired password.
	 */
	static final String CHANGE_PASSWORD_PERMISSIONS = "isFullyAuthenticated() or "
			+ "(principal instanceof T(ca.corefacility.bioinformatics.irida.model.user.User) and !principal.isCredentialsNonExpired())";

	/**
	 * {@inheritDoc}
	 */
	@PreAuthorize("permitAll")
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;

	/**
	 * Get a user from the database with the supplied username.
	 * 
	 * @param username
	 *            the user's username.
	 * @return the user corresponding to the username.
	 */
	@PreAuthorize("permitAll")
	public User getUserByUsername(String username) throws EntityNotFoundException;

	/**
	 * Get a user from the database with the supplied email address
	 * 
	 * @param email
	 *            The email address to read a user for
	 * @return The user with the given email address
	 * @throws EntityNotFoundException
	 *             If no user has the given email address
	 */
	@PreAuthorize("permitAll")
	public User loadUserByEmail(String email) throws EntityNotFoundException;

	/**
	 * Get all users associated with a particular project.
	 * 
	 * @param project
	 *            the project to get users for.
	 * @return the users associated with the project.
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasPermission(#project, 'canReadProject')")
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
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasPermission(#project, 'canReadProject')")
	public List<User> getUsersAvailableForProject(Project project);

	/**
	 * Get {@link User}s for a {@link Project} that have a particular role
	 * 
	 * @param project
	 *            The project to find users for
	 * @param projectRole
	 *            The {@link ProjectRole} a user needs to have to be returned
	 * @return A Collection of {@code Join<Project,User>}s that have the given
	 *         role
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasPermission(#project, 'canReadProject')")
	public Collection<Join<Project, User>> getUsersForProjectByRole(Project project, ProjectRole projectRole);

	/**
	 * Change the password on the {@link User} account. This method may be
	 * called by a fully-authenticated {@link User}, or by a {@link User} who is
	 * <i>NOT</i> fully-authenticated and {@link User#isCredentialsNonExpired()}
	 * returns <code>false</code>.
	 * 
	 * @param userId
	 *            the identifier of the account to change the password for.
	 * @param password
	 *            the new password for the user account.
	 * @return the user entity with the updated password.
	 */
	@PreAuthorize(CHANGE_PASSWORD_PERMISSIONS)
	public User changePassword(Long userId, String password);

	/**
	 * Get the set of {@link User} that belong to a {@link Group}.
	 * 
	 * @param g
	 *            the {@link Group} to get {@link User} relationships for.
	 * @return the collection of {@link Join} types between {@link User} and the
	 *         specified {@link Group}.
	 * @throws EntityNotFoundException
	 *             if the {@link Group} cannot be found.
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public Collection<Join<User, Group>> getUsersForGroup(Group g) throws EntityNotFoundException;

}
