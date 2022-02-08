package ca.corefacility.bioinformatics.irida.web.controller.test.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import ca.corefacility.bioinformatics.irida.web.assembler.resource.ResponseProjectResource;
import ca.corefacility.bioinformatics.irida.web.assembler.resource.ResponseResource;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.Link;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.google.common.collect.Lists;

import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.model.enums.ProjectRole;
import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectUserJoin;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.user.UserService;
import ca.corefacility.bioinformatics.irida.web.assembler.resource.ResourceCollection;
import ca.corefacility.bioinformatics.irida.web.controller.api.RESTUsersController;

/**
 * Unit tests for {@link RESTUsersController}.
 */
public class UserControllerTest {

	private RESTUsersController controller;
	private UserService userService;
	private ProjectService projectService;

	@BeforeEach
	public void setUp() {
		userService = mock(UserService.class);
		projectService = mock(ProjectService.class);
		controller = new RESTUsersController(userService, projectService);

		// fake out the servlet response so that the URI builder will work.
		RequestAttributes ra = new ServletRequestAttributes(new MockHttpServletRequest());
		RequestContextHolder.setRequestAttributes(ra);
	}

	@Test
	public void testGetUserProjects() {
		// set up expectations
		String username = "fbristow";
		User u = new User();
		u.setUsername(username);
		List<Join<Project, User>> projects = new ArrayList<>();
		Project p = TestDataFactory.constructProject();
		Join<Project, User> join = new ProjectUserJoin(p, u, ProjectRole.PROJECT_USER);
		projects.add(join);
		// set up mocks
		when(userService.getUserByUsername(username)).thenReturn(u);
		when(projectService.getProjectsForUser(u)).thenReturn(projects);
		// run the test
		ResponseProjectResource<ResourceCollection<Project>> output = controller.getUserProjects(username);
		ResourceCollection<Project> pulledProjects = (ResourceCollection<Project>) output.getProjectResources();
		List<Project> projectResources = pulledProjects.getResources();
		assertEquals(1, projectResources.size());
		Project resource = projectResources.get(0);
		assertEquals(p.getName(), resource.getName());
		assertEquals(1, resource.getLinks()
				.size());
		Link link = resource.getLinks()
				.get(0);
		assertEquals(IanaLinkRelations.SELF, link.getRel());
		assertTrue(link.getHref()
				.contains(p.getId()
						.toString()));
	}

	@Test
	public void testGetUserProjectsBadUser() {
		String username = "superbad";
		when(userService.getUserByUsername(username)).thenThrow(new EntityNotFoundException(username));
		try {
			controller.getUserProjects(username);
			fail();
		} catch (EntityNotFoundException e) {
		} catch (Exception e) {
			fail();
		}
	}

	@Test
	public void testGetAllUsers() {
		String username = "fbristow";
		User u = new User();
		u.setUsername(username);
		List<User> users = Lists.newArrayList(u);
		when(userService.findAll()).thenReturn(users);
		when(userService.count()).thenReturn(1L);

		ResponseResource<ResourceCollection<User>> output = controller.listAllResources();

		ResourceCollection<User> usersCollection = output.getResource();
		assertEquals(1, usersCollection.size(), "users collection is the wrong size.");
		User userResource = usersCollection.iterator()
				.next();
		assertEquals(username, userResource.getUsername(), "username is not correct.");
	}
}