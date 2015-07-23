package ca.corefacility.bioinformatics.irida.service;

import ca.corefacility.bioinformatics.irida.model.event.ProjectEvent;
import ca.corefacility.bioinformatics.irida.model.user.User;

/**
 * Scheduled task for emailing new {@link ProjectEvent}s to {@link User}s
 */
public interface ProjectEventEmailScheduledTask {
	/**
	 * Task that checks for new events to mail to users then fires an email
	 */
	public void emailUserTasks();
}
