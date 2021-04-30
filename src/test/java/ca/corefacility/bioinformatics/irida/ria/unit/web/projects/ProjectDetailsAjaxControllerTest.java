package ca.corefacility.bioinformatics.irida.ria.unit.web.projects;

import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.projects.ProjectDetailsAjaxController;
import ca.corefacility.bioinformatics.irida.ria.web.projects.dto.ProjectInfoResponse;
import ca.corefacility.bioinformatics.irida.ria.web.projects.settings.dto.UpdateProjectAttributeRequest;
import ca.corefacility.bioinformatics.irida.ria.web.services.UIMetadataService;
import ca.corefacility.bioinformatics.irida.ria.web.services.UIProjectsService;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.web.controller.test.unit.TestDataFactory;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.*;

public class ProjectDetailsAjaxControllerTest {
	private  ProjectService projectService;
	private ProjectDetailsAjaxController controller;
	private UIMetadataService metadataService;
	private UIProjectsService service;

	@Before
	public void setUp() {
		projectService = mock(ProjectService.class);
		MessageSource messageSource = mock(MessageSource.class);
		service = mock(UIProjectsService.class);
		metadataService = mock(UIMetadataService.class);
		controller = new ProjectDetailsAjaxController(projectService, service, metadataService, messageSource);

		Project project = TestDataFactory.constructProject();
		when(projectService.read(anyLong())).thenReturn(project);
		when(service.getProjectInfo(TestDataFactory.TEST_PROJECT_ID)).thenReturn(new ProjectInfoResponse(project, true, true));
	}

	@Test
	public void testGetProjectDetails() {
		ResponseEntity<ProjectInfoResponse> response = controller.getProjectDetails(TestDataFactory.TEST_PROJECT_ID);
		assertEquals(response.getStatusCode(), HttpStatus.OK);
		ProjectInfoResponse content = response.getBody();
		assert content != null;
		assertEquals(TestDataFactory.TEST_PROJECT_DESCRIPTION, content.getDescription());
		assertEquals(TestDataFactory.TEST_PROJECT_LABEL, content.getLabel());
		assertEquals(TestDataFactory.TEST_PROJECT_ORGANISM, content.getOrganism());
	}

	@Test
	public void testUpdateProjectDetails() {
		UpdateProjectAttributeRequest request = new UpdateProjectAttributeRequest("organism", "Salmonella");
		ResponseEntity<String> response = controller.updateProjectDetails(TestDataFactory.TEST_PROJECT_ID, request, Locale.ENGLISH);
		assertEquals(response.getStatusCode(), HttpStatus.OK);
		verify(projectService, times(1)).update(any(Project.class));
	}
}
