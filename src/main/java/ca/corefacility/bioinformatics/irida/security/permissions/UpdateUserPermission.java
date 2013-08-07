package ca.corefacility.bioinformatics.irida.security.permissions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.security.core.Authentication;

import ca.corefacility.bioinformatics.irida.model.Role;
import ca.corefacility.bioinformatics.irida.model.User;
import ca.corefacility.bioinformatics.irida.repositories.UserRepository;
import ca.corefacility.bioinformatics.irida.security.permissions.IridaPermissionEvaluator.Permission;

/**
 * Confirms that the authenticated user is allowed to modify another (or their
 * own) user account.
 * 
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 * 
 */
public class UpdateUserPermission implements Permission, ApplicationContextAware {

	private static final String PERMISSION_PROVIDED = "canUpdateUser";

	private static final Logger logger = LoggerFactory.getLogger(UpdateUserPermission.class);

	private ApplicationContext applicationContext;
	private UserRepository userService;

	public void setApplicationContext(ApplicationContext context) {
		this.applicationContext = context;
	}

	@Override
	public boolean isAllowed(Authentication authentication, Object targetDomainObject) {
		logger.trace("Checking if [" + authentication + "] can modify [" + targetDomainObject + "]");
		this.userService = applicationContext.getBean(UserRepository.class);

		// really quick check: if the principle is of ROLE_CLIENT, they should
		// be rejected immediately.
		if (authentication.getAuthorities().contains(Role.ROLE_CLIENT)) {
			logger.trace("Tool attempting to modify itself: [" + authentication + "], attempt rejected.");
			return false;
		}

		User u;
		boolean isAdmin = authentication.getAuthorities().contains(Role.ROLE_ADMIN);
		boolean isOwnAccount = false;
		// business rules specify that the authenticated user must have a
		// role of administrator, or the user is trying to modify their own
		// account.

		if (!isAdmin) {
			logger.trace("User is not admin, checking if user is trying to modify own account.");

			// we can be passed either a long (which is the user id) or a user
			// object
			if (targetDomainObject instanceof Long) {
				u = userService.read((Long) targetDomainObject);
			} else if (targetDomainObject instanceof User) {
				u = (User) targetDomainObject;
			} else {
				throw new IllegalArgumentException("Parameter to " + getClass().getName()
						+ " must be of type Long or User.");
			}

			User authenticated = userService.getUserByUsername(authentication.getName());
			isOwnAccount = authenticated.equals(u);
			logger.trace("User is trying to modify own account: [" + isOwnAccount + "].");
		}

		logger.trace("Allowing modification of user account based on authenticated principle? ["
				+ (isAdmin || isOwnAccount) + "]");
		return isAdmin || isOwnAccount;
	}

	@Override
	public String getPermissionProvided() {
		return PERMISSION_PROVIDED;
	}

}
