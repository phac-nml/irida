package ca.corefacility.bioinformatics.irida.repositories.joins.project;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import ca.corefacility.bioinformatics.irida.model.enums.ProjectRole;
import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectUserJoin;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.user.User;

/**
 * A repository for {@link ProjectUserJoin}.
 */
public interface ProjectUserJoinRepository
		extends CrudRepository<ProjectUserJoin, Long>, JpaSpecificationExecutor<ProjectUserJoin> {
	/**
	 * Get all {@link User}s associated with a project.
	 *
	 * @param project the {@link Project} to get {@link User}s for.
	 * @return A Collection of {@link ProjectUserJoin}s describing users for this project
	 */
	@Query("select j from ProjectUserJoin j where j.project = ?1")
	public List<Join<Project, User>> getUsersForProject(Project project);

	/**
	 * Get a page of {@link User}s associated with a project.
	 *
	 * @param project the {@link Project} to get {@link User}s for.
	 * @param search  the string to filter on username
	 * @param page    the page request
	 * @return a page of users.
	 */
	@Query("from ProjectUserJoin j where j.project = ?1 and (CONCAT(j.user.firstName, ' ', j.user.lastName) like %?2% or j.user.username like %?2% or j.user.email like %?2%)")
	public Page<Join<Project, User>> getUsersForProject(final Project project, final String search,
			final Pageable page);

	/**
	 * Get the number of {@link User}s in a given {@link Project}
	 *
	 * @param project Project to get users for
	 * @return Long count of users in a project
	 */
	@Query("select count(j.id) from ProjectUserJoin j where j.project = ?1")
	public Long countUsersForProject(Project project);

	/**
	 * Get {@link User}s for a {@link Project} that have a particular role
	 *
	 * @param project     The project to find users for
	 * @param projectRole The {@link ProjectRole} a user needs to have to be returned
	 * @return A Collection of {@link ProjectUserJoin}s that have the given role
	 */
	@Query("select j from ProjectUserJoin j where j.project = ?1 and j.projectRole = ?2")
	public List<Join<Project, User>> getUsersForProjectByRole(Project project, ProjectRole projectRole);

	/**
	 * Get all {@link Project}s associated with a particular {@link User}.
	 *
	 * @param user the user to get projects for.
	 * @return A collection of {@link ProjectUserJoin}s describing the projects associated with the user.
	 */
	@Query("select j from ProjectUserJoin j where j.user = ?1")
	public List<Join<Project, User>> getProjectsForUser(User user);

	/**
	 * Get the join object between a given {@link Project} and {@link User}
	 *
	 * @param project The project of the join
	 * @param user    The user of the join
	 * @return The relationship between the project and user
	 */
	@Query("from ProjectUserJoin j where j.project = ?1 and j.user = ?2")
	public ProjectUserJoin getProjectJoinForUser(Project project, User user);

}
