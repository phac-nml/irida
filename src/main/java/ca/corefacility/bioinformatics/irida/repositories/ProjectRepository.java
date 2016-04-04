package ca.corefacility.bioinformatics.irida.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.access.prepost.PreAuthorize;

import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.user.User;

/**
 * Specialized repository for {@link Project}.
 * 
 */
public interface ProjectRepository extends IridaJpaRepository<Project, Long> {

	/**
	 * Sub-expressions for filtering and paging projects on different property
	 * names.
	 */
	static final String PROJECT_ID_LIKE = "(p.id like CONCAT('%', :projectName, '%'))";
	static final String PROJECT_NAME_LIKE = "(p.name like CONCAT('%', :projectName,'%'))";
	static final String PROJECT_ORGANISM_LIKE = "(:organismName = '' and p.organism = null or p.organism like CONCAT('%', :organismName, '%'))";
	static final String EXCLUDE_PROJECT = "p != :exclude";
	/**
	 * Sub-expressions for filtering and paging projects on permissions (via
	 * user groups and project membership).
	 */
	static final String USER_ON_PROJECT = "(p in (select puj.project from ProjectUserJoin puj where puj.user = :forUser))";
	static final String USER_IN_GROUP = "(p in (select ugpj.project from UserGroupJoin ugj, UserGroupProjectJoin ugpj where ugj.group = ugpj.userGroup and ugj.user = :forUser))";
	static final String PROJECT_PERMISSIONS = "(" + USER_ON_PROJECT + " or " + USER_IN_GROUP + ")";
	
	/**
	 * This query should be used when the user is filtering projects on specific
	 * attributes (i.e., searching by organism in the filters page).
	 */
	static final String FILTER_INDIVIDUAL_FIELDS = ":projectName != :organismName and ((" + PROJECT_NAME_LIKE + " or " + PROJECT_ID_LIKE + ") and " + PROJECT_ORGANISM_LIKE + ")";
	/**
	 * This query should be used when the user is filtering projects using the
	 * GLOBAL search box
	 */
	static final String FILTER_ALL_FIELDS = ":projectName = :organismName and (" + PROJECT_NAME_LIKE + " or " + PROJECT_ID_LIKE + " or " + PROJECT_ORGANISM_LIKE + ")";

	/**
	 * Load a page of {@link Project}s for a specific {@link User}.
	 * 
	 * @param searchName
	 *            the name of the project to search for
	 * @param searchOrganism
	 *            the name of the organism to search for
	 * @param user
	 *            the user to load projects for
	 * @param page
	 *            the page request
	 * @return a page of {@link Project}.
	 */
	@Query("from Project p where (" + FILTER_INDIVIDUAL_FIELDS + " or " + FILTER_ALL_FIELDS + ") and " + PROJECT_PERMISSIONS)
	public Page<Project> findProjectsForUser(final @Param("projectName") String searchName,
			final @Param("organismName") String searchOrganism, final @Param("forUser") User user, final Pageable page);

	/**
	 * Load all projects in the system that match a name and organism.
	 * 
	 * @param searchName
	 *            the name to search for
	 * @param organismName
	 *            the organism to search for
	 * @param page
	 *            the page request
	 * @return a page of {@link Project}.
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@Query("from Project p where (" + FILTER_INDIVIDUAL_FIELDS + " or " + FILTER_ALL_FIELDS + ")")
	public Page<Project> findAllProjectsByNameOrOrganism(final @Param("projectName") String searchName,
			final @Param("organismName") String organismName, final Pageable page);
	
	/**
	 * Load up a page of {@link Project}s, excluding the specified
	 * {@link Project}.
	 * 
	 * @param name
	 *            the name of the project to search for
	 * @param exclude
	 *            the project to exclude from results
	 * @param page
	 *            the page request
	 * @return a page of {@link Project}.
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@Query("from Project p where " + PROJECT_NAME_LIKE + " and " + EXCLUDE_PROJECT)
	public Page<Project> findAllProjectsByNameExcludingProject(final @Param("projectName") String name,
			final @Param("exclude") Project exclude, final Pageable page);

	/**
	 * Load a page of {@link Project}s for a specific {@link User}, excluding a
	 * {@link Project}.
	 * 
	 * @param name
	 *            the name of the project to search for
	 * @param exclude
	 *            the project to exclude from results
	 * @param user
	 *            the user account to load projects for
	 * @param page
	 *            the page request
	 * @return a page of {@link Project}.
	 */
	@Query("from Project p where " + PROJECT_NAME_LIKE + " and " + EXCLUDE_PROJECT + " and " + PROJECT_PERMISSIONS)
	public Page<Project> findProjectsByNameExcludingProjectForUser(final @Param("projectName") String name,
			final @Param("exclude") Project exclude, final @Param("forUser") User user, final Pageable page);
}
