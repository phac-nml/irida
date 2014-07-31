package ca.corefacility.bioinformatics.irida.ria.unit.web.projects;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;

import org.junit.Before;
import org.junit.Test;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import ca.corefacility.bioinformatics.irida.model.Project;
import ca.corefacility.bioinformatics.irida.model.enums.ProjectRole;
import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectUserJoin;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.ria.web.projects.ProjectControllerUtils;
import ca.corefacility.bioinformatics.irida.ria.web.projects.ProjectMembersController;
import ca.corefacility.bioinformatics.irida.ria.web.projects.ProjectsController;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.SequenceFileService;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;
import ca.corefacility.bioinformatics.irida.service.user.UserService;

public class ProjectMembersControllerTest {
	// Services
	private ProjectService projectService;
	private SampleService sampleService;
	private UserService userService;
	private SequenceFileService sequenceFileService;
	private ProjectControllerUtils projectUtils;
	private ProjectTestUtils projectTestUtils;
	private static final String USER_NAME = "testme";

	ProjectMembersController controller;

	@Before
	public void setUp() {
		projectService = mock(ProjectService.class);
		sampleService = mock(SampleService.class);
		userService = mock(UserService.class);
		sequenceFileService = mock(SequenceFileService.class);
		projectUtils = mock(ProjectControllerUtils.class);
		controller = new ProjectMembersController(projectUtils, projectService);
		projectTestUtils = new ProjectTestUtils(projectService, sampleService, userService, sequenceFileService, projectUtils);

		projectTestUtils.mockSidebarInfo();
	}

	@Test
	public void testGetProjectUsersPage() {
		Model model = new ExtendedModelMap();
		Long projectId = 1L;
		Principal principal = () -> USER_NAME;
		assertEquals("Gets the correct project members page",
				controller.getProjectUsersPage(model, principal, projectId), ProjectsController.PROJECT_MEMBERS_PAGE);
	}
}
