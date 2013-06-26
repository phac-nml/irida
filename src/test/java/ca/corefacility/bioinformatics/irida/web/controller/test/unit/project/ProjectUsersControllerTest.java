package ca.corefacility.bioinformatics.irida.web.controller.test.unit.project;

import ca.corefacility.bioinformatics.irida.model.Project;
import ca.corefacility.bioinformatics.irida.model.Relationship;
import ca.corefacility.bioinformatics.irida.model.Role;
import ca.corefacility.bioinformatics.irida.model.User;
import ca.corefacility.bioinformatics.irida.model.roles.impl.Identifier;
import ca.corefacility.bioinformatics.irida.model.roles.impl.UserIdentifier;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.UserService;
import ca.corefacility.bioinformatics.irida.web.assembler.resource.ResourceCollection;
import ca.corefacility.bioinformatics.irida.web.assembler.resource.RootResource;
import ca.corefacility.bioinformatics.irida.web.assembler.resource.user.UserResource;
import ca.corefacility.bioinformatics.irida.web.controller.api.GenericController;
import ca.corefacility.bioinformatics.irida.web.controller.api.projects.ProjectUsersController;
import ca.corefacility.bioinformatics.irida.web.controller.api.projects.ProjectsController;
import ca.corefacility.bioinformatics.irida.web.controller.test.unit.TestDataFactory;
import com.google.common.collect.ImmutableMap;
import com.google.common.net.HttpHeaders;
import org.junit.Before;
import org.junit.Test;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.ModelMap;

import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

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
    public void testGetUsersForProject() {
        String projectId = UUID.randomUUID().toString();
        String username = "fbristow";
        UserIdentifier userId = new UserIdentifier(username);
        User u = new User();
        u.setUsername(username);
        u.setIdentifier(userId);
        Identifier id = new Identifier();
        id.setIdentifier(projectId);
        Relationship r = new Relationship(userId, id);
        r.setIdentifier(new Identifier());

        Collection<Relationship> relationshipCollection = new ArrayList<>();
        relationshipCollection.add(r);

        when(userService.getUsersForProject(id)).thenReturn(relationshipCollection);
        when(userService.getUserByUsername(username)).thenReturn(u);

        ModelMap map = controller.getUsersForProject(projectId);

        verify(userService, times(1)).getUsersForProject(id);
        verify(userService, times(1)).getUserByUsername(username);

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
        assertEquals("http://localhost/projects/" + projectId + "/users/" + username, relationship.getHref());
        assertTrue(users.getLink("self").getHref().contains(projectId));
    }

    @Test
    public void testAddUserToProject() {
        Project p = TestDataFactory.constructProject();
        User u = TestDataFactory.constructUser();

        when(projectService.read(p.getIdentifier())).thenReturn(p);
        when(userService.getUserByUsername(u.getUsername())).thenReturn(u);

        // prepare the "user" for addition to the project, just a map of userId and a username.
        Map<String, String> user = ImmutableMap.of(ProjectUsersController.USER_ID_KEY, u.getUsername());

        // add the user to the project
        ResponseEntity<String> response = controller.addUserToProject(p.getIdentifier().getIdentifier(), user);

        // confirm that the service method was called
        verify(projectService, times(1)).addUserToProject(p, u, new Role("ROLE_USER"));
        verify(projectService, times(1)).read(p.getIdentifier());
        verify(userService, times(1)).getUserByUsername(u.getUsername());

        // check that the response is as expected:
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        List<String> locations = response.getHeaders().get(HttpHeaders.LOCATION);
        assertNotNull(locations);
        assertFalse(locations.isEmpty());
        assertEquals(1, locations.size());
        assertEquals("http://localhost/projects/" + p.getIdentifier().getIdentifier() + "/users/" + u.getUsername(), locations.iterator().next());
    }

    @Test
    public void testRemoveUserFromProject() {
        Project p = TestDataFactory.constructProject();
        User u = TestDataFactory.constructUser();

        String projectId = p.getIdentifier().getIdentifier();

        when(projectService.read(p.getIdentifier())).thenReturn(p);
        when(userService.getUserByUsername(u.getIdentifier().getIdentifier())).thenReturn(u);

        ModelMap modelMap = controller.removeUserFromProject(projectId, u.getIdentifier().getIdentifier());

        verify(projectService).read(p.getIdentifier());
        verify(userService).getUserByUsername(u.getIdentifier().getIdentifier());
        verify(projectService).removeUserFromProject(p, u);

        Object o = modelMap.get(GenericController.RESOURCE_NAME);
        assertTrue(o instanceof RootResource);
        RootResource r = (RootResource) o;
        // confirm that a project link exists
        Link projectLink = r.getLink(ProjectsController.REL_PROJECT);
        assertNotNull(projectLink);
        assertEquals("http://localhost/projects/" + projectId, projectLink.getHref());

        // confirm that a project users link exists
        Link projectUsersLink = r.getLink(ProjectUsersController.REL_PROJECT_USERS);
        assertNotNull(projectUsersLink);
        assertEquals("http://localhost/projects/" + projectId + "/users", projectUsersLink.getHref());
    }

}
