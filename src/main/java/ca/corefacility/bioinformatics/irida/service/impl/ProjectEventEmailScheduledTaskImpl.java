package ca.corefacility.bioinformatics.irida.service.impl;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.support.CronSequenceGenerator;
import org.springframework.stereotype.Component;

import ca.corefacility.bioinformatics.irida.model.event.ProjectEvent;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.service.EmailController;
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
	
	@Value("${irida.scheduled.subscription.cron}")
	private String scheduledCronString;

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
		if (emailController.isMailConfigured()) {
			logger.trace("Checking for users with subscriptions");
			List<User> usersWithEmailSubscriptions = userService.getUsersWithEmailSubscriptions();
						
			// find the number of milliseconds in the configured time
			long timeInMillis = Calendar.getInstance().getTimeInMillis();
			CronSequenceGenerator gen = new CronSequenceGenerator(scheduledCronString);
			long futureTime = gen.next(new Date()).getTime();
			long difference = futureTime - timeInMillis;

			Date lastTime = new Date(timeInMillis - difference);
			
			logger.trace("Getting events after " + lastTime);

			for (User user : usersWithEmailSubscriptions) {
				logger.trace("Checking for events for user " + user.getUsername());
				List<ProjectEvent> eventsToEmailToUser = eventService.getEventsForUserAfterDate(user, lastTime);

				if (!eventsToEmailToUser.isEmpty()) {
					logger.trace("Sending subscription email to " + user.getUsername() + " with "
							+ eventsToEmailToUser.size() + " events");
					emailController.sendSubscriptionUpdateEmail(user, eventsToEmailToUser);
				}
			}
		}
	}

}
