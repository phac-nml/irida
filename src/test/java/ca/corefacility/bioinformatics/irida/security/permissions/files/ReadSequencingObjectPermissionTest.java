package ca.corefacility.bioinformatics.irida.security.permissions.files;

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

import org.junit.Before;
import org.junit.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectSampleJoin;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sample.SampleSequencingObjectJoin;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SingleEndSequenceFile;
import ca.corefacility.bioinformatics.irida.model.user.Role;
import ca.corefacility.bioinformatics.irida.repositories.joins.project.ProjectSampleJoinRepository;
import ca.corefacility.bioinformatics.irida.repositories.joins.sample.SampleSequencingObjectJoinRepository;
import ca.corefacility.bioinformatics.irida.repositories.sample.SampleRepository;
import ca.corefacility.bioinformatics.irida.repositories.sequencefile.SequencingObjectRepository;
import ca.corefacility.bioinformatics.irida.repositories.user.UserRepository;
import ca.corefacility.bioinformatics.irida.security.permissions.files.ReadSequencingObjectPermission;
import ca.corefacility.bioinformatics.irida.security.permissions.project.ReadProjectPermission;
import ca.corefacility.bioinformatics.irida.security.permissions.sample.ReadSamplePermission;

/**
 * Testing the permission for {@link ReadSequencingObjectPermission}
 */
public class ReadSequencingObjectPermissionTest {
	private ReadSequencingObjectPermission permission;
	ReadSamplePermission samplePermission;
	ReadProjectPermission readProjectPermission;
	private UserRepository userRepository;
	private SequencingObjectRepository sequencingObjectRepository;
	private ProjectSampleJoinRepository psjRepository;
	private SampleSequencingObjectJoinRepository ssoRepository;
	private SampleRepository sampleRepository;

	@Before
	public void setUp() {
		userRepository = mock(UserRepository.class);
		ssoRepository = mock(SampleSequencingObjectJoinRepository.class);
		sequencingObjectRepository = mock(SequencingObjectRepository.class);
		psjRepository = mock(ProjectSampleJoinRepository.class);
		sampleRepository = mock(SampleRepository.class);
		readProjectPermission = mock(ReadProjectPermission.class);

		samplePermission = new ReadSamplePermission(sampleRepository, psjRepository, readProjectPermission);
		permission = new ReadSequencingObjectPermission(sequencingObjectRepository, samplePermission, ssoRepository);
	}

	@Test
	public void testGrantPermission() {
		Project p = new Project();
		Sample s = new Sample();
		List<Join<Project, Sample>> projectSampleList = new ArrayList<>();
		projectSampleList.add(new ProjectSampleJoin(p, s, true));

		SingleEndSequenceFile sf = new SingleEndSequenceFile(null);

		SampleSequencingObjectJoin join = new SampleSequencingObjectJoin(s, sf);

		when(psjRepository.getProjectForSample(s)).thenReturn(projectSampleList);
		when(sequencingObjectRepository.findById(1L)).thenReturn(Optional.of(sf));
		when(ssoRepository.getSampleForSequencingObject(sf)).thenReturn(join);
		when(readProjectPermission.isAllowed(any(), eq(p))).thenReturn(true);

		Authentication auth = new UsernamePasswordAuthenticationToken("fbristow", "password1");

		assertTrue("permission was not granted.", permission.isAllowed(auth, 1L));

		verify(sequencingObjectRepository).findById(1L);
		verify(psjRepository).getProjectForSample(s);
		verify(ssoRepository).getSampleForSequencingObject(sf);
		verify(readProjectPermission).isAllowed(any(), eq(p));
	}

	@Test
	public void testRejectPermission() {
		Project p = new Project();
		Sample s = new Sample();
		List<Join<Project, Sample>> projectSampleList = new ArrayList<>();
		projectSampleList.add(new ProjectSampleJoin(p, s, true));

		SingleEndSequenceFile sf = new SingleEndSequenceFile(null);
		SampleSequencingObjectJoin join = new SampleSequencingObjectJoin(s, sf);

		when(psjRepository.getProjectForSample(s)).thenReturn(projectSampleList);
		when(sequencingObjectRepository.findById(1L)).thenReturn(Optional.of(sf));
		when(ssoRepository.getSampleForSequencingObject(sf)).thenReturn(join);
		when(readProjectPermission.isAllowed(any(), eq(p))).thenReturn(false);

		Authentication auth = new UsernamePasswordAuthenticationToken("fbristow", "password1");

		assertFalse("permission was granted.", permission.isAllowed(auth, 1L));

		verify(sequencingObjectRepository).findById(1L);
		verify(psjRepository).getProjectForSample(s);
		verify(ssoRepository).getSampleForSequencingObject(sf);
		verify(readProjectPermission).isAllowed(any(), eq(p));
	}

	@Test
	public void testPermitAdmin() {
		Collection<GrantedAuthority> roles = new ArrayList<>();
		roles.add(Role.ROLE_ADMIN);

		Authentication auth = new UsernamePasswordAuthenticationToken("fbristow", "password1", roles);
		when(sequencingObjectRepository.findById(1L)).thenReturn(Optional.of(new SingleEndSequenceFile(null)));

		assertTrue("permission was not granted to admin.", permission.isAllowed(auth, 1L));

		// we should fast pass through to permission granted for administrators.
		verifyZeroInteractions(userRepository);
		verifyZeroInteractions(psjRepository);
		verifyZeroInteractions(userRepository);
		verifyZeroInteractions(ssoRepository);
	}
}
