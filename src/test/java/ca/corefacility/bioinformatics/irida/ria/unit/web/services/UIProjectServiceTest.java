package ca.corefacility.bioinformatics.irida.ria.unit.web.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.MessageSource;

import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.ria.web.projects.dto.CreateProjectRequest;
import ca.corefacility.bioinformatics.irida.ria.web.services.UIProjectsService;
import ca.corefacility.bioinformatics.irida.security.permissions.project.ManageLocalProjectSettingsPermission;
import ca.corefacility.bioinformatics.irida.security.permissions.project.ProjectOwnerPermission;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;

import com.google.common.collect.ImmutableList;

import static org.mockito.Mockito.*;

public class UIProjectServiceTest {
	private final Project PROJECT_01 = new Project();
	private final Long PROJECT_01_ID = 1L;
	private UIProjectsService service;
	private ProjectService projectService;
	private SampleService sampleService;

	@BeforeEach
	public void setUp() {
		projectService = mock(ProjectService.class);
		sampleService = mock(SampleService.class);
		MessageSource messageSource = mock(MessageSource.class);
		ProjectOwnerPermission projectOwnerPermission = mock(ProjectOwnerPermission.class);
		ManageLocalProjectSettingsPermission manageLocalProjectSettingsPermission = mock(
				ManageLocalProjectSettingsPermission.class);
		service = new UIProjectsService(projectService, sampleService, messageSource, projectOwnerPermission,
				manageLocalProjectSettingsPermission);

		// Set up the project
		PROJECT_01.setId(PROJECT_01_ID);
		PROJECT_01.setName("Test Project");
		when(projectService.create(any(Project.class))).thenReturn(PROJECT_01);
		when(projectService.createProjectWithSamples(any(Project.class), anyList(), anyBoolean())).thenReturn(
				PROJECT_01);
	}

	@Test
	public void testCreateProject() {
		CreateProjectRequest request = new CreateProjectRequest();
		request.setName("Test Project");
		request.setLock(false);
		service.createProject(request);

		verify(projectService, times(1)).create(any(Project.class));

		request.setSamples(ImmutableList.of(1L, 2L));
		service.createProject(request);
		verify(projectService, times(1)).createProjectWithSamples(any(Project.class), anyList(), anyBoolean());
	}
}
