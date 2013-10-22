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

import com.google.common.collect.ImmutableList;

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
		when(applicationContext.getBean("projectRepository")).thenReturn(projectRepository);
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
		when(projectRepository.findOne(1l)).thenReturn(p);
		when(userRepository.getUsersForProject(p)).thenReturn(projectUsers);

		Authentication auth = new UsernamePasswordAuthenticationToken("fbristow", "password1");

		assertTrue("permission was not granted.", readProjectPermission.isAllowed(auth, 1l));

		verify(userRepository).getUserByUsername(username);
		verify(projectRepository).findOne(1l);
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
		when(projectRepository.findOne(1l)).thenReturn(p);
		when(userRepository.getUsersForProject(p)).thenReturn(projectUsers);

		Authentication auth = new UsernamePasswordAuthenticationToken("fbristow", "password1");

		assertFalse("permission was granted.", readProjectPermission.isAllowed(auth, 1l));

		verify(userRepository).getUserByUsername(username);
		verify(projectRepository).findOne(1l);
		verify(userRepository).getUsersForProject(p);
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
		Collection<GrantedAuthority> roles = ImmutableList.of((GrantedAuthority) new Role("ROLE_ADMIN",
				"Aww yiss, administrator privileges."));
		Authentication auth = new UsernamePasswordAuthenticationToken("fbristow", "password1", roles);

		assertTrue("permission should be granted to admin.", readProjectPermission.isAllowed(auth, 1l));

		// we should fast pass through permission granted for administrators.
		verifyZeroInteractions(userRepository);
		verifyZeroInteractions(projectRepository);
	}
}
