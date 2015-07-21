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

@Component
public class ProjectEventEmailScheduledTaskImpl implements ProjectEventEmailScheduledTask {

	private static final Logger logger = LoggerFactory.getLogger(ProjectEventEmailScheduledTaskImpl.class);

	@Autowired
	UserService userService;

	@Autowired
	ProjectEventService eventService;

	Long cooldown = 60000L;

	@Override
	public void emailUserTasks() {
		List<User> usersWithEmailSubscriptions = userService.getUsersWithEmailSubscriptions();

		for (User u : usersWithEmailSubscriptions) {
			checkForNewEvents(u);
		}
	}

	private void checkForNewEvents(User user) {
		logger.debug("Getting events for " + user.getUsername());

		List<ProjectEvent> eventsToEmailToUser = eventService.getEventsToEmailToUser(user, cooldown);

		if (!eventsToEmailToUser.isEmpty()) {
			for (ProjectEvent e : eventsToEmailToUser) {
				logger.debug("Event: " + e.getLabel());
			}

			userService.update(user.getId(), ImmutableMap.of("lastSubscriptionEmail", new Date()));
		}
	}

}
