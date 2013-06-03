package ca.corefacility.bioinformatics.irida.web.controller.test.unit;

import ca.corefacility.bioinformatics.irida.model.*;
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
import ca.corefacility.bioinformatics.irida.web.controller.api.SequenceFileController;
import com.google.common.net.HttpHeaders;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.ui.ModelMap;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link ProjectsController}.
 */
public class ProjectsControllerTest {
    ProjectsController controller;
    SamplesController samplesController;
    SequenceFileController sequenceFilesController;
    ProjectService projectService;
    UserService userService;
    RelationshipService relationshipService;

    @Before
    public void setUp() {
        projectService = mock(ProjectService.class);
        userService = mock(UserService.class);
        relationshipService = mock(RelationshipService.class);
        samplesController = mock(SamplesController.class);
        sequenceFilesController = mock(SequenceFileController.class);

        controller = new ProjectsController(projectService, userService, samplesController, sequenceFilesController);
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
        Sample s = constructSample();
        Project p = constructProject();
        String projectId = p.getIdentifier().getIdentifier();

        SampleResource sr = new SampleResource();
        sr.setResource(s);
        Relationship r = new Relationship(p.getIdentifier(), s.getIdentifier());

        when(projectService.read(p.getIdentifier())).thenReturn(p);
        when(projectService.addSampleToProject(p, s)).thenReturn(r);
        when(samplesController.mapResourceToType(sr)).thenReturn(s);

        ResponseEntity<String> response = controller.addSampleToProject(p.getIdentifier().getIdentifier(), sr);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        // the location header should correspond to the created sample URL under the samples controller.
        List<String> locations = response.getHeaders().get(HttpHeaders.LOCATION);
        assertNotNull(locations);
        assertFalse(locations.isEmpty());
        assertEquals(1, locations.size());
        assertEquals("http://localhost/samples/" + s.getIdentifier().getIdentifier(), locations.iterator().next());

        // the Link header should contain a reference to the relationship between sample and project.
        List<String> links = response.getHeaders().get(HttpHeaders.LINK);
        assertNotNull(links);
        assertFalse(links.isEmpty());
        assertEquals(1, links.size());
        assertEquals("<http://localhost/projects/" + projectId + "/samples/" + s.getIdentifier().getIdentifier() +
                ">; rel=relationship", links.iterator().next());
    }

    @Test
    public void testAddSequenceFileToProject() throws IOException {
        File f = Files.createTempFile(UUID.randomUUID().toString(), null).toFile();
        f.deleteOnExit();
        MockMultipartFile mmf = new MockMultipartFile("filename", "filename", "blurgh", FileCopyUtils.copyToByteArray(new FileInputStream(f)));

        SequenceFile sf = constructSequenceFile();
        Project p = constructProject();
        Relationship r = new Relationship(p.getIdentifier(), sf.getIdentifier());
        String projectId = p.getIdentifier().getIdentifier();
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add(HttpHeaders.LOCATION, "http://localhost/sequenceFiles/" + sf.getIdentifier().getIdentifier());
        ResponseEntity<String> fileCreationResponse = new ResponseEntity<>("success", headers, HttpStatus.CREATED);

        when(sequenceFilesController.create(mmf)).thenReturn(fileCreationResponse);
        when(projectService.read(p.getIdentifier())).thenReturn(p);
        when(projectService.addSequenceFileToProject(eq(p), any(SequenceFile.class))).thenReturn(r);

        ResponseEntity<String> response = controller.addSequenceFileToProject(p.getIdentifier().getIdentifier(), mmf);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        List<String> locations = response.getHeaders().get(HttpHeaders.LOCATION);
        assertNotNull(locations);
        assertFalse(locations.isEmpty());
        assertEquals(1, locations.size());
        assertEquals("http://localhost/sequenceFiles/" + sf.getIdentifier().getIdentifier(), locations.iterator().next());

        List<String> links = response.getHeaders().get(HttpHeaders.LINK);
        assertNotNull(links);
        assertFalse(links.isEmpty());
        assertEquals(1, links.size());
        assertEquals("<http://localhost/projects/" + projectId + "/sequenceFiles/" + sf.getIdentifier().getIdentifier()
                + ">; rel=relationship", links.iterator().next());
    }

    /**
     * Construct a simple {@link Sample}.
     *
     * @return a sample with a name and identifier.
     */
    private Sample constructSample() {
        String sampleId = UUID.randomUUID().toString();
        Identifier sampleIdentifier = new Identifier();
        sampleIdentifier.setIdentifier(sampleId);
        String sampleName = "sampleName";
        Sample s = new Sample();
        s.setSampleName(sampleName);
        s.setIdentifier(sampleIdentifier);
        return s;
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

    /**
     * Construct a simple {@link SequenceFile}.
     *
     * @return a {@link SequenceFile} with identifier.
     */
    private SequenceFile constructSequenceFile() {
        String sequenceFileId = UUID.randomUUID().toString();
        Identifier sequenceFileIdentifier = new Identifier();
        sequenceFileIdentifier.setIdentifier(sequenceFileId);
        SequenceFile sf = new SequenceFile();
        sf.setIdentifier(sequenceFileIdentifier);
        return sf;
    }
}
