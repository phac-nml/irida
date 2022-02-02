package ca.corefacility.bioinformatics.irida.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

import ca.corefacility.bioinformatics.irida.model.subscription.ProjectSubscription;
import ca.corefacility.bioinformatics.irida.model.user.User;

/**
 * Repository for storing, removing, and retrieving {@link ProjectSubscription}s
 */
public interface ProjectSubscriptionRepository extends IridaJpaRepository<ProjectSubscription, Long> {

	/**
	 * Find all the {@link ProjectSubscription} for the given user.
	 *
	 * @param user the user assiated with project subscriptions
	 * @param page the page request
	 * @return a page of {@link ProjectSubscription}.
	 */
	@Query("from ProjectSubscription ps where ps.user = ?1")
	public Page<ProjectSubscription> findAllProjectSubscriptionsByUser(User user, Pageable page);
}
