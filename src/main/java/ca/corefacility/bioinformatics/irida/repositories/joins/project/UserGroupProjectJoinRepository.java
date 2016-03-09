package ca.corefacility.bioinformatics.irida.repositories.joins.project;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;

import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.user.User;
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
	 * Find the projects where the specified user is in a group on the project.
	 * 
	 * @param u
	 *            the user.
	 * @return the projects that the user is in via a group.
	 */
	@Query("from UserGroupProjectJoin ugpj where ?1 in (select user from UserGroupJoin where group = ugpj)")
	public Collection<UserGroupProjectJoin> findProjectsByUser(final User u);
}
