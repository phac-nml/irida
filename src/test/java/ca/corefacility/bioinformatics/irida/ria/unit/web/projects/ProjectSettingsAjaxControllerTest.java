package ca.corefacility.bioinformatics.irida.ria.unit.web.projects;

import java.security.Principal;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Sort;

import ca.corefacility.bioinformatics.irida.model.joins.impl.RelatedProjectJoin;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.user.Role;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.ria.web.projects.settings.ProjectSettingsAjaxController;
import ca.corefacility.bioinformatics.irida.ria.web.projects.settings.dto.AssociatedProject;
import ca.corefacility.bioinformatics.irida.security.permissions.project.ProjectOwnerPermission;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.user.UserService;

import com.google.common.collect.Lists;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class ProjectSettingsAjaxControllerTest {
	private ProjectService projectService;
	private UserService userService;
	private ProjectOwnerPermission projectOwnerPermission;
	private ProjectSettingsAjaxController controller;

	@Before
	public void setUp() {
		projectService = mock(ProjectService.class);
		userService = mock(UserService.class);
		projectOwnerPermission = mock(ProjectOwnerPermission.class);
		controller = new ProjectSettingsAjaxController(projectService, projectOwnerPermission, userService);
	}

	@Test
	public void testGetProjectAssociatedProjects() {
		long CURRENT_PROJECT_ID = 1L;

		Project project1 = new Project("project1");
		when(projectService.read(CURRENT_PROJECT_ID)).thenReturn(project1);

		// Set up associated projects
		Project project2 = new Project("project2");
		project2.setId(2L);
		Project project3 = new Project("project3");
		project3.setId(3L);
		List<RelatedProjectJoin> relatedJoins = Lists.newArrayList(new RelatedProjectJoin(project1, project2));
		when(projectService.getRelatedProjects(project1)).thenReturn(relatedJoins);

		Page<Project> projectPage = new PageImpl<>(Lists.newArrayList(project2, project3));
		when(projectService.getUnassociatedProjects(project1, "", 0, Integer.MAX_VALUE, Sort.Direction.ASC,
				"name")).thenReturn(projectPage);

		String USERNAME = "Fred";
		Principal principal = () -> USERNAME;
		User user = new User();
		user.setSystemRole(Role.ROLE_ADMIN);
		when(userService.getUserByUsername(USERNAME)).thenReturn(user);
		List<AssociatedProject> associatedProjects = controller.getProjectAssociatedProjects(CURRENT_PROJECT_ID,
				principal);

		assertEquals("As admin all available projects should be returned", 2, associatedProjects.size());
		assertTrue("project2 Should be associated to project1", associatedProjects.get(0)
				.getId() == 2L && associatedProjects.get(0)
				.isAssociated());
		assertFalse("project3 Should not be associated to project1", associatedProjects.get(1)
				.getId() == 3L && associatedProjects.get(1)
				.isAssociated());
	}

	@Test
	public void testRemoveAssociatedProject() {
		Project project = new Project();
		project.setId(1L);
		Project associatedProject = new Project();
		associatedProject.setId(2L);

		when(projectService.read(1L)).thenReturn(project);
		when(projectService.read(2L)).thenReturn(associatedProject);
		controller.removeAssociatedProject(1L, 2L);
		verify(projectService, times(1)).removeRelatedProject(project, associatedProject);
	}

	@Test
	public void testAddAssociatedProject() {
		Project project = new Project();
		project.setId(1L);
		Project associatedProject = new Project();
		associatedProject.setId(2L);

		when(projectService.read(1L)).thenReturn(project);
		when(projectService.read(2L)).thenReturn(associatedProject);
		controller.addAssociatedProject(1L, 2L);
		verify(projectService, times(1)).addRelatedProject(project, associatedProject);
	}
}
