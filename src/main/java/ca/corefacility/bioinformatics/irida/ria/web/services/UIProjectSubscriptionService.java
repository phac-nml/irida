package ca.corefacility.bioinformatics.irida.ria.web.services;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import ca.corefacility.bioinformatics.irida.model.enums.ProjectRole;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectUserJoin;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.subscription.ProjectSubscription;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.model.user.group.UserGroupProjectJoin;
import ca.corefacility.bioinformatics.irida.ria.web.exceptions.UIEntityNotFoundException;
import ca.corefacility.bioinformatics.irida.ria.web.models.tables.TableRequest;
import ca.corefacility.bioinformatics.irida.ria.web.models.tables.TableResponse;
import ca.corefacility.bioinformatics.irida.ria.web.users.dto.UserProjectDetailsModel;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.ProjectSubscriptionService;
import ca.corefacility.bioinformatics.irida.service.user.UserService;

/**
 * A utility class for formatting responses for the project subscriptions page UI.
 */
@Component
public class UIProjectSubscriptionService {
	private final ProjectSubscriptionService projectSubscriptionService;
	private final UserService userService;
	private final ProjectService projectService;
	private final MessageSource messageSource;

	@Autowired
	public UIProjectSubscriptionService(ProjectSubscriptionService projectSubscriptionService, UserService userService,
			ProjectService projectService, MessageSource messageSource) {
		this.projectSubscriptionService = projectSubscriptionService;
		this.userService = userService;
		this.projectService = projectService;
		this.messageSource = messageSource;
	}

	/**
	 * Update an existing project subscription
	 *
	 * @param id        - the identifier of the {@link ProjectSubscription}
	 * @param subscribe - whether to subscribe or unsubscribe the user to/from the project
	 * @param locale    - {@link Locale} of the current user.
	 * @return a message to user about the result of the update
	 * @throws UIEntityNotFoundException if there is an error updating the project subscription
	 */
	@Transactional
	public String updateProjectSubscription(Long id, boolean subscribe, Locale locale)
			throws UIEntityNotFoundException {
		String message = null;
		ProjectSubscription projectSubscription = null;

		try {
			projectSubscription = projectSubscriptionService.read(id);
		} catch (Exception e) {
			throw new UIEntityNotFoundException(
					messageSource.getMessage("server.UserProjectsPage.notification.error", new Object[] {}, locale));
		}

		projectSubscription.setEmailSubscription(subscribe);
		if (subscribe) {
			message = messageSource.getMessage("server.UserProjectsPage.subscribe.notification.success",
					new Object[] { projectSubscription.getProject().getName() }, locale);
		} else {
			message = messageSource.getMessage("server.UserProjectsPage.unsubscribe.notification.success",
					new Object[] { projectSubscription.getProject().getName() }, locale);
		}

		return message;
	}

	/**
	 * Get all the project subscriptions associated with a user
	 *
	 * @param userId       - the id for the user to show project subscriptions for
	 * @param tableRequest - details about the current page of the table requested
	 * @return all project subscriptions for a specific user
	 */
	public TableResponse<UserProjectDetailsModel> getProjectSubscriptionsForUser(Long userId,
			TableRequest tableRequest) {
		User user = userService.read(userId);

		Page<ProjectSubscription> page = projectSubscriptionService.getProjectSubscriptionsForUser(user,
				tableRequest.getCurrent(), tableRequest.getPageSize(), tableRequest.getSort());

		List<UserProjectDetailsModel> projectSubscriptions = page.getContent().stream().map(projectSubscription -> {
			User subscriptionUser = projectSubscription.getUser();
			Project subscriptionProject = projectSubscription.getProject();
			ProjectUserJoin projectUserJoin = projectService.getProjectUserJoin(subscriptionUser, subscriptionProject);
			Collection<UserGroupProjectJoin> userGroupProjectJoins = projectService.getUserGroupProjectJoins(
					subscriptionUser, subscriptionProject);
			return new UserProjectDetailsModel(projectSubscription,
					ProjectRole.getMaxRoleForProjectsAndGroups(projectUserJoin, userGroupProjectJoins));
		}).collect(Collectors.toList());
		return new TableResponse<>(projectSubscriptions, page.getTotalElements());
	}
}
