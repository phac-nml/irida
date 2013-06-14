package ca.corefacility.bioinformatics.irida.web.controller.test.unit.samples;

import ca.corefacility.bioinformatics.irida.exceptions.InvalidPropertyException;
import ca.corefacility.bioinformatics.irida.model.Project;
import ca.corefacility.bioinformatics.irida.model.Relationship;
import ca.corefacility.bioinformatics.irida.model.Sample;
import ca.corefacility.bioinformatics.irida.model.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.roles.impl.Identifier;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.RelationshipService;
import ca.corefacility.bioinformatics.irida.service.SampleService;
import ca.corefacility.bioinformatics.irida.service.SequenceFileService;
import ca.corefacility.bioinformatics.irida.web.assembler.resource.ResourceCollection;
import ca.corefacility.bioinformatics.irida.web.assembler.resource.RootResource;
import ca.corefacility.bioinformatics.irida.web.assembler.resource.sequencefile.SequenceFileResource;
import ca.corefacility.bioinformatics.irida.web.controller.api.GenericController;
import ca.corefacility.bioinformatics.irida.web.controller.api.samples.SampleSequenceFilesController;
import ca.corefacility.bioinformatics.irida.web.controller.links.PageLink;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import com.google.common.net.HttpHeaders;
import org.junit.Before;
import org.junit.Test;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.ui.ModelMap;
import org.springframework.util.FileCopyUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link SampleSequenceFilesController}.
 */
public class SampleSequenceFilesControllerTest {
    private SampleSequenceFilesController controller;
    private SequenceFileService sequenceFileService;
    private SampleService sampleService;
    private RelationshipService relationshipService;
    private ProjectService projectService;

    @Before
    public void setUp() {
        sampleService = mock(SampleService.class);
        sequenceFileService = mock(SequenceFileService.class);
        relationshipService = mock(RelationshipService.class);
        projectService = mock(ProjectService.class);

        controller = new SampleSequenceFilesController(sequenceFileService, sampleService, relationshipService, projectService);
    }

    @Test
    public void testGetSampleSequenceFiles() throws IOException {
        Project p = constructProject();
        Sample s = constructSample();
        SequenceFile sf = constructSequenceFile();
        Relationship r = new Relationship();
        r.setIdentifier(new Identifier());
        r.setSubject(s.getIdentifier());
        r.setObject(sf.getIdentifier());
        Collection<Relationship> relationships = Sets.newHashSet(r);

        // mock out the service calls
        when(relationshipService.getRelationshipsForEntity(s.getIdentifier(), Sample.class, SequenceFile.class))
                .thenReturn(relationships);
        when(sequenceFileService.read(sf.getIdentifier())).thenReturn(sf);

        ModelMap modelMap = controller.getSampleSequenceFiles(p.getIdentifier().getIdentifier(), s.getIdentifier().getIdentifier());

        // verify that the service calls were used.
        verify(relationshipService, times(1)).getRelationshipsForEntity(s.getIdentifier(), Sample.class,
                SequenceFile.class);
        verify(sequenceFileService, times(1)).read(sf.getIdentifier());

        Object o = modelMap.get(GenericController.RESOURCE_NAME);
        assertTrue(o instanceof ResourceCollection);
        @SuppressWarnings("unchecked")
        ResourceCollection<SequenceFileResource> resources = (ResourceCollection<SequenceFileResource>) o;
        assertNotNull(resources);
        assertEquals(1, resources.size());
        assertNotNull(resources.getLink(PageLink.REL_SELF));
        assertNotNull(resources.getLink(SampleSequenceFilesController.REL_SAMPLE));
        SequenceFileResource sfr = resources.iterator().next();
        assertNotNull(sfr.getLink(PageLink.REL_SELF));
        assertNotNull(sfr.getLink(GenericController.REL_RELATIONSHIP));
        assertEquals(sf.getFile().toString(), sfr.getFile());
    }

    @Test
    public void testRemoveSequenceFileFromSample() throws IOException {
        Project p = constructProject();
        Sample s = constructSample();
        SequenceFile sf = constructSequenceFile();
        Relationship r = new Relationship();
        r.setSubject(p.getIdentifier());
        r.setObject(sf.getIdentifier());

        when(projectService.read(p.getIdentifier())).thenReturn(p);
        when(sampleService.read(s.getIdentifier())).thenReturn(s);
        when(sequenceFileService.read(sf.getIdentifier())).thenReturn(sf);
        when(sampleService.removeSequenceFileFromSample(p, s, sf)).thenReturn(r);

        ModelMap modelMap = controller.removeSequenceFileFromSample(p.getIdentifier().getIdentifier(),
                s.getIdentifier().getIdentifier(), sf.getIdentifier().getIdentifier());

        verify(projectService, times(1)).read(p.getIdentifier());
        verify(sampleService, times(1)).read(s.getIdentifier());
        verify(sequenceFileService, times(1)).read(sf.getIdentifier());

        verify(sampleService, times(1)).removeSequenceFileFromSample(p, s, sf);

        Object o = modelMap.get(GenericController.RESOURCE_NAME);
        assertNotNull(o);
        assertTrue(o instanceof RootResource);
        RootResource resource = (RootResource) o;
        List<Link> links = resource.getLinks();
        Set<String> rels = Sets.newHashSet(SampleSequenceFilesController.REL_SAMPLE,
                SampleSequenceFilesController.REL_PROJECT_SEQUENCE_FILE);
        for (Link l : links) {
            assertTrue(rels.contains(l.getRel()));
            assertNotNull(rels.remove(l.getRel()));
        }

        assertTrue(rels.isEmpty());
    }

    @Test
    public void testGetSequenceFileForSample() throws IOException {
        Project p = constructProject();
        Sample s = constructSample();
        SequenceFile sf = constructSequenceFile();
        Relationship projectSampleRelationship = new Relationship();
        projectSampleRelationship.setSubject(p.getIdentifier());
        projectSampleRelationship.setObject(s.getIdentifier());
        Relationship sampleSequenceFileRelationship = new Relationship();
        sampleSequenceFileRelationship.setSubject(s.getIdentifier());
        sampleSequenceFileRelationship.setObject(sf.getIdentifier());

        when(relationshipService.getRelationship(p.getIdentifier(), s.getIdentifier())).thenReturn(projectSampleRelationship);
        when(relationshipService.getRelationship(s.getIdentifier(), sf.getIdentifier())).thenReturn(sampleSequenceFileRelationship);
        when(sequenceFileService.read(sf.getIdentifier())).thenReturn(sf);

        ModelMap modelMap = controller.getSequenceFileForSample(p.getIdentifier().getIdentifier(),
                s.getIdentifier().getIdentifier(), sf.getIdentifier().getIdentifier());

        verify(relationshipService, times(1)).getRelationship(p.getIdentifier(), s.getIdentifier());
        verify(relationshipService, times(1)).getRelationship(s.getIdentifier(), sf.getIdentifier());
        verify(sequenceFileService, times(1)).read(sf.getIdentifier());

        Object o = modelMap.get(GenericController.RESOURCE_NAME);
        assertNotNull(o);
        assertTrue(o instanceof SequenceFileResource);
        SequenceFileResource sfr = (SequenceFileResource) o;
        assertEquals(sf.getFile().toString(), sfr.getFile());
        List<Link> links = sfr.getLinks();
        Set<String> rels = Sets.newHashSet(PageLink.REL_SELF, SampleSequenceFilesController.REL_PROJECT_SEQUENCE_FILE,
                GenericController.REL_RELATIONSHIP);
        for (Link l : links) {
            assertTrue(rels.contains(l.getRel()));
            assertNotNull(rels.remove(l.getRel()));
        }

        assertTrue(rels.isEmpty());
    }

    @Test
    public void testAddNewSequenceFileToSample() throws IOException {
        Project p = constructProject();
        Sample s = constructSample();
        SequenceFile sf = constructSequenceFile();
        Relationship sampleSequenceFileRelationship = new Relationship(s.getIdentifier(), sf.getIdentifier());
        String projectId = p.getIdentifier().getIdentifier();
        String sampleId = s.getIdentifier().getIdentifier();
        String sequenceFileId = sf.getIdentifier().getIdentifier();

        File f = Files.createTempFile(UUID.randomUUID().toString(), null).toFile();
        f.deleteOnExit();
        MockMultipartFile mmf = new MockMultipartFile("filename", "filename", "blurgh", FileCopyUtils.copyToByteArray(f));

        when(sampleService.read(s.getIdentifier())).thenReturn(s);
        when(sequenceFileService.createSequenceFileWithOwner(any(SequenceFile.class), eq(Sample.class),
                eq(s.getIdentifier()))).thenReturn(sampleSequenceFileRelationship);
        when(projectService.read(p.getIdentifier())).thenReturn(p);

        ResponseEntity<String> response = controller.addNewSequenceFileToSample(p.getIdentifier().getIdentifier(),
                s.getIdentifier().getIdentifier(), mmf);

        verify(sampleService).getSampleForProject(p, s.getIdentifier());
        verify(projectService).read(p.getIdentifier());
        verify(sampleService, times(1)).read(s.getIdentifier());
        verify(sequenceFileService).createSequenceFileWithOwner(any(SequenceFile.class), eq(Sample.class),
                eq(s.getIdentifier()));

        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        List<String> locations = response.getHeaders().get(HttpHeaders.LOCATION);
        assertNotNull(locations);
        assertFalse(locations.isEmpty());
        assertEquals(1, locations.size());
        assertEquals("http://localhost/sequenceFiles/" + sequenceFileId, locations.iterator().next());

        List<String> links = response.getHeaders().get(HttpHeaders.LINK);
        assertNotNull(links);
        assertFalse(links.isEmpty());
        assertEquals(1, locations.size());
        assertEquals("<http://localhost/projects/" + projectId + "/samples/" + sampleId +
                "/sequenceFiles/" + sequenceFileId + ">; rel=relationship", links.iterator().next());
    }

    @Test
    public void testAddExistingSequenceFileToSample() throws IOException {
        Project p = constructProject();
        Sample s = constructSample();
        SequenceFile sf = constructSequenceFile();
        Relationship sampleSequenceFileRelationship = new Relationship(s.getIdentifier(), sf.getIdentifier());
        String projectId = p.getIdentifier().getIdentifier();
        String sampleId = s.getIdentifier().getIdentifier();
        String sequenceFileId = sf.getIdentifier().getIdentifier();

        when(projectService.read(p.getIdentifier())).thenReturn(p);
        when(sampleService.getSampleForProject(p, s.getIdentifier())).thenReturn(s);
        when(sampleService.read(s.getIdentifier())).thenReturn(s);
        when(sequenceFileService.read(sf.getIdentifier())).thenReturn(sf);
        when(sampleService.addSequenceFileToSample(s, sf)).thenReturn(sampleSequenceFileRelationship);

        Map<String, String> requestBody = ImmutableMap.of(SampleSequenceFilesController.SEQUENCE_FILE_ID_KEY, sequenceFileId);

        ResponseEntity<String> response = controller.addExistingSequenceFileToSample(projectId, sampleId, requestBody);

        verify(projectService).read(p.getIdentifier());
        verify(sampleService).getSampleForProject(p, s.getIdentifier());
        verify(sampleService).read(s.getIdentifier());
        verify(sequenceFileService).read(sf.getIdentifier());
        verify(sampleService).addSequenceFileToSample(s, sf);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        List<String> locations = response.getHeaders().get(HttpHeaders.LOCATION);
        assertNotNull(locations);
        assertFalse(locations.isEmpty());
        assertEquals(1, locations.size());
        // the sequence file location is still the same, but we've added a new relationship
        assertEquals("http://localhost/sequenceFiles/" + sequenceFileId, locations.iterator().next());

        List<String> links = response.getHeaders().get(HttpHeaders.LINK);
        assertNotNull(links);
        assertFalse(links.isEmpty());
        assertEquals(1, links.size());
        assertEquals("<http://localhost/projects/" + projectId + "/samples/" + sampleId +
                "/sequenceFiles/" + sequenceFileId + ">; rel=relationship", links.iterator().next());
    }

    @Test
    public void testAddExistingSequenceFileToSampleBadRequest() {
        Map<String, String> requestBody = new HashMap<>();
        try {
            controller.addExistingSequenceFileToSample(UUID.randomUUID().toString(), UUID.randomUUID().toString(), requestBody);
            fail();
        } catch (InvalidPropertyException e) {

        } catch (Exception e) {
            fail();
        }
    }

    /**
     * Construct a simple {@link ca.corefacility.bioinformatics.irida.model.Sample}.
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
     * Construct a simple {@link SequenceFile}.
     *
     * @return a {@link SequenceFile} with identifier.
     */
    private SequenceFile constructSequenceFile() throws IOException {
        String sequenceFileId = UUID.randomUUID().toString();
        Identifier sequenceFileIdentifier = new Identifier();
        Path f = Files.createTempFile(null, null);
        sequenceFileIdentifier.setIdentifier(sequenceFileId);
        SequenceFile sf = new SequenceFile();
        sf.setIdentifier(sequenceFileIdentifier);
        sf.setFile(f);
        return sf;
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
