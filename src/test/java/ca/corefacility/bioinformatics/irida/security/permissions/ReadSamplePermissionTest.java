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
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectSampleJoin;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectUserJoin;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.user.Role;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.repositories.joins.project.ProjectSampleJoinRepository;
import ca.corefacility.bioinformatics.irida.repositories.joins.project.ProjectUserJoinRepository;
import ca.corefacility.bioinformatics.irida.repositories.sample.SampleRepository;
import ca.corefacility.bioinformatics.irida.repositories.user.UserRepository;

/**
 * Tests for {@link ReadSamplePermission}.
 * 
 * 
 */
public class ReadSamplePermissionTest {
	private ReadSamplePermission readSamplePermission;
	private UserRepository userRepository;
	private SampleRepository sampleRepository;
	private ProjectUserJoinRepository pujRepository;
	private ProjectSampleJoinRepository psjRepository;

	@Before
	public void setUp() {
		userRepository = mock(UserRepository.class);
		sampleRepository = mock(SampleRepository.class);
		pujRepository = mock(ProjectUserJoinRepository.class);
		psjRepository = mock(ProjectSampleJoinRepository.class);
		readSamplePermission = new ReadSamplePermission(sampleRepository, userRepository, pujRepository, psjRepository);
	}

	@Test
	public void testGrantPermission() {
		String username = "fbristow";
		User u = new User();
		u.setUsername(username);
		Project p = new Project();
		Sample s = new Sample();
		List<Join<Project, User>> projectUsers = new ArrayList<>();
		projectUsers.add(new ProjectUserJoin(p, u,ProjectRole.PROJECT_USER));
		List<Join<Project, Sample>> projectSampleList = new ArrayList<>();
		projectSampleList.add(new ProjectSampleJoin(p, s));

		when(userRepository.loadUserByUsername(username)).thenReturn(u);
		when(psjRepository.getProjectForSample(s)).thenReturn(projectSampleList);
		when(sampleRepository.findOne(1l)).thenReturn(s);
		when(pujRepository.getUsersForProject(p)).thenReturn(projectUsers);

		Authentication auth = new UsernamePasswordAuthenticationToken("fbristow", "password1");

		assertTrue("permission was not granted.", readSamplePermission.isAllowed(auth, 1l));

		verify(userRepository).loadUserByUsername(username);
		verify(sampleRepository).findOne(1l);
		verify(psjRepository).getProjectForSample(s);
		verify(pujRepository).getUsersForProject(p);
	}

	@Test
	public void testGrantPermissionWithDomainObject() {
		String username = "fbristow";
		User u = new User();
		u.setUsername(username);
		Project p = new Project();
		Sample s = new Sample();
		List<Join<Project, User>> projectUsers = new ArrayList<>();
		projectUsers.add(new ProjectUserJoin(p, u,ProjectRole.PROJECT_USER));
		List<Join<Project, Sample>> projectSampleList = new ArrayList<>();
		projectSampleList.add(new ProjectSampleJoin(p, s));

		when(userRepository.loadUserByUsername(username)).thenReturn(u);
		when(psjRepository.getProjectForSample(s)).thenReturn(projectSampleList);
		when(sampleRepository.findOne(1l)).thenReturn(s);
		when(pujRepository.getUsersForProject(p)).thenReturn(projectUsers);

		Authentication auth = new UsernamePasswordAuthenticationToken("fbristow", "password1");

		assertTrue("permission was not granted.", readSamplePermission.isAllowed(auth, s));

		verify(userRepository).loadUserByUsername(username);
		verify(psjRepository).getProjectForSample(s);
		verify(pujRepository).getUsersForProject(p);
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
		List<Join<Project, Sample>> projectSampleList = new ArrayList<>();
		projectSampleList.add(new ProjectSampleJoin(p, s));
		List<Join<Project, User>> projectUsers = new ArrayList<>();
		projectUsers.add(new ProjectUserJoin(p, new User(),ProjectRole.PROJECT_USER));

		when(userRepository.loadUserByUsername(username)).thenReturn(u);
		when(psjRepository.getProjectForSample(s)).thenReturn(projectSampleList);
		when(sampleRepository.findOne(1l)).thenReturn(s);
		when(pujRepository.getUsersForProject(p)).thenReturn(projectUsers);

		Authentication auth = new UsernamePasswordAuthenticationToken("fbristow", "password1");

		assertFalse("permission was granted.", readSamplePermission.isAllowed(auth, 1l));

		verify(userRepository).loadUserByUsername(username);
		verify(sampleRepository).findOne(1l);
		verify(psjRepository).getProjectForSample(s);
		verify(pujRepository).getUsersForProject(p);
	}

	@Test
	public void testPermitAdmin() {
		Collection<GrantedAuthority> roles = new ArrayList<>();
		roles.add(Role.ROLE_ADMIN);

		Authentication auth = new UsernamePasswordAuthenticationToken("fbristow", "password1", roles);

		assertTrue("permission was not granted to admin.", readSamplePermission.isAllowed(auth, 1l));

		// we should fast pass through to permission granted for administrators.
		verifyZeroInteractions(userRepository);
		verifyZeroInteractions(psjRepository);
		verifyZeroInteractions(userRepository);
		verifyZeroInteractions(sampleRepository);
	}
}
