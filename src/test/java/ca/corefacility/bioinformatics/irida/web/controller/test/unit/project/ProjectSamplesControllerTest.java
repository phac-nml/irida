package ca.corefacility.bioinformatics.irida.web.controller.test.unit.project;

import ca.corefacility.bioinformatics.irida.model.IridaThing;
import ca.corefacility.bioinformatics.irida.model.Project;
import ca.corefacility.bioinformatics.irida.model.Sample;
import ca.corefacility.bioinformatics.irida.model.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectSampleJoin;
import ca.corefacility.bioinformatics.irida.model.joins.impl.SampleSequenceFileJoin;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.SampleService;
import ca.corefacility.bioinformatics.irida.service.SequenceFileService;
import ca.corefacility.bioinformatics.irida.web.assembler.resource.LabelledRelationshipResource;
import ca.corefacility.bioinformatics.irida.web.assembler.resource.ResourceCollection;
import ca.corefacility.bioinformatics.irida.web.assembler.resource.RootResource;
import ca.corefacility.bioinformatics.irida.web.assembler.resource.sample.SampleResource;
import ca.corefacility.bioinformatics.irida.web.controller.api.GenericController;
import ca.corefacility.bioinformatics.irida.web.controller.api.projects.ProjectSamplesController;
import ca.corefacility.bioinformatics.irida.web.controller.api.projects.ProjectsController;
import ca.corefacility.bioinformatics.irida.web.controller.api.samples.SampleSequenceFilesController;
import ca.corefacility.bioinformatics.irida.web.controller.test.unit.TestDataFactory;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.net.HttpHeaders;

import org.junit.Before;
import org.junit.Test;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.ModelMap;

import java.io.IOException;
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
    private SequenceFileService sequenceFileService;

    @Before
    public void setUp() {
        projectService = mock(ProjectService.class);
        sampleService = mock(SampleService.class);
        sequenceFileService = mock(SequenceFileService.class);
        controller = new ProjectSamplesController(projectService, sampleService, sequenceFileService);
    }

    @Test
    public void testAddSampleToProject() {
        Sample s = TestDataFactory.constructSample();
        Project p = TestDataFactory.constructProject();
        Long projectId = p.getId();

        SampleResource sr = new SampleResource();
        sr.setResource(s);
        Join<Project, Sample> r = new ProjectSampleJoin(p, s);

        when(projectService.read(p.getId())).thenReturn(p);
        when(projectService.addSampleToProject(p, s)).thenReturn(r);

        ResponseEntity<String> response = controller.addSampleToProject(p.getId(), sr);

        verify(projectService, times(1)).read(p.getId());
        verify(projectService, times(1)).addSampleToProject(p, s);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        // the location header should correspond to the created sample URL under the samples controller.
        List<String> locations = response.getHeaders().get(HttpHeaders.LOCATION);
        assertNotNull(locations);
        assertFalse(locations.isEmpty());
        assertEquals(1, locations.size());
        assertEquals("http://localhost/projects/" + projectId + "/samples/" + s.getId(), locations.iterator().next());
    }

    @Test
    public void testRemoveSampleFromProject() {
        Project p = TestDataFactory.constructProject();
        Sample s = TestDataFactory.constructSample();

        when(projectService.read(p.getId())).thenReturn(p);
        when(sampleService.read(s.getId())).thenReturn(s);

        ModelMap modelMap = controller.removeSampleFromProject(p.getId(), s.getId());

        // verify that we actually tried to remove the sample from the project.
        verify(projectService, times(1)).removeSampleFromProject(p, s);
        verify(projectService, times(1)).read(p.getId());
        verify(sampleService, times(1)).read(s.getId());

        // confirm that the response looks right.
        Object o = modelMap.get(GenericController.RESOURCE_NAME);
        assertTrue(o instanceof RootResource);
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
        Join<Project, Sample> r = new ProjectSampleJoin(p, s);
        
        @SuppressWarnings("unchecked")
		List<Join<Project, Sample>> relationships = Lists.newArrayList(r);

        when(sampleService.getSamplesForProject(p)).thenReturn(relationships);
        when(projectService.read(p.getId())).thenReturn(p);

        ModelMap modelMap = controller.getProjectSamples(p.getId());

        verify(sampleService, times(1)).getSamplesForProject(p);
        verify(projectService, times(1)).read(p.getId());

        Object o = modelMap.get(GenericController.RESOURCE_NAME);
        assertTrue(o instanceof ResourceCollection);
        @SuppressWarnings("unchecked")
        ResourceCollection<SampleResource> samples = (ResourceCollection<SampleResource>) o;
        assertEquals(1, samples.size());
        List<Link> resourceLinks = samples.getLinks();
        assertEquals(1, resourceLinks.size());
        Link self = resourceLinks.iterator().next();
        assertEquals("self", self.getRel());
        assertEquals("http://localhost/projects/" + p.getId() + "/samples", self.getHref());
        SampleResource resource = samples.iterator().next();
        assertEquals(s.getSampleName(), resource.getSampleName());
        List<Link> links = resource.getLinks();
        Set<String> rels = Sets.newHashSet(Link.REL_SELF);
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
        Join<Sample, SequenceFile> r = new SampleSequenceFileJoin(s, sf);
        @SuppressWarnings("unchecked")
		List<Join<Sample, SequenceFile>> relationships = Lists.newArrayList(r);

        when(projectService.read(p.getId())).thenReturn(p);
        when(sampleService.getSampleForProject(p, s.getId())).thenReturn(s);
        // mock out the service calls
        when(sequenceFileService.getSequenceFilesForSample(s)).thenReturn(relationships);

        ModelMap modelMap = controller.getProjectSample(p.getId(), s.getId());

        verify(sampleService).getSampleForProject(p, s.getId());
        verify(projectService).read(p.getId());
        verify(sequenceFileService).getSequenceFilesForSample(s);

        Object o = modelMap.get(GenericController.RESOURCE_NAME);
        assertTrue(o instanceof SampleResource);
        SampleResource sr = (SampleResource) o;

        Link selfLink = sr.getLink(Link.REL_SELF);
        Link sequenceFilesLink = sr.getLink(SampleSequenceFilesController.REL_SAMPLE_SEQUENCE_FILES);
        Link projectLink = sr.getLink(ProjectSamplesController.REL_PROJECT);

        String projectLocation = "http://localhost/projects/" + p.getId();
        String sampleLocation = projectLocation + "/samples/" + s.getId();

        assertNotNull(selfLink);
        assertEquals(sampleLocation, selfLink.getHref());

        assertNotNull(sequenceFilesLink);
        assertEquals(sampleLocation + "/sequenceFiles", sequenceFilesLink.getHref());

        assertNotNull(projectLink);
        assertEquals(projectLocation, projectLink.getHref());

        // confirm that the sequence files were added as related resources
        o = modelMap.get(GenericController.RELATED_RESOURCES_NAME);
        assertTrue(o instanceof Map);
        @SuppressWarnings("unchecked")
        Map<String, ResourceCollection<LabelledRelationshipResource<IridaThing, IridaThing>>> related =
                (Map<String, ResourceCollection<LabelledRelationshipResource<IridaThing, IridaThing>>>) o;
        assertTrue(related.containsKey("sequenceFiles"));
        ResourceCollection<LabelledRelationshipResource<IridaThing, IridaThing>> sequenceFiles = related.get("sequenceFiles");
        assertEquals(1, sequenceFiles.size());
        LabelledRelationshipResource<IridaThing, IridaThing> labelledRelationship = sequenceFiles.iterator().next();
        Link sequenceFileLink = labelledRelationship.getLink(Link.REL_SELF);
        assertEquals(sampleLocation + "/sequenceFiles/" + sf.getId(), sequenceFileLink.getHref());
    }

    @Test
    public void testUpdateSample() {
        Project p = TestDataFactory.constructProject();
        Sample s = TestDataFactory.constructSample();
        Map<String, Object> updatedFields = ImmutableMap.of("sampleName", (Object) "some new name");

        when(projectService.read(p.getId())).thenReturn(p);
        when(sampleService.update(s.getId(), updatedFields)).thenReturn(s);

        ModelMap modelMap = controller.updateSample(p.getId(), s.getId(), updatedFields);

        verify(sampleService).getSampleForProject(p, s.getId());
        verify(sampleService).update(s.getId(), updatedFields);

        Object o = modelMap.get(GenericController.RESOURCE_NAME);
        assertNotNull(o);
        assertTrue(o instanceof RootResource);
        RootResource resource = (RootResource) o;
        Map<String, String> links = linksToMap(resource.getLinks());
        String self = links.get(Link.REL_SELF);
        assertEquals("http://localhost/projects/" + p.getId() + "/samples/" + s.getId(), self);
        String sequenceFiles = links.get(SampleSequenceFilesController.REL_SAMPLE_SEQUENCE_FILES);
        assertEquals("http://localhost/projects/" + p.getId() + "/samples/" + s.getId() + "/sequenceFiles", sequenceFiles);
        String project = links.get(ProjectsController.REL_PROJECT);
        assertEquals("http://localhost/projects/" + p.getId(), project);
    }

    private Map<String, String> linksToMap(List<Link> links) {
        Map<String, String> linksMap = new HashMap<>();

        for (Link l : links) {
            linksMap.put(l.getRel(), l.getHref());
        }

        return linksMap;
    }
}
