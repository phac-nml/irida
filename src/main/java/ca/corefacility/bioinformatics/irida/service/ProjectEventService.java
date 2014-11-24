package ca.corefacility.bioinformatics.irida.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import ca.corefacility.bioinformatics.irida.model.event.ProjectEvent;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.user.User;

/**
 * Service for reading and managing {@link ProjectEvent}s
 * 
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 *
 */
public interface ProjectEventService extends CRUDService<Long, ProjectEvent> {

	/**
	 * Get the events for a given project
	 * 
	 * @param project
	 *            The project to get events for
	 * @param max
	 *            The maximum number of events to show
	 * @return A List of {@link ProjectEvent}s
	 */
	public Page<ProjectEvent> getEventsForProject(Project project, Pageable pageable);

	/**
	 * Get the events on all projects for a given user
	 * 
	 * @param user
	 *            The {@link User} to get events for
	 * @param max
	 *            The maximum number of events to show
	 * @return A List of {@link ProjectEvent}s
	 */
	public Page<ProjectEvent> getEventsForUser(User user, Pageable pageable);

	/**
	 * Get the 10 most recent events for a given {@link Project}.
	 * 
	 * @param project
	 *            The {@link Project} to get events for
	 * @return A page of the (at most) 10 most recent events
	 * @see ProjectEventService#getEventsForProject(Project, Pageable)
	 */
	public default Page<ProjectEvent> getLastTenEventsForProject(Project project) {
		return getEventsForProject(project, new PageRequest(0, 10));
	}

	/**
	 * Get the 10 most recent events for a given {@link User}.
	 * 
	 * @param user
	 *            The {@link User} to get events for
	 * @return A page of the (at most) 10 most recent events
	 * @see ProjectEventService#getEventsForUser(User, Pageable)
	 */
	public default Page<ProjectEvent> getLastTenEventsForUser(User user) {
		return getEventsForUser(user, new PageRequest(0, 10));
	}
}
