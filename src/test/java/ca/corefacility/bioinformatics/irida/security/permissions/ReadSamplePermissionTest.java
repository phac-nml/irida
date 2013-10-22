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
import ca.corefacility.bioinformatics.irida.model.Sample;
import ca.corefacility.bioinformatics.irida.model.User;
import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectSampleJoin;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectUserJoin;
import ca.corefacility.bioinformatics.irida.repositories.ProjectRepository;
import ca.corefacility.bioinformatics.irida.repositories.SampleRepository;
import ca.corefacility.bioinformatics.irida.repositories.UserRepository;
import com.google.common.collect.Lists;

/**
 * Tests for {@link ReadSamplePermission}.
 * 
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 * 
 */
public class ReadSamplePermissionTest {
	private ReadSamplePermission readSamplePermission;
	private UserRepository userRepository;
	private ProjectRepository projectRepository;
	private SampleRepository sampleRepository;

	@Before
	public void setUp() {
		ApplicationContext applicationContext = mock(ApplicationContext.class);
		userRepository = mock(UserRepository.class);
		projectRepository = mock(ProjectRepository.class);
		sampleRepository = mock(SampleRepository.class);
		readSamplePermission = new ReadSamplePermission();
		readSamplePermission.setApplicationContext(applicationContext);

		when(applicationContext.getBean(UserRepository.class)).thenReturn(userRepository);
		when(applicationContext.getBean(ProjectRepository.class)).thenReturn(projectRepository);
		when(applicationContext.getBean("sampleRepository")).thenReturn(sampleRepository);
	}

	@Test
	public void testGrantPermission() {
		String username = "fbristow";
		User u = new User();
		u.setUsername(username);
		Project p = new Project();
		Sample s = new Sample();
		Collection<Join<Project, User>> projectUsers = new ArrayList<>();
		projectUsers.add(new ProjectUserJoin(p, u));
		Collection<ProjectSampleJoin> projectSampleList = Lists.newArrayList(new ProjectSampleJoin(p, s));

		when(userRepository.getUserByUsername(username)).thenReturn(u);
		when(projectRepository.getProjectForSample(s)).thenReturn(projectSampleList);
		when(sampleRepository.findOne(1l)).thenReturn(s);
		when(userRepository.getUsersForProject(p)).thenReturn(projectUsers);

		Authentication auth = new UsernamePasswordAuthenticationToken("fbristow", "password1");

		assertTrue("permission was not granted.", readSamplePermission.isAllowed(auth, 1l));

		verify(userRepository).getUserByUsername(username);
		verify(sampleRepository).findOne(1l);
		verify(projectRepository).getProjectForSample(s);
		verify(userRepository).getUsersForProject(p);
	}

	@Test
	public void testGrantPermissionWithDomainObject() {
		String username = "fbristow";
		User u = new User();
		u.setUsername(username);
		Project p = new Project();
		Sample s = new Sample();
		Collection<Join<Project, User>> projectUsers = new ArrayList<>();
		projectUsers.add(new ProjectUserJoin(p, u));
		Collection<ProjectSampleJoin> projectSampleList = Lists.newArrayList(new ProjectSampleJoin(p, s));


		when(userRepository.getUserByUsername(username)).thenReturn(u);
		when(projectRepository.getProjectForSample(s)).thenReturn(projectSampleList);
		when(sampleRepository.findOne(1l)).thenReturn(s);
		when(userRepository.getUsersForProject(p)).thenReturn(projectUsers);

		Authentication auth = new UsernamePasswordAuthenticationToken("fbristow", "password1");

		assertTrue("permission was not granted.", readSamplePermission.isAllowed(auth, s));

		verify(userRepository).getUserByUsername(username);
		verify(projectRepository).getProjectForSample(s);
		verify(userRepository).getUsersForProject(p);
		// we didn't need to load the domain object for this test.
		verifyZeroInteractions(sampleRepository);
	}

	@Test
	public void testRejectPermission() {
		String username = "fbristow";
		User u = new User();
		u.setUsername(username);
		Project p = new Project();
		Sample s = new Sample();
		Collection<ProjectSampleJoin> projectSampleList = Lists.newArrayList(new ProjectSampleJoin(p, s));


		Collection<Join<Project, User>> projectUsers = new ArrayList<>();
		projectUsers.add(new ProjectUserJoin(p, new User()));

		when(userRepository.getUserByUsername(username)).thenReturn(u);
		when(projectRepository.getProjectForSample(s)).thenReturn(projectSampleList);
		when(sampleRepository.findOne(1l)).thenReturn(s);
		when(userRepository.getUsersForProject(p)).thenReturn(projectUsers);

		Authentication auth = new UsernamePasswordAuthenticationToken("fbristow", "password1");

		assertFalse("permission was granted.", readSamplePermission.isAllowed(auth, 1l));

		verify(userRepository).getUserByUsername(username);
		verify(sampleRepository).findOne(1l);
		verify(projectRepository).getProjectForSample(s);
		verify(userRepository).getUsersForProject(p);
	}

	@Test
	public void testPermitAdmin() {
		Collection<GrantedAuthority> roles = new ArrayList<>();
		roles.add(Role.ROLE_ADMIN);

		Authentication auth = new UsernamePasswordAuthenticationToken("fbristow", "password1", roles);

		assertTrue("permission was not granted to admin.", readSamplePermission.isAllowed(auth, 1l));

		// we should fast pass through to permission granted for administrators.
		verifyZeroInteractions(userRepository);
		verifyZeroInteractions(projectRepository);
		verifyZeroInteractions(userRepository);
		verifyZeroInteractions(sampleRepository);
	}
}
