package ca.corefacility.bioinformatics.irida.web.controller.test.unit.project;

import ca.corefacility.bioinformatics.irida.model.Project;
import ca.corefacility.bioinformatics.irida.model.Relationship;
import ca.corefacility.bioinformatics.irida.model.Sample;
import ca.corefacility.bioinformatics.irida.model.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.roles.impl.Identifier;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.RelationshipService;
import ca.corefacility.bioinformatics.irida.service.SampleService;
import ca.corefacility.bioinformatics.irida.web.assembler.resource.ResourceCollection;
import ca.corefacility.bioinformatics.irida.web.assembler.resource.RootResource;
import ca.corefacility.bioinformatics.irida.web.assembler.resource.sample.SampleResource;
import ca.corefacility.bioinformatics.irida.web.controller.api.GenericController;
import ca.corefacility.bioinformatics.irida.web.controller.api.links.PageLink;
import ca.corefacility.bioinformatics.irida.web.controller.api.projects.ProjectSamplesController;
import ca.corefacility.bioinformatics.irida.web.controller.api.projects.ProjectsController;
import ca.corefacility.bioinformatics.irida.web.controller.api.samples.SampleSequenceFilesController;
import ca.corefacility.bioinformatics.irida.web.controller.test.unit.TestDataFactory;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import com.google.common.net.HttpHeaders;
import org.junit.Before;
import org.junit.Test;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.ModelMap;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
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
    private RelationshipService relationshipService;

    @Before
    public void setUp() {
        projectService = mock(ProjectService.class);
        sampleService = mock(SampleService.class);
        relationshipService = mock(RelationshipService.class);
        controller = new ProjectSamplesController(projectService, sampleService, relationshipService);
    }

    @Test
    public void testAddSampleToProject() {
        Sample s = TestDataFactory.constructSample();
        Project p = TestDataFactory.constructProject();
        String projectId = p.getIdentifier().getIdentifier();

        SampleResource sr = new SampleResource();
        sr.setResource(s);
        Relationship r = new Relationship(p.getIdentifier(), s.getIdentifier());

        when(projectService.read(p.getIdentifier())).thenReturn(p);
        when(projectService.addSampleToProject(p, s)).thenReturn(r);

        ResponseEntity<String> response = controller.addSampleToProject(p.getIdentifier().getIdentifier(), sr);

        verify(projectService, times(1)).read(p.getIdentifier());
        verify(projectService, times(1)).addSampleToProject(p, s);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        // the location header should correspond to the created sample URL under the samples controller.
        List<String> locations = response.getHeaders().get(HttpHeaders.LOCATION);
        assertNotNull(locations);
        assertFalse(locations.isEmpty());
        assertEquals(1, locations.size());
        assertEquals("http://localhost/projects/" + projectId + "/samples/" + s.getIdentifier().getIdentifier(), locations.iterator().next());
    }

    @Test
    public void testRemoveSampleFromProject() {
        Project p = TestDataFactory.constructProject();
        Sample s = TestDataFactory.constructSample();

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
        Project p = TestDataFactory.constructProject();
        Sample s = TestDataFactory.constructSample();
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
        List<Link> resourceLinks = samples.getLinks();
        assertEquals(1, resourceLinks.size());
        Link self = resourceLinks.iterator().next();
        assertEquals("self", self.getRel());
        assertEquals("http://localhost/projects/" + projectId + "/samples", self.getHref());
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
    public void testGetIndividualSample() throws IOException {
        Project p = TestDataFactory.constructProject();
        Sample s = TestDataFactory.constructSample();
        SequenceFile sf = TestDataFactory.constructSequenceFile();

        when(projectService.read(p.getIdentifier())).thenReturn(p);
        when(sampleService.getSampleForProject(p, s.getIdentifier())).thenReturn(s);

        ModelMap modelMap = controller.getProjectSample(p.getIdentifier().getIdentifier(),
                s.getIdentifier().getIdentifier());

        verify(sampleService, times(1)).getSampleForProject(p, s.getIdentifier());
        verify(projectService, times(1)).read(p.getIdentifier());

        Object o = modelMap.get(GenericController.RESOURCE_NAME);
        assertTrue(o instanceof SampleResource);
        SampleResource sr = (SampleResource) o;
        Set<String> rels = Sets.newHashSet(PageLink.REL_SELF, SampleSequenceFilesController.REL_SAMPLE_SEQUENCE_FILES,
                ProjectSamplesController.REL_PROJECT);
        List<Link> links = sr.getLinks();
        for (Link link : links) {
            assertTrue(rels.contains(link.getRel()));
            assertNotNull(rels.remove(link.getRel()));
        }
        assertTrue(rels.isEmpty());
    }

    @Test
    public void testUpdateSample() {
        Project p = TestDataFactory.constructProject();
        Sample s = TestDataFactory.constructSample();
        Relationship r = new Relationship(p.getIdentifier(), s.getIdentifier());
        Map<String, Object> updatedFields = ImmutableMap.of("sampleName", (Object) "some new name");
        String projectId = p.getIdentifier().getIdentifier();
        String sampleId = s.getIdentifier().getIdentifier();

        when(projectService.read(p.getIdentifier())).thenReturn(p);
        when(sampleService.update(s.getIdentifier(), updatedFields)).thenReturn(s);

        ModelMap modelMap = controller.updateSample(projectId, sampleId, updatedFields);

        verify(sampleService).getSampleForProject(p, s.getIdentifier());
        verify(sampleService).update(s.getIdentifier(), updatedFields);

        Object o = modelMap.get(GenericController.RESOURCE_NAME);
        assertNotNull(o);
        assertTrue(o instanceof RootResource);
        RootResource resource = (RootResource) o;
        Map<String, String> links = linksToMap(resource.getLinks());
        String self = links.get(PageLink.REL_SELF);
        assertEquals("http://localhost/projects/" + projectId + "/samples/" + sampleId, self);
        String sequenceFiles = links.get(SampleSequenceFilesController.REL_SAMPLE_SEQUENCE_FILES);
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
}
