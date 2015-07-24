package ca.corefacility.bioinformatics.irida.service;

import ca.corefacility.bioinformatics.irida.model.event.ProjectEvent;
import ca.corefacility.bioinformatics.irida.model.user.User;

/**
 * Scheduled task for emailing new {@link ProjectEvent}s to {@link User}s
 */
public interface ProjectEventEmailScheduledTask {
	// run daily
	public static String CRON_STRING = "0 0 0 * * *";

	/**
	 * Task that checks for new events to mail to users then fires an email.
	 * This method should be made to run once per day.
	 */
	public void emailUserTasks();

}
