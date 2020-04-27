package ca.corefacility.bioinformatics.irida.ria.unit.web.projects;

import org.junit.Before;
import org.junit.Test;
import org.springframework.context.MessageSource;
import org.springframework.ui.ExtendedModelMap;

import ca.corefacility.bioinformatics.irida.ria.web.projects.settings.ProjectMembersController;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.user.UserGroupService;
import ca.corefacility.bioinformatics.irida.service.user.UserService;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

public class ProjectMembersControllerTest {
	// Services
	private ProjectService projectService;
	private UserService userService;
	private UserGroupService userGroupService;
	private ProjectTestUtils projectTestUtils;
	private MessageSource messageSource;
	private static final String USER_NAME = "testme";

	ProjectMembersController controller;

	@Before
	public void setUp() {
		projectService = mock(ProjectService.class);
		userService = mock(UserService.class);
		messageSource = mock(MessageSource.class);
		userGroupService = mock(UserGroupService.class);
		controller = new ProjectMembersController(projectService, userGroupService, messageSource);
		projectTestUtils = new ProjectTestUtils(projectService, userService);

		projectTestUtils.mockSidebarInfo();
	}

	@Test
	public void testGetProjectUsersPage() {
		ExtendedModelMap model = new ExtendedModelMap();
		assertEquals("Gets the correct project members page", "projects/settings/pages/members",
				controller.getProjectUsersPage(model));
		
		assertEquals("Should be the memebers subpage", model.get("page"), "members");
	}
}
