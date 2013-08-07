package ca.corefacility.bioinformatics.irida.security.permissions;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
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

import ca.corefacility.bioinformatics.irida.model.Project;
import ca.corefacility.bioinformatics.irida.model.Role;
import ca.corefacility.bioinformatics.irida.model.User;
import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectUserJoin;
import ca.corefacility.bioinformatics.irida.repositories.ProjectRepository;
import ca.corefacility.bioinformatics.irida.repositories.UserRepository;

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

	@Before
	public void setUp() {
		ApplicationContext applicationContext = mock(ApplicationContext.class);
		userRepository = mock(UserRepository.class);
		projectRepository = mock(ProjectRepository.class);
		readProjectPermission = new ReadProjectPermission();
		readProjectPermission.setApplicationContext(applicationContext);

		when(applicationContext.getBean(UserRepository.class)).thenReturn(userRepository);
		when(applicationContext.getBean(ProjectRepository.class)).thenReturn(projectRepository);
	}

	@Test
	public void testGrantPermission() {
		String username = "fbristow";
		User u = new User();
		u.setUsername(username);
		Project p = new Project();
		Collection<Join<Project, User>> projectUsers = new ArrayList<>();
		projectUsers.add(new ProjectUserJoin(p, u));

		when(userRepository.getUserByUsername(username)).thenReturn(u);
		when(projectRepository.read(1l)).thenReturn(p);
		when(userRepository.getUsersForProject(p)).thenReturn(projectUsers);

		Authentication auth = new UsernamePasswordAuthenticationToken("fbristow", "password1");

		assertTrue("permission was not granted.", readProjectPermission.isAllowed(auth, 1l));

		verify(userRepository).getUserByUsername(username);
		verify(projectRepository).read(1l);
		verify(userRepository).getUsersForProject(p);
	}

	@Test
	public void testRejectPermission() {
		String username = "fbristow";
		User u = new User();
		u.setUsername(username);
		Project p = new Project();
		Collection<Join<Project, User>> projectUsers = new ArrayList<>();
		projectUsers.add(new ProjectUserJoin(p, new User()));

		when(userRepository.getUserByUsername(username)).thenReturn(u);
		when(projectRepository.read(1l)).thenReturn(p);
		when(userRepository.getUsersForProject(p)).thenReturn(projectUsers);

		Authentication auth = new UsernamePasswordAuthenticationToken("fbristow", "password1");

		assertFalse("permission was granted.", readProjectPermission.isAllowed(auth, 1l));

		verify(userRepository).getUserByUsername(username);
		verify(projectRepository).read(1l);
		verify(userRepository).getUsersForProject(p);
	}

	@Test
	public void testPermitAdmin() {
		String username = "fbristow";
		User u = new User();
		u.setUsername(username);
		u.setRole(Role.ROLE_ADMIN);
		Project p = new Project();
		Collection<Join<Project, User>> projectUsers = new ArrayList<>();
		projectUsers.add(new ProjectUserJoin(p, new User()));

		when(userRepository.getUserByUsername(username)).thenReturn(u);
		when(projectRepository.read(1l)).thenReturn(p);
		when(userRepository.getUsersForProject(p)).thenReturn(projectUsers);

		Collection<GrantedAuthority> roles = new ArrayList<>();
		roles.add(Role.ROLE_ADMIN);

		Authentication auth = new UsernamePasswordAuthenticationToken("fbristow", "password1", roles);

		assertTrue("permission was not granted to admin.", readProjectPermission.isAllowed(auth, 1l));

		// we should fast pass through to permission granted for administrators.
		verifyZeroInteractions(userRepository);
		verifyZeroInteractions(projectRepository);
		verifyZeroInteractions(userRepository);
	}
}
