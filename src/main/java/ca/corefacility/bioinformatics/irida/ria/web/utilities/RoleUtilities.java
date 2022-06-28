package ca.corefacility.bioinformatics.irida.ria.web.utilities;

import ca.corefacility.bioinformatics.irida.model.user.Role;
import ca.corefacility.bioinformatics.irida.model.user.User;

/**
 * Utilities class to help with role access.
 */
public class RoleUtilities {

	/**
	 * Check if the user is an Admin
	 *
	 * @param user The user to be checked
	 * @return if the user is an admin
	 */
	public static boolean isAdmin(User user) {
		return user.getAuthorities().contains(Role.ROLE_ADMIN);
	}
}
