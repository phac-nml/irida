package ca.corefacility.bioinformatics.irida.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.Query;

import ca.corefacility.bioinformatics.irida.model.event.ProjectEvent;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.user.User;

/**
 * Repository for storing events that occurred on a project
 * 
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 *
 */
public interface ProjectEventRepository extends IridaJpaRepository<ProjectEvent, Long> {

	/**
	 * Get the events for a given project
	 * 
	 * @param project
	 *            The project to get events for
	 * @return A List of {@link ProjectEvent}s
	 */
	@Query("FROM ProjectEvent e WHERE e.project=?1")
	public List<ProjectEvent> getEventsForProject(Project project);

	/**
	 * Get the events on all projects for a given user
	 * 
	 * @param user
	 *            The {@link User} to get events for
	 * @return A List of {@link ProjectEvent}s
	 */
	@Query("SELECT e FROM ProjectEvent e INNER JOIN e.project as p INNER JOIN p.users as u WHERE u.user=?1")
	public List<ProjectEvent> getEventsForUser(User user);
}
