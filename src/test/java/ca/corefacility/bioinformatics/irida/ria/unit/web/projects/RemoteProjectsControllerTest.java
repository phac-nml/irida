package ca.corefacility.bioinformatics.irida.ria.unit.web.projects;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import ca.corefacility.bioinformatics.irida.model.remote.RemoteRelatedProject;
import ca.corefacility.bioinformatics.irida.ria.web.projects.RemoteProjectsController;
import ca.corefacility.bioinformatics.irida.service.RemoteRelatedProjectService;
import ca.corefacility.bioinformatics.irida.service.remote.ProjectRemoteService;
import ca.corefacility.bioinformatics.irida.service.remote.model.RemoteProject;

public class RemoteProjectsControllerTest {
	RemoteProjectsController controller;
	ProjectRemoteService projectRemoteService;
	RemoteRelatedProjectService remoteRelatedProjectService;

	@Before
	public void setUp() {
		projectRemoteService = mock(ProjectRemoteService.class);
		remoteRelatedProjectService = mock(RemoteRelatedProjectService.class);
		controller = new RemoteProjectsController(projectRemoteService, remoteRelatedProjectService);
	}

	@Test
	public void testRead() {
		Long remoteProjectId = 1l;
		RemoteRelatedProject rrp = new RemoteRelatedProject();
		RemoteProject rp = new RemoteProject();
		rp.setId(2l);
		rp.setName("project name");

		when(remoteRelatedProjectService.read(remoteProjectId)).thenReturn(rrp);
		when(projectRemoteService.read(rrp)).thenReturn(rp);

		Map<String, Object> read = controller.read(remoteProjectId);

		assertTrue(read.containsKey("id"));
		assertTrue(read.containsKey("name"));
		verify(remoteRelatedProjectService).read(remoteProjectId);
		verify(projectRemoteService).read(rrp);
	}
}
