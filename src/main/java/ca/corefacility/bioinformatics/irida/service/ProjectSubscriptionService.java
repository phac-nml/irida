package ca.corefacility.bioinformatics.irida.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;

import ca.corefacility.bioinformatics.irida.model.subscription.ProjectSubscription;
import ca.corefacility.bioinformatics.irida.model.user.User;

/**
 * A specialized service layer for project subscriptions.
 */
public interface ProjectSubscriptionService extends CRUDService<Long, ProjectSubscription> {

	/**
	 * Get a page of project subscriptions associated with a user.
	 *
	 * @param user the user to show project subscriptions for.
	 * @param page the current page of results.
	 * @param size the number of results on the page.
	 * @param sort the sort properties.
	 * @return {@link Page} of {@link ProjectSubscription}s.
	 */
	public Page<ProjectSubscription> getProjectSubscriptionsForUser(User user, int page, int size, Sort sort);

}
