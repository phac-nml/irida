package ca.corefacility.bioinformatics.irida.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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
	@Query("from ProjectSubscription ps where ps.user = :user")
	public Page<ProjectSubscription> findAllProjectSubscriptionsByUser(@Param("user") User user, Pageable page);

	/**
	 * Find the {@link ProjectSubscription} for the given user and project.
	 *
	 * @param user    the the user associated with the project subscription
	 * @param project the project associated with the project subscription
	 * @return a {@link ProjectSubscription}.
	 */
	@Query("from ProjectSubscription ps where ps.user = ?1 and ps.project = ?2")
	public ProjectSubscription findProjectSubscriptionByUserAndProject(User user, Project project);
}
