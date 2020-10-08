package ca.corefacility.bioinformatics.irida.security.permissions.user;

import java.util.Optional;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import com.google.common.collect.ImmutableList;

import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.model.user.group.UserGroup;
import ca.corefacility.bioinformatics.irida.model.user.group.UserGroupJoin;
import ca.corefacility.bioinformatics.irida.model.user.group.UserGroupJoin.UserGroupRole;
import ca.corefacility.bioinformatics.irida.repositories.user.UserGroupJoinRepository;
import ca.corefacility.bioinformatics.irida.repositories.user.UserGroupRepository;
import ca.corefacility.bioinformatics.irida.repositories.user.UserRepository;
import ca.corefacility.bioinformatics.irida.security.permissions.user.UpdateUserGroupPermission;

/**
 * Tests for {@link UpdateUserGroupPermission}.
 * 
 * 
 */
public class UpdateUserGroupPermissionTest {
	private UpdateUserGroupPermission updateUserPermission;
	private UserRepository userRepository;
	private UserGroupRepository userGroupRepository;
	private UserGroupJoinRepository userGroupJoinRepository;

	@Before
	public void setUp() {
		userRepository = mock(UserRepository.class);
		userGroupRepository = mock(UserGroupRepository.class);
		userGroupJoinRepository = mock(UserGroupJoinRepository.class);

		updateUserPermission = new UpdateUserGroupPermission(userGroupRepository, userGroupJoinRepository,
				userRepository);
	}

	@Test
	public void testGrantPermission() {
		final String username = "user";
		final User u = new User();
		final UserGroup ug = new UserGroup("group");
		u.setUsername(username);

		when(userRepository.loadUserByUsername(username)).thenReturn(u);
		when(userRepository.findById(1L)).thenReturn(Optional.of(u));
		when(userGroupRepository.findById(1L)).thenReturn(Optional.of(ug));
		when(userGroupJoinRepository.findUsersInGroup(ug))
				.thenReturn(ImmutableList.of(new UserGroupJoin(u, ug, UserGroupRole.GROUP_OWNER)));

		final Authentication auth = new UsernamePasswordAuthenticationToken(username, "password1");

		assertTrue("permission was not granted.", updateUserPermission.isAllowed(auth, 1L));
	}
	
	@Test
	public void testRejectNotGroupOwnerPermission() {
		final String username = "user";
		final User u = new User();
		final UserGroup ug = new UserGroup("group");
		u.setUsername(username);

		when(userRepository.loadUserByUsername(username)).thenReturn(u);
		when(userRepository.findById(1L)).thenReturn(Optional.of(u));
		when(userGroupRepository.findById(1L)).thenReturn(Optional.of(ug));
		when(userGroupJoinRepository.findUsersInGroup(ug))
				.thenReturn(ImmutableList.of(new UserGroupJoin(u, ug, UserGroupRole.GROUP_MEMBER)));

		final Authentication auth = new UsernamePasswordAuthenticationToken(username, "password1");

		assertFalse("permission should not be granted.", updateUserPermission.isAllowed(auth, 1L));
	}
	
	@Test
	public void testRejectNotInGroup() {
		final String username = "user";
		final User u = new User();
		final User u2 = new User();
		final UserGroup ug = new UserGroup("group");
		u.setUsername(username);

		when(userRepository.loadUserByUsername(username)).thenReturn(u);
		when(userRepository.findById(1L)).thenReturn(Optional.of(u));
		when(userGroupRepository.findById(1L)).thenReturn(Optional.of(ug));
		when(userGroupJoinRepository.findUsersInGroup(ug))
				.thenReturn(ImmutableList.of(new UserGroupJoin(u2, ug, UserGroupRole.GROUP_OWNER)));

		final Authentication auth = new UsernamePasswordAuthenticationToken(username, "password1");

		assertFalse("permission should not be granted.", updateUserPermission.isAllowed(auth, 1L));
	}
}
