package ca.corefacility.bioinformatics.irida.web.controller.test.unit.project;

import ca.corefacility.bioinformatics.irida.model.Project;
import ca.corefacility.bioinformatics.irida.model.Relationship;
import ca.corefacility.bioinformatics.irida.model.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.roles.impl.Identifier;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.RelationshipService;
import ca.corefacility.bioinformatics.irida.service.SequenceFileService;
import ca.corefacility.bioinformatics.irida.web.assembler.resource.ResourceCollection;
import ca.corefacility.bioinformatics.irida.web.assembler.resource.RootResource;
import ca.corefacility.bioinformatics.irida.web.assembler.resource.sequencefile.SequenceFileResource;
import ca.corefacility.bioinformatics.irida.web.controller.api.GenericController;
import ca.corefacility.bioinformatics.irida.web.controller.api.SequenceFileController;
import ca.corefacility.bioinformatics.irida.web.controller.api.projects.ProjectSequenceFilesController;
import ca.corefacility.bioinformatics.irida.web.controller.api.projects.ProjectsController;
import ca.corefacility.bioinformatics.irida.web.controller.links.PageLink;
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
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link ProjectSequenceFilesController}
 */
public class ProjectSequenceFilesControllerTest {

    private ProjectSequenceFilesController controller;
    private ProjectService projectService;
    private SequenceFileService sequenceFileService;
    private RelationshipService relationshipService;
    private SequenceFileController sequenceFilesController;

    public ProjectSequenceFilesControllerTest() {
    }

    @Before
    public void setUp() {
        projectService = mock(ProjectService.class);
        sequenceFileService = mock(SequenceFileService.class);
        sequenceFilesController = mock(SequenceFileController.class);
        relationshipService = mock(RelationshipService.class);

        controller = new ProjectSequenceFilesController(projectService, sequenceFileService, relationshipService, sequenceFilesController);
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

        when(sequenceFileService.createSequenceFileWithOwner(any(SequenceFile.class), eq(Project.class), eq(p.getIdentifier()))).thenReturn(r);

        ResponseEntity<String> response = controller.addSequenceFileToProject(p.getIdentifier().getIdentifier(), mmf);

        verify(sequenceFileService, times(1))
                .createSequenceFileWithOwner(any(SequenceFile.class), eq(Project.class), eq(p.getIdentifier()));

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

    @Test
    public void testRemoveSequenceFileFromProject() throws IOException {
        Project p = constructProject();
        SequenceFile sf = constructSequenceFile();

        String projectId = p.getIdentifier().getIdentifier();
        String sequenceFileId = sf.getIdentifier().getIdentifier();

        when(projectService.read(p.getIdentifier())).thenReturn(p);
        when(sequenceFileService.read(sf.getIdentifier())).thenReturn(sf);

        // remove the file
        ModelMap modelMap = controller.removeSequenceFileFromProject(projectId, sequenceFileId);

        verify(projectService, times(1)).read(p.getIdentifier());
        verify(sequenceFileService, times(1)).read(sf.getIdentifier());
        // confirm that we called the appropriate service method
        verify(projectService, times(1)).removeSequenceFileFromProject(p, sf);

        // confirm that the response looks right.
        Object o = modelMap.get(GenericController.RESOURCE_NAME);
        assertTrue(o instanceof RootResource);
        @SuppressWarnings("unchecked")
        RootResource resource = (RootResource) o;
        List<Link> links = resource.getLinks();

        // should be two links in the response, one back to the individual project, the other to the samples collection
        Set<String> rels = Sets.newHashSet(ProjectsController.REL_PROJECT, ProjectSequenceFilesController.REL_PROJECT_SEQUENCE_FILES);
        for (Link link : links) {
            assertTrue(rels.contains(link.getRel()));
            assertNotNull(rels.remove(link.getRel()));
        }
        assertTrue(rels.isEmpty());
    }

    @Test
    public void testGetSequenceFilesForProject() throws IOException {
        Project p = constructProject();
        SequenceFile sf = constructSequenceFile();
        Relationship r = new Relationship();
        r.setSubject(p.getIdentifier());
        r.setObject(sf.getIdentifier());
        r.setIdentifier(new Identifier());
        Collection<Relationship> relationships = Sets.newHashSet(r);

        String projectId = p.getIdentifier().getIdentifier();

        when(relationshipService.getRelationshipsForEntity(p.getIdentifier(), Project.class, SequenceFile.class)).thenReturn(relationships);
        when(sequenceFileService.read(sf.getIdentifier())).thenReturn(sf);

        ModelMap modelMap = controller.getProjectSequenceFiles(projectId);

        verify(relationshipService, times(1)).getRelationshipsForEntity(p.getIdentifier(), Project.class, SequenceFile.class);
        verify(sequenceFileService, times(1)).read(sf.getIdentifier());

        Object o = modelMap.get(GenericController.RESOURCE_NAME);
        assertTrue(o instanceof ResourceCollection);
        @SuppressWarnings("unchecked")
        ResourceCollection<SequenceFileResource> samples = (ResourceCollection<SequenceFileResource>) o;
        assertEquals(1, samples.size());
        SequenceFileResource resource = samples.iterator().next();
        assertEquals(sf.getFile().getFileName().toString(), resource.getFile());
        List<Link> links = resource.getLinks();
        Set<String> rels = Sets.newHashSet(PageLink.REL_SELF, GenericController.REL_RELATIONSHIP);
        for (Link link : links) {
            assertTrue(rels.contains(link.getRel()));
            assertNotNull(rels.remove(link.getRel()));
        }
        assertTrue(rels.isEmpty());
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
}
