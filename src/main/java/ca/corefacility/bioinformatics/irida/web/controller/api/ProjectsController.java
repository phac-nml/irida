/*
 * Copyright 2013 Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ca.corefacility.bioinformatics.irida.web.controller.api;

import ca.corefacility.bioinformatics.irida.model.*;
import ca.corefacility.bioinformatics.irida.model.enums.Order;
import ca.corefacility.bioinformatics.irida.model.roles.impl.Identifier;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.SampleService;
import ca.corefacility.bioinformatics.irida.service.UserService;
import ca.corefacility.bioinformatics.irida.web.assembler.resource.ResourceCollection;
import ca.corefacility.bioinformatics.irida.web.assembler.resource.RootResource;
import ca.corefacility.bioinformatics.irida.web.assembler.resource.project.ProjectResource;
import ca.corefacility.bioinformatics.irida.web.assembler.resource.sample.SampleResource;
import ca.corefacility.bioinformatics.irida.web.assembler.resource.user.UserResource;
import ca.corefacility.bioinformatics.irida.web.controller.links.LabelledRelationshipResource;
import com.google.common.net.HttpHeaders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.ExposesResourceFor;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

/**
 * Controller for managing {@link Project}s in the database.
 *
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 */
@Controller
@ExposesResourceFor(Project.class)
@RequestMapping(value = "/projects")
public class ProjectsController extends GenericController<Identifier, Project, ProjectResource> {

    /**
     * rel used for accessing an individual project.
     */
    public static final String PROJECT_REL = "project";
    /**
     * rel used for accessing the list of samples associated with a project.
     */
    public static final String PROJECT_SAMPLES_REL = "project/samples";
    /**
     * Logger.
     */
    private static final Logger logger = LoggerFactory.getLogger(ProjectsController.class);
    /**
     * rel used for accessing users associated with a project.
     */
    private static final String PROJECT_USERS_REL = "project/users";
    /**
     * A label that's used to list the users associated with a project.
     */
    private static final String PROJECT_USERS_MAP_LABEL = "users";
    /**
     * A label that's used to list the samples associated with a project.
     */
    private static final String PROJECT_SAMPLES_MAP_LABEL = "samples";
    /**
     * A label that's used to list the sequence files associated with a project.
     */
    private static final String PROJECT_SEQUENCE_FILES_MAP_LABEL = "sequenceFiles";
    /**
     * Reference to {@link UserService} for getting users associated with a project.
     */
    private UserService userService;
    /**
     * Reference to {@link ProjectService} for managing projects.
     */
    private ProjectService projectService;
    /**
     * Reference to {@link SampleService} for managing samples associated with projects.
     */
    private SampleService sampleService;
    /**
     * Reference to {@link SamplesController} for managing related samples.
     */
    private SamplesController samplesController;
    /**
     * Reference to {@link SequenceFileController} for managing related sequence files.
     */
    private SequenceFileController sequenceFileController;

    /**
     * Constructor for {@link ProjectsController}, requires a reference to a {@link ProjectService}.
     *
     * @param projectService the {@link ProjectService} to be used by this controller.
     */
    @Autowired
    public ProjectsController(ProjectService projectService, UserService userService, SampleService sampleService,
                              SamplesController samplesController, SequenceFileController sequenceFileController) {
        super(projectService, Project.class, Identifier.class, ProjectResource.class);
        this.userService = userService;
        this.projectService = projectService;
        this.sampleService = sampleService;
        this.samplesController = samplesController;
        this.sequenceFileController = sequenceFileController;
    }

    /**
     * A default constructor is required for the convenience <code>methodOn</code> method that the Spring HATEOAS
     * project provides. This method *should not* be used by anyone.
     */
    protected ProjectsController() {
        super(null, null, null, null);
    }

    /**
     * Get all users associated with a project.
     *
     * @param projectId the project id to get users for.
     * @return a model with a collection of user resources.
     */
    @RequestMapping(value = "/{projectId}/users", method = RequestMethod.GET)
    public ModelMap getUsersForProject(@PathVariable String projectId) {
        Identifier id = new Identifier();
        id.setIdentifier(projectId);
        ResourceCollection<UserResource> resources = new ResourceCollection<>();

        // get all of the users belonging to this project
        Collection<Relationship> relationships = userService.getUsersForProject(id);

        // for each of those relationships, retrieve the complete user object
        // and convert to a resource suitable for sending back to the client.
        for (Relationship r : relationships) {
            User u = userService.getUserByUsername(r.getSubject().getIdentifier());
            UserResource ur = new UserResource(u);
            ur.add(linkTo(UsersController.class).slash(u.getIdentifier().getIdentifier()).withSelfRel());
            resources.add(ur);
        }

        // add a link to this resource to the response
        resources.add(
                linkTo(methodOn(ProjectsController.class, String.class).getUsersForProject(projectId)).withSelfRel());

        // prepare the response for the client
        ModelMap model = new ModelMap();
        model.addAttribute(GenericController.RESOURCE_NAME, resources);

        return model;
    }

    /**
     * Add a relationship between a {@link Project} and a {@link User}.
     *
     * @param representation the JSON key-value pair that contains the identifier for the project and the identifier for
     *                       the user.
     * @return a response indicating that the collection was modified.
     */
    @RequestMapping(value = "/{projectId}/users", method = RequestMethod.POST)
    public ResponseEntity<String> addUserToProject(@PathVariable String projectId,
                                                   @RequestBody Map<String, Object> representation) {
        Identifier id = new Identifier();
        id.setIdentifier(projectId);
        // first, get the project
        Project p = projectService.read(id);

        // then, get the user
        User u = userService.getUserByUsername(representation.get("userIdentifier").toString());
        Role r = new Role();
        r.setName("ROLE_USER");

        // then add the user to the project with the specified role.
        projectService.addUserToProject(p, u, r);

        return new ResponseEntity<>("success", HttpStatus.OK);
    }

    /**
     * Create a new sample resource and create a relationship between the sample and the project.
     *
     * @param projectId the identifier of the project that you want to add the sample to.
     * @param sample    the sample that you want to create.
     * @return a response indicating that the sample was created and appropriate location information.
     */
    @RequestMapping(value = "/{projectId}/samples", method = RequestMethod.POST)
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
        String relationshipLocation = linkTo(methodOn(ProjectsController.class).getProjectSample(projectId, sampleId)).withSelfRel().getHref();
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
    @RequestMapping(value = "/{projectId}/samples", method = RequestMethod.GET)
    public ModelMap getProjectSamples(@PathVariable String projectId) {
        return new ModelMap();
    }

    /**
     * Get the representation of a specific sample that's associated with the project.
     *
     * @param sampleId the sample identifier that we're looking for.
     * @return a representation of the specific sample.
     */
    @RequestMapping(value = "/{projectId}/samples/{sampleId}", method = RequestMethod.GET)
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
    @RequestMapping(value = "/{projectId}/samples/{sampleId}", method = RequestMethod.DELETE)
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
        resource.add(linkTo(methodOn(ProjectsController.class).getProjectSamples(projectId)).withRel(PROJECT_SAMPLES_REL));
        resource.add(linkTo(ProjectsController.class).slash(projectId).withRel(PROJECT_REL));

        // add the links to the response.
        modelMap.addAttribute(GenericController.RESOURCE_NAME, resource);

        return modelMap;
    }

    /**
     * Create a new {@link SequenceFile} resource and add a relationship between the {@link SequenceFile} and the
     * {@link Project}.
     *
     * @param projectId the identifier of the project to add the sequence file to.
     * @param file      the file to add to the project.
     * @return a response entity indicating the success of the addition.
     * @throws IOException if the sample file cannot be saved.
     */
    @RequestMapping(value = "/{projectId}/sequenceFiles", consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
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
        String relationshipLocation = linkTo(methodOn(ProjectsController.class).getProjectSequenceFile(projectId,
                sequenceFileId)).withSelfRel().getHref();
        relationshipLocation = "<" + relationshipLocation + ">; rel=relationship";

        // construct a set of headers to add to the response
        MultiValueMap<String, String> responseHeaders = new LinkedMultiValueMap<>();
        responseHeaders.add(HttpHeaders.LOCATION, location);
        responseHeaders.add(HttpHeaders.LINK, relationshipLocation);

        return new ResponseEntity<>("success", responseHeaders, HttpStatus.CREATED);
    }

    /**
     * Get the representation of a sequence file that's associated with a project.
     *
     * @param projectId      the project that the sequence file belongs to.
     * @param sequenceFileId the identifier of the sequence file.
     * @return a representation of the sequence file.
     */
    @RequestMapping(value = "/{projectId}/sequenceFiles/{sequenceFileId}", method = RequestMethod.GET)
    public ModelMap getProjectSequenceFile(@PathVariable String projectId, @PathVariable String sequenceFileId) {
        return sequenceFileController.getResource(sequenceFileId);
    }

    /**
     * Projects should be sorted in descending order by default.
     *
     * @return a descending sort order (<code>Order.DESCENDING</code>).
     */
    @Override
    protected Order getDefaultSortOrder() {
        return Order.DESCENDING;
    }

    /**
     * Map a {@link ProjectResource} to a {@link Project}.
     *
     * @param pr the resource to map.
     * @return an instance of {@link Project}.
     */
    @Override
    protected Project mapResourceToType(ProjectResource pr) {
        Project p = new Project();
        p.setName(pr.getName());
        return p;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Map<String, Class<?>> getUniquelyRelatedClasses() {
        Map<String, Class<?>> relatedResources = new HashMap<>();
        relatedResources.put(PROJECT_SAMPLES_MAP_LABEL, Sample.class);
        relatedResources.put(PROJECT_SEQUENCE_FILES_MAP_LABEL, SequenceFile.class);
        return relatedResources;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    protected Map<String, ResourceCollection<LabelledRelationshipResource>> constructCustomRelatedResourceCollections(Project project) {
        Map<String, ResourceCollection<LabelledRelationshipResource>> resources = new HashMap<>();

        resources.put(PROJECT_USERS_MAP_LABEL, getUsersForProject(project));

        return resources;
    }

    /**
     * Get the users for this project as a collection of {@link ca.corefacility.bioinformatics.irida.web.controller.links.LabelledRelationshipResource}.
     *
     * @param project the project to get the users for.
     * @return labels and identifiers for the users attached to the project.
     */
    private ResourceCollection<LabelledRelationshipResource> getUsersForProject(Project project) {
        Collection<Relationship> relationships = userService.getUsersForProject(project.getIdentifier());
        ResourceCollection<LabelledRelationshipResource> userResources = new ResourceCollection<>(relationships.size());
        for (Relationship r : relationships) {
            Identifier userIdentifier = r.getSubject();
            LabelledRelationshipResource resource = new LabelledRelationshipResource(userIdentifier.getLabel(), r);
            // rel pointing at the user instance
            resource.add(linkTo(UsersController.class).slash(userIdentifier.getIdentifier()).withSelfRel());
            // rel telling the client how to delete the relationship between the user and the project.
            resource.add(linkTo(RelationshipsController.class).slash(r.getIdentifier().getIdentifier())
                    .withRel(REL_RELATIONSHIP));
            userResources.add(resource);
        }

        userResources.add(linkTo(methodOn(ProjectsController.class, String.class).getUsersForProject(
                project.getIdentifier().getIdentifier())).withRel(PROJECT_USERS_REL));
        userResources.setTotalResources(relationships.size());

        return userResources;
    }

    /**
     * The {@link ProjectsController} should tell the client how to find the users for a specific {@link Project}.
     *
     * @param p the {@link Project} to construct custom links for.
     * @return a collection of custom links.
     */
    @Override
    protected Collection<Link> constructCustomResourceLinks(Project p) {
        Collection<Link> links = new HashSet<>();
        links.add(linkTo(ProjectsController.class).
                slash(p.getIdentifier().getIdentifier()).slash("users").
                withRel(PROJECT_USERS_REL));

        return links;
    }
}
