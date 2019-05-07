package ca.corefacility.bioinformatics.irida.repositories.joins.project;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;

import ca.corefacility.bioinformatics.irida.model.enums.ProjectRole;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.model.user.group.UserGroup;
import ca.corefacility.bioinformatics.irida.model.user.group.UserGroupProjectJoin;
import ca.corefacility.bioinformatics.irida.repositories.IridaJpaRepository;

/**
 * Repository for {@link UserGroupProjectJoin}.
 */
public interface UserGroupProjectJoinRepository extends IridaJpaRepository<UserGroupProjectJoin, Long> {

	/**
	 * Find all groups with access to the project.
	 * 
	 * @param p
	 *            the project to check
	 * @return the groups assigned to the project.
	 */
	public Collection<UserGroupProjectJoin> findGroupsByProject(final Project p);

	/**
	 * Find all projects linked to the group.
	 *
	 * @param ug the user group to check
	 * @return the projects linked to the user group.
	 */
	public Collection<UserGroupProjectJoin> findProjectsByUserGroup(final UserGroup ug);
	
	/**
	 * Find all groups with access to the project with a specific role.
	 * 
	 * @param p
	 *            the project
	 * @param projectRole
	 *            the role
	 * @return the groups with the specified role on the project.
	 */
	public Collection<UserGroupProjectJoin> findGroupsByProjectAndProjectRole(final Project p,
			final ProjectRole projectRole);

	/**
	 * Find the projects where the specified user is in a group on the project.
	 * 
	 * @param u
	 *            the user.
	 * @return the projects that the user is in via a group.
	 */
	@Query("from UserGroupProjectJoin ugpj where ugpj.userGroup in (select group from UserGroupJoin where user = ?1)")
	public Collection<UserGroupProjectJoin> findProjectsByUser(final User u);

	/**
	 * Find the join for a user group and project.
	 * 
	 * @param p
	 *            the project
	 * @param userGroup
	 *            the user group
	 * @return the relationship between the group and project.
	 */
	public UserGroupProjectJoin findByProjectAndUserGroup(final Project p, final UserGroup userGroup);
}
