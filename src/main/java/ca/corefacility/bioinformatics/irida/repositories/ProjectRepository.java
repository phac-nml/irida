package ca.corefacility.bioinformatics.irida.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.security.access.prepost.PreAuthorize;

import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.user.User;

/**
 * Specialized repository for {@link Project}.
 * 
 */
public interface ProjectRepository extends IridaJpaRepository<Project, Long> {

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
	@Query("from Project p where name like %?1% and p != ?2")
	public Page<Project> findAllProjectsByNameExcludingProject(final String name, final Project exclude,
			final Pageable page);

	static final String PROJECT_NAME_LIKE = "p.name like CONCAT(?1,'%')";
	static final String EXCLUDE_PROJECT = "p != ?2";
	static final String USER_ON_PROJECT = "(?3 in (select puj.user from ProjectUserJoin puj where puj.project = p))";
	static final String USER_IN_GROUP = "(?3 in (select ugj.user from UserGroupJoin ugj, UserGroupProjectJoin ugpj where ugj.group = ugpj.userGroup and ugpj.project = p))";

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
	@Query("from Project p where " + PROJECT_NAME_LIKE + " and " + EXCLUDE_PROJECT + " and (" + USER_IN_GROUP + " or "
			+ USER_ON_PROJECT + ")")
	public Page<Project> findProjectsByNameExcludingProjectForUser(final String name, final Project exclude,
			final User user, final Pageable page);
}
