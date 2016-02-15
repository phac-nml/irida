package ca.corefacility.bioinformatics.irida.service.user;

import java.util.Collection;

import ca.corefacility.bioinformatics.irida.model.user.group.UserGroup;
import ca.corefacility.bioinformatics.irida.model.user.group.UserGroupJoin;
import ca.corefacility.bioinformatics.irida.service.CRUDService;

/**
 * Service for working with {@link UserGroup}s.
 * 
 */
public interface UserGroupService extends CRUDService<Long, UserGroup> {

	/**
	 * Get all of the users in the group.
	 * 
	 * @param userGroup
	 *            the group to get users for.
	 * @return the users in the group.
	 */
	public Collection<UserGroupJoin> getUsersForGroup(final UserGroup userGroup);
}
