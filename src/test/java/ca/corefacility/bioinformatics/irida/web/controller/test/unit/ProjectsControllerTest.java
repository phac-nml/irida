package ca.corefacility.bioinformatics.irida.web.controller.test.unit;

import ca.corefacility.bioinformatics.irida.model.Project;
import ca.corefacility.bioinformatics.irida.model.Relationship;
import ca.corefacility.bioinformatics.irida.model.Sample;
import ca.corefacility.bioinformatics.irida.model.User;
import ca.corefacility.bioinformatics.irida.model.roles.impl.Identifier;
import ca.corefacility.bioinformatics.irida.model.roles.impl.UserIdentifier;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.RelationshipService;
import ca.corefacility.bioinformatics.irida.service.UserService;
import ca.corefacility.bioinformatics.irida.web.assembler.resource.ResourceCollection;
import ca.corefacility.bioinformatics.irida.web.assembler.resource.sample.SampleResource;
import ca.corefacility.bioinformatics.irida.web.assembler.resource.user.UserResource;
import ca.corefacility.bioinformatics.irida.web.controller.api.GenericController;
import ca.corefacility.bioinformatics.irida.web.controller.api.ProjectsController;
import ca.corefacility.bioinformatics.irida.web.controller.api.SamplesController;
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link ProjectsController}.
 */
public class ProjectsControllerTest {
    ProjectsController controller;
    SamplesController samplesController;
    ProjectService projectService;
    UserService userService;
    RelationshipService relationshipService;

    @Before
    public void setUp() {
        projectService = mock(ProjectService.class);
        userService = mock(UserService.class);
        relationshipService = mock(RelationshipService.class);
        samplesController = mock(SamplesController.class);

        controller = new ProjectsController(projectService, userService);
        controller.setSamplesController(samplesController);
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
    public void testAddSampleToProject() {
        String projectId = UUID.randomUUID().toString();
        String sampleId = UUID.randomUUID().toString();
        Identifier projectIdentifier = new Identifier();
        projectIdentifier.setIdentifier(projectId);
        Identifier sampleIdentifier = new Identifier();
        sampleIdentifier.setIdentifier(sampleId);
        Project p = new Project();
        String sampleName = "sampleName";
        Sample s = new Sample();
        s.setSampleName(sampleName);
        s.setIdentifier(sampleIdentifier);
        SampleResource sr = new SampleResource();
        sr.setResource(s);
        Relationship r = new Relationship();
        r.setSubject(p.getIdentifier());
        r.setObject(s.getIdentifier());

        when(projectService.read(projectIdentifier)).thenReturn(p);
        when(projectService.addSampleToProject(p, s)).thenReturn(r);
        when(samplesController.mapResourceToType(sr)).thenReturn(s);

        ResponseEntity<String> response = controller.addSampleToProject(projectId, sr);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        // the location header should correspond to the created sample URL under the samples controller.
        List<String> locations = response.getHeaders().get(HttpHeaders.LOCATION);
        assertNotNull(locations);
        assertFalse(locations.isEmpty());
        assertEquals(1, locations.size());
        assertEquals("http://localhost/samples/" + sampleId, locations.iterator().next());

        // the Link header should contain a reference to the relationship between sample and project.
        List<String> links = response.getHeaders().get(HttpHeaders.LINK);
        assertNotNull(links);
        assertFalse(links.isEmpty());
        assertEquals(1, links.size());
        assertEquals("<http://localhost/projects/" + projectId + "/samples/" + sampleId + ">; rel=relationship", links.iterator().next());
    }
}
