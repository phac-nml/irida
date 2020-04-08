package ca.corefacility.bioinformatics.irida.ria.unit.web.projects;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

import java.security.Principal;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.springframework.context.MessageSource;
import org.springframework.ui.ExtendedModelMap;

import ca.corefacility.bioinformatics.irida.exceptions.ProjectWithoutOwnerException;
import ca.corefacility.bioinformatics.irida.model.enums.ProjectRole;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.ria.web.projects.ProjectControllerUtils;
import ca.corefacility.bioinformatics.irida.ria.web.projects.settings.ProjectMembersController;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.user.UserGroupService;
import ca.corefacility.bioinformatics.irida.service.user.UserService;

import com.google.common.collect.Lists;

public class ProjectMembersControllerTest {
	// Services
	private ProjectService projectService;
	private UserService userService;
	private UserGroupService userGroupService;
	private ProjectControllerUtils projectUtils;
	private ProjectTestUtils projectTestUtils;
	private MessageSource messageSource;
	private static final String USER_NAME = "testme";

	ProjectMembersController controller;

	@Before
	public void setUp() {
		projectService = mock(ProjectService.class);
		userService = mock(UserService.class);
		projectUtils = mock(ProjectControllerUtils.class);
		messageSource = mock(MessageSource.class);
		userGroupService = mock(UserGroupService.class);
		controller = new ProjectMembersController(projectUtils, projectService, userService, userGroupService, messageSource);
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
		
		assertEquals("Should be the memebers subpage", model.get("page"), "members");
	}
}
