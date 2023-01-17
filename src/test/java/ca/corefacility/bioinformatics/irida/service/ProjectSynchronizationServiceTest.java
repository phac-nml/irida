package ca.corefacility.bioinformatics.irida.service;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Date;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import ca.corefacility.bioinformatics.irida.exceptions.IridaOAuthException;
import ca.corefacility.bioinformatics.irida.exceptions.LinkNotFoundException;
import ca.corefacility.bioinformatics.irida.exceptions.ProjectSynchronizationException;
import ca.corefacility.bioinformatics.irida.model.RemoteAPI;
import ca.corefacility.bioinformatics.irida.model.assembly.UploadedAssembly;
import ca.corefacility.bioinformatics.irida.model.joins.impl.SampleGenomeAssemblyJoin;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.project.ProjectSyncFrequency;
import ca.corefacility.bioinformatics.irida.model.remote.RemoteStatus;
import ca.corefacility.bioinformatics.irida.model.remote.RemoteStatus.SyncStatus;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sample.SampleSequencingObjectJoin;
import ca.corefacility.bioinformatics.irida.model.sample.metadata.MetadataEntry;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.Fast5Object;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFilePair;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.service.remote.*;
import ca.corefacility.bioinformatics.irida.service.sample.MetadataTemplateService;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class ProjectSynchronizationServiceTest {

	@Mock
	private ProjectService projectService;
	@Mock
	private SampleService sampleService;
	@Mock
	private SequencingObjectService objectService;
	@Mock
	private MetadataTemplateService metadataTemplateService;
	@Mock
	private ProjectRemoteService projectRemoteService;
	@Mock
	private SampleRemoteService sampleRemoteService;
	@Mock
	private SingleEndSequenceFileRemoteService singleEndRemoteService;
	@Mock
	private SequenceFilePairRemoteService pairRemoteService;
	@Mock
	private RemoteAPITokenService tokenService;
	@Mock
	private EmailController emailController;
	@Mock
	private GenomeAssemblyService assemblyService;
	@Mock
	private GenomeAssemblyRemoteService assemblyRemoteService;
	@Mock
	private Fast5ObjectRemoteService fast5ObjectRemoteService;

	ProjectSynchronizationService syncService;

	Project expired;
	Project upToDate;
	Project neverSync;
	RemoteAPI api;

	@BeforeEach
	public void setup() {
		MockitoAnnotations.openMocks(this);

		syncService = new ProjectSynchronizationService(projectService, sampleService, objectService,
				metadataTemplateService, assemblyService, projectRemoteService, sampleRemoteService,
				singleEndRemoteService, pairRemoteService, assemblyRemoteService, fast5ObjectRemoteService,
				tokenService, emailController);

		api = new RemoteAPI();
		expired = new Project();
		expired.setId(1L);
		expired.setRemoteProjectHash(1);
		RemoteStatus expStatus = new RemoteStatus("http://expired", api);
		expStatus.setId(1L);
		expStatus.setLastUpdate(new Date(1));
		expStatus.setSyncStatus(RemoteStatus.SyncStatus.SYNCHRONIZED);
		expired.setSyncFrequency(ProjectSyncFrequency.WEEKLY);
		expired.setRemoteStatus(expStatus);

		upToDate = new Project();
		upToDate.setId(2L);
		RemoteStatus upToDateStatus = new RemoteStatus("http://upToDate", api);
		upToDateStatus.setId(2L);
		upToDateStatus.setLastUpdate(new Date());
		upToDateStatus.setSyncStatus(RemoteStatus.SyncStatus.SYNCHRONIZED);
		upToDate.setSyncFrequency(ProjectSyncFrequency.WEEKLY);
		upToDate.setRemoteStatus(upToDateStatus);

		neverSync = new Project();
		neverSync.setId(3L);
		RemoteStatus neverSyncStatus = new RemoteStatus("http://never", api);
		neverSyncStatus.setId(3L);
		neverSyncStatus.setLastUpdate(new Date());
		neverSyncStatus.setSyncStatus(RemoteStatus.SyncStatus.SYNCHRONIZED);
		neverSync.setSyncFrequency(ProjectSyncFrequency.NEVER);
		neverSync.setRemoteStatus(neverSyncStatus);
	}

	@Test
	public void testFindMarkedProjectsToSync() {
		when(projectService.getRemoteProjects()).thenReturn(Lists.newArrayList(expired, upToDate, neverSync));

		syncService.findMarkedProjectsToSync();

		assertEquals(RemoteStatus.SyncStatus.MARKED, expired.getRemoteStatus().getSyncStatus());
		assertEquals(RemoteStatus.SyncStatus.SYNCHRONIZED, upToDate.getRemoteStatus().getSyncStatus());
		assertEquals(RemoteStatus.SyncStatus.UNSYNCHRONIZED, neverSync.getRemoteStatus().getSyncStatus());
	}

	@Test
	public void testSyncProjects() {
		expired.getRemoteStatus().setSyncStatus(SyncStatus.MARKED);
		when(projectService.read(expired.getId())).thenReturn(expired);
		Project remoteProject = new Project();
		remoteProject.setRemoteStatus(expired.getRemoteStatus());
		User readBy = new User();
		expired.getRemoteStatus().setReadBy(readBy);
		when(projectService.getProjectsWithRemoteSyncStatus(RemoteStatus.SyncStatus.MARKED))
				.thenReturn(Lists.newArrayList(expired));
		when(projectRemoteService.read(expired.getRemoteStatus().getURL())).thenReturn(remoteProject);

		when(projectRemoteService.getProjectHash(remoteProject)).thenReturn(2);

		when(projectService.update(remoteProject)).thenReturn(remoteProject);

		syncService.findMarkedProjectsToSync();

		verify(projectService, times(3)).update(any(Project.class));
		verify(projectRemoteService).getProjectHash(remoteProject);

		assertEquals(SyncStatus.SYNCHRONIZED, remoteProject.getRemoteStatus().getSyncStatus());
	}

	@Test
	public void testSyncProjectsSameHash() {
		expired.getRemoteStatus().setSyncStatus(SyncStatus.MARKED);
		when(projectService.read(expired.getId())).thenReturn(expired);
		Project remoteProject = new Project();
		remoteProject.setRemoteStatus(expired.getRemoteStatus());
		User readBy = new User();
		expired.getRemoteStatus().setReadBy(readBy);
		when(projectService.getProjectsWithRemoteSyncStatus(RemoteStatus.SyncStatus.MARKED))
				.thenReturn(Lists.newArrayList(expired));
		when(projectRemoteService.read(expired.getRemoteStatus().getURL())).thenReturn(remoteProject);

		when(projectRemoteService.getProjectHash(remoteProject)).thenReturn(expired.getRemoteProjectHash());

		when(projectService.update(remoteProject)).thenReturn(remoteProject);

		syncService.findMarkedProjectsToSync();

		verify(projectService, times(2)).update(any(Project.class));
		verify(projectRemoteService).getProjectHash(remoteProject);

		verifyNoInteractions(sampleRemoteService);

		assertEquals(SyncStatus.SYNCHRONIZED, remoteProject.getRemoteStatus().getSyncStatus());
	}

	@Test
	public void testSyncProjectsUnauthorized() {
		expired.getRemoteStatus().setSyncStatus(SyncStatus.MARKED);
		when(projectService.read(expired.getId())).thenReturn(expired);
		Project remoteProject = new Project();
		remoteProject.setRemoteStatus(expired.getRemoteStatus());
		User readBy = new User();
		expired.getRemoteStatus().setReadBy(readBy);
		when(projectService.getProjectsWithRemoteSyncStatus(RemoteStatus.SyncStatus.MARKED))
				.thenReturn(Lists.newArrayList(expired));
		when(projectRemoteService.read(expired.getRemoteStatus().getURL()))
				.thenThrow(new IridaOAuthException("unauthorized", api));

		syncService.findMarkedProjectsToSync();

		assertEquals(SyncStatus.UNAUTHORIZED, remoteProject.getRemoteStatus().getSyncStatus());

		verify(emailController).sendProjectSyncUnauthorizedEmail(expired);
	}

	@Test
	public void testSyncNewSample() {
		Sample sample = new Sample();
		RemoteStatus sampleStatus = new RemoteStatus("http://sample", api);
		sample.setRemoteStatus(sampleStatus);

		when(sampleService.create(sample)).thenReturn(sample);
		when(sampleService.updateSampleMetadata(eq(sample), anySet())).thenReturn(sample);

		syncService.syncSample(sample, expired, Maps.newHashMap());

		verify(projectService).addSampleToProject(expired, sample, true);

		assertEquals(SyncStatus.SYNCHRONIZED, sample.getRemoteStatus().getSyncStatus());
	}

	@Test
	public void testExistingSample() {
		Sample sample = new Sample();
		RemoteStatus sampleStatus = new RemoteStatus("http://sample", api);
		sample.setRemoteStatus(sampleStatus);

		Sample existingSample = new Sample();
		existingSample.setRemoteStatus(sampleStatus);

		when(sampleService.update(any(Sample.class))).thenReturn(sample);
		when(sampleService.updateSampleMetadata(eq(sample), anySet())).thenReturn(sample);

		syncService.syncSample(sample, expired, ImmutableMap.of("http://sample", existingSample));

		verify(projectService, times(0)).addSampleToProject(expired, sample, true);
		verify(sampleService, times(2)).update(any(Sample.class));
	}

	@Test
	public void testSyncSampleNoAssemblies() {
		Sample sample = new Sample();
		RemoteStatus sampleStatus = new RemoteStatus("http://sample", api);
		sample.setRemoteStatus(sampleStatus);

		when(sampleService.create(sample)).thenReturn(sample);
		when(sampleService.updateSampleMetadata(eq(sample), ArgumentMatchers.<Set<MetadataEntry>>any()))
				.thenReturn(sample);
		when(assemblyRemoteService.getGenomeAssembliesForSample(sample))
				.thenThrow(new LinkNotFoundException("no link"));

		syncService.syncSample(sample, expired, Maps.newHashMap());

		verify(projectService).addSampleToProject(expired, sample, true);
		verify(assemblyRemoteService, times(0)).mirrorAssembly(any(UploadedAssembly.class));

		assertEquals(SyncStatus.SYNCHRONIZED, sample.getRemoteStatus().getSyncStatus());
	}

	@Test
	public void testSyncExistingSampleWithDeletedFiles() {
		Sample sample = new Sample();
		RemoteStatus sampleStatus = new RemoteStatus("http://sample", api);
		sample.setRemoteStatus(sampleStatus);

		Sample existingSample = new Sample();
		existingSample.setRemoteStatus(sampleStatus);

		SequenceFilePair localPair = new SequenceFilePair();
		RemoteStatus localPairStatus = new RemoteStatus("http://pair", api);
		localPair.setRemoteStatus(localPairStatus);
		SampleSequencingObjectJoin sso = new SampleSequencingObjectJoin(sample, localPair);
		when(objectService.getSequencingObjectsForSample(existingSample)).thenReturn(Collections.singletonList(sso));

		when(sampleService.update(any(Sample.class))).thenReturn(sample);
		when(sampleService.updateSampleMetadata(eq(sample), anySet())).thenReturn(sample);

		syncService.syncSample(sample, expired, ImmutableMap.of("http://sample", existingSample));

		verify(projectService, times(0)).addSampleToProject(expired, sample, true);
		verify(sampleService, times(2)).update(any(Sample.class));
		verify(sampleService, times(1)).removeSequencingObjectFromSample(sample, localPair);
	}

	@Test
	public void testSyncExistingSampleWithDeletedAssemblies() {
		Sample sample = new Sample();
		RemoteStatus sampleStatus = new RemoteStatus("http://sample", api);
		sample.setRemoteStatus(sampleStatus);

		Sample existingSample = new Sample();
		existingSample.setRemoteStatus(sampleStatus);

		UploadedAssembly localAssembly = new UploadedAssembly(null);
		localAssembly.setId(1L);
		RemoteStatus localAssemblyStatus = new RemoteStatus("http://assembly", api);
		localAssembly.setRemoteStatus(localAssemblyStatus);
		SampleGenomeAssemblyJoin sga = new SampleGenomeAssemblyJoin(sample, localAssembly);
		when(assemblyService.getAssembliesForSample(existingSample)).thenReturn(Collections.singletonList(sga));

		when(sampleService.update(any(Sample.class))).thenReturn(sample);
		when(sampleService.updateSampleMetadata(eq(sample), anySet())).thenReturn(sample);

		syncService.syncSample(sample, expired, ImmutableMap.of("http://sample", existingSample));

		verify(projectService, times(0)).addSampleToProject(expired, sample, true);
		verify(sampleService, times(2)).update(any(Sample.class));
		verify(assemblyService, times(1)).removeGenomeAssemblyFromSample(sample, localAssembly.getId());
	}

	@Test
	public void testSyncFiles() {
		Sample sample = new Sample();

		SequenceFilePair pair = new SequenceFilePair();
		RemoteStatus pairStatus = new RemoteStatus("http://pair", api);
		pair.setRemoteStatus(pairStatus);
		pair.setId(1L);

		when(pairRemoteService.mirrorSequencingObject(pair)).thenReturn(pair);

		syncService.syncSequenceFilePair(pair, sample);

		verify(pairRemoteService).mirrorSequencingObject(pair);
		verify(objectService).createSequencingObjectInSample(pair, sample);
	}

	@Test
	public void testSyncSampleNoFast5() {
		Sample sample = new Sample();
		RemoteStatus sampleStatus = new RemoteStatus("http://sample", api);
		sample.setRemoteStatus(sampleStatus);

		when(sampleService.create(sample)).thenReturn(sample);
		when(sampleService.updateSampleMetadata(eq(sample), ArgumentMatchers.<Set<MetadataEntry>>any()))
				.thenReturn(sample);
		when(fast5ObjectRemoteService.getFast5FilesForSample(sample)).thenThrow(new LinkNotFoundException("no link"));

		syncService.syncSample(sample, expired, Maps.newHashMap());

		verify(projectService).addSampleToProject(expired, sample, true);
		verify(assemblyRemoteService, times(0)).mirrorAssembly(any(UploadedAssembly.class));

		assertEquals(SyncStatus.SYNCHRONIZED, sample.getRemoteStatus().getSyncStatus());
	}

	@Test
	public void testSyncAssemblies() {
		Sample sample = new Sample();

		UploadedAssembly assembly = new UploadedAssembly(null);
		RemoteStatus pairStatus = new RemoteStatus("http://assembly", api);
		assembly.setRemoteStatus(pairStatus);
		assembly.setId(1L);

		when(assemblyRemoteService.mirrorAssembly(assembly)).thenReturn(assembly);

		syncService.syncAssembly(assembly, sample);

		verify(assemblyRemoteService).mirrorAssembly(assembly);
		verify(assemblyService).createAssemblyInSample(sample, assembly);
	}

	@Test
	public void testSyncFast5Files() {
		Sample sample = new Sample();
		Path p1 = Paths.get("src/test/resources/files/testfast5file.fast5");
		Fast5Object fast5Object = new Fast5Object(new SequenceFile(p1));
		RemoteStatus fast5Status = new RemoteStatus("http://fast5", api);
		fast5Object.setRemoteStatus(fast5Status);
		fast5Object.setId(1L);

		when(fast5ObjectRemoteService.mirrorSequencingObject(fast5Object)).thenReturn(fast5Object);

		syncService.syncFast5File(fast5Object, sample);

		verify(fast5ObjectRemoteService).mirrorSequencingObject(fast5Object);
		verify(objectService).createSequencingObjectInSample(fast5Object, sample);
	}

	@Test
	public void testSyncFilesError() {
		Sample sample = new Sample();

		SequenceFilePair pair = new SequenceFilePair();
		RemoteStatus pairStatus = new RemoteStatus("http://pair", api);
		pair.setRemoteStatus(pairStatus);
		pair.setId(1L);

		when(pairRemoteService.mirrorSequencingObject(pair)).thenReturn(pair);
		when(objectService.createSequencingObjectInSample(pair, sample))
				.thenThrow(new NullPointerException("Bad file"));

		assertThrows(ProjectSynchronizationException.class, () -> {
			syncService.syncSequenceFilePair(pair, sample);
		});
	}
}
