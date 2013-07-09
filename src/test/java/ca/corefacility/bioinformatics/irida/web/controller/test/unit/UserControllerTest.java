package ca.corefacility.bioinformatics.irida.web.controller.test.unit;

import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.model.Project;
import ca.corefacility.bioinformatics.irida.model.User;
import ca.corefacility.bioinformatics.irida.model.roles.impl.Identifier;
import ca.corefacility.bioinformatics.irida.model.roles.impl.UserIdentifier;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.RelationshipService;
import ca.corefacility.bioinformatics.irida.service.UserService;
import ca.corefacility.bioinformatics.irida.web.assembler.resource.ResourceCollection;
import ca.corefacility.bioinformatics.irida.web.assembler.resource.project.ProjectResource;
import ca.corefacility.bioinformatics.irida.web.assembler.resource.user.UserResource;
import ca.corefacility.bioinformatics.irida.web.controller.api.GenericController;
import ca.corefacility.bioinformatics.irida.web.controller.api.UsersController;
import ca.corefacility.bioinformatics.irida.web.controller.api.links.PageLink;

import com.google.common.collect.Lists;

import org.junit.Before;
import org.junit.Test;
import org.springframework.hateoas.Link;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.ui.ModelMap;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link UsersController}.
 *
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 */
public class UserControllerTest {

    private UsersController controller;
    private UserService userService;
    private ProjectService projectService;
    private RelationshipService relationshipService;

    @Before
    public void setUp() {
        userService = mock(UserService.class);
        projectService = mock(ProjectService.class);
        relationshipService = mock(RelationshipService.class);
        controller = new UsersController(userService, projectService, relationshipService);

        // fake out the servlet response so that the URI builder will work.
        RequestAttributes ra = new ServletRequestAttributes(new MockHttpServletRequest());
        RequestContextHolder.setRequestAttributes(ra);
    }

    @Test
    public void testGetUserProjects() {
        // set up expectations
        String username = "fbristow";
        String projectName = "Super duper project";
        User u = new User();
        u.setUsername(username);
        Collection<Project> projects = new HashSet<>();
        Project p = new Project();
        p.setIdentifier(new Identifier());
        p.setName(projectName);
        projects.add(p);
        // set up mocks
        when(userService.getUserByUsername(username)).thenReturn(u);
        when(projectService.getProjectsForUser(u)).thenReturn(projects);
        // run the test
        ModelMap output = controller.getUserProjects(username);
        @SuppressWarnings("unchecked")
        ResourceCollection<ProjectResource> pulledProjects = (ResourceCollection<ProjectResource>) output
                .get("projectResources");
        List<ProjectResource> projectResources = pulledProjects.getResources();
        assertEquals(1, projectResources.size());
        ProjectResource resource = projectResources.get(0);
        assertEquals(projectName, resource.getName());
        assertEquals(1, resource.getLinks().size());
        Link link = resource.getLinks().get(0);
        assertEquals(PageLink.REL_SELF, link.getRel());
        assertTrue(link.getHref().contains(p.getIdentifier().getUUID().toString()));
    }

    @Test
    public void testGetUserProjectsBadUser() {
        String username = "superbad";
        when(userService.getUserByUsername(username)).thenThrow(
                new EntityNotFoundException(username));
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
        u.setIdentifier(new UserIdentifier(username));
        List<User> users = Lists.newArrayList(u);
        when(userService.list()).thenReturn(users);

        ModelMap output = controller.listAllResources();

        @SuppressWarnings("unchecked")
		ResourceCollection<UserResource> usersCollection = (ResourceCollection<UserResource>) output.get(
                GenericController.RESOURCE_NAME);
        assertEquals("user resource collection total resources is wrong.", 1, usersCollection.getTotalResources());
        assertEquals("users collection is the wrong size.", 1, usersCollection.size());
        UserResource userResource = usersCollection.iterator().next();
        assertEquals("username is not correct.", username, userResource.getUsername());
    }
}