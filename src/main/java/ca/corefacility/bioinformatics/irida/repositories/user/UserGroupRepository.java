package ca.corefacility.bioinformatics.irida.repositories.user;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;

import ca.corefacility.bioinformatics.irida.model.user.group.UserGroup;
import ca.corefacility.bioinformatics.irida.model.user.group.UserGroupJoin;
import ca.corefacility.bioinformatics.irida.repositories.IridaJpaRepository;

/**
 * A repository for {@link UserGroup}.
 *
 */
public interface UserGroupRepository extends IridaJpaRepository<UserGroup, Long> {

	/**
	 * Get a collection of users in a group.
	 * 
	 * @param group
	 *            the group to get users for.
	 * @return the users in the group.
	 */
	@Query("from UserGroupJoin ugh where ugh.group = ?1")
	public Collection<UserGroupJoin> findUsersInGroup(final UserGroup group);
}
