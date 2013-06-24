package ca.corefacility.bioinformatics.irida.web.controller.api.projects;

import ca.corefacility.bioinformatics.irida.model.Project;
import ca.corefacility.bioinformatics.irida.model.Relationship;
import ca.corefacility.bioinformatics.irida.model.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.roles.impl.Identifier;
import ca.corefacility.bioinformatics.irida.service.CRUDService;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.RelationshipService;
import ca.corefacility.bioinformatics.irida.service.SequenceFileService;
import ca.corefacility.bioinformatics.irida.web.assembler.resource.ResourceCollection;
import ca.corefacility.bioinformatics.irida.web.assembler.resource.RootResource;
import ca.corefacility.bioinformatics.irida.web.assembler.resource.sequencefile.SequenceFileResource;
import ca.corefacility.bioinformatics.irida.web.controller.api.GenericController;
import com.google.common.net.HttpHeaders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

/**
 * Controller for managing relationships between {@link Project} and {@link SequenceFile}.
 *
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 */
@Controller
public class ProjectSequenceFilesController {
    /**
     * rel used for accessing the list of sequence files associated with a project.
     */
    public static final String REL_PROJECT_SEQUENCE_FILES = "project/sequenceFiles";
    /**
     * rel used for accessing a specific sequence file associated with a project.
     */
    public static final String REL_PROJECT_SEQUENCE_FILE = "project/sequenceFile";
    /**
     * rel used for downloading a sequence file in fasta format.
     */
    public static final String REL_PROJECT_SEQUENCE_FILE_FASTA = "project/sequenceFile/fasta";
    /**
     * logger.
     */
    private static final Logger logger = LoggerFactory.getLogger(ProjectSequenceFilesController.class);
    /**
     * Reference to {@link ProjectService}.
     */
    private ProjectService projectService;
    /**
     * Reference to {@link CRUDService} for managing {@link SequenceFile}.
     */
    private SequenceFileService sequenceFileService;
    /**
     * Reference to {@link RelationshipService} for managing {@link Relationship}.
     */
    private RelationshipService relationshipService;


    protected ProjectSequenceFilesController() {
    }

    @Autowired
    public ProjectSequenceFilesController(ProjectService projectService, SequenceFileService sequenceFileService,
                                          RelationshipService relationshipService) {
        this.projectService = projectService;
        this.sequenceFileService = sequenceFileService;
        this.relationshipService = relationshipService;
    }

    /**
     * Create a new {@link SequenceFile} resource and add a {@link Relationship} between the {@link SequenceFile} and
     * the {@link Project}.
     *
     * @param projectId the {@link Identifier} of the {@link Project} to add the {@link SequenceFile} to.
     * @param file      the file to add to the {@link Project}.
     * @return a response entity indicating the success of the addition.
     * @throws java.io.IOException if the {@link SequenceFile} cannot be saved.
     */
    @RequestMapping(value = "/projects/{projectId}/sequenceFiles", consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            method = RequestMethod.POST)
    public ResponseEntity<String> addSequenceFileToProject(@PathVariable String projectId,
                                                           @RequestParam("file") MultipartFile file) throws IOException {
        Identifier id = new Identifier();
        id.setIdentifier(projectId);

        logger.trace("Adding sequence file to project [" + projectId + "]");
        // create a temporary file to send back to the service
        Path temp = Files.createTempDirectory(null);
        Path target = temp.resolve(file.getOriginalFilename());

        logger.trace("Writing MultipartFile to temporary file.");
        target = Files.write(target, file.getBytes());

        // construct the sequence file that we're going to create
        SequenceFile sf = new SequenceFile(target);

        logger.trace("Diving into service to create file.");
        // add the sequence file to the database and create the relationship between the resources
        Relationship r = sequenceFileService.createSequenceFileWithOwner(sf, Project.class, id);

        logger.trace("Sequence file created with id [" + r.getObject().getIdentifier() + "]");
        // erase the temp files.
        logger.trace("Cleaning up temporary files.");
        Files.deleteIfExists(target);
        Files.deleteIfExists(temp);
        logger.trace("Temporary files removed.");

        logger.trace("Constructing location header.");
        String sequenceFileId = r.getObject().getIdentifier();
        String location = linkTo(methodOn(ProjectSequenceFilesController.class).getProjectSequenceFile(projectId, sequenceFileId)).withSelfRel().getHref();

        // construct a set of headers to add to the response
        MultiValueMap<String, String> responseHeaders = new LinkedMultiValueMap<>();
        responseHeaders.add(HttpHeaders.LOCATION, location);

        logger.trace("Responding to client.");
        return new ResponseEntity<>("success", responseHeaders, HttpStatus.CREATED);
    }

    /**
     * Remove a {@link SequenceFile} from the collection of {@link SequenceFile}s associated with a {@link Project}.
     *
     * @param projectId      the {@link Project} {@link Identifier}.
     * @param sequenceFileId the {@link SequenceFile} {@link Identifier}.
     * @return a set of links back to the {@link SequenceFile} collection and the individual {@link Project}.
     */
    @RequestMapping(value = "/projects/{projectId}/sequenceFiles/{sequenceFileId}", method = RequestMethod.DELETE)
    public ModelMap removeSequenceFileFromProject(@PathVariable String projectId, @PathVariable String sequenceFileId) {
        ModelMap modelMap = new ModelMap();

        Identifier projectIdentifier = new Identifier();
        projectIdentifier.setIdentifier(projectId);

        Identifier sequenceFileIdentifier = new Identifier();
        sequenceFileIdentifier.setIdentifier(sequenceFileId);

        // load the appropriate data
        Project p = projectService.read(projectIdentifier);
        SequenceFile sf = sequenceFileService.read(sequenceFileIdentifier);

        // remove the sequence file from the project
        projectService.removeSequenceFileFromProject(p, sf);

        // construct a response
        // respond to the client.
        RootResource resource = new RootResource();
        // add links back to the collection of samples and to the project itself.
        resource.add(linkTo(methodOn(ProjectSequenceFilesController.class).getProjectSequenceFiles(projectId)).withRel(REL_PROJECT_SEQUENCE_FILES));
        resource.add(linkTo(ProjectsController.class).slash(projectId).withRel(ProjectsController.REL_PROJECT));

        // add the links to the response.
        modelMap.addAttribute(GenericController.RESOURCE_NAME, resource);

        return modelMap;
    }

    /**
     * Get the list of {@link SequenceFile}s associated with a {@link Project}.
     *
     * @param projectId the {@link Identifier} for the {@link Project}.
     * @return a representation of all {@link SequenceFile}s associated with the {@link Project}.
     */
    @RequestMapping(value = "/projects/{projectId}/sequenceFiles", method = RequestMethod.GET)
    public ModelMap getProjectSequenceFiles(@PathVariable String projectId) {
        ModelMap modelMap = new ModelMap();

        Identifier id = new Identifier();
        id.setIdentifier(projectId);

        Collection<Relationship> relationships = relationshipService.
                getRelationshipsForEntity(id, Project.class, SequenceFile.class);
        ResourceCollection<SequenceFileResource> sampleResources = new ResourceCollection<>(relationships.size());

        for (Relationship r : relationships) {
            SequenceFile sequenceFile = sequenceFileService.read(r.getObject());
            String sequenceFileId = sequenceFile.getIdentifier().getIdentifier();
            SequenceFileResource sr = new SequenceFileResource();
            sr.setResource(sequenceFile);
            sr.add(linkTo(methodOn(ProjectSequenceFilesController.class).
                    getProjectSequenceFile(projectId, sequenceFileId)).withSelfRel());
            Link fastaLink = linkTo(methodOn(ProjectSequenceFilesController.class).getProjectSequenceFile(projectId,
                    sequenceFileId)).withRel(ProjectSequenceFilesController.REL_PROJECT_SEQUENCE_FILE_FASTA);
            // we need to add the fasta suffix manually to the end, so that web-based clients can find the file.
            sr.add(new Link(fastaLink.getHref() + ".fasta", ProjectSequenceFilesController.REL_PROJECT_SEQUENCE_FILE_FASTA));
            sampleResources.add(sr);
        }

        modelMap.addAttribute(GenericController.RESOURCE_NAME, sampleResources);

        return modelMap;
    }

    /**
     * Get the representation of a {@link SequenceFile} that's associated with a {@link Project}.
     *
     * @param projectId      the {@link Project} that the {@link SequenceFile} belongs to.
     * @param sequenceFileId the {@link Identifier} of the {@link SequenceFile}.
     * @return a representation of the {@link SequenceFile}.
     */
    @RequestMapping(value = "/projects/{projectId}/sequenceFiles/{sequenceFileId}", method = RequestMethod.GET)
    public ModelMap getProjectSequenceFile(@PathVariable String projectId, @PathVariable String sequenceFileId) {
        SequenceFile sf = getSequenceFileForProject(projectId, sequenceFileId);

        ModelMap modelMap = new ModelMap();

        // prepare response for the client
        SequenceFileResource sfr = new SequenceFileResource();
        sfr.setResource(sf);

        // construct self link, project link, relationship link.
        sfr.add(linkTo(methodOn(ProjectSequenceFilesController.class)
                .getProjectSequenceFile(projectId, sequenceFileId)).withSelfRel());
        sfr.add(linkTo(ProjectsController.class).slash(projectId).withRel(ProjectsController.REL_PROJECT));
        Link fastaLink = linkTo(methodOn(ProjectSequenceFilesController.class).getProjectSequenceFile(projectId,
                sequenceFileId)).withRel(ProjectSequenceFilesController.REL_PROJECT_SEQUENCE_FILE_FASTA);
        // we need to add the fasta suffix manually to the end, so that web-based clients can find the file.
        sfr.add(new Link(fastaLink.getHref() + ".fasta", ProjectSequenceFilesController.REL_PROJECT_SEQUENCE_FILE_FASTA));

        modelMap.addAttribute(GenericController.RESOURCE_NAME, sfr);

        return modelMap;
    }

    /**
     * Get the specified {@link SequenceFile} from the supplied {@link Project}.
     *
     * @param projectId      the {@link Identifier} of the {@link Project}.
     * @param sequenceFileId the {@link Identifier} of the {@link SequenceFile}.
     * @return the {@link SequenceFile}.
     */
    private SequenceFile getSequenceFileForProject(String projectId, String sequenceFileId) {
        // get the project
        Project p = projectService.read(new Identifier(projectId));

        // get the sequence file
        SequenceFile sf = sequenceFileService.getSequenceFileFromProject(p, new Identifier(sequenceFileId));

        return sf;
    }
}
