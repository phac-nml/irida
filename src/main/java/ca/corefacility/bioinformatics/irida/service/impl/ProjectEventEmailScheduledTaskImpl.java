package ca.corefacility.bioinformatics.irida.service.impl;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.support.CronSequenceGenerator;
import org.springframework.stereotype.Component;

import ca.corefacility.bioinformatics.irida.model.event.ProjectEvent;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectUserJoin;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.service.EmailController;
import ca.corefacility.bioinformatics.irida.service.ProjectEventEmailScheduledTask;
import ca.corefacility.bioinformatics.irida.service.ProjectEventService;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
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

	ProjectService projectService;

	@Value("${irida.scheduled.subscription.cron}")
	private String scheduledCronString = "0 0 0 * * *";

	@Autowired
	public ProjectEventEmailScheduledTaskImpl(UserService userService, ProjectEventService eventService,
			ProjectService projectService, EmailController emailController) {
		super();
		this.userService = userService;
		this.eventService = eventService;
		this.projectService = projectService;
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

			Date lastTime = getPriorDateFromCronString(scheduledCronString);

			logger.trace("Getting events after " + lastTime);

			for (User user : usersWithEmailSubscriptions) {
				logger.trace("Checking for events for user " + user.getUsername());
				List<ProjectEvent> eventsToEmailToUser = eventService.getEventsForUserAfterDate(user, lastTime);

				// Get the set of projects the user is subscribed to
				Set<Project> projectsWithSubscription = projectService.getProjectsForUser(user).stream().filter(j -> {
					ProjectUserJoin puj = (ProjectUserJoin) j;
					return puj.isEmailSubscription();
				}).map(j -> j.getSubject()).collect(Collectors.toSet());

				// filter the events to ensure the user is subscribed
				eventsToEmailToUser = eventsToEmailToUser.stream()
						.filter(e -> projectsWithSubscription.contains(e.getProject())).collect(Collectors.toList());

				if (!eventsToEmailToUser.isEmpty()) {
					logger.trace("Sending subscription email to " + user.getUsername() + " with "
							+ eventsToEmailToUser.size() + " events");
					emailController.sendSubscriptionUpdateEmail(user, eventsToEmailToUser);
				}
			}
		}
	}

	/**
	 * Get the last time the job was run from the given cron string
	 *
	 * @param cron the cron string
	 * @return A Date of the last time the job was run
	 */
	public static Date getPriorDateFromCronString(String cron) {
		// find the number of milliseconds in the configured time
		long timeInMillis = Calendar.getInstance().getTimeInMillis();
		CronSequenceGenerator gen = new CronSequenceGenerator(cron);
		long futureTime = gen.next(new Date()).getTime();
		long difference = futureTime - timeInMillis;

		return new Date(timeInMillis - difference);
	}

	/**
	 * Ge the cron string for this scheduled task
	 *
	 * @return the cron string
	 */
	public String getScheduledCronString() {
		return scheduledCronString;
	}
}
