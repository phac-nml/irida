package ca.corefacility.bioinformatics.irida.web.controller.api.samples;

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
import ca.corefacility.bioinformatics.irida.web.controller.api.SequenceFileController;
import ca.corefacility.bioinformatics.irida.web.controller.api.projects.ProjectSamplesController;
import ca.corefacility.bioinformatics.irida.web.controller.api.projects.ProjectSequenceFilesController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Collection;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

/**
 * Controller for managing relationships between {@link Sample} and {@link SequenceFile}.
 *
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 */
@Controller
public class SampleSequenceFilesController {
    /**
     * Rel to get back to the sample.
     */
    public static final String REL_SAMPLE = "sample";
    /**
     * Rel to get to the new location of the sequence file.
     */
    public static final String REL_PROJECT_SEQUENCE_FILE = "project/sequenceFile";
    /**
     * Reference to the {@link SequenceFileService}.
     */
    private SequenceFileService sequenceFileService;
    /**
     * Reference to the {@link SampleService}.
     */
    private SampleService sampleService;
    /**
     * Reference to the {@link RelationshipService}.
     */
    private RelationshipService relationshipService;
    /**
     * Reference to the {@link ProjectService}.
     */
    private ProjectService projectService;

    protected SampleSequenceFilesController() {
    }

    @Autowired
    public SampleSequenceFilesController(SequenceFileService sequenceFileService, SampleService sampleService,
                                         RelationshipService relationshipService, ProjectService projectService) {
        this.sequenceFileService = sequenceFileService;
        this.sampleService = sampleService;
        this.relationshipService = relationshipService;
        this.projectService = projectService;
    }

    /**
     * Get the {@link SequenceFile} entities associated with a specific {@link Sample}.
     *
     * @param sampleId the identifier for the {@link Sample}.
     * @return the {@link SequenceFile} entities associated with the {@link Sample}.
     */
    @RequestMapping(value = "/projects/{projectId}/samples/{sampleId}/sequenceFiles", method = RequestMethod.GET)
    public ModelMap getSampleSequenceFiles(@PathVariable String projectId, @PathVariable String sampleId) {
        ModelMap modelMap = new ModelMap();
        Identifier sampleIdentifier = new Identifier(sampleId);

        // Use the RelationshipService to get the set of SequenceFile identifiers associated with a Sample, then
        // retrieve each of the SequenceFiles and prepare for serialization.
        Collection<Relationship> relationships = relationshipService.getRelationshipsForEntity(sampleIdentifier,
                Sample.class, SequenceFile.class);
        ResourceCollection<SequenceFileResource> resources = new ResourceCollection<>(relationships.size());
        for (Relationship r : relationships) {
            SequenceFile sf = sequenceFileService.read(r.getObject());
            String sequenceFileId = sf.getIdentifier().getIdentifier();
            SequenceFileResource sfr = new SequenceFileResource();
            sfr.setResource(sf);
            sfr.add(linkTo(methodOn(SequenceFileController.class).getResource(sequenceFileId))
                    .withSelfRel());
            sfr.add(linkTo(methodOn(SampleSequenceFilesController.class).getSequenceFileForSample(projectId, sampleId, sequenceFileId))
                    .withRel(GenericController.REL_RELATIONSHIP));
            resources.add(sfr);
        }

        // add a link to this collection
        resources.add(linkTo(methodOn(SampleSequenceFilesController.class).getSampleSequenceFiles(projectId, sampleId))
                .withSelfRel());
        // add a link back to the sample
        resources.add(linkTo(methodOn(ProjectSamplesController.class).getProjectSample(projectId, sampleId))
                .withRel(SampleSequenceFilesController.REL_SAMPLE));

        modelMap.addAttribute(GenericController.RESOURCE_NAME, resources);
        return modelMap;
    }

    /**
     * Remove a {@link SequenceFile} from a {@link Sample}. The {@link SequenceFile} will be moved to the
     * {@link ca.corefacility.bioinformatics.irida.model.Project} that is related to this {@link Sample}.
     *
     * @param projectId      the destination {@link ca.corefacility.bioinformatics.irida.model.Project} identifier.
     * @param sampleId       the source {@link Sample} identifier.
     * @param sequenceFileId the identifier of the {@link SequenceFile} to move.
     * @return a status indicating the success of the move.
     */
    @RequestMapping(value = "/projects/{projectId}/samples/{sampleId}/sequenceFiles/{sequenceFileId}", method = RequestMethod.DELETE)
    public ModelMap removeSequenceFileFromSample(@PathVariable String projectId,
                                                 @PathVariable String sampleId,
                                                 @PathVariable String sequenceFileId) {
        ModelMap modelMap = new ModelMap();
        // load the project, sample and sequence file from the database
        Project p = projectService.read(new Identifier(projectId));
        Sample s = sampleService.read(new Identifier(sampleId));
        SequenceFile sf = sequenceFileService.read(new Identifier(sequenceFileId));

        // ask the service to remove the sample from the sequence file and associate it with the project. The service
        // responds with the new relationship between the project and the sequence file.
        Relationship r = sampleService.removeSequenceFileFromSample(p, s, sf);

        // respond with a link to the sample, the new location of the sequence file (as it is associated with the
        // project)
        RootResource resource = new RootResource();
        resource.add(linkTo(methodOn(ProjectSamplesController.class).getProjectSample(projectId, sampleId))
                .withRel(REL_SAMPLE));
        resource.add(linkTo(methodOn(ProjectSequenceFilesController.class)
                .getProjectSequenceFile(projectId, r.getObject().getIdentifier())).withRel(REL_PROJECT_SEQUENCE_FILE));

        modelMap.addAttribute(GenericController.RESOURCE_NAME, resource);

        return modelMap;
    }

    /**
     * Get a specific {@link SequenceFile} associated with a {@link Sample}.
     *
     * @param projectId      the identifier of the {@link ca.corefacility.bioinformatics.irida.model.Project}.
     * @param sampleId       the identifier of the {@link Sample}.
     * @param sequenceFileId the identifier of the {@link SequenceFile}.
     * @return a representation of the {@link SequenceFile}.
     */
    @RequestMapping(value = "/projects/{projectId}/samples/{sampleId}/sequenceFiles/{sequenceFileId}", method = RequestMethod.GET)
    public ModelMap getSequenceFileForSample(@PathVariable String projectId, @PathVariable String sampleId,
                                             @PathVariable String sequenceFileId) {
        ModelMap modelMap = new ModelMap();
        Identifier projectIdentifier = new Identifier(projectId);
        Identifier sampleIdentifier = new Identifier(sampleId);
        Identifier sequenceFileIdentifier = new Identifier(sequenceFileId);
        // test for the existence of a relationship between the project, sample and sequence files
        Relationship projectSampleRel = relationshipService.getRelationship(projectIdentifier, sampleIdentifier);
        Relationship sampleSequenceFileRel = relationshipService.getRelationship(sampleIdentifier, sequenceFileIdentifier);

        // if the relationships exist, load the sequence file from the database and prepare for serialization.
        SequenceFile sf = sequenceFileService.read(sampleSequenceFileRel.getObject());
        SequenceFileResource sfr = new SequenceFileResource();
        sfr.setResource(sf);

        // add links to the resource
        sfr.add(linkTo(methodOn(SequenceFileController.class).getResource(sequenceFileId)).withSelfRel());
        sfr.add(linkTo(methodOn(SampleSequenceFilesController.class).getSampleSequenceFiles(projectId, sampleId))
                .withRel(REL_PROJECT_SEQUENCE_FILE));
        sfr.add(linkTo(methodOn(SampleSequenceFilesController.class).getSequenceFileForSample(projectId, sampleId,
                sequenceFileId)).withRel(GenericController.REL_RELATIONSHIP));

        // add the resource to the response
        modelMap.addAttribute(GenericController.RESOURCE_NAME, sfr);

        return modelMap;
    }
}
