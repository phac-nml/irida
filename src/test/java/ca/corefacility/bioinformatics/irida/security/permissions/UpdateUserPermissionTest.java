package ca.corefacility.bioinformatics.irida.security.permissions;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collection;

import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import ca.corefacility.bioinformatics.irida.model.Role;
import ca.corefacility.bioinformatics.irida.model.User;
import ca.corefacility.bioinformatics.irida.repositories.UserRepository;

/**
 * Tests for {@link UpdateUserPermission}.
 * 
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 * 
 */
public class UpdateUserPermissionTest {
	private UpdateUserPermission updateUserPermission;
	private UserRepository userRepository;

	@Before
	public void setUp() {
		ApplicationContext applicationContext = mock(ApplicationContext.class);
		userRepository = mock(UserRepository.class);

		updateUserPermission = new UpdateUserPermission();
		updateUserPermission.setApplicationContext(applicationContext);

		when(applicationContext.getBean("userRepository")).thenReturn(userRepository);
		when(applicationContext.getBean(UserRepository.class)).thenReturn(userRepository);
	}

	@Test
	public void testGrantPermission() {
		String username = "fbristow";
		User u = new User();
		u.setUsername(username);

		when(userRepository.getUserByUsername(username)).thenReturn(u);
		when(userRepository.read(1l)).thenReturn(u);

		Authentication auth = new UsernamePasswordAuthenticationToken("fbristow", "password1");

		assertTrue("permission was not granted.", updateUserPermission.isAllowed(auth, 1l));

		verify(userRepository).getUserByUsername(username);
		verify(userRepository).read(1l);
	}

	@Test
	public void testRejectPermission() {
		String username = "fbristow";
		User u = new User();
		u.setUsername(username);

		when(userRepository.getUserByUsername(username)).thenReturn(u);
		when(userRepository.read(1l)).thenReturn(new User());

		Authentication auth = new UsernamePasswordAuthenticationToken("fbristow", "password1");

		assertFalse("permission was granted.", updateUserPermission.isAllowed(auth, 1l));

		verify(userRepository).getUserByUsername(username);
		verify(userRepository).read(1l);
	}

	@Test
	public void testPermitAdmin() {
		Collection<GrantedAuthority> roles = new ArrayList<>();
		roles.add(Role.ROLE_ADMIN);

		Authentication auth = new UsernamePasswordAuthenticationToken("fbristow", "password1", roles);

		assertTrue("permission was not granted to admin.", updateUserPermission.isAllowed(auth, 1l));

		// we should fast pass through to permission granted for administrators.
		verifyZeroInteractions(userRepository);
	}

	@Test
	public void testRejectClient() {
		Collection<GrantedAuthority> roles = new ArrayList<>();
		roles.add(Role.ROLE_CLIENT);

		Authentication authentication = new UsernamePasswordAuthenticationToken("fbristow", "password1", roles);

		assertFalse("permission was granted to client.", updateUserPermission.isAllowed(authentication, 1l));
	}
}
