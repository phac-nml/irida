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

	@Test
	public void testAddProjectMember() {
		Long projectId = 1L;
		Long userId = 2L;
		Project project = new Project();
		User user = new User(userId, "tom", null, null, "Tom", "Matthews", null);
		ProjectRole projectRole = ProjectRole.PROJECT_USER;

		when(projectService.read(projectId)).thenReturn(project);
		when(userService.read(userId)).thenReturn(user);
		when(messageSource.getMessage(any(), any(), any())).thenReturn("My random string");

		controller.addProjectMember(projectId, userId, projectRole.toString(), Locale.US);

		verify(projectService).read(projectId);
		verify(userService).read(userId);
		verify(projectService).addUserToProject(project, user, projectRole);
	}

	@Test
	public void testGetUsersAvailableForProject() {
		String term = "tom";
		Long projectId = 1L;
		Long userId = 2L;
		Project project = new Project();
		project.setId(projectId);
		List<User> users = Lists.newArrayList(new User(userId, "tom", null, null, "Tom", "Matthews", null));

		when(projectService.read(projectId)).thenReturn(project);
		when(userService.getUsersAvailableForProject(project, term)).thenReturn(users);

		Collection<User> usersAvailableForProject = controller.getUsersAvailableForProject(projectId, term);

		assertFalse(usersAvailableForProject.isEmpty());
		assertEquals("should only have 1 user.", 1, usersAvailableForProject.size());
		assertEquals("should have the specified user on project.", userId, usersAvailableForProject.iterator().next().getId());

		verify(projectService).read(projectId);
		verify(userService).getUsersAvailableForProject(project, term);
	}

	@Test
	public void testRemoveUserFromProject() throws ProjectWithoutOwnerException {
		Long projectId = 1L;
		Long userId = 2L;
		User user = new User(userId, "tom", null, null, null, null, null);
		Project project = new Project("test");
		project.setId(projectId);

		when(userService.read(userId)).thenReturn(user);
		when(projectService.read(projectId)).thenReturn(project);
		when(messageSource.getMessage(any(), any(), any())).thenReturn("");

		controller.removeUser(projectId, userId, null);

		verify(userService).read(userId);
		verify(projectService).read(projectId);
		verify(projectService).removeUserFromProject(project, user);
	}

	@Test
	public void testUdateUserRole() throws ProjectWithoutOwnerException {
		Long projectId = 1L;
		Long userId = 2L;
		Project project = new Project();
		User user = new User(userId, "tom", null, null, "Tom", "Matthews", null);
		ProjectRole projectRole = ProjectRole.PROJECT_USER;

		when(projectService.read(projectId)).thenReturn(project);
		when(userService.read(userId)).thenReturn(user);
		when(messageSource.getMessage(any(), any(), any())).thenReturn("");

		controller.updateUserRole(projectId, userId, projectRole.toString(), null);

		verify(projectService).read(projectId);
		verify(userService).read(userId);
		verify(projectService).updateUserProjectRole(project, user, projectRole);
	}

	public void testUdateUserSelfRole() throws ProjectWithoutOwnerException {
		Long projectId = 1L;
		Long userId = 2L;
		Project project = new Project();
		User user = new User(userId, USER_NAME, null, null, "Tom", "Matthews", null);
		ProjectRole projectRole = ProjectRole.PROJECT_USER;

		when(projectService.read(projectId)).thenReturn(project);
		when(userService.read(userId)).thenReturn(user);
		when(messageSource.getMessage(any(), any(), any())).thenReturn("");

		final Map<String, String> result = controller.updateUserRole(projectId, userId, projectRole.toString(), null);
		assertTrue("should have failure message.", result.containsKey("failure"));
	}
}
