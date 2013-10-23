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
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import ca.corefacility.bioinformatics.irida.model.Project;
import ca.corefacility.bioinformatics.irida.model.Role;
import ca.corefacility.bioinformatics.irida.model.Sample;
import ca.corefacility.bioinformatics.irida.model.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.User;
import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectSampleJoin;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectUserJoin;
import ca.corefacility.bioinformatics.irida.model.joins.impl.SampleSequenceFileJoin;
import ca.corefacility.bioinformatics.irida.repositories.SampleRepository;
import ca.corefacility.bioinformatics.irida.repositories.SequenceFileRepository;
import ca.corefacility.bioinformatics.irida.repositories.UserRepository;
import ca.corefacility.bioinformatics.irida.repositories.joins.project.ProjectSampleJoinRepository;
import ca.corefacility.bioinformatics.irida.repositories.joins.project.ProjectUserJoinRepository;

/**
 * Tests for {@link ReadSequenceFilePermission}.
 * 
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 * 
 */
public class ReadSequenceFilePermissionTest {
	private ReadSequenceFilePermission readSequenceFilePermission;
	private UserRepository userRepository;
	private SampleRepository sampleRepository;
	private SequenceFileRepository sequenceFileRepository;
	private ProjectUserJoinRepository pujRepository;
	private ProjectSampleJoinRepository psjRepository;

	@Before
	public void setUp() {
		ApplicationContext applicationContext = mock(ApplicationContext.class);
		userRepository = mock(UserRepository.class);
		sampleRepository = mock(SampleRepository.class);
		sequenceFileRepository = mock(SequenceFileRepository.class);
		pujRepository = mock(ProjectUserJoinRepository.class);
		psjRepository = mock(ProjectSampleJoinRepository.class);

		readSequenceFilePermission = new ReadSequenceFilePermission();
		readSequenceFilePermission.setApplicationContext(applicationContext);

		when(applicationContext.getBean(UserRepository.class)).thenReturn(userRepository);
		when(applicationContext.getBean(SampleRepository.class)).thenReturn(sampleRepository);
		when(applicationContext.getBean(ProjectSampleJoinRepository.class)).thenReturn(psjRepository);
		when(applicationContext.getBean(ProjectUserJoinRepository.class)).thenReturn(pujRepository);
		when(applicationContext.getBean("sequenceFileRepository")).thenReturn(sequenceFileRepository);
	}

	@Test
	public void testGrantPermission() {
		String username = "fbristow";
		User u = new User();
		u.setUsername(username);
		Project p = new Project();
		Sample s = new Sample();
		List<Join<Project, User>> projectUsers = new ArrayList<>();
		projectUsers.add(new ProjectUserJoin(p, u));
		List<Join<Project,Sample>> projectSampleList = new ArrayList<>();
		projectSampleList.add(new ProjectSampleJoin(p, s));

		
		SequenceFile sf = new SequenceFile();
		SampleSequenceFileJoin sampleSequenceFile = new SampleSequenceFileJoin(s, sf);

		when(userRepository.loadUserByUsername(username)).thenReturn(u);
		when(psjRepository.getProjectForSample(s)).thenReturn(projectSampleList);
		when(sequenceFileRepository.findOne(1l)).thenReturn(sf);
		when(pujRepository.getUsersForProject(p)).thenReturn(projectUsers);
		when(sampleRepository.getSampleForSequenceFile(sf)).thenReturn(sampleSequenceFile);

		Authentication auth = new UsernamePasswordAuthenticationToken("fbristow", "password1");

		assertTrue("permission was not granted.", readSequenceFilePermission.isAllowed(auth, 1l));

		verify(userRepository).loadUserByUsername(username);
		verify(sequenceFileRepository).findOne(1l);
		verify(psjRepository).getProjectForSample(s);
		verify(pujRepository).getUsersForProject(p);
		verify(sampleRepository).getSampleForSequenceFile(sf);
	}

	@Test
	public void testRejectPermission() {
		String username = "fbristow";
		User u = new User();
		u.setUsername(username);
		Project p = new Project();
		Sample s = new Sample();
		List<Join<Project, User>> projectUsers = new ArrayList<>();
		projectUsers.add(new ProjectUserJoin(p, new User()));
		List<Join<Project, Sample>> projectSampleList = new ArrayList<>();
		projectSampleList.add(new ProjectSampleJoin(p, s));
		SequenceFile sf = new SequenceFile();
		SampleSequenceFileJoin sampleSequenceFile = new SampleSequenceFileJoin(s, sf);

		when(userRepository.loadUserByUsername(username)).thenReturn(u);
		when(psjRepository.getProjectForSample(s)).thenReturn(projectSampleList);
		when(sequenceFileRepository.findOne(1l)).thenReturn(sf);
		when(pujRepository.getUsersForProject(p)).thenReturn(projectUsers);
		when(sampleRepository.getSampleForSequenceFile(sf)).thenReturn(sampleSequenceFile);

		Authentication auth = new UsernamePasswordAuthenticationToken("fbristow", "password1");

		assertFalse("permission was granted.", readSequenceFilePermission.isAllowed(auth, 1l));

		verify(userRepository).loadUserByUsername(username);
		verify(sequenceFileRepository).findOne(1l);
		verify(psjRepository).getProjectForSample(s);
		verify(pujRepository).getUsersForProject(p);
		verify(sampleRepository).getSampleForSequenceFile(sf);
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
		verifyZeroInteractions(sampleRepository);
		verifyZeroInteractions(sequenceFileRepository);
	}
}
