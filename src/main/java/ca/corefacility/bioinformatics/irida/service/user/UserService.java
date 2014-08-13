package ca.corefacility.bioinformatics.irida.service.user;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.validation.ConstraintViolationException;
import javax.validation.Valid;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import ca.corefacility.bioinformatics.irida.exceptions.EntityExistsException;
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
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 */
public interface UserService extends CRUDService<Long, User>, UserDetailsService {

	/**
	 * If a user is an administrator, they are permitted to update any user
	 * property. If a manager or user is updating an account, they should not be
	 * permitted to change the role of the user (only administrators can create
	 * users with role other than Role.ROLE_USER).
	 */
	static final String UPDATE_USER_PERMISSIONS = "hasRole('ROLE_ADMIN') or "
			+ "(!#properties.containsKey('systemRole') and hasPermission(#uid, 'canUpdateUser'))";

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
	@PreAuthorize(CHANGE_PASSWORD_PERMISSIONS)
	public User changePassword(Long userId, String password);

	/**
	 * {@inheritDoc}
	 */
	@PreAuthorize(CREATE_USER_PERMISSIONS)
	public User create(@Valid User u) throws EntityExistsException, ConstraintViolationException;

	/**
	 * If a user is an administrator, they are permitted to create a user
	 * account with any role. If a user is a manager, then they are only
	 * permitted to create user accounts with a ROLE_USER role.
	 */
	static final String CREATE_USER_PERMISSIONS = "hasRole('ROLE_ADMIN') or "
			+ "((#u.getSystemRole() == T(ca.corefacility.bioinformatics.irida.model.user.Role).ROLE_USER) and hasRole('ROLE_MANAGER'))";

	/**
	 * {@inheritDoc}
	 */
	@PreAuthorize(UPDATE_USER_PERMISSIONS)
	public User update(Long uid, Map<String, Object> properties);

	/**
	 * {@inheritDoc}
	 */
	@PreAuthorize("hasRole('ROLE_MANAGER')")
	public void delete(Long id);

	/**
	 * {@inheritDoc}
	 */
	@PreAuthorize("hasRole('ROLE_USER')")
	public Iterable<User> findAll();

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
	public Collection<Join<User, Group>> getUsersForGroup(Group g) throws EntityNotFoundException;

}
