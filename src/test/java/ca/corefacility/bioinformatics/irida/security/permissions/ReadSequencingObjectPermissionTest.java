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
import ca.corefacility.bioinformatics.irida.model.sample.SampleSequencingObjectJoin;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SingleEndSequenceFile;
import ca.corefacility.bioinformatics.irida.model.user.Role;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.repositories.joins.project.ProjectSampleJoinRepository;
import ca.corefacility.bioinformatics.irida.repositories.joins.project.ProjectUserJoinRepository;
import ca.corefacility.bioinformatics.irida.repositories.joins.sample.SampleSequencingObjectJoinRepository;
import ca.corefacility.bioinformatics.irida.repositories.sample.SampleRepository;
import ca.corefacility.bioinformatics.irida.repositories.sequencefile.SequencingObjectRepository;
import ca.corefacility.bioinformatics.irida.repositories.user.UserRepository;

/**
 * Testing the permission for {@link ReadSequencingObjectPermission}
 */
public class ReadSequencingObjectPermissionTest {
	private ReadSequencingObjectPermission permission;
	ReadSamplePermission samplePermission;
	private UserRepository userRepository;
	private SequencingObjectRepository sequencingObjectRepository;
	private ProjectUserJoinRepository pujRepository;
	private ProjectSampleJoinRepository psjRepository;
	private SampleSequencingObjectJoinRepository ssoRepository;
	private SampleRepository sampleRepository;

	@Before
	public void setUp() {
		userRepository = mock(UserRepository.class);
		ssoRepository = mock(SampleSequencingObjectJoinRepository.class);
		sequencingObjectRepository = mock(SequencingObjectRepository.class);
		pujRepository = mock(ProjectUserJoinRepository.class);
		psjRepository = mock(ProjectSampleJoinRepository.class);
		sampleRepository = mock(SampleRepository.class);

		samplePermission = new ReadSamplePermission(sampleRepository, userRepository, pujRepository, psjRepository);
		permission = new ReadSequencingObjectPermission(sequencingObjectRepository, samplePermission, ssoRepository);
	}

	@Test
	public void testGrantPermission() {
		String username = "fbristow";
		User u = new User();
		u.setUsername(username);
		Project p = new Project();
		Sample s = new Sample();
		List<Join<Project, User>> projectUsers = new ArrayList<>();
		projectUsers.add(new ProjectUserJoin(p, u, ProjectRole.PROJECT_USER));
		List<Join<Project, Sample>> projectSampleList = new ArrayList<>();
		projectSampleList.add(new ProjectSampleJoin(p, s));

		SingleEndSequenceFile sf = new SingleEndSequenceFile(null);

		SampleSequencingObjectJoin join = new SampleSequencingObjectJoin(s, sf);

		when(userRepository.loadUserByUsername(username)).thenReturn(u);
		when(psjRepository.getProjectForSample(s)).thenReturn(projectSampleList);
		when(sequencingObjectRepository.findOne(1L)).thenReturn(sf);
		when(pujRepository.getUsersForProject(p)).thenReturn(projectUsers);
		when(ssoRepository.getSampleForSequencingObject(sf)).thenReturn(join);

		Authentication auth = new UsernamePasswordAuthenticationToken("fbristow", "password1");

		assertTrue("permission was not granted.", permission.isAllowed(auth, 1L));

		verify(userRepository).loadUserByUsername(username);
		verify(sequencingObjectRepository).findOne(1L);
		verify(psjRepository).getProjectForSample(s);
		verify(pujRepository).getUsersForProject(p);
		verify(ssoRepository).getSampleForSequencingObject(sf);
	}

	@Test
	public void testRejectPermission() {
		String username = "fbristow";
		User u = new User();
		u.setUsername(username);
		Project p = new Project();
		Sample s = new Sample();
		List<Join<Project, User>> projectUsers = new ArrayList<>();
		projectUsers.add(new ProjectUserJoin(p, new User(), ProjectRole.PROJECT_USER));
		List<Join<Project, Sample>> projectSampleList = new ArrayList<>();
		projectSampleList.add(new ProjectSampleJoin(p, s));

		SingleEndSequenceFile sf = new SingleEndSequenceFile(null);
		SampleSequencingObjectJoin join = new SampleSequencingObjectJoin(s, sf);

		when(userRepository.loadUserByUsername(username)).thenReturn(u);
		when(psjRepository.getProjectForSample(s)).thenReturn(projectSampleList);
		when(sequencingObjectRepository.findOne(1L)).thenReturn(sf);
		when(pujRepository.getUsersForProject(p)).thenReturn(projectUsers);
		when(ssoRepository.getSampleForSequencingObject(sf)).thenReturn(join);

		Authentication auth = new UsernamePasswordAuthenticationToken("fbristow", "password1");

		assertFalse("permission was granted.", permission.isAllowed(auth, 1L));

		verify(userRepository).loadUserByUsername(username);
		verify(sequencingObjectRepository).findOne(1L);
		verify(psjRepository).getProjectForSample(s);
		verify(pujRepository).getUsersForProject(p);
		verify(ssoRepository).getSampleForSequencingObject(sf);
	}

	@Test
	public void testPermitAdmin() {
		Collection<GrantedAuthority> roles = new ArrayList<>();
		roles.add(Role.ROLE_ADMIN);

		Authentication auth = new UsernamePasswordAuthenticationToken("fbristow", "password1", roles);

		assertTrue("permission was not granted to admin.", permission.isAllowed(auth, 1L));

		// we should fast pass through to permission granted for administrators.
		verifyZeroInteractions(userRepository);
		verifyZeroInteractions(psjRepository);
		verifyZeroInteractions(userRepository);
		verifyZeroInteractions(ssoRepository);
		verifyZeroInteractions(sequencingObjectRepository);
	}
}
