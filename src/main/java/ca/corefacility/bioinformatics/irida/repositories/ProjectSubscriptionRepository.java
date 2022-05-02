package ca.corefacility.bioinformatics.irida.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.subscription.ProjectSubscription;
import ca.corefacility.bioinformatics.irida.model.user.User;

/**
 * Repository for storing, removing, and retrieving {@link ProjectSubscription}s
 */
public interface ProjectSubscriptionRepository extends IridaJpaRepository<ProjectSubscription, Long> {

	/**
	 * Find all the {@link ProjectSubscription} for the given user.
	 *
	 * @param user the user associated with project subscriptions
	 * @param page the page request
	 * @return a page of {@link ProjectSubscription}.
	 */
	@Query("from ProjectSubscription ps where ps.user = ?1")
	public Page<ProjectSubscription> findAllProjectSubscriptionsByUser(User user, Pageable page);

	/**
	 * Find the {@link ProjectSubscription} for the given user and project.
	 *
	 * @param user    the the user associated with the project subscription
	 * @param project the project associated with the project subscription
	 * @return a {@link ProjectSubscription}.
	 */
	@Query("from ProjectSubscription ps where ps.user = ?1 and ps.project = ?2")
	public ProjectSubscription findProjectSubscriptionByUserAndProject(User user, Project project);

	/**
	 * Get a list of all {@link User}s who are subscribed to any {@link Project}.
	 *
	 * @return A List of {@link User}
	 */
	@Query("Select distinct ps.user from ProjectSubscription ps where ps.emailSubscription = true")
	public List<User> getUsersWithSubscriptions();

	/**
	 * Get a List of all {@link Project}s that a given {@link User} is subscribed to.
	 *
	 * @param user the user to show project subscriptions for
	 * @return A List of {@link Project}s
	 */
	@Query("Select ps.project from ProjectSubscription ps where ps.user = ?1 and ps.emailSubscription = true")
	public List<Project> getProjectsForUserWithSubscriptions(User user);
}
