package ca.corefacility.bioinformatics.irida.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import ca.corefacility.bioinformatics.irida.model.event.ProjectEvent;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.user.User;

/**
 * Service for reading and managing {@link ProjectEvent}s
 * 
 *
 */
public interface ProjectEventService extends CRUDService<Long, ProjectEvent> {

	/**
	 * Get the events for a given project
	 * 
	 * @param project
	 *            The project to get events for
	 * @param pageable
	 *            The page description
	 * @return A List of {@link ProjectEvent}s
	 */
	public Page<ProjectEvent> getEventsForProject(Project project, Pageable pageable);

	/**
	 * Get the events on all projects for a given user
	 * 
	 * @param user
	 *            The {@link User} to get events for
	 * @param pageable
	 *            The page description.
	 * @return A List of {@link ProjectEvent}s
	 */
	public Page<ProjectEvent> getEventsForUser(User user, Pageable pageable);

	/**
	 * Get a list of events which occurred after the
	 * {@link User#getLastSubscriptionEmail()}. This method will also ensure
	 * that no events occurred in a cooldown period between the current date and
	 * {@code cooldown} milliseconds ago.
	 * 
	 * @param user
	 *            {@link User} to get events for
	 * @param cooldown
	 *            cooldown period in milliseconds
	 * @return List of {@link ProjectEvent}
	 */
	public List<ProjectEvent> getEventsToEmailToUser(User user, long cooldown);

}
