/*
 * Copyright 2013 Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ca.corefacility.bioinformatics.irida.web.controller.test.unit;

import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.model.Project;
import ca.corefacility.bioinformatics.irida.model.User;
import ca.corefacility.bioinformatics.irida.model.roles.impl.Identifier;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.UserService;
import ca.corefacility.bioinformatics.irida.web.assembler.resource.ResourceCollection;
import ca.corefacility.bioinformatics.irida.web.assembler.resource.project.ProjectResource;
import ca.corefacility.bioinformatics.irida.web.controller.api.UsersController;
import ca.corefacility.bioinformatics.irida.web.controller.links.PageLink;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import org.springframework.hateoas.Link;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.ModelAndView;

/**
 * Unit tests for {@link UsersController}.
 *
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 */
public class UserControllerTest {

    private UsersController controller;
    private UserService userService;
    private ProjectService projectService;

    @Before
    public void setUp() {
        userService = mock(UserService.class);
        projectService = mock(ProjectService.class);
        controller = new UsersController(userService, projectService);

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
        ModelAndView output = controller.getUserProjects(username);
        @SuppressWarnings("unchecked")
        ResourceCollection<ProjectResource> pulledProjects = (ResourceCollection<ProjectResource>) output.getModel().get("projectResources");
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
}