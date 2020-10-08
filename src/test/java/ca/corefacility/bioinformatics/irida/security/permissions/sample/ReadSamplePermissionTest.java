package ca.corefacility.bioinformatics.irida.security.permissions.sample;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import javax.swing.text.html.Option;

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
import ca.corefacility.bioinformatics.irida.repositories.sample.SampleRepository;
import ca.corefacility.bioinformatics.irida.security.permissions.project.ReadProjectPermission;
import ca.corefacility.bioinformatics.irida.security.permissions.sample.ReadSamplePermission;

/**
 * Tests for {@link ReadSamplePermission}.
 * 
 * 
 */
public class ReadSamplePermissionTest {
	private ReadSamplePermission readSamplePermission;
	private SampleRepository sampleRepository;
	private ProjectSampleJoinRepository psjRepository;
	private ReadProjectPermission readProjectPermission;

	@Before
	public void setUp() {
		sampleRepository = mock(SampleRepository.class);
		psjRepository = mock(ProjectSampleJoinRepository.class);
		readProjectPermission = mock(ReadProjectPermission.class);
		readSamplePermission = new ReadSamplePermission(sampleRepository, psjRepository, readProjectPermission);
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
		projectSampleList.add(new ProjectSampleJoin(p, s, true));

		when(psjRepository.getProjectForSample(s)).thenReturn(projectSampleList);
		when(sampleRepository.findById(1L)).thenReturn(Optional.of(s));
		when(readProjectPermission.isAllowed(any(), eq(p))).thenReturn(true);

		Authentication auth = new UsernamePasswordAuthenticationToken("fbristow", "password1");

		assertTrue("permission was not granted.", readSamplePermission.isAllowed(auth, 1L));

		verify(sampleRepository).findById(1L);
		verify(psjRepository).getProjectForSample(s);
		verify(readProjectPermission).isAllowed(any(), eq(p));
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
		projectSampleList.add(new ProjectSampleJoin(p, s, true));

		when(psjRepository.getProjectForSample(s)).thenReturn(projectSampleList);
		when(sampleRepository.findById(1L)).thenReturn(Optional.of(s));
		when(readProjectPermission.isAllowed(any(), eq(p))).thenReturn(true);

		Authentication auth = new UsernamePasswordAuthenticationToken("fbristow", "password1");

		assertTrue("permission was not granted.", readSamplePermission.isAllowed(auth, s));

		verify(psjRepository).getProjectForSample(s);
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
		projectSampleList.add(new ProjectSampleJoin(p, s, true));
		List<Join<Project, User>> projectUsers = new ArrayList<>();
		projectUsers.add(new ProjectUserJoin(p, new User(),ProjectRole.PROJECT_USER));

		when(psjRepository.getProjectForSample(s)).thenReturn(projectSampleList);
		when(sampleRepository.findById(1L)).thenReturn(Optional.of(s));
		when(readProjectPermission.isAllowed(any(), eq(p))).thenReturn(false);

		Authentication auth = new UsernamePasswordAuthenticationToken("fbristow", "password1");

		assertFalse("permission was granted.", readSamplePermission.isAllowed(auth, 1L));

		verify(sampleRepository).findById(1L);
		verify(psjRepository).getProjectForSample(s);
	}

	@Test
	public void testPermitAdmin() {
		Collection<GrantedAuthority> roles = new ArrayList<>();
		roles.add(Role.ROLE_ADMIN);

		Authentication auth = new UsernamePasswordAuthenticationToken("fbristow", "password1", roles);
		when(sampleRepository.findById(1L)).thenReturn(Optional.of(new Sample()));

		assertTrue("permission was not granted to admin.", readSamplePermission.isAllowed(auth, 1L));

		// we should fast pass through to permission granted for administrators.
		verifyZeroInteractions(psjRepository);
	}
}
