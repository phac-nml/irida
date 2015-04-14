package ca.corefacility.bioinformatics.irida.ria.unit.web.projects;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import ca.corefacility.bioinformatics.irida.model.RemoteAPI;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.remote.RemoteRelatedProject;
import ca.corefacility.bioinformatics.irida.ria.web.projects.RemoteProjectsController;
import ca.corefacility.bioinformatics.irida.service.RemoteRelatedProjectService;
import ca.corefacility.bioinformatics.irida.service.remote.ProjectRemoteService;

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
	public void testReadRemoteRelatedProject() {
		Long remoteProjectId = 1L;
		RemoteRelatedProject rrp = new RemoteRelatedProject();
		Project rp = new Project();
		rp.setId(2L);
		rp.setName("project name");
		rrp.setRemoteAPI(new RemoteAPI());

		when(remoteRelatedProjectService.read(remoteProjectId)).thenReturn(rrp);
		when(projectRemoteService.read(rrp)).thenReturn(rp);

		Map<String, Object> read = controller.readRemoteRelatedProject(remoteProjectId);

		assertTrue(read.containsKey("id"));
		assertTrue(read.containsKey("name"));
		verify(remoteRelatedProjectService).read(remoteProjectId);
		verify(projectRemoteService).read(rrp);
	}
}
