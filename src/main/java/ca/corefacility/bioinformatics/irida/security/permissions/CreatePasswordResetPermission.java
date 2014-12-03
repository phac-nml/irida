package ca.corefacility.bioinformatics.irida.security.permissions;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import ca.corefacility.bioinformatics.irida.model.user.PasswordReset;
import ca.corefacility.bioinformatics.irida.model.user.Role;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.repositories.user.PasswordResetRepository;
import ca.corefacility.bioinformatics.irida.repositories.user.UserRepository;

@Component
public class CreatePasswordResetPermission extends BasePermission<PasswordReset, String> {

	private final UserRepository userRepository;

	@Autowired
	public CreatePasswordResetPermission(PasswordResetRepository repository, UserRepository userRepository) {
		super(PasswordReset.class, String.class, repository);
		this.userRepository = userRepository;
	}

	private static final String PERMISSION_PROVIDED = "canCreatePasswordReset";

	@Override
	public String getPermissionProvided() {
		return PERMISSION_PROVIDED;
	}

	@Override
	protected boolean customPermissionAllowed(Authentication authentication, PasswordReset targetDomainObject) {
		User loggedInUser = userRepository.loadUserByUsername(authentication.getName());
		User resetUser = targetDomainObject.getUser();

		/**
		 * If the reset is being created for the current user, or the logged in
		 * user is an admin, or the logged in user is a ROLE_MANAGER and they're
		 * not changing a ROLE_ADMIN
		 */
		if (loggedInUser.equals(resetUser)
				|| loggedInUser.getAuthorities().contains(Role.ROLE_ADMIN)
				|| (loggedInUser.getAuthorities().contains(Role.ROLE_MANAGER) && !resetUser.getAuthorities().contains(
						Role.ROLE_ADMIN))) {
			return true;
		}

		return false;

	}

}
