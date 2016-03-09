package ca.corefacility.bioinformatics.irida.security.permissions;

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
import ca.corefacility.bioinformatics.irida.repositories.joins.sample.SampleSequenceFileJoinRepository;
import ca.corefacility.bioinformatics.irida.repositories.sequencefile.SequenceFileRepository;

/**
 * Tests for {@link ReadSequenceFilePermission}.
 * 
 * 
 */
public class ReadSequenceFilePermissionTest {
	private ReadSequenceFilePermission readSequenceFilePermission;
	private SequenceFileRepository sequenceFileRepository;
	private SampleSequenceFileJoinRepository ssfRepository;
	private ReadSamplePermission readSamplePermission;

	@Before
	public void setUp() {
		ssfRepository = mock(SampleSequenceFileJoinRepository.class);
		sequenceFileRepository = mock(SequenceFileRepository.class);
		readSamplePermission = mock(ReadSamplePermission.class);

		readSequenceFilePermission = new ReadSequenceFilePermission(sequenceFileRepository, ssfRepository,
				readSamplePermission);
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

		SequenceFile sf = new SequenceFile();
		SampleSequenceFileJoin sampleSequenceFile = new SampleSequenceFileJoin(s, sf);

		when(sequenceFileRepository.findOne(1L)).thenReturn(sf);
		when(ssfRepository.getSampleForSequenceFile(sf)).thenReturn(sampleSequenceFile);
		when(readSamplePermission.isAllowed(any(), eq(s))).thenReturn(true);

		Authentication auth = new UsernamePasswordAuthenticationToken("fbristow", "password1");

		assertTrue("permission was not granted.", readSequenceFilePermission.isAllowed(auth, 1L));

		verify(sequenceFileRepository).findOne(1L);
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
		projectUsers.add(new ProjectUserJoin(p, new User(), ProjectRole.PROJECT_USER));
		List<Join<Project, Sample>> projectSampleList = new ArrayList<>();
		projectSampleList.add(new ProjectSampleJoin(p, s));
		SequenceFile sf = new SequenceFile();
		SampleSequenceFileJoin sampleSequenceFile = new SampleSequenceFileJoin(s, sf);

		when(sequenceFileRepository.findOne(1L)).thenReturn(sf);
		when(ssfRepository.getSampleForSequenceFile(sf)).thenReturn(sampleSequenceFile);
		when(readSamplePermission.isAllowed(any(), eq(s))).thenReturn(false);

		Authentication auth = new UsernamePasswordAuthenticationToken("fbristow", "password1");

		assertFalse("permission was granted.", readSequenceFilePermission.isAllowed(auth, 1L));

		verify(sequenceFileRepository).findOne(1L);
		verify(ssfRepository).getSampleForSequenceFile(sf);
	}

	@Test
	public void testPermitAdmin() {
		Collection<GrantedAuthority> roles = new ArrayList<>();
		roles.add(Role.ROLE_ADMIN);

		Authentication auth = new UsernamePasswordAuthenticationToken("fbristow", "password1", roles);

		assertTrue("permission was not granted to admin.", readSequenceFilePermission.isAllowed(auth, 1L));

		// we should fast pass through to permission granted for administrators.
		verifyZeroInteractions(ssfRepository);
		verifyZeroInteractions(sequenceFileRepository);
		verifyZeroInteractions(readSamplePermission);
	}
}
