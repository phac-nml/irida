package ca.corefacility.bioinformatics.irida.ria.unit.web.projects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.springframework.ui.ExtendedModelMap;

import ca.corefacility.bioinformatics.irida.model.RemoteAPI;
import ca.corefacility.bioinformatics.irida.model.remote.RemoteProject;
import ca.corefacility.bioinformatics.irida.model.remote.RemoteRelatedProject;
import ca.corefacility.bioinformatics.irida.ria.utilities.CacheObject;
import ca.corefacility.bioinformatics.irida.ria.utilities.RemoteObjectCache;
import ca.corefacility.bioinformatics.irida.ria.web.projects.RemoteProjectsController;
import ca.corefacility.bioinformatics.irida.service.RemoteRelatedProjectService;
import ca.corefacility.bioinformatics.irida.service.remote.ProjectRemoteService;

public class RemoteProjectsControllerTest {
	RemoteProjectsController controller;
	ProjectRemoteService projectRemoteService;
	RemoteRelatedProjectService remoteRelatedProjectService;
	RemoteObjectCache<RemoteProject> projectCache;

	@SuppressWarnings("unchecked")
	@Before
	public void setUp() {
		projectRemoteService = mock(ProjectRemoteService.class);
		remoteRelatedProjectService = mock(RemoteRelatedProjectService.class);
		projectCache = mock(RemoteObjectCache.class);
		controller = new RemoteProjectsController(projectRemoteService, remoteRelatedProjectService, projectCache);
	}

	@Test
	public void testReadRemoteRelatedProject() {
		Long remoteProjectId = 1l;
		RemoteRelatedProject rrp = new RemoteRelatedProject();
		RemoteProject rp = new RemoteProject();
		rp.setId(2l);
		rp.setName("project name");
		rrp.setRemoteAPI(new RemoteAPI());

		when(remoteRelatedProjectService.read(remoteProjectId)).thenReturn(rrp);
		when(projectRemoteService.read(rrp)).thenReturn(rp);

		Map<String, Object> read = controller.readRemoteRelatedProject(remoteProjectId);

		assertTrue(read.containsKey("id"));
		assertTrue(read.containsKey("name"));
		verify(remoteRelatedProjectService).read(remoteProjectId);
		verify(projectRemoteService).read(rrp);
		verify(projectCache).addResource(rp, rrp.getRemoteAPI());
	}

	@Test
	public void testReadRemoteProject() {
		ExtendedModelMap model = new ExtendedModelMap();
		Integer projectCacheId = 2;
		RemoteProject remoteProject = new RemoteProject();
		RemoteAPI api = new RemoteAPI();
		CacheObject<RemoteProject> cacheObject = new CacheObject<>(remoteProject, api);

		when(projectCache.readResource(projectCacheId)).thenReturn(cacheObject);
		String readRemoteProject = controller.readRemoteProject(projectCacheId, model);
		assertEquals(RemoteProjectsController.REMOTE_PROJECT_VIEW, readRemoteProject);
		assertTrue(model.containsAttribute("project"));
		assertTrue(model.containsAttribute("api"));

		verify(projectCache).readResource(projectCacheId);
	}
}
