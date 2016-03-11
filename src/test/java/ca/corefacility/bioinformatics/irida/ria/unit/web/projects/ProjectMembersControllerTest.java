package ca.corefacility.bioinformatics.irida.ria.unit.web.projects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.security.Principal;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.springframework.context.MessageSource;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import com.google.common.collect.Lists;

import ca.corefacility.bioinformatics.irida.exceptions.ProjectWithoutOwnerException;
import ca.corefacility.bioinformatics.irida.model.enums.ProjectRole;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.ria.exceptions.ProjectSelfEditException;
import ca.corefacility.bioinformatics.irida.ria.web.projects.ProjectControllerUtils;
import ca.corefacility.bioinformatics.irida.ria.web.projects.ProjectMembersController;
import ca.corefacility.bioinformatics.irida.ria.web.projects.ProjectsController;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.user.UserService;

public class ProjectMembersControllerTest {
	// Services
	private ProjectService projectService;
	private UserService userService;
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
		controller = new ProjectMembersController(projectUtils, projectService, userService, messageSource);
		projectTestUtils = new ProjectTestUtils(projectService, userService);

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
		when(userService.getUsersAvailableForProject(project)).thenReturn(users);

		Collection<User> usersAvailableForProject = controller.getUsersAvailableForProject(projectId, term);

		assertFalse(usersAvailableForProject.isEmpty());
		assertEquals("should only have 1 user.", 1, usersAvailableForProject.size());
		assertEquals("should have the specified user on project.", userId, usersAvailableForProject.iterator().next().getId());

		verify(projectService).read(projectId);
		verify(userService).getUsersAvailableForProject(project);
	}

	@Test
	public void testRemoveUserFromProject() throws ProjectWithoutOwnerException, ProjectSelfEditException {
		Long projectId = 1L;
		Long userId = 2L;
		User user = new User(userId, "tom", null, null, null, null, null);
		Project project = new Project("test");
		project.setId(projectId);
		Principal principal = () -> USER_NAME;

		when(userService.read(userId)).thenReturn(user);
		when(projectService.read(projectId)).thenReturn(project);

		controller.removeUser(projectId, userId, principal, null);

		verify(userService).read(userId);
		verify(projectService).read(projectId);
		verify(projectService).removeUserFromProject(project, user);
	}

	@Test
	public void testUdateUserRole() throws ProjectWithoutOwnerException, ProjectSelfEditException {
		Long projectId = 1L;
		Long userId = 2L;
		Project project = new Project();
		User user = new User(userId, "tom", null, null, "Tom", "Matthews", null);
		ProjectRole projectRole = ProjectRole.PROJECT_USER;

		Principal principal = () -> USER_NAME;

		when(projectService.read(projectId)).thenReturn(project);
		when(userService.read(userId)).thenReturn(user);

		controller.updateUserRole(projectId, userId, projectRole.toString(), principal, null);

		verify(projectService).read(projectId);
		verify(userService).read(userId);
		verify(projectService).updateUserProjectRole(project, user, projectRole);
	}

	@Test(expected = ProjectSelfEditException.class)
	public void testUdateUserSelfRole() throws ProjectWithoutOwnerException, ProjectSelfEditException {
		Long projectId = 1L;
		Long userId = 2L;
		Project project = new Project();
		User user = new User(userId, USER_NAME, null, null, "Tom", "Matthews", null);
		ProjectRole projectRole = ProjectRole.PROJECT_USER;

		Principal principal = () -> USER_NAME;

		when(projectService.read(projectId)).thenReturn(project);
		when(userService.read(userId)).thenReturn(user);

		controller.updateUserRole(projectId, userId, projectRole.toString(), principal, null);
	}
}
