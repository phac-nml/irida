package ca.corefacility.bioinformatics.irida.web.controller.api.projects;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
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

import ca.corefacility.bioinformatics.irida.model.Project;
import ca.corefacility.bioinformatics.irida.model.Sample;
import ca.corefacility.bioinformatics.irida.model.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.SampleService;
import ca.corefacility.bioinformatics.irida.service.SequenceFileService;
import ca.corefacility.bioinformatics.irida.web.assembler.resource.LabelledRelationshipResource;
import ca.corefacility.bioinformatics.irida.web.assembler.resource.ResourceCollection;
import ca.corefacility.bioinformatics.irida.web.assembler.resource.RootResource;
import ca.corefacility.bioinformatics.irida.web.assembler.resource.sample.SampleResource;
import ca.corefacility.bioinformatics.irida.web.controller.api.GenericController;
import ca.corefacility.bioinformatics.irida.web.controller.api.samples.SampleSequenceFilesController;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;
import org.springframework.web.bind.annotation.RequestParam;
import com.google.common.net.HttpHeaders;

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
     * Reference to {@link SequenceFileService}.
     */
    private SequenceFileService sequenceFileService;


    protected ProjectSamplesController() {
    }

    @Autowired
    public ProjectSamplesController(ProjectService projectService, SampleService sampleService, SequenceFileService sequenceFileService) {
        this.projectService = projectService;
        this.sampleService = sampleService;
        this.sequenceFileService = sequenceFileService;
    }

    /**
     * Create a new sample resource and create a relationship between the sample and the project.
     *
     * @param projectId the identifier of the project that you want to add the sample to.
     * @param sample    the sample that you want to create.
     * @return a response indicating that the sample was created and appropriate location information.
     */
    @RequestMapping(value = "/projects/{projectId}/samples", method = RequestMethod.POST)
    public ResponseEntity<String> addSampleToProject(@PathVariable Long projectId, @RequestBody SampleResource sample) {
        // load the project that we're adding to
        Project p = projectService.read(projectId);

        // construct the sample that we're going to create
        Sample s = sample.getResource();

        // add the sample to the project
        Join<Project, Sample> r = projectService.addSampleToProject(p, s);

        // construct a link to the sample itself on the samples controller
        Long sampleId = r.getObject().getId();
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
    public ModelMap getProjectSamples(@PathVariable Long projectId, @RequestParam(required = false) String externalSampleId) {
        
        if(externalSampleId == null || externalSampleId.isEmpty()){
            ModelMap modelMap = new ModelMap();
            Project p = projectService.read(projectId);
            List<Join<Project, Sample>> relationships = sampleService.getSamplesForProject(p);

            ResourceCollection<SampleResource> sampleResources = new ResourceCollection<>(relationships.size());

            for (Join<Project, Sample> r : relationships) {
                Sample sample = r.getObject();
                SampleResource sr = new SampleResource();
                sr.setResource(sample);
                sr.add(linkTo(methodOn(ProjectSamplesController.class).
                        getProjectSample(projectId, sample.getId())).withSelfRel());
                sampleResources.add(sr);
            }

            sampleResources.add(linkTo(methodOn(ProjectSamplesController.class).getProjectSamples(projectId,null)).withSelfRel());
            sampleResources.setTotalResources(relationships.size());

            modelMap.addAttribute(GenericController.RESOURCE_NAME, sampleResources);

            return modelMap;
        }
        else
        {
            return getProjectSamplesById(projectId, externalSampleId);
        }
    }
    
    /**
     * Get the list of {@link Sample} associated with this {@link Project} that have the given sampleId
     *
     * @param projectId the identifier of the {@link Project} to get the {@link Sample}s for.
     * @return the list of {@link Sample}s associated with this {@link Project}.
     */
    public ModelMap getProjectSamplesById( Long projectId, String sampleId) {
        ModelMap modelMap = new ModelMap();
        Project p = projectService.read(projectId);
        
        Sample sampleBySampleId = sampleService.getSampleBySampleId(sampleId);

        ResourceCollection<SampleResource> sampleResources = new ResourceCollection<>(1);
        SampleResource sr = new SampleResource();
        sr.setResource(sampleBySampleId);
        sr.add(linkTo(methodOn(ProjectSamplesController.class).
                    getProjectSample(projectId, sampleBySampleId.getId())).withSelfRel());
        sampleResources.add(sr);

        sampleResources.add(linkTo(methodOn(ProjectSamplesController.class).getProjectSamples(projectId,sampleId)).withSelfRel());
        sampleResources.setTotalResources(1);

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
    public ModelMap getProjectSample(@PathVariable Long projectId, @PathVariable Long sampleId) {
        ModelMap modelMap = new ModelMap();
        // load the project
        Project p = projectService.read(projectId);
        // get the sample for the project.
        Sample s = sampleService.getSampleForProject(p, sampleId);

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
        List<Join<Sample, SequenceFile>> relationships = sequenceFileService.getSequenceFilesForSample(s);
        ResourceCollection<LabelledRelationshipResource<Sample, SequenceFile>> sequenceFileResources =
                new ResourceCollection<>(relationships.size());

        for (Join<Sample, SequenceFile> r : relationships) {
            SequenceFile sf = r.getObject();
            LabelledRelationshipResource<Sample, SequenceFile> resource = new LabelledRelationshipResource<>(sf.getLabel(), r);
            resource.add(linkTo(methodOn(SampleSequenceFilesController.class).getSequenceFileForSample(projectId,
                    sampleId, sf.getId())).withSelfRel());
            sequenceFileResources.add(resource);
        }

        sequenceFileResources.add(linkTo(methodOn(SampleSequenceFilesController.class)
                .getSampleSequenceFiles(projectId, sampleId)).withSelfRel());
        Map<String, ResourceCollection<LabelledRelationshipResource<Sample, SequenceFile>>> relatedResources = new HashMap<>();
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
    public ModelMap removeSampleFromProject(@PathVariable Long projectId, @PathVariable Long sampleId) {
        ModelMap modelMap = new ModelMap();

        // load the sample and project
        Project p = projectService.read(projectId);
        Sample s = sampleService.read(sampleId);

        // remove the relationship.
        projectService.removeSampleFromProject(p, s);

        // respond to the client.
        RootResource resource = new RootResource();
        // add links back to the collection of samples and to the project itself.
        resource.add(linkTo(methodOn(ProjectSamplesController.class)
                .getProjectSamples(projectId,null)).withRel(REL_PROJECT_SAMPLES));
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
    public ModelMap updateSample(@PathVariable Long projectId, @PathVariable Long sampleId,
                                 @RequestBody Map<String, Object> updatedFields) {
        ModelMap modelMap = new ModelMap();

        // confirm that the project is related to the sample
        Project p = projectService.read(projectId);
        sampleService.getSampleForProject(p, sampleId);

        // issue an update request
        sampleService.update(sampleId, updatedFields);

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
