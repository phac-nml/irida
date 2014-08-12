package ca.corefacility.bioinformatics.irida.web.controller.test.unit.project;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.ModelMap;

import ca.corefacility.bioinformatics.irida.exceptions.ProjectWithoutOwnerException;
import ca.corefacility.bioinformatics.irida.model.enums.ProjectRole;
import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectUserJoin;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.user.UserService;
import ca.corefacility.bioinformatics.irida.web.assembler.resource.ResourceCollection;
import ca.corefacility.bioinformatics.irida.web.assembler.resource.RootResource;
import ca.corefacility.bioinformatics.irida.web.assembler.resource.user.UserResource;
import ca.corefacility.bioinformatics.irida.web.controller.api.GenericController;
import ca.corefacility.bioinformatics.irida.web.controller.api.projects.ProjectUsersController;
import ca.corefacility.bioinformatics.irida.web.controller.api.projects.ProjectsController;
import ca.corefacility.bioinformatics.irida.web.controller.test.unit.TestDataFactory;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.net.HttpHeaders;

/**
 * Tests for {@link ProjectUsersController}.
 */
public class ProjectUsersControllerTest {
    private ProjectUsersController controller;
    private ProjectService projectService;
    private UserService userService;

    @Before
    public void setUp() {
        projectService = mock(ProjectService.class);
        userService = mock(UserService.class);
        controller = new ProjectUsersController(userService, projectService);
    }

    @Test
    public void testGetUsersForProject() throws ProjectWithoutOwnerException {
        String username = "fbristow";
        User u = new User();
        u.setUsername(username);
        u.setId(1L);
        Project p = TestDataFactory.constructProject();
        Join<Project, User> join = new ProjectUserJoin(p, u,ProjectRole.PROJECT_OWNER);
        @SuppressWarnings("unchecked")
		List<Join<Project, User>> relationships = Lists.newArrayList(join);

        when(userService.getUsersForProject(p)).thenReturn(relationships);
        when(projectService.read(p.getId())).thenReturn(p);

        ModelMap map = controller.getUsersForProject(p.getId());

        verify(projectService, times(1)).read(p.getId());
        verify(userService, times(1)).getUsersForProject(p);

        Object o = map.get(GenericController.RESOURCE_NAME);
        assertNotNull(o);
        assertTrue(o instanceof ResourceCollection);
        @SuppressWarnings("unchecked")
        ResourceCollection<UserResource> users = (ResourceCollection<UserResource>) o;
        assertEquals(1, users.size());
        UserResource ur = users.iterator().next();
        assertTrue(ur.getLink("self").getHref().endsWith(username));
        Link relationship = ur.getLink(GenericController.REL_RELATIONSHIP);
        assertNotNull(relationship);
        assertEquals("http://localhost/projects/" + p.getId() + "/users/" + username, relationship.getHref());
        assertTrue(users.getLink("self").getHref().contains(p.getId().toString()));
    }

    @Test
    public void testAddUserToProject() {
        Project p = TestDataFactory.constructProject();
        User u = TestDataFactory.constructUser();

        when(projectService.read(p.getId())).thenReturn(p);
        when(userService.getUserByUsername(u.getUsername())).thenReturn(u);

        // prepare the "user" for addition to the project, just a map of userId and a username.
        Map<String, String> user = ImmutableMap.of(ProjectUsersController.USER_ID_KEY, u.getUsername());

        // add the user to the project
        ResponseEntity<String> response = controller.addUserToProject(p.getId(), user);

        // confirm that the service method was called
        verify(projectService, times(1)).addUserToProject(p, u, ProjectRole.PROJECT_USER);
        verify(projectService, times(1)).read(p.getId());
        verify(userService, times(1)).getUserByUsername(u.getUsername());

        // check that the response is as expected:
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        List<String> locations = response.getHeaders().get(HttpHeaders.LOCATION);
        assertNotNull(locations);
        assertFalse(locations.isEmpty());
        assertEquals(1, locations.size());
        assertEquals("http://localhost/projects/" + p.getId() + "/users/" + u.getUsername(), locations.iterator().next());
    }

    @Test
    public void testRemoveUserFromProject() throws ProjectWithoutOwnerException {
        Project p = TestDataFactory.constructProject();
        User u = TestDataFactory.constructUser();

        when(projectService.read(p.getId())).thenReturn(p);
        when(userService.getUserByUsername(u.getUsername())).thenReturn(u);

        ModelMap modelMap = controller.removeUserFromProject(p.getId(), u.getUsername());

        verify(projectService).read(p.getId());
        verify(userService).getUserByUsername(u.getUsername());
        verify(projectService).removeUserFromProject(p, u);

        Object o = modelMap.get(GenericController.RESOURCE_NAME);
        assertTrue(o instanceof RootResource);
        RootResource r = (RootResource) o;
        // confirm that a project link exists
        Link projectLink = r.getLink(ProjectsController.REL_PROJECT);
        assertNotNull(projectLink);
        assertEquals("http://localhost/projects/" + p.getId(), projectLink.getHref());

        // confirm that a project users link exists
        Link projectUsersLink = r.getLink(ProjectUsersController.REL_PROJECT_USERS);
        assertNotNull(projectUsersLink);
        assertEquals("http://localhost/projects/" + p.getId() + "/users", projectUsersLink.getHref());
    }

}
