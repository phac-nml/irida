package ca.corefacility.bioinformatics.irida.web.controller.test.unit.projects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;

import ca.corefacility.bioinformatics.irida.model.enums.ProjectMetadataRole;
import ca.corefacility.bioinformatics.irida.web.assembler.resource.ResponseResource;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletResponse;

import ca.corefacility.bioinformatics.irida.exceptions.ProjectWithoutOwnerException;
import ca.corefacility.bioinformatics.irida.model.enums.ProjectRole;
import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectUserJoin;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.user.UserService;
import ca.corefacility.bioinformatics.irida.web.assembler.resource.LabelledRelationshipResource;
import ca.corefacility.bioinformatics.irida.web.assembler.resource.ResourceCollection;
import ca.corefacility.bioinformatics.irida.web.assembler.resource.RootResource;
import ca.corefacility.bioinformatics.irida.web.controller.api.RESTGenericController;
import ca.corefacility.bioinformatics.irida.web.controller.api.projects.RESTProjectUsersController;
import ca.corefacility.bioinformatics.irida.web.controller.api.projects.RESTProjectsController;
import ca.corefacility.bioinformatics.irida.web.controller.test.unit.TestDataFactory;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.net.HttpHeaders;

/**
 * Tests for {@link RESTProjectUsersController}.
 */
public class ProjectUsersControllerTest {
	private RESTProjectUsersController controller;
	private ProjectService projectService;
	private UserService userService;

	@BeforeEach
	public void setUp() {
		projectService = mock(ProjectService.class);
		userService = mock(UserService.class);
		controller = new RESTProjectUsersController(userService, projectService);
	}

	@Test
	public void testGetUsersForProject() throws ProjectWithoutOwnerException {
		String username = "fbristow";
		User u = new User();
		u.setUsername(username);
		u.setId(1L);
		Project p = TestDataFactory.constructProject();
		Join<Project, User> join = new ProjectUserJoin(p, u, ProjectRole.PROJECT_OWNER);
		List<Join<Project, User>> relationships = Lists.newArrayList(join);

		when(userService.getUsersForProject(p)).thenReturn(relationships);
		when(projectService.read(p.getId())).thenReturn(p);

		ResponseResource<ResourceCollection<User>> responseResource = controller.getUsersForProject(p.getId());

		verify(projectService, times(1)).read(p.getId());
		verify(userService, times(1)).getUsersForProject(p);

		ResourceCollection<User> users = responseResource.getResource();
		assertNotNull(users);
		assertEquals(1, users.size());
		User ur = users.iterator()
				.next();
		assertTrue(ur.getLink("self")
				.map(i -> i.getHref()).orElse(null)
				.endsWith(username));
		Link relationship = ur.getLink(RESTGenericController.REL_RELATIONSHIP).map(i -> i).orElse(null);
		assertNotNull(relationship);
		assertEquals("http://localhost/api/projects/" + p.getId() + "/users/" + username, relationship.getHref());
		assertTrue(users.getLink("self")
				.map(i -> i.getHref()).orElse(null)
				.contains(p.getId()
						.toString()));
	}

	@Test
	public void testAddUserToProject() throws ProjectWithoutOwnerException {
		Project p = TestDataFactory.constructProject();
		User u = TestDataFactory.constructUser();
		ProjectRole r = ProjectRole.PROJECT_USER;
		ProjectMetadataRole metadataRole = ProjectMetadataRole.LEVEL_1;
		ProjectUserJoin j = new ProjectUserJoin(p, u, r);
		MockHttpServletResponse response = new MockHttpServletResponse();

		when(projectService.read(p.getId())).thenReturn(p);
		when(userService.getUserByUsername(u.getUsername())).thenReturn(u);
		when(projectService.addUserToProject(p, u, r, metadataRole)).thenReturn(j);
		// prepare the "user" for addition to the project, just a map of userId and a username.
		Map<String, String> userMap = ImmutableMap.of(RESTProjectUsersController.USER_ID_KEY, u.getUsername());

		// add the user to the project
		ResponseResource<LabelledRelationshipResource<Project, User>> responseResource = controller.addUserToProject(
				p.getId(), userMap, response);

		// confirm that the service method was called
		verify(projectService, times(1)).addUserToProject(p, u, ProjectRole.PROJECT_USER, ProjectMetadataRole.LEVEL_1);
		verify(projectService, times(1)).read(p.getId());
		verify(userService, times(1)).getUserByUsername(u.getUsername());
		// check that the response is as expected:
		assertEquals(HttpStatus.CREATED.value(), response.getStatus(), "Response must be CREATED");
		//check for a correct user link
		String location = response.getHeader(HttpHeaders.LOCATION);
		assertNotNull(location, "location must not be null");
		assertFalse(location.isEmpty(), "location must not be empty");
		assertEquals("http://localhost/api/projects/" + p.getId() + "/users/" + u.getUsername(), location,
				"location must be correct");
		//check the ModelMap's resource type
		LabelledRelationshipResource<Project, User> lrr = responseResource.getResource();
		assertNotNull(lrr, "labelled resource must not be null");
		ProjectUserJoin pj = (ProjectUserJoin) lrr.getResource();
		assertNotNull(pj, "project must not be null");
		User user = pj.getObject();
		assertNotNull(user, "user must not be null");
		assertEquals(user.getUsername(), u.getUsername(), "Username must be correct");
		//check for a correct relationship link
		assertTrue(lrr.getLink("self").map(i -> i.getHref()).orElse(null).endsWith(u.getUsername()),
				"relationship link must be correct");
		Link relationship = lrr.getLink(RESTGenericController.REL_RELATIONSHIP).map(i -> i).orElse(null);
		assertNotNull(relationship, "relationship link must exist");
		assertEquals("http://localhost/api/projects/" + p.getId() + "/users/" + u.getUsername(),
				relationship.getHref(), "relationship link must be correct");
		// confirm that a project link exists
		Link projectLink = lrr.getLink(RESTProjectsController.REL_PROJECT).map(i -> i).orElse(null);
		assertNotNull(projectLink, "project link must exist");
		assertEquals("http://localhost/api/projects/" + p.getId(), projectLink.getHref(),
				"project link must be correct");
		// confirm that a project users link exists
		Link projectUsersLink = lrr.getLink(RESTProjectUsersController.REL_PROJECT_USERS).map(i -> i).orElse(null);
		assertNotNull(projectUsersLink, "project users link must exist");
		assertEquals("http://localhost/api/projects/" + p.getId() + "/users", projectUsersLink.getHref(),
				"project users link must be correct");
	}

	@Test
	public void testAddUserToProjectWithRole() throws ProjectWithoutOwnerException {
		Project p = TestDataFactory.constructProject();
		User u = TestDataFactory.constructUser();
		ProjectRole r = ProjectRole.PROJECT_OWNER;
		ProjectMetadataRole metadataRole = ProjectMetadataRole.LEVEL_4;
		ProjectUserJoin j = new ProjectUserJoin(p, u, r, metadataRole);
		MockHttpServletResponse response = new MockHttpServletResponse();

		when(projectService.read(p.getId())).thenReturn(p);
		when(userService.getUserByUsername(u.getUsername())).thenReturn(u);
		when(projectService.addUserToProject(p, u, r, metadataRole)).thenReturn(j);

		//Note: Adding user as a project owner instead of basic user
		Map<String, String> userMap = ImmutableMap.of(RESTProjectUsersController.USER_ID_KEY, u.getUsername(),
				RESTProjectUsersController.USER_ROLE_KEY, r.toString(), RESTProjectUsersController.METADATA_ROLE_KEY,
				metadataRole.toString());

		// add the user to the project
		ResponseResource<LabelledRelationshipResource<Project, User>> responseResource = controller.addUserToProject(
				p.getId(), userMap, response);

		// confirm that the service method was called
		verify(projectService, times(1)).addUserToProject(p, u, ProjectRole.PROJECT_OWNER, ProjectMetadataRole.LEVEL_4);
		verify(projectService, times(1)).read(p.getId());
		verify(userService, times(1)).getUserByUsername(u.getUsername());
		// check that the response is as expected:
		assertEquals(HttpStatus.CREATED.value(), response.getStatus(), "Response must be CREATED");
		//check for a correct user link
		String location = response.getHeader(HttpHeaders.LOCATION);
		assertNotNull(location, "location must not be null");
		assertFalse(location.isEmpty(), "location must not be empty");
		assertEquals("http://localhost/api/projects/" + p.getId() + "/users/" + u.getUsername(), location,
				"location must be correct");
		//check the ModelMap's resource type
		LabelledRelationshipResource<Project, User> lrr = responseResource.getResource();
		assertNotNull(lrr, "labelled resource must not be null");
		ProjectUserJoin pj = (ProjectUserJoin) lrr.getResource();
		assertNotNull(pj, "project must not be null");
		User user = pj.getObject();
		assertNotNull(user, "user must not be null");
		assertEquals(user.getUsername(), u.getUsername(), "Username must be correct");
		//check for a correct relationship link
		assertTrue(lrr.getLink("self").map(i -> i.getHref()).orElse(null).endsWith(u.getUsername()),
				"relationship link must be correct");
		Link relationship = lrr.getLink(RESTGenericController.REL_RELATIONSHIP).map(i -> i).orElse(null);
		assertNotNull(relationship, "relationship link must exist");
		assertEquals("http://localhost/api/projects/" + p.getId() + "/users/" + u.getUsername(), relationship.getHref(),
				"relationship link must be correct");
		// confirm that a project link exists
		Link projectLink = lrr.getLink(RESTProjectsController.REL_PROJECT).map(i -> i).orElse(null);
		assertNotNull(projectLink, "project link must exist");
		assertEquals("http://localhost/api/projects/" + p.getId(), projectLink.getHref(),
				"project link must be correct");
		// confirm that a project users link exists
		Link projectUsersLink = lrr.getLink(RESTProjectUsersController.REL_PROJECT_USERS).map(i -> i).orElse(null);
		assertNotNull(projectUsersLink, "project users link must exist");
		assertEquals("http://localhost/api/projects/" + p.getId() + "/users", projectUsersLink.getHref(),
				"project users link must be correct");
	}

	@Test
	public void testRemoveUserFromProject() throws ProjectWithoutOwnerException {
		Project p = TestDataFactory.constructProject();
		User u = TestDataFactory.constructUser();

		when(projectService.read(p.getId())).thenReturn(p);
		when(userService.getUserByUsername(u.getUsername())).thenReturn(u);

		ResponseResource<RootResource> responseResource = controller.removeUserFromProject(p.getId(), u.getUsername());

		verify(projectService).read(p.getId());
		verify(userService).getUserByUsername(u.getUsername());
		verify(projectService).removeUserFromProject(p, u);

		RootResource r = responseResource.getResource();
		// confirm that a project link exists
		Link projectLink = r.getLink(RESTProjectsController.REL_PROJECT).map(i -> i).orElse(null);
		assertNotNull(projectLink);
		assertEquals("http://localhost/api/projects/" + p.getId(), projectLink.getHref());

		// confirm that a project users link exists
		Link projectUsersLink = r.getLink(RESTProjectUsersController.REL_PROJECT_USERS).map(i -> i).orElse(null);
		assertNotNull(projectUsersLink);
		assertEquals("http://localhost/api/projects/" + p.getId() + "/users", projectUsersLink.getHref());
	}

}
