package ca.corefacility.bioinformatics.irida.security.permissions.project;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import ca.corefacility.bioinformatics.irida.model.enums.ProjectMetadataRole;
import ca.corefacility.bioinformatics.irida.model.enums.ProjectRole;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectUserJoin;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.user.Role;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.model.user.group.UserGroup;
import ca.corefacility.bioinformatics.irida.model.user.group.UserGroupProjectJoin;
import ca.corefacility.bioinformatics.irida.repositories.ProjectRepository;
import ca.corefacility.bioinformatics.irida.repositories.joins.project.ProjectUserJoinRepository;
import ca.corefacility.bioinformatics.irida.repositories.joins.project.UserGroupProjectJoinRepository;
import ca.corefacility.bioinformatics.irida.repositories.user.UserRepository;

import com.google.common.collect.ImmutableList;

/**
 * Tests for {@link ReadProjectPermission}.
 * 
 * 
 */
public class ReadProjectPermissionTest {
	private ReadProjectPermission readProjectPermission;
	private UserRepository userRepository;
	private ProjectRepository projectRepository;
	private ProjectUserJoinRepository pujRepository;
	private UserGroupProjectJoinRepository ugpjRepository;

	@BeforeEach
	public void setUp() {
		userRepository = mock(UserRepository.class);
		projectRepository = mock(ProjectRepository.class);
		pujRepository = mock(ProjectUserJoinRepository.class);
		ugpjRepository = mock(UserGroupProjectJoinRepository.class);
		readProjectPermission = new ReadProjectPermission(projectRepository, userRepository, pujRepository,
				ugpjRepository);
	}

	@Test
	public void testGrantPermission() {
		String username = "fbristow";
		User u = new User();
		u.setUsername(username);
		Project p = new Project();
		ProjectUserJoin projectUser = new ProjectUserJoin(p, u, ProjectRole.PROJECT_USER);

		when(userRepository.loadUserByUsername(username)).thenReturn(u);
		when(projectRepository.findById(1L)).thenReturn(Optional.of(p));
		when(pujRepository.getProjectJoinForUser(p, u)).thenReturn(projectUser);

		Authentication auth = new UsernamePasswordAuthenticationToken("fbristow", "password1");

		assertTrue(readProjectPermission.isAllowed(auth, 1L), "permission was not granted.");

		verify(userRepository).loadUserByUsername(username);
		verify(projectRepository).findById(1L);
		verify(pujRepository).getProjectJoinForUser(p, u);
	}

	@Test
	public void testRejectPermission() {
		String username = "fbristow";
		User u = new User();
		u.setUsername(username);
		Project p = new Project();

		when(userRepository.loadUserByUsername(username)).thenReturn(u);
		when(projectRepository.findById(1L)).thenReturn(Optional.of(p));
		when(pujRepository.getProjectJoinForUser(p, u)).thenReturn(null);
		when(ugpjRepository.findGroupsForProjectAndUser(p, u)).thenReturn(ImmutableList.of());

		Authentication auth = new UsernamePasswordAuthenticationToken("fbristow", "password1");

		assertFalse(readProjectPermission.isAllowed(auth, 1L), "permission was granted.");

		verify(userRepository).loadUserByUsername(username);
		verify(projectRepository).findById(1L);
		verify(pujRepository).getProjectJoinForUser(p, u);
		verify(ugpjRepository).findGroupsForProjectAndUser(p, u);
	}

	@Test
	public void testPermitAdmin() {
		Collection<GrantedAuthority> roles = new ArrayList<>();
		roles.add(Role.ROLE_ADMIN);

		Authentication auth = new UsernamePasswordAuthenticationToken("fbristow", "password1", roles);
		when(projectRepository.findById(1L)).thenReturn(Optional.of(new Project()));

		assertTrue(readProjectPermission.isAllowed(auth, 1L), "permission should be granted to admin.");

		// we should fast pass through to permission granted for administrators.
		verifyNoInteractions(userRepository);
	}

	@Test
	public void testGrantPermissionByGroup() {
		final String username = "fbristow";
		final User u = new User();
		u.setUsername(username);
		final Project p = new Project();
		final UserGroup g = new UserGroup("The group");

		final List<UserGroupProjectJoin> projectGroups = new ArrayList<>();
		projectGroups.add(new UserGroupProjectJoin(p, g, ProjectRole.PROJECT_USER, ProjectMetadataRole.LEVEL_1));

		when(userRepository.loadUserByUsername(username)).thenReturn(u);
		when(projectRepository.findById(1L)).thenReturn(Optional.of(p));
		when(pujRepository.getProjectJoinForUser(p, u)).thenReturn(null);
		when(ugpjRepository.findGroupsForProjectAndUser(p, u)).thenReturn(projectGroups);

		Authentication auth = new UsernamePasswordAuthenticationToken("fbristow", "password1");

		assertTrue(readProjectPermission.isAllowed(auth, 1L), "permission should be granted by user group.");

		verify(userRepository).loadUserByUsername(username);
		verify(projectRepository).findById(1L);
		verify(pujRepository).getProjectJoinForUser(p, u);
		verify(ugpjRepository).findGroupsForProjectAndUser(p, u);
	}
}
