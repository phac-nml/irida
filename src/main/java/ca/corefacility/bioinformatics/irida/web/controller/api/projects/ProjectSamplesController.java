package ca.corefacility.bioinformatics.irida.web.controller.api.projects;

import ca.corefacility.bioinformatics.irida.model.Project;
import ca.corefacility.bioinformatics.irida.model.Relationship;
import ca.corefacility.bioinformatics.irida.model.Sample;
import ca.corefacility.bioinformatics.irida.model.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.roles.impl.Identifier;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.RelationshipService;
import ca.corefacility.bioinformatics.irida.service.SampleService;
import ca.corefacility.bioinformatics.irida.web.assembler.resource.LabelledRelationshipResource;
import ca.corefacility.bioinformatics.irida.web.assembler.resource.ResourceCollection;
import ca.corefacility.bioinformatics.irida.web.assembler.resource.RootResource;
import ca.corefacility.bioinformatics.irida.web.assembler.resource.sample.SampleResource;
import ca.corefacility.bioinformatics.irida.web.controller.api.GenericController;
import ca.corefacility.bioinformatics.irida.web.controller.api.samples.SampleSequenceFilesController;
import com.google.common.net.HttpHeaders;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

/**
 * Controller for managing relationships between {@link Project} and {@link Sample}.
 *
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 */
@Controller
public class ProjectSamplesController {
    /**
     * Rel to get to the project that this sample belongs to.
     */
    public static final String REL_PROJECT = "sample/project";
    /**
     * rel used for accessing the list of samples associated with a project.
     */
    public static final String REL_PROJECT_SAMPLES = "project/samples";
    /**
     * Reference to {@link ProjectService}.
     */
    private ProjectService projectService;
    /**
     * Reference to {@link SampleService}.
     */
    private SampleService sampleService;
    /**
     * Reference to {@link RelationshipService}.
     */
    private RelationshipService relationshipService;

    protected ProjectSamplesController() {
    }

    @Autowired
    public ProjectSamplesController(ProjectService projectService, SampleService sampleService,
                                    RelationshipService relationshipService) {
        this.projectService = projectService;
        this.sampleService = sampleService;
        this.relationshipService = relationshipService;
    }

    /**
     * Create a new sample resource and create a relationship between the sample and the project.
     *
     * @param projectId the identifier of the project that you want to add the sample to.
     * @param sample    the sample that you want to create.
     * @return a response indicating that the sample was created and appropriate location information.
     */
    @RequestMapping(value = "/projects/{projectId}/samples", method = RequestMethod.POST)
    public ResponseEntity<String> addSampleToProject(@PathVariable String projectId, @RequestBody SampleResource sample) {
        Identifier id = new Identifier();
        id.setIdentifier(projectId);

        // load the project that we're adding to
        Project p = projectService.read(id);

        // construct the sample that we're going to create
        Sample s = sample.getResource();

        // add the sample to the project
        Relationship r = projectService.addSampleToProject(p, s);

        // construct a link to the sample itself on the samples controller
        String sampleId = r.getObject().getIdentifier();
        String location = linkTo(methodOn(ProjectSamplesController.class)
                .getProjectSample(projectId, sampleId)).withSelfRel().getHref();

        // construct a set of headers to add to the response
        MultiValueMap<String, String> responseHeaders = new LinkedMultiValueMap<>();
        responseHeaders.add(HttpHeaders.LOCATION, location);

        // respond to the request
        return new ResponseEntity<>("success", responseHeaders, HttpStatus.CREATED);
    }

    /**
     * Get the list of {@link Sample} associated with this {@link Project}.
     *
     * @param projectId the identifier of the {@link Project} to get the {@link Sample}s for.
     * @return the list of {@link Sample}s associated with this {@link Project}.
     */
    @RequestMapping(value = "/projects/{projectId}/samples", method = RequestMethod.GET)
    public ModelMap getProjectSamples(@PathVariable String projectId) {
        ModelMap modelMap = new ModelMap();

        Identifier id = new Identifier();
        id.setIdentifier(projectId);

        Collection<Relationship> relationships = relationshipService.
                getRelationshipsForEntity(id, Project.class, Sample.class);
        ResourceCollection<SampleResource> sampleResources = new ResourceCollection<>(relationships.size());

        for (Relationship r : relationships) {
            Sample sample = sampleService.read(r.getObject());
            SampleResource sr = new SampleResource();
            sr.setResource(sample);
            sr.add(linkTo(methodOn(ProjectSamplesController.class).
                    getProjectSample(projectId, sample.getIdentifier().getIdentifier())).withSelfRel());
            sampleResources.add(sr);
        }

        sampleResources.add(linkTo(methodOn(ProjectSamplesController.class).getProjectSamples(projectId)).withSelfRel());
        sampleResources.setTotalResources(relationships.size());

        modelMap.addAttribute(GenericController.RESOURCE_NAME, sampleResources);

        return modelMap;
    }

    /**
     * Get the representation of a specific sample that's associated with the project.
     *
     * @param projectId the {@link Project} identifier that the {@link Sample} should be associated with.
     * @param sampleId  the {@link Sample} identifier that we're looking for.
     * @return a representation of the specific sample.
     */
    @RequestMapping(value = "/projects/{projectId}/samples/{sampleId}", method = RequestMethod.GET)
    public ModelMap getProjectSample(@PathVariable String projectId, @PathVariable String sampleId) {
        ModelMap modelMap = new ModelMap();
        Identifier projectIdentifier = new Identifier(projectId);
        Identifier sampleIdentifier = new Identifier(sampleId);

        // load the project
        Project p = projectService.read(projectIdentifier);
        // get the sample for the project.
        Sample s = sampleService.getSampleForProject(p, sampleIdentifier);

        // prepare the sample for serializing to the client
        SampleResource sr = new SampleResource();
        sr.setResource(s);

        // add a link to: 1) self, 2) sequenceFiles, 3) project
        sr.add(linkTo(methodOn(ProjectSamplesController.class).getProjectSample(projectId, sampleId)).withSelfRel());
        sr.add(linkTo(ProjectsController.class).slash(projectId).withRel(REL_PROJECT));
        sr.add(linkTo(methodOn(SampleSequenceFilesController.class).getSampleSequenceFiles(projectId, sampleId))
                .withRel(SampleSequenceFilesController.REL_SAMPLE_SEQUENCE_FILES));

        // add the sample resource to the response
        modelMap.addAttribute(GenericController.RESOURCE_NAME, sr);

        // get some related sequence files for the sample
        Collection<Relationship> relationships = relationshipService
                .getRelationshipsForEntity(sampleIdentifier, Sample.class, SequenceFile.class);
        ResourceCollection<LabelledRelationshipResource> sequenceFileResources =
                new ResourceCollection<>(relationships.size());

        for (Relationship r : relationships) {
            Identifier sequenceFileIdentifier = r.getObject();
            LabelledRelationshipResource resource = new LabelledRelationshipResource(sequenceFileIdentifier.getLabel(), r);
            resource.add(linkTo(methodOn(SampleSequenceFilesController.class).getSequenceFileForSample(projectId,
                    sampleId, sequenceFileIdentifier.getIdentifier())).withSelfRel());
            Link fastaLink = linkTo(methodOn(SampleSequenceFilesController.class).getSequenceFileForSample(projectId,
                    sampleId, sequenceFileIdentifier.getIdentifier())).withRel(SampleSequenceFilesController.REL_SAMPLE_SEQUENCE_FILE_FASTA);
            // we need to add the fasta suffix manually to the end, so that web-based clients can find the file.
            resource.add(new Link(fastaLink.getHref() + ".fasta", SampleSequenceFilesController.REL_SAMPLE_SEQUENCE_FILE_FASTA));
            sequenceFileResources.add(resource);
        }

        sequenceFileResources.add(linkTo(methodOn(SampleSequenceFilesController.class)
                .getSampleSequenceFiles(projectId, sampleId)).withSelfRel());
        Map<String, ResourceCollection<LabelledRelationshipResource>> relatedResources = new HashMap<>();
        relatedResources.put("sequenceFiles", sequenceFileResources);
        modelMap.addAttribute(GenericController.RELATED_RESOURCES_NAME, relatedResources);

        return modelMap;
    }

    /**
     * Remove a specific {@link Sample} from the collection of {@link Sample}s associated with a {@link Project}.
     *
     * @param projectId the {@link Project} identifier.
     * @param sampleId  the {@link Sample} identifier.
     * @return a response including links back to the specific {@link Project} and collection of {@link Sample}.
     */
    @RequestMapping(value = "/projects/{projectId}/samples/{sampleId}", method = RequestMethod.DELETE)
    public ModelMap removeSampleFromProject(@PathVariable String projectId, @PathVariable String sampleId) {
        ModelMap modelMap = new ModelMap();
        Identifier projectIdentifier = new Identifier();
        projectIdentifier.setIdentifier(projectId);

        Identifier sampleIdentifier = new Identifier();
        sampleIdentifier.setIdentifier(sampleId);

        // load the sample and project
        Project p = projectService.read(projectIdentifier);
        Sample s = sampleService.read(sampleIdentifier);

        // remove the relationship.
        projectService.removeSampleFromProject(p, s);

        // respond to the client.
        RootResource resource = new RootResource();
        // add links back to the collection of samples and to the project itself.
        resource.add(linkTo(methodOn(ProjectSamplesController.class)
                .getProjectSamples(projectId)).withRel(REL_PROJECT_SAMPLES));
        resource.add(linkTo(ProjectsController.class).slash(projectId).withRel(ProjectsController.REL_PROJECT));

        // add the links to the response.
        modelMap.addAttribute(GenericController.RESOURCE_NAME, resource);

        return modelMap;
    }

    /**
     * Update a {@link Sample} details.
     *
     * @param projectId     the identifier of the {@link Project} that the {@link Sample} belongs to.
     * @param sampleId      the identifier of the {@link Sample}.
     * @param updatedFields the updated fields of the {@link Sample}.
     * @return a response including links to the {@link Project} and {@link Sample}.
     */
    @RequestMapping(value = "/projects/{projectId}/samples/{sampleId}", method = RequestMethod.PATCH,
            consumes = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ModelMap updateSample(@PathVariable String projectId, @PathVariable String sampleId,
                                 @RequestBody Map<String, Object> updatedFields) {
        ModelMap modelMap = new ModelMap();
        Identifier projectIdentifier = new Identifier(projectId);
        Identifier sampleIdentifier = new Identifier(sampleId);
        // confirm that the project is related to the sample
        Project p = projectService.read(projectIdentifier);
        sampleService.getSampleForProject(p, sampleIdentifier);

        // issue an update request
        sampleService.update(sampleIdentifier, updatedFields);

        // respond to the client with a link to self, sequence files collection and project.
        RootResource resource = new RootResource();
        resource.add(linkTo(methodOn(ProjectSamplesController.class).getProjectSample(projectId, sampleId))
                .withSelfRel());
        resource.add(linkTo(methodOn(SampleSequenceFilesController.class).getSampleSequenceFiles(projectId, sampleId))
                .withRel(SampleSequenceFilesController.REL_SAMPLE_SEQUENCE_FILES));
        resource.add(linkTo(ProjectsController.class).slash(projectId).withRel(ProjectsController.REL_PROJECT));

        modelMap.addAttribute(GenericController.RESOURCE_NAME, resource);

        return modelMap;
    }
}
