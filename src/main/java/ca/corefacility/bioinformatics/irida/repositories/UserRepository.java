package ca.corefacility.bioinformatics.irida.repositories;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.security.core.userdetails.UserDetailsService;

import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.model.Project;
import ca.corefacility.bioinformatics.irida.model.User;
import ca.corefacility.bioinformatics.irida.model.enums.ProjectRole;
import ca.corefacility.bioinformatics.irida.model.joins.Join;

/**
 * Specialized repository for {@link User}.
 * 
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 */
public interface UserRepository extends PagingAndSortingRepository<User, Long>, UserDetailsService {

	/**
	 * Get a user from the database with the supplied username.
	 * 
	 * @param username
	 *            the user's username.
	 * @return the user corresponding to the username.
	 * @throws EntityNotFoundException
	 *             If no user can be found with the supplied username.
	 */
	@Query("select u from User where u.username = ?1")
	public User loadUserByUsername(String username) throws EntityNotFoundException;

	/**
	 * Get all {@link User}s associated with a project.
	 * 
	 * @param project
	 *            the {@link Project} to get {@link User}s for.
	 * @return A Collection of {@link Join<Project,User>}s describing users for
	 *         this project
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

}
