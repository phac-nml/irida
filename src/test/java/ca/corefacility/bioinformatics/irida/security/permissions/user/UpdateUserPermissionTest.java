package ca.corefacility.bioinformatics.irida.security.permissions.user;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

import javax.swing.text.html.Option;

import org.junit.Before;
import org.junit.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import ca.corefacility.bioinformatics.irida.model.user.Role;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.repositories.user.UserRepository;
import ca.corefacility.bioinformatics.irida.security.permissions.user.UpdateUserPermission;

/**
 * Tests for {@link UpdateUserPermission}.
 * 
 * 
 */
public class UpdateUserPermissionTest {
	private UpdateUserPermission updateUserPermission;
	private UserRepository userRepository;

	@Before
	public void setUp() {
		userRepository = mock(UserRepository.class);

		updateUserPermission = new UpdateUserPermission(userRepository);
	}

	@Test
	public void testGrantPermission() {
		String username = "fbristow";
		User u = new User();
		u.setUsername(username);

		when(userRepository.loadUserByUsername(username)).thenReturn(u);
		when(userRepository.findById(1L)).thenReturn(Optional.of(u));

		Authentication auth = new UsernamePasswordAuthenticationToken("fbristow", "password1");

		assertTrue("permission was not granted.", updateUserPermission.isAllowed(auth, 1L));

		verify(userRepository).loadUserByUsername(username);
		verify(userRepository).findById(1L);
	}

	@Test
	public void testRejectPermission() {
		String username = "fbristow";
		User u = new User();
		u.setUsername(username);

		when(userRepository.loadUserByUsername(username)).thenReturn(u);
		when(userRepository.findById(1L)).thenReturn(Optional.of(new User()));

		Authentication auth = new UsernamePasswordAuthenticationToken("fbristow", "password1");

		assertFalse("permission was granted.", updateUserPermission.isAllowed(auth, 1L));

		verify(userRepository).loadUserByUsername(username);
		verify(userRepository).findById(1L);
	}

	@Test
	public void testPermitAdmin() {
		Collection<GrantedAuthority> roles = new ArrayList<>();
		roles.add(Role.ROLE_ADMIN);

		Authentication auth = new UsernamePasswordAuthenticationToken("fbristow", "password1", roles);
		when(userRepository.findById(1L)).thenReturn(Optional.of(new User()));

		assertTrue("permission was not granted to admin.", updateUserPermission.isAllowed(auth, 1L));
	}

	@Test
	public void testRejectClient() {
		Collection<GrantedAuthority> roles = new ArrayList<>();
		roles.add(Role.ROLE_SEQUENCER);

		Authentication auth = new UsernamePasswordAuthenticationToken("fbristow", "password1", roles);

		when(userRepository.findById(1L)).thenReturn(Optional.of(new User()));
		assertFalse("permission was granted to client.", updateUserPermission.isAllowed(auth, 1L));
	}
}
