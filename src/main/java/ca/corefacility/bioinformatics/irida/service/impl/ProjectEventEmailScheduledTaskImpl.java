package ca.corefacility.bioinformatics.irida.service.impl;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.collect.ImmutableMap;

import ca.corefacility.bioinformatics.irida.model.event.ProjectEvent;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.service.ProjectEventEmailScheduledTask;
import ca.corefacility.bioinformatics.irida.service.ProjectEventService;
import ca.corefacility.bioinformatics.irida.service.user.UserService;

/**
 * Implementation of {@link ProjectEventEmailScheduledTask} which sends emails
 * to users when they have new events
 */
@Component
public class ProjectEventEmailScheduledTaskImpl implements ProjectEventEmailScheduledTask {

	private static final Logger logger = LoggerFactory.getLogger(ProjectEventEmailScheduledTaskImpl.class);

	UserService userService;

	ProjectEventService eventService;

	EmailController emailController;

	Long cooldown = 10 * 60 * 1000L; // 10 minutes

	@Autowired
	public ProjectEventEmailScheduledTaskImpl(UserService userService, ProjectEventService eventService,
			EmailController emailController) {
		super();
		this.userService = userService;
		this.eventService = eventService;
		this.emailController = emailController;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void emailUserTasks() {
		logger.trace("Checking for users with subscriptions");
		List<User> usersWithEmailSubscriptions = userService.getUsersWithEmailSubscriptions();

		for (User user : usersWithEmailSubscriptions) {
			logger.trace("Checking for events for user " + user.getUsername());
			List<ProjectEvent> eventsToEmailToUser = eventService.getEventsToEmailToUser(user, cooldown);

			if (!eventsToEmailToUser.isEmpty()) {
				logger.debug("Sending " + eventsToEmailToUser.size() + " to user.");
				emailController.sendSubscriptionUpdateEmail(user, eventsToEmailToUser);

				userService.update(user.getId(), ImmutableMap.of("lastSubscriptionEmail", new Date()));
			}
		}
	}

}
