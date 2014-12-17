package ca.corefacility.bioinformatics.irida.service.impl.unit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import javax.validation.Validator;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import ca.corefacility.bioinformatics.irida.model.RemoteAPI;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.remote.RemoteProject;
import ca.corefacility.bioinformatics.irida.model.remote.RemoteSample;
import ca.corefacility.bioinformatics.irida.model.remote.RemoteSequenceFile;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.snapshot.ProjectSnapshot;
import ca.corefacility.bioinformatics.irida.model.snapshot.SampleSnapshot;
import ca.corefacility.bioinformatics.irida.model.snapshot.SequenceFileSnapshot;
import ca.corefacility.bioinformatics.irida.model.snapshot.Snapshot;
import ca.corefacility.bioinformatics.irida.model.snapshot.remote.RemoteSnapshot;
import ca.corefacility.bioinformatics.irida.model.user.Role;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.repositories.SnapshotRepository;
import ca.corefacility.bioinformatics.irida.repositories.remote.SequenceFileRemoteRepository;
import ca.corefacility.bioinformatics.irida.repositories.user.UserRepository;
import ca.corefacility.bioinformatics.irida.service.SnapshotService;
import ca.corefacility.bioinformatics.irida.service.impl.SnapshotServiceImpl;

import com.google.common.collect.Lists;

public class SnapshotServiceImplTest {
	private SnapshotService service;
	private SnapshotRepository snapshotRepository;
	private SequenceFileRemoteRepository sequenceFileRemoteRepository;
	private UserRepository userRepository;
	private Validator validator;

	User user;

	@Before
	public void setup() {
		snapshotRepository = mock(SnapshotRepository.class);
		sequenceFileRemoteRepository = mock(SequenceFileRemoteRepository.class);
		userRepository = mock(UserRepository.class);
		service = new SnapshotServiceImpl(snapshotRepository, sequenceFileRemoteRepository, userRepository, validator);

		user = new User("bob", null, "bob1", null, null, null);
		UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(user, user.getPassword(),
				Lists.newArrayList(Role.ROLE_ADMIN));
		SecurityContextHolder.getContext().setAuthentication(auth);

		when(userRepository.loadUserByUsername(user.getUsername())).thenReturn(user);
	}

	@After
	public void teardown() {
		SecurityContextHolder.clearContext();
	}

	@Test
	public void testTakeLocalSnapshot() {
		Project project = new Project("a new project");
		Sample sample = new Sample("i am a sample");
		SequenceFile file = new SequenceFile(Paths.get("/tmp"));

		service.takeSnapshot(Lists.newArrayList(project), Lists.newArrayList(sample), Lists.newArrayList(file));

		ArgumentCaptor<Snapshot> snapshotCaptor = ArgumentCaptor.forClass(Snapshot.class);
		verify(snapshotRepository).save(snapshotCaptor.capture());
		verify(userRepository).loadUserByUsername(user.getUsername());
		Snapshot snapshot = snapshotCaptor.getValue();

		List<ProjectSnapshot> snapshotProjects = snapshot.getProjects();
		assertEquals(1, snapshotProjects.size());
		ProjectSnapshot projectSnapshot = snapshotProjects.iterator().next();
		assertEquals(project.getName(), projectSnapshot.getName());

		List<SampleSnapshot> snapshotSamples = snapshot.getSamples();
		assertEquals(1, snapshotSamples.size());
		SampleSnapshot sampleSnapshot = snapshotSamples.iterator().next();
		assertEquals(sample.getSampleName(), sampleSnapshot.getSampleName());

		List<SequenceFileSnapshot> snapshotFiles = snapshot.getSequenceFiles();
		assertEquals(1, snapshotFiles.size());
		SequenceFileSnapshot fileSnapshot = snapshotFiles.iterator().next();
		assertEquals(file.getFile(), fileSnapshot.getFile());

		assertEquals(user, snapshot.getCreatedBy());
	}

	@Test
	public void testTakeRemoteSnapshot() {
		RemoteProject project = new RemoteProject();
		project.setName("remote project name");
		RemoteSample sample = new RemoteSample();
		sample.setSampleName("i am a sample");
		RemoteSequenceFile file = new RemoteSequenceFile();
		RemoteAPI api = new RemoteAPI();
		file.setRemoteAPI(api);
		Path downloadedFilePath = Paths.get("/tmp");

		when(sequenceFileRemoteRepository.downloadRemoteSequenceFile(file, api)).thenReturn(downloadedFilePath);

		service.takeSnapshot(Lists.newArrayList(project), Lists.newArrayList(sample), Lists.newArrayList(file));

		verify(sequenceFileRemoteRepository).downloadRemoteSequenceFile(file, api);
		ArgumentCaptor<Snapshot> snapshotCaptor = ArgumentCaptor.forClass(Snapshot.class);
		verify(snapshotRepository).save(snapshotCaptor.capture());
		verify(userRepository).loadUserByUsername(user.getUsername());
		Snapshot snapshot = snapshotCaptor.getValue();

		List<ProjectSnapshot> snapshotProjects = snapshot.getProjects();
		assertEquals(1, snapshotProjects.size());
		ProjectSnapshot projectSnapshot = snapshotProjects.iterator().next();
		assertEquals(project.getName(), projectSnapshot.getName());
		assertTrue(projectSnapshot instanceof RemoteSnapshot);

		List<SampleSnapshot> snapshotSamples = snapshot.getSamples();
		assertEquals(1, snapshotSamples.size());
		SampleSnapshot sampleSnapshot = snapshotSamples.iterator().next();
		assertEquals(sample.getSampleName(), sampleSnapshot.getSampleName());
		assertTrue(sampleSnapshot instanceof RemoteSnapshot);

		List<SequenceFileSnapshot> snapshotFiles = snapshot.getSequenceFiles();
		assertEquals(1, snapshotFiles.size());
		SequenceFileSnapshot fileSnapshot = snapshotFiles.iterator().next();
		assertEquals(downloadedFilePath, fileSnapshot.getFile());
		assertTrue(fileSnapshot instanceof RemoteSnapshot);

		assertEquals(user, snapshot.getCreatedBy());

	}
}
