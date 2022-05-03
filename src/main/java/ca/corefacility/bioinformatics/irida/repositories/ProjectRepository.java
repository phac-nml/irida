package ca.corefacility.bioinformatics.irida.repositories;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.access.prepost.PreAuthorize;

import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.remote.RemoteStatus.SyncStatus;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.ria.web.admin.dto.statistics.GenericStatModel;

/**
 * Specialized repository for {@link Project}.
 */
public interface ProjectRepository extends IridaJpaRepository<Project, Long>, ProjectRepositoryCustom {

	/**
	 * Sub-expressions for filtering and paging projects on different property names.
	 */
	static final String PROJECT_NAME_LIKE = "(p.name like CONCAT('%', :projectName,'%'))";
	static final String EXCLUDE_PROJECT = "p != :exclude";
	/**
	 * Sub-expressions for filtering and paging projects on permissions (via user groups and project membership).
	 */
	static final String USER_ON_PROJECT = "(p in (select puj.project from ProjectUserJoin puj where puj.user = :forUser))";
	static final String USER_IN_GROUP = "(p in (select ugpj.project from UserGroupJoin ugj, UserGroupProjectJoin ugpj where ugj.group = ugpj.userGroup and ugj.user = :forUser))";
	static final String PROJECT_PERMISSIONS = "(" + USER_ON_PROJECT + " or " + USER_IN_GROUP + ")";

	static final String MANAGER_ON_PROJECT = "(p in (select puj.project from ProjectUserJoin puj where puj.user = :forUser and puj.projectRole = ca.corefacility.bioinformatics.irida.model.enums.ProjectRole.PROJECT_OWNER))";
	static final String MANAGER_IN_GROUP = "(p in (select ugpj.project from UserGroupJoin ugj, UserGroupProjectJoin ugpj where ugj.group = ugpj.userGroup and ugj.user = :forUser and ugpj.projectRole = ca.corefacility.bioinformatics.irida.model.enums.ProjectRole.PROJECT_OWNER))";
	static final String PROJECT_MANAGER_PERMISSION = "(" + MANAGER_ON_PROJECT + " or " + MANAGER_IN_GROUP + ")";

	/**
	 * Load up a page of {@link Project}s, excluding the specified {@link Project}.
	 *
	 * @param name    the name of the project to search for
	 * @param exclude the project to exclude from results
	 * @param page    the page request
	 * @return a page of {@link Project}.
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@Query("from Project p where " + PROJECT_NAME_LIKE + " and " + EXCLUDE_PROJECT)
	public Page<Project> findAllProjectsByNameExcludingProject(final @Param("projectName") String name,
			final @Param("exclude") Project exclude, final Pageable page);

	/**
	 * Load a page of {@link Project}s for a specific {@link User}, excluding a {@link Project}.
	 *
	 * @param name    the name of the project to search for
	 * @param exclude the project to exclude from results
	 * @param user    the user account to load projects for
	 * @param page    the page request
	 * @return a page of {@link Project}
	 */
	@Query("from Project p where " + PROJECT_NAME_LIKE + " and " + EXCLUDE_PROJECT + " and "
			+ PROJECT_MANAGER_PERMISSION)
	public Page<Project> findManageableProjectsByName(final @Param("projectName") String name,
			final @Param("exclude") Project exclude, final @Param("forUser") User user, final Pageable page);

	/**
	 * Get a list of {@link Project}s from remote sites that have a given {@link SyncStatus}
	 *
	 * @param syncStatus the {@link SyncStatus} to get {@link Project}s for
	 * @return a list of {@link Project}
	 */
	@Query("FROM Project p WHERE p.remoteStatus.syncStatus=:syncStatus")
	public List<Project> getProjectsWithRemoteSyncStatus(@Param("syncStatus") SyncStatus syncStatus);

	/**
	 * Get a list of all {@link Project}s from remote sites
	 *
	 * @return a list of {@link Project}
	 */
	@Query("FROM Project p WHERE p.remoteStatus != NULL")
	public List<Project> getRemoteProjects();

	/**
	 * Get a count of all {@link Project}s created within time period
	 *
	 * @param createdDate the minimum created date for projects
	 * @return a count of {@link Project}s
	 */
	@Query("select count(p.id) from Project p where p.createdDate >= ?1")
	public Long countProjectsCreatedInTimePeriod(Date createdDate);

	/**
	 * Get a list of {@link GenericStatModel}s for projects created in the past n time period and grouped by the format
	 * provided.
	 *
	 * @param createdDate   The minimum created date for projects
	 * @param groupByFormat The format to use for grouping the results.
	 * @return A list of {@link GenericStatModel}s
	 */
	@Query("select new ca.corefacility.bioinformatics.irida.ria.web.admin.dto.statistics.GenericStatModel(function('date_format', p.createdDate, ?2), count(p.id))"
			+ "from Project p where p.createdDate >= ?1 group by function('date_format', p.createdDate, ?2) order by p.createdDate asc")
	public List<GenericStatModel> countProjectsCreatedGrouped(Date createdDate, String groupByFormat);
}
