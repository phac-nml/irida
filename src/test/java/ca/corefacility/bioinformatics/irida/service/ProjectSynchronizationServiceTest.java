package ca.corefacility.bioinformatics.irida.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Matchers.any;

import ca.corefacility.bioinformatics.irida.model.RemoteAPI;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.project.ProjectSyncFrequency;
import ca.corefacility.bioinformatics.irida.model.remote.RemoteStatus;
import ca.corefacility.bioinformatics.irida.model.remote.RemoteStatus.SyncStatus;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.service.remote.ProjectRemoteService;
import ca.corefacility.bioinformatics.irida.service.remote.ProjectSynchronizationService;
import ca.corefacility.bioinformatics.irida.service.remote.SampleRemoteService;
import ca.corefacility.bioinformatics.irida.service.remote.SequenceFilePairRemoteService;
import ca.corefacility.bioinformatics.irida.service.remote.SingleEndSequenceFileRemoteService;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;

public class ProjectSynchronizationServiceTest {

	@Mock
	private ProjectService projectService;
	@Mock
	private SampleService sampleService;
	@Mock
	private SequencingObjectService objectService;
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

	ProjectSynchronizationService syncService;

	Project expired;
	Project upToDate;
	Project neverSync;
	RemoteAPI api;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);

		syncService = new ProjectSynchronizationService(projectService, sampleService, objectService,
				projectRemoteService, sampleRemoteService, singleEndRemoteService, pairRemoteService, tokenService);

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
}
