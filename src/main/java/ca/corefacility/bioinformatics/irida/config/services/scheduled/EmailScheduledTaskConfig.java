package ca.corefacility.bioinformatics.irida.config.services.scheduled;

import ca.corefacility.bioinformatics.irida.service.ProjectEventEmailScheduledTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * Scheduled taks configuration for sending digest emails
 */
@Profile({ "prod", "email" })
@Configuration
public class EmailScheduledTaskConfig {

	@Autowired
	private ProjectEventEmailScheduledTask eventEmailTask;

	/**
	 * Check for any new events for users who are subscribed to projects and
	 * email them
	 */
	@Scheduled(cron = "${irida.scheduled.subscription.cron}")
	public void emailProjectEvents() {
		eventEmailTask.emailUserTasks();
	}
}
