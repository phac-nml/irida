package ca.corefacility.bioinformatics.irida.ria.unit.web.projects;

import java.security.Principal;

import org.junit.Before;
import org.junit.Test;
import org.springframework.ui.ExtendedModelMap;

import ca.corefacility.bioinformatics.irida.ria.web.projects.ProjectControllerUtils;
import ca.corefacility.bioinformatics.irida.ria.web.projects.settings.ProjectMembersController;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.user.UserService;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

public class ProjectMembersControllerTest {
	// Services
	private ProjectService projectService;
	private UserService userService;
	private ProjectControllerUtils projectUtils;
	private ProjectTestUtils projectTestUtils;
	private static final String USER_NAME = "testme";

	ProjectMembersController controller;

	@Before
	public void setUp() {
		projectService = mock(ProjectService.class);
		userService = mock(UserService.class);
		projectUtils = mock(ProjectControllerUtils.class);
		controller = new ProjectMembersController(projectUtils, projectService);
		projectTestUtils = new ProjectTestUtils(projectService, userService);

		projectTestUtils.mockSidebarInfo();
	}

	@Test
	public void testGetProjectUsersPage() {
		ExtendedModelMap model = new ExtendedModelMap();
		Long projectId = 1L;
		Principal principal = () -> USER_NAME;
		assertEquals("Gets the correct project members page", "projects/settings/pages/members",
				controller.getProjectUsersPage(model, principal, projectId));
		
		assertEquals("Should be the members subpage", model.get("page"), "members");
	}
}
