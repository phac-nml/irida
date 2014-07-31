package ca.corefacility.bioinformatics.irida.ria.unit.web.projects;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import com.google.common.collect.Lists;

import ca.corefacility.bioinformatics.irida.exceptions.ProjectWithoutOwnerException;
import ca.corefacility.bioinformatics.irida.model.Project;
import ca.corefacility.bioinformatics.irida.model.enums.ProjectRole;
import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectUserJoin;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.ria.exceptions.ProjectSelfEditException;
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
		controller = new ProjectMembersController(projectUtils, projectService, userService);
		projectTestUtils = new ProjectTestUtils(projectService, sampleService, userService, sequenceFileService,
				projectUtils);

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
		Long projectId = 1l;
		Long userId = 2l;
		Project project = new Project();
		User user = new User(userId, "tom", null, null, "Tom", "Matthews", null);
		ProjectRole projectRole = ProjectRole.PROJECT_USER;

		when(projectService.read(projectId)).thenReturn(project);
		when(userService.read(userId)).thenReturn(user);

		controller.addProjectMember(projectId, userId, projectRole.toString());

		verify(projectService).read(projectId);
		verify(userService).read(userId);
		verify(projectService).addUserToProject(project, user, projectRole);
	}

	@Test
	public void testGetUsersAvailableForProject() {
		String term = "tom";
		Long projectId = 1l;
		Long userId = 2l;
		Project project = new Project();
		project.setId(projectId);
		List<User> users = Lists.newArrayList(new User(userId, "tom", null, null, "Tom", "Matthews", null));

		when(projectService.read(projectId)).thenReturn(project);
		when(userService.getUsersAvailableForProject(project)).thenReturn(users);

		Map<Long, String> usersAvailableForProject = controller.getUsersAvailableForProject(projectId, term);

		assertFalse(usersAvailableForProject.isEmpty());
		assertTrue(usersAvailableForProject.containsKey(userId));

		verify(projectService).read(projectId);
		verify(userService).getUsersAvailableForProject(project);
	}

	@Test
	public void testRemoveUserFromProject() throws ProjectWithoutOwnerException, ProjectSelfEditException {
		Long projectId = 1l;
		Long userId = 2l;
		User user = new User(userId, "tom", null, null, null, null, null);
		Project project = new Project("test");
		project.setId(projectId);
		Principal principal = () -> USER_NAME;

		when(userService.read(userId)).thenReturn(user);
		when(projectService.read(projectId)).thenReturn(project);

		controller.removeUser(projectId, userId, principal);

		verify(userService).read(userId);
		verify(projectService).read(projectId);
		verify(projectService).removeUserFromProject(project, user);
	}

	@Test
	public void testUdateUserRole() throws ProjectWithoutOwnerException, ProjectSelfEditException {
		Long projectId = 1l;
		Long userId = 2l;
		Project project = new Project();
		User user = new User(userId, "tom", null, null, "Tom", "Matthews", null);
		ProjectRole projectRole = ProjectRole.PROJECT_USER;

		Principal principal = () -> USER_NAME;

		when(projectService.read(projectId)).thenReturn(project);
		when(userService.read(userId)).thenReturn(user);

		controller.updateUserRole(projectId, userId, projectRole.toString(), principal);

		verify(projectService).read(projectId);
		verify(userService).read(userId);
		verify(projectService).updateUserProjectRole(project, user, projectRole);
	}

	@Test(expected = ProjectSelfEditException.class)
	public void testUdateUserSelfRole() throws ProjectWithoutOwnerException, ProjectSelfEditException {
		Long projectId = 1l;
		Long userId = 2l;
		Project project = new Project();
		User user = new User(userId, USER_NAME, null, null, "Tom", "Matthews", null);
		ProjectRole projectRole = ProjectRole.PROJECT_USER;

		Principal principal = () -> USER_NAME;

		when(projectService.read(projectId)).thenReturn(project);
		when(userService.read(userId)).thenReturn(user);

		controller.updateUserRole(projectId, userId, projectRole.toString(), principal);
	}

	@Test
	public void testGetAjaxUsersListForProject() {
		Long projectId = 32L;
		Project project = new Project("test");
		project.setId(projectId);
		Collection<Join<Project, User>> users = projectTestUtils.getUsersForProject(project);
		when(userService.getUsersForProject(any(Project.class))).thenReturn(users);
		Map<String, Collection<Join<Project, User>>> usersReturned = controller.getAjaxProjectMemberMap(projectId);
		assertTrue("Has a data attribute required for data tables", usersReturned.containsKey("data"));
		assertEquals("Has the correct number of users.", usersReturned.get("data").size(), 2);
	}
}
