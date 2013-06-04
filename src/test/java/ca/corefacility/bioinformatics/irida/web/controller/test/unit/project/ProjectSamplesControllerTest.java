package ca.corefacility.bioinformatics.irida.web.controller.test.unit.project;

import ca.corefacility.bioinformatics.irida.model.Project;
import ca.corefacility.bioinformatics.irida.model.Relationship;
import ca.corefacility.bioinformatics.irida.model.Sample;
import ca.corefacility.bioinformatics.irida.model.roles.impl.Identifier;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.SampleService;
import ca.corefacility.bioinformatics.irida.web.assembler.resource.RootResource;
import ca.corefacility.bioinformatics.irida.web.assembler.resource.sample.SampleResource;
import ca.corefacility.bioinformatics.irida.web.controller.api.GenericController;
import ca.corefacility.bioinformatics.irida.web.controller.api.SamplesController;
import ca.corefacility.bioinformatics.irida.web.controller.api.projects.ProjectSamplesController;
import ca.corefacility.bioinformatics.irida.web.controller.api.projects.ProjectsController;
import com.google.common.collect.Sets;
import com.google.common.net.HttpHeaders;
import org.junit.Before;
import org.junit.Test;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.ui.ModelMap;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Tests for {@link ProjectSamplesController}.
 */
public class ProjectSamplesControllerTest {
    private ProjectSamplesController controller;
    private ProjectService projectService;
    private SampleService sampleService;
    private SamplesController samplesController;

    @Before
    public void setUp() {
        projectService = mock(ProjectService.class);
        sampleService = mock(SampleService.class);
        samplesController = mock(SamplesController.class);
        controller = new ProjectSamplesController(projectService, sampleService, samplesController);
        // fake out the servlet response so that the URI builder will work.
        RequestAttributes ra = new ServletRequestAttributes(new MockHttpServletRequest());
        RequestContextHolder.setRequestAttributes(ra);
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
    public void testRemoveSampleFromProject() {
        Project p = constructProject();
        Sample s = constructSample();

        String projectId = p.getIdentifier().getIdentifier();
        String sampleId = s.getIdentifier().getIdentifier();

        when(projectService.read(p.getIdentifier())).thenReturn(p);
        when(sampleService.read(s.getIdentifier())).thenReturn(s);

        ModelMap modelMap = controller.removeSampleFromProject(projectId, sampleId);

        // verify that we actually tried to remove the sample from the project.
        verify(projectService, times(1)).removeSampleFromProject(p, s);

        // confirm that the response looks right.
        Object o = modelMap.get(GenericController.RESOURCE_NAME);
        assertTrue(o instanceof RootResource);
        @SuppressWarnings("unchecked")
        RootResource resource = (RootResource) o;
        List<Link> links = resource.getLinks();

        // should be two links in the response, one back to the individual project, the other to the samples collection
        Set<String> rels = Sets.newHashSet(ProjectsController.PROJECT_REL, ProjectSamplesController.PROJECT_SAMPLES_REL);
        for (Link link : links) {
            assertTrue(rels.contains(link.getRel()));
            assertNotNull(rels.remove(link.getRel()));
        }
        assertTrue(rels.isEmpty());
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
}
