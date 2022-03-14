package ca.corefacility.bioinformatics.irida.service;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import ca.corefacility.bioinformatics.irida.model.event.ProjectEvent;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.user.User;

/**
 * Service for reading and managing {@link ProjectEvent}s
 */
public interface ProjectEventService extends CRUDService<Long, ProjectEvent> {

	/**
	 * Get the events for a given project
	 *
	 * @param project  The project to get events for
	 * @param pageable The page description
	 * @return A List of {@link ProjectEvent}s
	 */
	public Page<ProjectEvent> getEventsForProject(Project project, Pageable pageable);

	/**
	 * Get the events on all projects for a given user
	 *
	 * @param user     The {@link User} to get events for
	 * @param pageable The page description.
	 * @return A List of {@link ProjectEvent}s
	 */
	public Page<ProjectEvent> getEventsForUser(User user, Pageable pageable);

	/**
	 * Get a list of events for a {@link User} which occurred after the given
	 * date.
	 *
	 * @param user      {@link User} to get events for
	 * @param beginning Get events after this date
	 * @return List of {@link ProjectEvent}
	 */
	public List<ProjectEvent> getEventsForUserAfterDate(User user, Date beginning);

	/**
	 * Get a list of events for all projects
	 *
	 * @param pageable The page description.
	 * @return List of {@link ProjectEvent}
	 */
	public Page<ProjectEvent> getAllProjectsEvents(Pageable pageable);
}
