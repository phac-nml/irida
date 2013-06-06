package ca.corefacility.bioinformatics.irida.web.controller.test.unit.project;

import ca.corefacility.bioinformatics.irida.model.Project;
import ca.corefacility.bioinformatics.irida.model.Relationship;
import ca.corefacility.bioinformatics.irida.model.Sample;
import ca.corefacility.bioinformatics.irida.model.roles.impl.Identifier;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.RelationshipService;
import ca.corefacility.bioinformatics.irida.service.SampleService;
import ca.corefacility.bioinformatics.irida.web.assembler.resource.ResourceCollection;
import ca.corefacility.bioinformatics.irida.web.assembler.resource.RootResource;
import ca.corefacility.bioinformatics.irida.web.assembler.resource.sample.SampleResource;
import ca.corefacility.bioinformatics.irida.web.controller.api.GenericController;
import ca.corefacility.bioinformatics.irida.web.controller.api.projects.ProjectSamplesController;
import ca.corefacility.bioinformatics.irida.web.controller.api.projects.ProjectsController;
import ca.corefacility.bioinformatics.irida.web.controller.api.samples.SamplesController;
import ca.corefacility.bioinformatics.irida.web.controller.links.PageLink;
import com.google.common.collect.ImmutableMap;
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

import java.util.*;

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
    private RelationshipService relationshipService;

    @Before
    public void setUp() {
        projectService = mock(ProjectService.class);
        sampleService = mock(SampleService.class);
        samplesController = mock(SamplesController.class);
        relationshipService = mock(RelationshipService.class);
        controller = new ProjectSamplesController(projectService, sampleService, relationshipService, samplesController);
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

        verify(projectService, times(1)).read(p.getIdentifier());
        verify(projectService, times(1)).addSampleToProject(p, s);
        verify(samplesController, times(1)).mapResourceToType(sr);

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
        verify(projectService, times(1)).read(p.getIdentifier());
        verify(sampleService, times(1)).read(s.getIdentifier());

        // confirm that the response looks right.
        Object o = modelMap.get(GenericController.RESOURCE_NAME);
        assertTrue(o instanceof RootResource);
        @SuppressWarnings("unchecked")
        RootResource resource = (RootResource) o;
        List<Link> links = resource.getLinks();

        // should be two links in the response, one back to the individual project, the other to the samples collection
        Set<String> rels = Sets.newHashSet(ProjectsController.REL_PROJECT, ProjectSamplesController.REL_PROJECT_SAMPLES);
        for (Link link : links) {
            assertTrue(rels.contains(link.getRel()));
            assertNotNull(rels.remove(link.getRel()));
        }
        assertTrue(rels.isEmpty());
    }

    @Test
    public void testGetProjectSamples() {
        Project p = constructProject();
        Sample s = constructSample();
        Relationship r = new Relationship();
        r.setSubject(p.getIdentifier());
        r.setObject(s.getIdentifier());
        r.setIdentifier(new Identifier());
        Collection<Relationship> relationships = Sets.newHashSet(r);

        String projectId = p.getIdentifier().getIdentifier();

        when(relationshipService.getRelationshipsForEntity(p.getIdentifier(),
                Project.class, Sample.class)).thenReturn(relationships);
        when(sampleService.read(s.getIdentifier())).thenReturn(s);

        ModelMap modelMap = controller.getProjectSamples(projectId);

        verify(relationshipService, times(1)).getRelationshipsForEntity(p.getIdentifier(), Project.class, Sample.class);
        verify(sampleService, times(1)).read(s.getIdentifier());

        Object o = modelMap.get(GenericController.RESOURCE_NAME);
        assertTrue(o instanceof ResourceCollection);
        @SuppressWarnings("unchecked")
        ResourceCollection<SampleResource> samples = (ResourceCollection<SampleResource>) o;
        assertEquals(1, samples.size());
        SampleResource resource = samples.iterator().next();
        assertEquals(s.getSampleName(), resource.getSampleName());
        List<Link> links = resource.getLinks();
        Set<String> rels = Sets.newHashSet(PageLink.REL_SELF);
        for (Link link : links) {
            assertTrue(rels.contains(link.getRel()));
            assertNotNull(rels.remove(link.getRel()));
        }
        assertTrue(rels.isEmpty());
    }

    @Test
    public void testGetIndividualSample() {
        Project p = constructProject();
        Sample s = constructSample();

        when(projectService.read(p.getIdentifier())).thenReturn(p);
        when(sampleService.getSampleForProject(p, s.getIdentifier())).thenReturn(s);

        ModelMap modelMap = controller.getProjectSample(p.getIdentifier().getIdentifier(),
                s.getIdentifier().getIdentifier());

        verify(sampleService, times(1)).getSampleForProject(p, s.getIdentifier());
        verify(projectService, times(1)).read(p.getIdentifier());

        Object o = modelMap.get(GenericController.RESOURCE_NAME);
        assertTrue(o instanceof SampleResource);
        SampleResource sr = (SampleResource) o;
        Set<String> rels = Sets.newHashSet(PageLink.REL_SELF, SamplesController.REL_SEQUENCE_FILES,
                SamplesController.REL_PROJECT);
        List<Link> links = sr.getLinks();
        for (Link link : links) {
            assertTrue(rels.contains(link.getRel()));
            assertNotNull(rels.remove(link.getRel()));
        }
        assertTrue(rels.isEmpty());
    }

    @Test
    public void testUpdateSample() {
        Project p = constructProject();
        Sample s = constructSample();
        Relationship r = new Relationship(p.getIdentifier(), s.getIdentifier());
        Map<String, Object> updatedFields = ImmutableMap.of("sampleName", (Object) "some new name");
        String projectId = p.getIdentifier().getIdentifier();
        String sampleId = s.getIdentifier().getIdentifier();

        when(relationshipService.getRelationship(p.getIdentifier(), s.getIdentifier())).thenReturn(r);
        when(sampleService.update(s.getIdentifier(), updatedFields)).thenReturn(s);

        ModelMap modelMap = controller.updateSample(projectId, sampleId, updatedFields);

        verify(relationshipService).getRelationship(p.getIdentifier(), s.getIdentifier());
        verify(sampleService).update(s.getIdentifier(), updatedFields);

        Object o = modelMap.get(GenericController.RESOURCE_NAME);
        assertNotNull(o);
        assertTrue(o instanceof RootResource);
        RootResource resource = (RootResource) o;
        Map<String, String> links = linksToMap(resource.getLinks());
        String self = links.get(PageLink.REL_SELF);
        assertEquals("http://localhost/projects/" + projectId + "/samples/" + sampleId, self);
        String sequenceFiles = links.get(SamplesController.REL_SEQUENCE_FILES);
        assertEquals("http://localhost/projects/" + projectId + "/samples/" + sampleId + "/sequenceFiles", sequenceFiles);
        String project = links.get(ProjectsController.REL_PROJECT);
        assertEquals("http://localhost/projects/" + projectId, project);
    }

    private Map<String, String> linksToMap(List<Link> links) {
        Map<String, String> linksMap = new HashMap<>();

        for (Link l : links) {
            linksMap.put(l.getRel(), l.getHref());
        }

        return linksMap;
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
