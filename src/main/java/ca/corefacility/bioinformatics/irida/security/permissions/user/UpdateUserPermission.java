package ca.corefacility.bioinformatics.irida.security.permissions.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import ca.corefacility.bioinformatics.irida.model.user.Role;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.repositories.user.UserRepository;
import ca.corefacility.bioinformatics.irida.security.permissions.RepositoryBackedPermission;

/**
 * Confirms that the authenticated user is allowed to modify another (or their
 * own) user account.
 * 
 * 
 */
@Component
public class UpdateUserPermission extends RepositoryBackedPermission<User, Long> {

	private static final String PERMISSION_PROVIDED = "canUpdateUser";

	private static final Logger logger = LoggerFactory.getLogger(UpdateUserPermission.class);

	private UserRepository userRepository;

	/**
	 * Construct an instance of {@link UpdateUserPermission}.
	 * 
	 * @param userRepository
	 *            the user repository.
	 */
	@Autowired
	public UpdateUserPermission(UserRepository userRepository) {
		super(User.class, Long.class, userRepository);
		this.userRepository = userRepository;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean customPermissionAllowed(Authentication authentication, User u) {
		logger.trace("Checking if [" + authentication + "] can modify [" + u + "]");

		// really quick check: if the principle is of ROLE_SEQUENCER, they
		// should
		// be rejected immediately.
		if (authentication.getAuthorities().contains(Role.ROLE_SEQUENCER)) {
			logger.trace("Tool attempting to modify itself: [" + authentication + "], attempt rejected.");
			return false;
		}

		boolean isOwnAccount = modifyingOwnAccount(authentication, u);
		boolean isAdmin = authentication.getAuthorities().contains(Role.ROLE_ADMIN);

		// We're not allowing a manager to modify an admin. This is checking if
		// the logged in user is a manager and if the passed model object is an
		// admin.
		boolean isManagerModifyingAdmin = authentication.getAuthorities().contains(Role.ROLE_MANAGER)
				&& u.getAuthorities().contains(Role.ROLE_ADMIN);

		return (isOwnAccount || isAdmin) && !isManagerModifyingAdmin;
	}

	/**
	 * Check to see if the user is modifying their own account.
	 * 
	 * @param authentication
	 *            the currently logged in user.
	 * @param u
	 *            the user that is being modified
	 * @return true if the authentication and user are the same thing.
	 */
	private boolean modifyingOwnAccount(Authentication authentication, User u) {
		boolean isOwnAccount = false;
		// business rules specify that the authenticated user must have a
		// role of administrator, or the user is trying to modify their own
		// account.

		logger.trace("User is not admin, checking if user is trying to modify own account.");

		User authenticated = userRepository.loadUserByUsername(authentication.getName());

		isOwnAccount = authenticated.equals(u);

		logger.trace("Allowing modification of user account based on authenticated principle? [" + isOwnAccount + "]");
		return isOwnAccount;
	}

	@Override
	public String getPermissionProvided() {
		return PERMISSION_PROVIDED;
	}

}
