package ca.corefacility.bioinformatics.irida.repositories;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import ca.corefacility.bioinformatics.irida.model.event.ProjectEvent;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.user.User;

/**
 * Repository for storing events that occurred on a project
 */
public interface ProjectEventRepository extends IridaJpaRepository<ProjectEvent, Long> {

	/**
	 * Query to get events for the specified user
	 */
	static final String GET_EVENTS_FOR_USER =
			"SELECT e FROM ProjectEvent e INNER JOIN e.project as p WHERE (" + ProjectRepository.USER_ON_PROJECT
					+ " or " + ProjectRepository.USER_IN_GROUP + ")";

	/**
	 * Get the events for a given project
	 *
	 * @param project  The project to get events for
	 * @param pageable the page description for what we should load.
	 * @return A List of {@link ProjectEvent}s
	 */
	@Query("FROM ProjectEvent e WHERE e.project=?1")
	public Page<ProjectEvent> getEventsForProject(Project project, Pageable pageable);

	/**
	 * Get the events for all projects
	 *
	 * @param pageable the page description for what we should load.
	 * @return A List of {@link ProjectEvent}s
	 */
	@Query("FROM ProjectEvent e")
	public Page<ProjectEvent> getAllProjectsEvents(Pageable pageable);

	/**
	 * Get the events on all projects for a given user
	 *
	 * @param user     The {@link User} to get events for
	 * @param pageable the page description for what we should load.
	 * @return A List of {@link ProjectEvent}s
	 */
	@Query(GET_EVENTS_FOR_USER)
	public Page<ProjectEvent> getEventsForUser(final @Param("forUser") User user, Pageable pageable);

	/**
	 * Get all {@link ProjectEvent}s for a given {@link User} that occurred
	 * after a given {@link Date}
	 *
	 * @param user      The {@link User} to get events for
	 * @param startTime The {@link Date} to get events after
	 * @return a List of {@link ProjectEvent}s
	 */
	@Query(GET_EVENTS_FOR_USER + " AND e.createdDate > :startTime")
	public List<ProjectEvent> getEventsForUserAfterDate(final @Param("forUser") User user,
			final @Param("startTime") Date startTime);
}
