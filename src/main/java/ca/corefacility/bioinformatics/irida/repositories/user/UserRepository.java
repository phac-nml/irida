package ca.corefacility.bioinformatics.irida.repositories.user;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.repositories.IridaJpaRepository;
import ca.corefacility.bioinformatics.irida.ria.web.admin.dto.statistics.GenericStatModel;

/**
 * Specialized repository for {@link User}.
 * 
 */
public interface UserRepository extends IridaJpaRepository<User, Long>, UserDetailsService, UserRepositoryCustom {

	/**
	 * Get a user from the database with the supplied username.
	 * 
	 * @param username
	 *            the user's username.
	 * @return the user corresponding to the username.
	 * @throws UsernameNotFoundException
	 *             If no user can be found with the supplied username.
	 */
	public User loadUserByUsername(String username) throws UsernameNotFoundException;

	/**
	 * Get the list of {@link User}s that are not associated with the current
	 * project. This is a convenience method for the front end to see what users
	 * can be added to the project.
	 *
	 * @param project The project we want to list the available users for
	 * @param term    A search term for a user's first or last name
	 * @return A List of {@link User}s that are not associated with the project.
	 */
	@Query("SELECT u FROM User u WHERE u NOT IN (SELECT f from ProjectUserJoin p JOIN p.user f WHERE p.project=?1) and (CONCAT(u.firstName, ' ', u.lastName) like %?2% or u.username like %?2% or u.email like %?2%)")
	public List<User> getUsersAvailableForProject(Project project, String term);

	/**
	 * Get a count of all {@link User}s logged in within time period
	 *
	 * @return a count of {@link User}s
	 */
	@Query("select count(u.id) from User u where u.lastLogin >= ?1")
	public Long countUsersLoggedInTimePeriod(Date createdDate);

	/**
	 * Get a count of all {@link User}s created in within time period
	 *
	 * @return a count of {@link User}s
	 */
	@Query("select count(u.id) from User u where u.createdDate >= ?1")
	public Long countUsersCreatedInTimePeriod(Date createdDate);

	/**
	 * Get a list of {@link GenericStatModel}s for users created in the last day and grouped by hour
	 *
	 * @param createdDate The minimum created date for users
	 * @return A list of {@link GenericStatModel}s
	 */
	@Query("select new ca.corefacility.bioinformatics.irida.ria.web.admin.dto.statistics.GenericStatModel(function('date_format', u.createdDate, '%H:00'), count(u.id))"
			+ "from User u where u.createdDate >= ?1 group by function('date_format', u.createdDate, '%H')")
	public List<GenericStatModel> countUsersCreatedHourly(Date createdDate);

	/**
	 * Get a list of {@link GenericStatModel}s for users created in the past 30 days and grouped by month and day
	 *
	 * @param createdDate The minimum created date for users
	 * @return A list of {@link GenericStatModel}s
	 */
	@Query("select new ca.corefacility.bioinformatics.irida.ria.web.admin.dto.statistics.GenericStatModel(function('date_format', u.createdDate, '%m/%d'), count(u.id))"
			+ "from User u where u.createdDate >= ?1 group by function('date_format', u.createdDate, '%m/%d')")
	public List<GenericStatModel> countUsersCreatedDaily(Date createdDate);

	/**
	 * Get a list of {@link GenericStatModel}s for users created in the past 365 days and grouped by month and year
	 *
	 * @param createdDate The minimum created date for users
	 * @return A list of {@link GenericStatModel}s
	 */
	@Query("select new ca.corefacility.bioinformatics.irida.ria.web.admin.dto.statistics.GenericStatModel(function('date_format', u.createdDate, '%m/%y'), count(u.id))"
			+ "from User u where u.createdDate >= ?1 group by function('date_format', u.lastLogin, '%m/%y')")
	public List<GenericStatModel> countUsersCreatedMonthly(Date createdDate);

	/**
	 * Get a list of {@link GenericStatModel}s for users created in the past 2,5 and 10 years and grouped by year
	 *
	 * @param createdDate The minimum created date for users
	 * @return A list of {@link GenericStatModel}s
	 */
	@Query("select new ca.corefacility.bioinformatics.irida.ria.web.admin.dto.statistics.GenericStatModel(function('date_format', u.createdDate, '%Y'), count(u.id))"
			+ "from User u where u.createdDate >= ?1 group by function('date_format', u.createdDate, '%Y')")
	public List<GenericStatModel> countUsersCreatedYearly(Date createdDate);
}
