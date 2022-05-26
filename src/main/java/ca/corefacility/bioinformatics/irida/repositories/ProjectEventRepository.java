package ca.corefacility.bioinformatics.irida.repositories;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import ca.corefacility.bioinformatics.irida.model.event.ProjectEvent;
import ca.corefacility.bioinformatics.irida.model.project.Project;

/**
 * Repository for storing events that occurred on a project
 */
public interface ProjectEventRepository extends IridaJpaRepository<ProjectEvent, Long> {

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
	 * Get the events for a given project
	 *
	 * @param project  The project to get events for
	 * @param pageable the page description for what we should load.
	 * @return A List of {@link ProjectEvent}s
	 */
	@Query("FROM ProjectEvent e WHERE e.project in (?1)")
	public Page<ProjectEvent> getEventsForProjects(List<Project> projects, Pageable pageable);

	/**
	 * Get the events for a given project
	 *
	 * @param project  The project to get events for
	 * @param pageable the page description for what we should load.
	 * @return A List of {@link ProjectEvent}s
	 */
	@Query("FROM ProjectEvent e WHERE e.project in (:projects) AND e.createdDate > :startTime")
	public List<ProjectEvent> getEventsForProjectsAfterDate(final @Param("projects") List<Project> projects,
			final @Param("startTime") Date startTime);

	/**
	 * Get the events for all projects
	 *
	 * @param pageable the page description for what we should load.
	 * @return A List of {@link ProjectEvent}s
	 */
	@Query("FROM ProjectEvent e")
	public Page<ProjectEvent> getAllProjectsEvents(Pageable pageable);
}
