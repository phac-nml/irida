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
import ca.corefacility.bioinformatics.irida.web.assembler.resource.user.UserResource;
import ca.corefacility.bioinformatics.irida.web.controller.api.GenericController;
import ca.corefacility.bioinformatics.irida.web.controller.api.projects.ProjectUsersController;
import com.google.common.collect.ImmutableMap;
import com.google.common.net.HttpHeaders;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.ui.ModelMap;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

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

        // fake out the servlet response so that the URI builder will work.
        RequestAttributes ra = new ServletRequestAttributes(new MockHttpServletRequest());
        RequestContextHolder.setRequestAttributes(ra);
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

        Collection<Relationship> relationshipCollection = new ArrayList<>();
        relationshipCollection.add(new Relationship(userId, id));

        when(userService.getUsersForProject(id)).thenReturn(relationshipCollection);
        when(userService.getUserByUsername(username)).thenReturn(u);

        ModelMap map = controller.getUsersForProject(projectId);
        Object o = map.get(GenericController.RESOURCE_NAME);
        assertNotNull(o);
        assertTrue(o instanceof ResourceCollection);
        @SuppressWarnings("unchecked")
        ResourceCollection<UserResource> users = (ResourceCollection<UserResource>) o;
        assertEquals(1, users.size());
        UserResource ur = users.iterator().next();
        assertTrue(ur.getLink("self").getHref().endsWith(username));
        assertTrue(users.getLink("self").getHref().contains(projectId));
    }

    @Test
    public void testAddUserToProject() {
        Project p = constructProject();
        User u = constructUser();

        when(projectService.read(p.getIdentifier())).thenReturn(p);
        when(userService.getUserByUsername(u.getUsername())).thenReturn(u);

        // prepare the "user" for addition to the project, just a map of userId and a username.
        Map<String, String> user = ImmutableMap.of(ProjectUsersController.USER_ID_KEY, u.getUsername());

        // add the user to the project
        ResponseEntity<String> response = controller.addUserToProject(p.getIdentifier().getIdentifier(), user);

        // confirm that the service method was called
        verify(projectService, times(1)).addUserToProject(p, u, new Role("ROLE_USER"));

        // check that the response is as expected:
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        List<String> locations = response.getHeaders().get(HttpHeaders.LOCATION);
        assertNotNull(locations);
        assertFalse(locations.isEmpty());
        assertEquals(1, locations.size());
        assertEquals("http://localhost/projects/" + p.getIdentifier().getIdentifier() + "/users/" + u.getUsername(), locations.iterator().next());
    }

    /**
     * Construct a simple {@link User}.
     *
     * @return a {@link User} with identifier.
     */
    private User constructUser() {
        User u = new User();
        String username = "fbristow";
        UserIdentifier uid = new UserIdentifier();
        uid.setIdentifier(username);
        u.setIdentifier(uid);
        u.setUsername(username);

        return u;
    }

    /**
     * Construct a simple {@link Project}.
     *
     * @return a project with a name and identifier.
     */
    private Project constructProject() {
        String projectId = UUID.randomUUID().toString();
        Identifier projectIdentifier = new Identifier();
        projectIdentifier.setIdentifier(projectId);
        Project p = new Project();
        p.setIdentifier(projectIdentifier);
        return p;
    }
}
