package ca.corefacility.bioinformatics.irida.security.permissions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;

import ca.corefacility.bioinformatics.irida.model.Role;
import ca.corefacility.bioinformatics.irida.model.User;
import ca.corefacility.bioinformatics.irida.repositories.UserRepository;

/**
 * Confirms that the authenticated user is allowed to modify another (or their
 * own) user account.
 * 
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 * 
 */
public class UpdateUserPermission extends BasePermission<User> {

	private static final String PERMISSION_PROVIDED = "canUpdateUser";

	private static final Logger logger = LoggerFactory.getLogger(UpdateUserPermission.class);

	/**
	 * Construct an instance of {@link UpdateUserPermission}.
	 */
	public UpdateUserPermission() {
		super(User.class, "userRepository");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean customPermissionAllowed(Authentication authentication, User u) {
		logger.trace("Checking if [" + authentication + "] can modify [" + u + "]");
		UserRepository userService = getApplicationContext().getBean(UserRepository.class);

		// really quick check: if the principle is of ROLE_CLIENT, they should
		// be rejected immediately.
		if (authentication.getAuthorities().contains(Role.ROLE_CLIENT)) {
			logger.trace("Tool attempting to modify itself: [" + authentication + "], attempt rejected.");
			return false;
		}

		boolean isOwnAccount = false;
		// business rules specify that the authenticated user must have a
		// role of administrator, or the user is trying to modify their own
		// account.

		logger.trace("User is not admin, checking if user is trying to modify own account.");

		User authenticated = userService.getUserByUsername(authentication.getName());
		
		isOwnAccount = authenticated.equals(u);

		logger.trace("Allowing modification of user account based on authenticated principle? [" + isOwnAccount + "]");
		return isOwnAccount;
	}

	@Override
	public String getPermissionProvided() {
		return PERMISSION_PROVIDED;
	}

}
