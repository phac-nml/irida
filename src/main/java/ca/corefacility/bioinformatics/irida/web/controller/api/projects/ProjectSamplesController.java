package ca.corefacility.bioinformatics.irida.web.controller.api.projects;

import ca.corefacility.bioinformatics.irida.model.Project;
import ca.corefacility.bioinformatics.irida.model.Relationship;
import ca.corefacility.bioinformatics.irida.model.Sample;
import ca.corefacility.bioinformatics.irida.model.roles.impl.Identifier;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.SampleService;
import ca.corefacility.bioinformatics.irida.web.assembler.resource.RootResource;
import ca.corefacility.bioinformatics.irida.web.assembler.resource.sample.SampleResource;
import ca.corefacility.bioinformatics.irida.web.controller.api.GenericController;
import ca.corefacility.bioinformatics.irida.web.controller.api.SamplesController;
import com.google.common.net.HttpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

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
     * rel used for accessing the list of samples associated with a project.
     */
    public static final String PROJECT_SAMPLES_REL = "project/samples";
    /**
     * Reference to {@link ProjectService}.
     */
    private ProjectService projectService;
    /**
     * Reference to {@link SampleService}
     */
    private SampleService sampleService;
    /**
     * Reference to {@link SamplesController}.
     */
    private SamplesController samplesController;

    protected ProjectSamplesController() {
    }

    @Autowired
    public ProjectSamplesController(ProjectService projectService, SampleService sampleService,
                                    SamplesController samplesController) {
        this.projectService = projectService;
        this.sampleService = sampleService;
        this.samplesController = samplesController;
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
        Sample s = samplesController.mapResourceToType(sample);

        // add the sample to the project
        Relationship r = projectService.addSampleToProject(p, s);

        // construct a link to the sample itself on the samples controller
        String sampleId = r.getObject().getIdentifier();
        String location = linkTo(SamplesController.class).slash(sampleId).withSelfRel().getHref();
        String relationshipLocation = linkTo(methodOn(ProjectSamplesController.class)
                .getProjectSample(projectId, sampleId)).withSelfRel().getHref();
        relationshipLocation = "<" + relationshipLocation + ">; rel=relationship";

        // construct a set of headers to add to the response
        MultiValueMap<String, String> responseHeaders = new LinkedMultiValueMap<>();
        responseHeaders.add(HttpHeaders.LOCATION, location);
        responseHeaders.add(HttpHeaders.LINK, relationshipLocation);

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
        throw new UnsupportedOperationException("not implemented");
    }

    /**
     * Get the representation of a specific sample that's associated with the project.
     *
     * @param sampleId the sample identifier that we're looking for.
     * @return a representation of the specific sample.
     */
    @RequestMapping(value = "/projects/{projectId}/samples/{sampleId}", method = RequestMethod.GET)
    public ModelMap getProjectSample(@PathVariable String projectId, @PathVariable String sampleId) {
        return samplesController.getResource(sampleId);
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
                .getProjectSamples(projectId)).withRel(PROJECT_SAMPLES_REL));
        resource.add(linkTo(ProjectsController.class).slash(projectId).withRel(ProjectsController.PROJECT_REL));

        // add the links to the response.
        modelMap.addAttribute(GenericController.RESOURCE_NAME, resource);

        return modelMap;
    }
}
