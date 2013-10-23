package ca.corefacility.bioinformatics.irida.repositories.joins;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import ca.corefacility.bioinformatics.irida.model.Project;
import ca.corefacility.bioinformatics.irida.model.User;
import ca.corefacility.bioinformatics.irida.model.enums.ProjectRole;
import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectUserJoin;

/**
 * A repository for {@link ProjectUserJoin}.
 * 
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 * 
 */
public interface ProjectUserJoinRepository extends CrudRepository<ProjectUserJoin, Long> {
	/**
	 * Get all {@link User}s associated with a project.
	 * 
	 * @param project
	 *            the {@link Project} to get {@link User}s for.
	 * @return A Collection of {@link Join<Project,User>}s describing users for
	 *         this project
	 */
	@Query("select j from ProjectUserJoin j where j.project = ?1")
	public List<Join<Project, User>> getUsersForProject(Project project);

	/**
	 * Get {@link User}s for a {@link Project} that have a particular role
	 * 
	 * @param project
	 *            The project to find users for
	 * @param projectRole
	 *            The {@link ProjectRole} a user needs to have to be returned
	 * @return A Collection of {@link Join<Project,User>}s that have the given
	 *         role
	 */
	@Query("select j from ProjectUserJoin j where j.project = ?1 and j.projectRole = ?2")
	public List<Join<Project, User>> getUsersForProjectByRole(Project project, ProjectRole projectRole);
}
