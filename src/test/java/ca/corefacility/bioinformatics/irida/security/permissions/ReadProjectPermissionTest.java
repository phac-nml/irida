package ca.corefacility.bioinformatics.irida.security.permissions;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import ca.corefacility.bioinformatics.irida.model.enums.ProjectRole;
import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectUserJoin;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.user.Role;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.repositories.ProjectRepository;
import ca.corefacility.bioinformatics.irida.repositories.joins.project.ProjectUserJoinRepository;
import ca.corefacility.bioinformatics.irida.repositories.user.UserRepository;

import com.google.common.collect.ImmutableList;

/**
 * Tests for {@link ReadProjectPermission}.
 * 
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 * 
 */
public class ReadProjectPermissionTest {
	private ReadProjectPermission readProjectPermission;
	private UserRepository userRepository;
	private ProjectRepository projectRepository;
	private ProjectUserJoinRepository pujRepository;

	@Before
	public void setUp() {
		userRepository = mock(UserRepository.class);
		projectRepository = mock(ProjectRepository.class);
		pujRepository = mock(ProjectUserJoinRepository.class);
		readProjectPermission = new ReadProjectPermission(projectRepository, userRepository, pujRepository);		
	}
	
	@Test
	public void testGrantPermission() {
		String username = "fbristow";
		User u = new User();
		u.setUsername(username);
		Project p = new Project();
		List<Join<Project, User>> projectUsers = new ArrayList<>();
		projectUsers.add(new ProjectUserJoin(p, u,ProjectRole.PROJECT_USER));

		when(userRepository.loadUserByUsername(username)).thenReturn(u);
		when(projectRepository.findOne(1l)).thenReturn(p);
		when(pujRepository.getUsersForProject(p)).thenReturn(projectUsers);

		Authentication auth = new UsernamePasswordAuthenticationToken("fbristow", "password1");

		assertTrue("permission was not granted.", readProjectPermission.isAllowed(auth, 1l));

		verify(userRepository).loadUserByUsername(username);
		verify(projectRepository).findOne(1l);
		verify(pujRepository).getUsersForProject(p);
	}

	@Test
	public void testRejectPermission() {
		String username = "fbristow";
		User u = new User();
		u.setUsername(username);
		Project p = new Project();
		List<Join<Project, User>> projectUsers = new ArrayList<>();
		projectUsers.add(new ProjectUserJoin(p, new User(),ProjectRole.PROJECT_USER));

		when(userRepository.loadUserByUsername(username)).thenReturn(u);
		when(projectRepository.findOne(1l)).thenReturn(p);
		when(pujRepository.getUsersForProject(p)).thenReturn(projectUsers);

		Authentication auth = new UsernamePasswordAuthenticationToken("fbristow", "password1");

		assertFalse("permission was granted.", readProjectPermission.isAllowed(auth, 1l));

		verify(userRepository).loadUserByUsername(username);
		verify(projectRepository).findOne(1l);
		verify(pujRepository).getUsersForProject(p);
	}

	@Test
	public void testPermitAdmin() {
		Collection<GrantedAuthority> roles = new ArrayList<>();
		roles.add(Role.ROLE_ADMIN);

		Authentication auth = new UsernamePasswordAuthenticationToken("fbristow", "password1", roles);

		assertTrue("permission should be granted to admin.", readProjectPermission.isAllowed(auth, 1l));

		// we should fast pass through to permission granted for administrators.
		verifyZeroInteractions(userRepository);
		verifyZeroInteractions(projectRepository);
	}

	@Test
	public void testPermitAdminWithoutDescription() {
		// Collection<GrantedAuthority> roles = new ArrayList<>();
		Collection<GrantedAuthority> roles = ImmutableList.of((GrantedAuthority) Role.ROLE_ADMIN);
		Authentication auth = new UsernamePasswordAuthenticationToken("fbristow", "password1", roles);

		assertTrue("permission should be granted to admin.", readProjectPermission.isAllowed(auth, 1l));

		// we should fast pass through permission granted for administrators.
		verifyZeroInteractions(userRepository);
		verifyZeroInteractions(projectRepository);
	}
}
