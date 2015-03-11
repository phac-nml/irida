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
import ca.corefacility.bioinformatics.irida.model.sample.SampleSequenceFileJoin;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.user.Role;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.repositories.joins.project.ProjectSampleJoinRepository;
import ca.corefacility.bioinformatics.irida.repositories.joins.project.ProjectUserJoinRepository;
import ca.corefacility.bioinformatics.irida.repositories.joins.sample.SampleSequenceFileJoinRepository;
import ca.corefacility.bioinformatics.irida.repositories.sequencefile.SequenceFileRepository;
import ca.corefacility.bioinformatics.irida.repositories.user.UserRepository;

/**
 * Tests for {@link ReadSequenceFilePermission}.
 * 
 * 
 */
public class ReadSequenceFilePermissionTest {
	private ReadSequenceFilePermission readSequenceFilePermission;
	private UserRepository userRepository;
	private SequenceFileRepository sequenceFileRepository;
	private ProjectUserJoinRepository pujRepository;
	private ProjectSampleJoinRepository psjRepository;
	private SampleSequenceFileJoinRepository ssfRepository;

	@Before
	public void setUp() {
		userRepository = mock(UserRepository.class);
		ssfRepository = mock(SampleSequenceFileJoinRepository.class);
		sequenceFileRepository = mock(SequenceFileRepository.class);
		pujRepository = mock(ProjectUserJoinRepository.class);
		psjRepository = mock(ProjectSampleJoinRepository.class);

		readSequenceFilePermission = new ReadSequenceFilePermission(sequenceFileRepository, userRepository, pujRepository, psjRepository,
				ssfRepository);
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

		SequenceFile sf = new SequenceFile();
		SampleSequenceFileJoin sampleSequenceFile = new SampleSequenceFileJoin(s, sf);

		when(userRepository.loadUserByUsername(username)).thenReturn(u);
		when(psjRepository.getProjectForSample(s)).thenReturn(projectSampleList);
		when(sequenceFileRepository.findOne(1l)).thenReturn(sf);
		when(pujRepository.getUsersForProject(p)).thenReturn(projectUsers);
		when(ssfRepository.getSampleForSequenceFile(sf)).thenReturn(sampleSequenceFile);

		Authentication auth = new UsernamePasswordAuthenticationToken("fbristow", "password1");

		assertTrue("permission was not granted.", readSequenceFilePermission.isAllowed(auth, 1l));

		verify(userRepository).loadUserByUsername(username);
		verify(sequenceFileRepository).findOne(1l);
		verify(psjRepository).getProjectForSample(s);
		verify(pujRepository).getUsersForProject(p);
		verify(ssfRepository).getSampleForSequenceFile(sf);
	}

	@Test
	public void testRejectPermission() {
		String username = "fbristow";
		User u = new User();
		u.setUsername(username);
		Project p = new Project();
		Sample s = new Sample();
		List<Join<Project, User>> projectUsers = new ArrayList<>();
		projectUsers.add(new ProjectUserJoin(p, new User(),ProjectRole.PROJECT_USER));
		List<Join<Project, Sample>> projectSampleList = new ArrayList<>();
		projectSampleList.add(new ProjectSampleJoin(p, s));
		SequenceFile sf = new SequenceFile();
		SampleSequenceFileJoin sampleSequenceFile = new SampleSequenceFileJoin(s, sf);

		when(userRepository.loadUserByUsername(username)).thenReturn(u);
		when(psjRepository.getProjectForSample(s)).thenReturn(projectSampleList);
		when(sequenceFileRepository.findOne(1l)).thenReturn(sf);
		when(pujRepository.getUsersForProject(p)).thenReturn(projectUsers);
		when(ssfRepository.getSampleForSequenceFile(sf)).thenReturn(sampleSequenceFile);

		Authentication auth = new UsernamePasswordAuthenticationToken("fbristow", "password1");

		assertFalse("permission was granted.", readSequenceFilePermission.isAllowed(auth, 1l));

		verify(userRepository).loadUserByUsername(username);
		verify(sequenceFileRepository).findOne(1l);
		verify(psjRepository).getProjectForSample(s);
		verify(pujRepository).getUsersForProject(p);
		verify(ssfRepository).getSampleForSequenceFile(sf);
	}

	@Test
	public void testPermitAdmin() {
		Collection<GrantedAuthority> roles = new ArrayList<>();
		roles.add(Role.ROLE_ADMIN);

		Authentication auth = new UsernamePasswordAuthenticationToken("fbristow", "password1", roles);

		assertTrue("permission was not granted to admin.", readSequenceFilePermission.isAllowed(auth, 1l));

		// we should fast pass through to permission granted for administrators.
		verifyZeroInteractions(userRepository);
		verifyZeroInteractions(psjRepository);
		verifyZeroInteractions(userRepository);
		verifyZeroInteractions(ssfRepository);
		verifyZeroInteractions(sequenceFileRepository);
	}
}
