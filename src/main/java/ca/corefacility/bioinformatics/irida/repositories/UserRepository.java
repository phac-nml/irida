package ca.corefacility.bioinformatics.irida.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.history.RevisionRepository;
import org.springframework.security.core.userdetails.UserDetailsService;

import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.model.Project;
import ca.corefacility.bioinformatics.irida.model.User;

/**
 * Specialized repository for {@link User}.
 * 
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 */
public interface UserRepository extends PagingAndSortingRepository<User, Long>, UserDetailsService,
		RevisionRepository<User, Long, Integer> {

	/**
	 * Get a user from the database with the supplied username.
	 * 
	 * @param username
	 *            the user's username.
	 * @return the user corresponding to the username.
	 * @throws EntityNotFoundException
	 *             If no user can be found with the supplied username.
	 */
	@Query("select u from User u where u.username = ?1")
	public User loadUserByUsername(String username) throws EntityNotFoundException;

	/**
	 * Get the list of {@link User}s that are not associated with the current
	 * project. This is a convenience method for the front end to see what users
	 * can be added to the project.
	 * 
	 * @param project
	 *            The project we want to list the available users for
	 * @return A List of {@link User}s that are not associated with the project.
	 */
	@Query("SELECT u FROM User u WHERE u NOT IN (SELECT f from ProjectUserJoin p JOIN p.user f WHERE p.project=?1)")
	public List<User> getUsersAvailableForProject(Project project);
}
