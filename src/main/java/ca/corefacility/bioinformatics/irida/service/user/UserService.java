package ca.corefacility.bioinformatics.irida.service.user;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.model.enums.ProjectRole;
import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectUserJoin;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.service.CRUDService;

/**
 * Customized service for accessing user objects.
 * 
 */
public interface UserService extends CRUDService<Long, User>, UserDetailsService {

	/**
	 * {@inheritDoc}
	 */
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;

	/**
	 * Get a user from the database with the supplied username.
	 * 
	 * @param username
	 *            the user's username.
	 * @return the user corresponding to the username.
	 * @throws EntityNotFoundException if the user cannot be found by that username
	 */
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
	public User loadUserByEmail(String email) throws EntityNotFoundException;

	/**
	 * Get all users associated with a particular project.
	 * 
	 * @param project
	 *            the project to get users for.
	 * @return the users associated with the project.
	 */
	public Collection<Join<Project, User>> getUsersForProject(Project project);
	
	/**
	 * Get a page of user accounts on a project filtered by username.
	 * 
	 * @param project
	 *            the project to get users for
	 * @param search
	 *            the string to filter on
	 * @param page
	 *            the current page
	 * @param size
	 *            the size of page
	 * @param sort
	 *            the properties to sort on
	 * @return a page of users.
	 */
	public Page<Join<Project, User>> searchUsersForProject(final Project project, final String search, int page,
			int size, Sort sort);

	/**
	 * Count the number of {@link User}s in a given {@link Project}
	 * 
	 * @param project
	 *            The {@link Project} to count {@link User}s for.
	 * @return Long number of {@link User}s in a {@link Project}
	 */
	public Long countUsersForProject(Project project);

	/**
	 * Get the list of {@link User}s that are not associated with the current
	 * project. This is a convenience method for the front end to see what users
	 * can be added to the project.
	 * 
	 * @param project
	 *            The project we want to list the available users for
	 * @param filter
	 *            the search string to filter usernames on.
	 * @return A List of {@link User}s that are not associated with the project.
	 */
	public List<User> getUsersAvailableForProject(Project project, String filter);

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
	public Collection<Join<Project, User>> getUsersForProjectByRole(Project project, ProjectRole projectRole);
	
	/**
	 * Get a List of all {@link User}s that are subscribed to any
	 * {@link Project}s
	 * 
	 * @return A List of {@link User}
	 */
	public List<User> getUsersWithEmailSubscriptions();

	/**
	 * Update a {@link ProjectUserJoin} to subscribe or unsubscribe a
	 * {@link User} to a given {@link Project}
	 * 
	 * @param user
	 *            the {@link User} to subscribe
	 * @param project
	 *            the {@link Project} to subscribe to
	 * @param subscribed
	 *            whether to subscribe or unsubscribe the user
	 * @return the updated {@link ProjectUserJoin}
	 */
	public ProjectUserJoin updateEmailSubscription(User user, Project project, boolean subscribed);

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
	public User changePassword(Long userId, String password);

	/**
	 * Get count of users logged on during the time period
	 * @return An {@link Long} count of users logged in
	 */
	public Long getUsersLoggedIn(Date createdDate);
}
