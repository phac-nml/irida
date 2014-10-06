package ca.corefacility.bioinformatics.irida.ria.unit.web.projects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;

import java.security.Principal;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.ui.ExtendedModelMap;

import ca.corefacility.bioinformatics.irida.model.RemoteAPI;
import ca.corefacility.bioinformatics.irida.model.enums.ProjectRole;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectUserJoin;
import ca.corefacility.bioinformatics.irida.model.joins.impl.RelatedProjectJoin;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.remote.RemoteRelatedProject;
import ca.corefacility.bioinformatics.irida.model.user.Role;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.ria.web.projects.AssociatedProjectsController;
import ca.corefacility.bioinformatics.irida.ria.web.projects.ProjectControllerUtils;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.RemoteRelatedProjectService;
import ca.corefacility.bioinformatics.irida.service.user.UserService;

import com.google.common.collect.Lists;

public class AssociatedProjectControllerTest {
	private static final String USER_NAME = "testme";

	private ProjectService projectService;
	private AssociatedProjectsController controller;
	private UserService userService;
	private ProjectControllerUtils projectUtils;
	private RemoteRelatedProjectService remoteRelatedProjectService;

	@Before
	public void setUp() {
		projectService = mock(ProjectService.class);
		userService = mock(UserService.class);
		projectUtils = mock(ProjectControllerUtils.class);
		remoteRelatedProjectService = mock(RemoteRelatedProjectService.class);
		controller = new AssociatedProjectsController(remoteRelatedProjectService, projectService, projectUtils,
				userService);
	}

	@Test
	public void testGetAssociatedProjectsPage() {

		ExtendedModelMap model = new ExtendedModelMap();
		Principal principal = () -> USER_NAME;
		Long projectId = 1l;
		User u = new User();
		u.setSystemRole(Role.ROLE_ADMIN);
		Project p = new Project("my project");
		p.setId(projectId);
		Project o = new Project("other project");
		o.setId(2l);
		List<RelatedProjectJoin> relatedProjects = Lists.newArrayList(new RelatedProjectJoin(p, o));

		RemoteAPI remoteAPI = new RemoteAPI();
		List<RemoteRelatedProject> remoteRelatedProjects = Lists.newArrayList(new RemoteRelatedProject(p, remoteAPI,
				"http://somewhere"));

		when(projectService.read(projectId)).thenReturn(p);

		when(userService.getUserByUsername(USER_NAME)).thenReturn(u);
		when(projectService.getRelatedProjects(p)).thenReturn(relatedProjects);
		when(remoteRelatedProjectService.getRemoteProjectsForProject(p)).thenReturn(remoteRelatedProjects);

		controller.getAssociatedProjectsPage(projectId, model, principal);

		assertTrue(model.containsAttribute("isAdmin"));
		assertTrue(model.containsAttribute("associatedProjects"));
		assertTrue(model.containsAttribute("remoteProjectsByApi"));

		verify(projectService).read(projectId);
		verify(userService, times(2)).getUserByUsername(USER_NAME);
		verify(projectService).getRelatedProjects(p);
		verify(remoteRelatedProjectService).getRemoteProjectsForProject(p);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testGetPotentialAssociatedProjectsAsAdmin() {
		Long projectId = 1l;
		Principal principal = () -> USER_NAME;
		int page = 1;
		int count = 10;
		String sortedBy = "id";
		String sortDir = "ASC";
		String projectName = "";

		Project p1 = new Project("p1");
		when(projectService.read(projectId)).thenReturn(p1);

		User user = new User();
		user.setSystemRole(Role.ROLE_ADMIN);
		when(userService.getUserByUsername(USER_NAME)).thenReturn(user);
		// (specification, page, count, sortDirection, sortedBy);
		Project p2 = new Project("p2");
		p2.setId(2l);
		Project p3 = new Project("p3");
		p3.setId(3l);

		List<RelatedProjectJoin> relatedJoins = Lists.newArrayList(new RelatedProjectJoin(p1, p2));
		when(projectService.getRelatedProjects(p1)).thenReturn(relatedJoins);

		Page<Project> projectPage = new PageImpl<>(Lists.newArrayList(p2, p3));
		when(projectService.search(any(Specification.class), eq(page), eq(count), any(Direction.class), eq(sortedBy)))
				.thenReturn(projectPage);

		Map<String, Object> potentialAssociatedProjects = controller.getPotentialAssociatedProjects(projectId,
				principal, page, count, sortedBy, sortDir, projectName);

		assertTrue(potentialAssociatedProjects.containsKey("associated"));

		List<Map<String, String>> associated = (List<Map<String, String>>) potentialAssociatedProjects
				.get("associated");
		assertEquals(2, associated.size());
		for (Map<String, String> pmap : associated) {
			if (pmap.get("id").equals("2")) {
				assertTrue(pmap.containsKey("associated"));
			}
		}

		verify(projectService).read(projectId);
		verify(userService).getUserByUsername(USER_NAME);
		verify(projectService).getRelatedProjects(p1);
		verify(projectService)
				.search(any(Specification.class), eq(page), eq(count), any(Direction.class), eq(sortedBy));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testGetPotentialAssociatedProjectsAsUser() {
		Long projectId = 1l;
		Principal principal = () -> USER_NAME;
		int page = 1;
		int count = 10;
		String sortedBy = "id";
		String sortDir = "ASC";
		String projectName = "";

		Project p1 = new Project("p1");
		when(projectService.read(projectId)).thenReturn(p1);

		User user = new User();
		user.setSystemRole(Role.ROLE_USER);
		when(userService.getUserByUsername(USER_NAME)).thenReturn(user);
		Project p2 = new Project("p2");
		p2.setId(2l);
		Project p3 = new Project("p3");
		p3.setId(3l);

		List<RelatedProjectJoin> relatedJoins = Lists.newArrayList(new RelatedProjectJoin(p1, p2));
		when(projectService.getRelatedProjects(p1)).thenReturn(relatedJoins);

		Page<ProjectUserJoin> projectPage = new PageImpl<>(Lists.newArrayList(new ProjectUserJoin(p2, user,
				ProjectRole.PROJECT_OWNER), new ProjectUserJoin(p3, user, ProjectRole.PROJECT_OWNER)));
		when(
				projectService.searchProjectUsers(any(Specification.class), eq(page), eq(count), any(Direction.class),
						eq("project." + sortedBy))).thenReturn(projectPage);

		Map<String, Object> potentialAssociatedProjects = controller.getPotentialAssociatedProjects(projectId,
				principal, page, count, sortedBy, sortDir, projectName);

		assertTrue(potentialAssociatedProjects.containsKey("associated"));

		List<Map<String, String>> associated = (List<Map<String, String>>) potentialAssociatedProjects
				.get("associated");
		assertEquals(2, associated.size());
		for (Map<String, String> pmap : associated) {
			if (pmap.get("id").equals("2")) {
				assertTrue(pmap.containsKey("associated"));
			}
		}

		verify(projectService).read(projectId);
		verify(userService).getUserByUsername(USER_NAME);
		verify(projectService).getRelatedProjects(p1);
		verify(projectService).searchProjectUsers(any(Specification.class), eq(page), eq(count), any(Direction.class),
				eq("project." + sortedBy));
	}
}
