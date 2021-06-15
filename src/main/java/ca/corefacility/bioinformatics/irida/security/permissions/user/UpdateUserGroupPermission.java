package ca.corefacility.bioinformatics.irida.security.permissions.user;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.model.user.group.UserGroup;
import ca.corefacility.bioinformatics.irida.model.user.group.UserGroupJoin;
import ca.corefacility.bioinformatics.irida.model.user.group.UserGroupJoin.UserGroupRole;
import ca.corefacility.bioinformatics.irida.repositories.user.UserGroupJoinRepository;
import ca.corefacility.bioinformatics.irida.repositories.user.UserGroupRepository;
import ca.corefacility.bioinformatics.irida.repositories.user.UserRepository;
import ca.corefacility.bioinformatics.irida.security.permissions.RepositoryBackedPermission;

/**
 * Confirms that the authenticated user is allowed to modify a user group.
 */
@Component
public class UpdateUserGroupPermission extends RepositoryBackedPermission<UserGroup, Long> {

	private static final String PERMISSION_PROVIDED = "canUpdateUserGroup";

	private static final Logger logger = LoggerFactory.getLogger(UpdateUserGroupPermission.class);

	private final UserGroupJoinRepository userGroupJoinRepository;
	private final UserRepository userRepository;

	/**
	 * Construct an instance of {@link UpdateUserGroupPermission}.
	 *
	 * @param userGroupRepository     the user group repository.
	 * @param userGroupJoinRepository the user group join repository
	 * @param userRepository          the user repository
	 */
	@Autowired
	public UpdateUserGroupPermission(final UserGroupRepository userGroupRepository,
			final UserGroupJoinRepository userGroupJoinRepository, final UserRepository userRepository) {
		super(UserGroup.class, Long.class, userGroupRepository);
		this.userGroupJoinRepository = userGroupJoinRepository;
		this.userRepository = userRepository;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean customPermissionAllowed(final Authentication authentication, final UserGroup g) {
		logger.trace("Checking if [" + authentication + "] can modify [" + g + "]");
		final User user = userRepository.loadUserByUsername(authentication.getName());
		final Optional<UserGroupJoin> userInGroup = userGroupJoinRepository.findUsersInGroup(g).stream()
				.filter(j -> j.getSubject().equals(user)).findAny();

		if (userInGroup.isPresent()) {
			final UserGroupJoin j = userInGroup.get();
			if (j.getRole().equals(UserGroupRole.GROUP_OWNER)) {
				logger.trace("User [" + user + "] is GROUP_OWNER in group [" + g + "], access is GRANTED.");
				return true;
			} else {
				logger.trace("User [" + user + "] is *not* GROUP_OWNER in group [" + g + "], access is DENIED.");
				return false;
			}
		} else {
			logger.trace("User [" + user + "] is not in group [" + g + "], access is DENIED.");
			return false;
		}
	}

	@Override
	public String getPermissionProvided() {
		return PERMISSION_PROVIDED;
	}

}
