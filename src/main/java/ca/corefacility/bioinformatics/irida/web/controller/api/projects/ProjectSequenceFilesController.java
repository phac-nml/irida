package ca.corefacility.bioinformatics.irida.web.controller.api.projects;

import ca.corefacility.bioinformatics.irida.model.Project;
import ca.corefacility.bioinformatics.irida.model.Relationship;
import ca.corefacility.bioinformatics.irida.model.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.roles.impl.Identifier;
import ca.corefacility.bioinformatics.irida.service.CRUDService;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.RelationshipService;
import ca.corefacility.bioinformatics.irida.web.assembler.resource.ResourceCollection;
import ca.corefacility.bioinformatics.irida.web.assembler.resource.RootResource;
import ca.corefacility.bioinformatics.irida.web.assembler.resource.sequencefile.SequenceFileResource;
import ca.corefacility.bioinformatics.irida.web.controller.api.GenericController;
import ca.corefacility.bioinformatics.irida.web.controller.api.RelationshipsController;
import ca.corefacility.bioinformatics.irida.web.controller.api.SequenceFileController;
import com.google.common.net.HttpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
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

import java.io.File;
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
    public static final String PROJECT_SEQUENCE_FILES_REL = "project/sequenceFiles";
    /**
     * Reference to {@link ProjectService}.
     */
    private ProjectService projectService;
    /**
     * Reference to {@link CRUDService} for managing {@link SequenceFile}.
     */
    private CRUDService<Identifier, SequenceFile> sequenceFileService;
    /**
     * Reference to {@link RelationshipService} for managing {@link Relationship}.
     */
    private RelationshipService relationshipService;
    /**
     * Reference to {@link SequenceFileController}.
     */
    private SequenceFileController sequenceFileController;

    protected ProjectSequenceFilesController() {
    }

    @Autowired
    public ProjectSequenceFilesController(ProjectService projectService,
                                          CRUDService<Identifier, SequenceFile> sequenceFileService,
                                          RelationshipService relationshipService, SequenceFileController sequenceFileController) {
        this.projectService = projectService;
        this.sequenceFileService = sequenceFileService;
        this.relationshipService = relationshipService;
        this.sequenceFileController = sequenceFileController;
    }

    /**
     * Create a new {@link ca.corefacility.bioinformatics.irida.model.SequenceFile} resource and add a relationship between the {@link ca.corefacility.bioinformatics.irida.model.SequenceFile} and the
     * {@link ca.corefacility.bioinformatics.irida.model.Project}.
     *
     * @param projectId the identifier of the project to add the sequence file to.
     * @param file      the file to add to the project.
     * @return a response entity indicating the success of the addition.
     * @throws java.io.IOException if the sample file cannot be saved.
     */
    @RequestMapping(value = "/projects/{projectId}/sequenceFiles", consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            method = RequestMethod.POST)
    public ResponseEntity<String> addSequenceFileToProject(@PathVariable String projectId,
                                                           @RequestParam("file") MultipartFile file) throws IOException {
        Identifier id = new Identifier();
        id.setIdentifier(projectId);

        // load the project that we're adding to
        Project p = projectService.read(id);

        // create a temporary file to send back to the service
        Path temp = Files.createTempDirectory(null);
        Path target = temp.resolve(file.getOriginalFilename());

        target = Files.write(target, file.getBytes());
        File f = target.toFile();

        // construct the sequence file that we're going to create
        SequenceFile sf = new SequenceFile(f);

        // add the sequence file to the database and create the relationship between the resources
        Relationship r = projectService.addSequenceFileToProject(p, sf);

        // erase the temp files.
        f.delete();
        temp.toFile().delete();

        String sequenceFileId = r.getObject().getIdentifier();
        String location = linkTo(SequenceFileController.class).slash(sequenceFileId).withSelfRel().getHref();
        String relationshipLocation = linkTo(methodOn(ProjectSequenceFilesController.class).getProjectSequenceFile(projectId,
                sequenceFileId)).withSelfRel().getHref();
        relationshipLocation = "<" + relationshipLocation + ">; rel=relationship";

        // construct a set of headers to add to the response
        MultiValueMap<String, String> responseHeaders = new LinkedMultiValueMap<>();
        responseHeaders.add(HttpHeaders.LOCATION, location);
        responseHeaders.add(HttpHeaders.LINK, relationshipLocation);

        return new ResponseEntity<>("success", responseHeaders, HttpStatus.CREATED);
    }

    /**
     * Remove a {@link SequenceFile} from the collection of {@link SequenceFile}s associated with a {@link Project}.
     *
     * @param projectId      the {@link Project} identifier.
     * @param sequenceFileId the {@link SequenceFile} identifier.
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
        resource.add(linkTo(methodOn(ProjectSequenceFilesController.class).getProjectSequenceFiles(projectId)).withRel(PROJECT_SEQUENCE_FILES_REL));
        resource.add(linkTo(ProjectsController.class).slash(projectId).withRel(ProjectsController.PROJECT_REL));

        // add the links to the response.
        modelMap.addAttribute(GenericController.RESOURCE_NAME, resource);

        return modelMap;
    }

    /**
     * Get the list of {@link SequenceFile}s associated with a {@link Project}.
     *
     * @param projectId the identifier for the {@link Project}.
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
            SequenceFileResource sr = new SequenceFileResource();
            sr.setResource(sequenceFile);
            sr.add(linkTo(methodOn(SequenceFileController.class).
                    getResource(sequenceFile.getIdentifier().getIdentifier())).withSelfRel());
            sr.add(linkTo(methodOn(RelationshipsController.class).
                    getResource(r.getIdentifier().getIdentifier())).withRel(GenericController.REL_RELATIONSHIP));
            sampleResources.add(sr);
        }

        modelMap.addAttribute(GenericController.RESOURCE_NAME, sampleResources);

        return modelMap;
    }

    /**
     * Get the representation of a sequence file that's associated with a project.
     *
     * @param projectId      the project that the sequence file belongs to.
     * @param sequenceFileId the identifier of the sequence file.
     * @return a representation of the sequence file.
     */
    @RequestMapping(value = "/projects/{projectId}/sequenceFiles/{sequenceFileId}", method = RequestMethod.GET)
    public ModelMap getProjectSequenceFile(@PathVariable String projectId, @PathVariable String sequenceFileId) {
        return sequenceFileController.getResource(sequenceFileId);
    }
}
