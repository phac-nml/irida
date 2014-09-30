package ca.corefacility.bioinformatics.irida.ria.unit.web.projects;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.security.Principal;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.ui.ExtendedModelMap;

import ca.corefacility.bioinformatics.irida.model.RemoteAPI;
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
}
