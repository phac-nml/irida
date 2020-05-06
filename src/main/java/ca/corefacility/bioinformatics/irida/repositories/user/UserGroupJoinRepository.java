package ca.corefacility.bioinformatics.irida.repositories.user;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.Query;

import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.model.user.group.UserGroup;
import ca.corefacility.bioinformatics.irida.model.user.group.UserGroupJoin;
import ca.corefacility.bioinformatics.irida.repositories.IridaJpaRepository;

/**
 * Repository for working with {@link UserGroupJoin}.
 */
public interface UserGroupJoinRepository extends IridaJpaRepository<UserGroupJoin, Long> {

	/**
	 * Get a collection of users in a group.
	 * 
	 * @param group
	 *            the group to get users for.
	 * @return the users in the group.
	 */
	@Query("from UserGroupJoin ugh where ugh.group = ?1")
	public Collection<UserGroupJoin> findUsersInGroup(final UserGroup group);

	/**
	 * Get a collection of users not in a group.
	 * 
	 * @param group
	 *            the group to exclude
	 * @return the users not in the group
	 */
	@Query("from User u where u not in (select user from UserGroupJoin where group = ?1) and (CONCAT(u.firstName, ' ', u.lastName) like %?2% or u.username like %?2% or u.email like %?2%)")
	public List<User> findUsersNotInGroup(final UserGroup group, final String term);
}
