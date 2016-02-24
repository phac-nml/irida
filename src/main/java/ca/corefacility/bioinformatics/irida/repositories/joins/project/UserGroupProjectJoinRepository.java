package ca.corefacility.bioinformatics.irida.repositories.joins.project;

import java.util.Collection;

import ca.corefacility.bioinformatics.irida.model.project.Project;
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
}
