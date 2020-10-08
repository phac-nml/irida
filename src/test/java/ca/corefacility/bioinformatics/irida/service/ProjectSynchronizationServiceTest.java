package ca.corefacility.bioinformatics.irida.service;

import java.util.Date;

import ca.corefacility.bioinformatics.irida.exceptions.IridaOAuthException;
import ca.corefacility.bioinformatics.irida.model.assembly.UploadedAssembly;
import ca.corefacility.bioinformatics.irida.service.impl.TestEmailController;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import ca.corefacility.bioinformatics.irida.exceptions.ProjectSynchronizationException;
import ca.corefacility.bioinformatics.irida.model.RemoteAPI;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.project.ProjectSyncFrequency;
import ca.corefacility.bioinformatics.irida.model.remote.RemoteStatus;
import ca.corefacility.bioinformatics.irida.model.remote.RemoteStatus.SyncStatus;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFilePair;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.service.remote.*;
import ca.corefacility.bioinformatics.irida.service.sample.MetadataTemplateService;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
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

	ProjectSynchronizationService syncService;

	Project expired;
	Project upToDate;
	Project neverSync;
	RemoteAPI api;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);

		syncService = new ProjectSynchronizationService(projectService, sampleService, objectService,
				metadataTemplateService, assemblyService, projectRemoteService, sampleRemoteService, singleEndRemoteService,
				pairRemoteService, assemblyRemoteService, tokenService, emailController);

		api = new RemoteAPI();
		expired = new Project();
		expired.setId(1L);
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

		when(projectService.update(remoteProject)).thenReturn(remoteProject);

		syncService.findMarkedProjectsToSync();

		verify(projectService, times(3)).update(any(Project.class));

		assertEquals(SyncStatus.SYNCHRONIZED, remoteProject.getRemoteStatus().getSyncStatus());
	}

	@Test
	public void testSyncProjectsUnauthorized() {
		expired.getRemoteStatus()
				.setSyncStatus(SyncStatus.MARKED);
		when(projectService.read(expired.getId())).thenReturn(expired);
		Project remoteProject = new Project();
		remoteProject.setRemoteStatus(expired.getRemoteStatus());
		User readBy = new User();
		expired.getRemoteStatus()
				.setReadBy(readBy);
		when(projectService.getProjectsWithRemoteSyncStatus(RemoteStatus.SyncStatus.MARKED)).thenReturn(
				Lists.newArrayList(expired));
		when(projectRemoteService.read(expired.getRemoteStatus()
				.getURL())).thenThrow(new IridaOAuthException("unauthorized", api));

		syncService.findMarkedProjectsToSync();

		assertEquals(SyncStatus.UNAUTHORIZED, remoteProject.getRemoteStatus()
				.getSyncStatus());

		verify(emailController).sendProjectSyncUnauthorizedEmail(expired);
	}

	@Test
	public void testSyncNewSample() {
		Sample sample = new Sample();
		RemoteStatus sampleStatus = new RemoteStatus("http://sample",api);
		sample.setRemoteStatus(sampleStatus);
		
		when(sampleService.create(sample)).thenReturn(sample);
		
		syncService.syncSample(sample, expired, Maps.newHashMap());
		
		verify(projectService).addSampleToProject(expired, sample, true);
		
		assertEquals(SyncStatus.SYNCHRONIZED,sample.getRemoteStatus().getSyncStatus());
	}
	
	@Test
	public void testExistingSample(){
		Sample sample = new Sample();
		RemoteStatus sampleStatus = new RemoteStatus("http://sample",api);
		sample.setRemoteStatus(sampleStatus);
		
		Sample existingSample = new Sample();
		existingSample.setRemoteStatus(sampleStatus);
		
		when(sampleService.update(any(Sample.class))).thenReturn(sample);
		
		syncService.syncSample(sample, expired, ImmutableMap.of("http://sample",existingSample));
		
		verify(projectService,times(0)).addSampleToProject(expired, sample, true);
		verify(sampleService,times(2)).update(any(Sample.class));
	}
	
	@Test
	public void testSyncFiles() {
		Sample sample = new Sample();
		
		SequenceFilePair pair = new SequenceFilePair();
		RemoteStatus pairStatus = new RemoteStatus("http://pair",api);
		pair.setRemoteStatus(pairStatus);
		pair.setId(1L);
		
		when(pairRemoteService.mirrorSequencingObject(pair)).thenReturn(pair);
		
		syncService.syncSequenceFilePair(pair, sample);
		
		verify(pairRemoteService).mirrorSequencingObject(pair);
		verify(objectService).createSequencingObjectInSample(pair, sample);
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
	
	@Test(expected = ProjectSynchronizationException.class)
	public void testSyncFilesError() {
		Sample sample = new Sample();
		
		SequenceFilePair pair = new SequenceFilePair();
		RemoteStatus pairStatus = new RemoteStatus("http://pair",api);
		pair.setRemoteStatus(pairStatus);
		pair.setId(1L);
		
		when(pairRemoteService.mirrorSequencingObject(pair)).thenReturn(pair);
		when(objectService.createSequencingObjectInSample(pair, sample)).thenThrow(new NullPointerException("Bad file"));
		
		syncService.syncSequenceFilePair(pair, sample);
	}
}
