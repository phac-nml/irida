package ca.corefacility.bioinformatics.irida.service.user;

import java.util.Collection;

import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.model.user.group.UserGroup;
import ca.corefacility.bioinformatics.irida.model.user.group.UserGroupJoin;
import ca.corefacility.bioinformatics.irida.model.user.group.UserGroupJoin.UserGroupRole;
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

	/**
	 * Add a user to the group with the specified role.
	 * 
	 * @param user
	 *            the user to add to the group
	 * @param userGroup
	 *            the group to add the user to
	 * @param role
	 *            the role that the user should have in the group
	 * @return the relationship created between the user and group.
	 */
	public UserGroupJoin addUserToGroup(final User user, final UserGroup userGroup, final UserGroupRole role);

	/**
	 * Change the role for the {@link User} in the {@link UserGroup}.
	 * 
	 * @param user
	 *            the user to change
	 * @param userGroup
	 *            the group to change
	 * @param role
	 *            the new role
	 * @return the modified relationship
	 */
	public UserGroupJoin changeUserGroupRole(final User user, final UserGroup userGroup, final UserGroupRole role);

	/**
	 * Remove the {@link User} from the {@link UserGroup}.
	 * 
	 * @param user
	 *            the user to remove
	 * @param userGroup
	 *            the group from which to remove the user.
	 */
	public void removeUserFromGroup(final User user, final UserGroup userGroup);
}
