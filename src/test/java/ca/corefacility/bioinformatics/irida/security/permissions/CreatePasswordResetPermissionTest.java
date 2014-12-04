package ca.corefacility.bioinformatics.irida.security.permissions;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import ca.corefacility.bioinformatics.irida.model.user.PasswordReset;
import ca.corefacility.bioinformatics.irida.model.user.Role;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.repositories.user.PasswordResetRepository;
import ca.corefacility.bioinformatics.irida.repositories.user.UserRepository;

public class CreatePasswordResetPermissionTest {
	private CreatePasswordResetPermission permission;
	private PasswordResetRepository repository;
	private UserRepository userRepository;

	private User loggedInUser;

	@Before
	public void setup() {
		userRepository = mock(UserRepository.class);
		repository = mock(PasswordResetRepository.class);
		permission = new CreatePasswordResetPermission(repository, userRepository);
	}

	@Test
	public void testUpdateByAdmin() {
		Authentication authenticationForRole = getAuthenticationForRole(Role.ROLE_ADMIN);

		String username = "jeff";

		User u = new User();
		u.setUsername(username);

		u.setSystemRole(Role.ROLE_USER);
		assertTrue(permission.customPermissionAllowed(authenticationForRole, new PasswordReset(u)));

		u.setSystemRole(Role.ROLE_MANAGER);
		assertTrue(permission.customPermissionAllowed(authenticationForRole, new PasswordReset(u)));

		u.setSystemRole(Role.ROLE_ADMIN);
		assertTrue(permission.customPermissionAllowed(authenticationForRole, new PasswordReset(u)));

		assertTrue(permission.customPermissionAllowed(authenticationForRole, new PasswordReset(loggedInUser)));
	}

	@Test
	public void testUpdateByManager() {
		Authentication authenticationForRole = getAuthenticationForRole(Role.ROLE_MANAGER);

		String username = "jeff";

		User u = new User();
		u.setUsername(username);

		u.setSystemRole(Role.ROLE_USER);
		assertTrue(permission.customPermissionAllowed(authenticationForRole, new PasswordReset(u)));

		u.setSystemRole(Role.ROLE_MANAGER);
		assertTrue(permission.customPermissionAllowed(authenticationForRole, new PasswordReset(u)));

		u.setSystemRole(Role.ROLE_ADMIN);
		assertFalse(permission.customPermissionAllowed(authenticationForRole, new PasswordReset(u)));

		assertTrue(permission.customPermissionAllowed(authenticationForRole, new PasswordReset(loggedInUser)));
	}

	@Test
	public void testUpdateByUser() {
		Authentication authenticationForRole = getAuthenticationForRole(Role.ROLE_USER);

		String username = "jeff";

		User u = new User();
		u.setUsername(username);

		u.setSystemRole(Role.ROLE_USER);
		assertFalse(permission.customPermissionAllowed(authenticationForRole, new PasswordReset(u)));

		u.setSystemRole(Role.ROLE_MANAGER);
		assertFalse(permission.customPermissionAllowed(authenticationForRole, new PasswordReset(u)));

		u.setSystemRole(Role.ROLE_ADMIN);
		assertFalse(permission.customPermissionAllowed(authenticationForRole, new PasswordReset(u)));

		assertTrue(permission.customPermissionAllowed(authenticationForRole, new PasswordReset(loggedInUser)));
	}

	private Authentication getAuthenticationForRole(Role role) {
		String username = "tom";
		String password = "Password1";
		loggedInUser = new User(username, password, null, "Tom", "Matthews", null);
		loggedInUser.setSystemRole(role);

		when(userRepository.loadUserByUsername(username)).thenReturn(loggedInUser);

		return new UsernamePasswordAuthenticationToken(username, password);

	}
}
