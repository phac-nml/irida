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
import ca.corefacility.bioinformatics.irida.service.UserService;
import ca.corefacility.bioinformatics.irida.web.assembler.resource.ResourceCollection;
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
     * Reference to {@link SamplesController} for managing related samples.
     */
    @Autowired
    private SamplesController samplesController;

    /**
     * Constructor for {@link ProjectsController}, requires a reference to a {@link ProjectService}.
     *
     * @param projectService the {@link ProjectService} to be used by this controller.
     */
    @Autowired
    public ProjectsController(ProjectService projectService, UserService userService) {
        super(projectService, Project.class, Identifier.class, ProjectResource.class);
        this.userService = userService;
        this.projectService = projectService;
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
            logger.debug(r.getSubject().getIdentifier());
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

    /**
     * Provide an instance of {@link SamplesController} for linking to {@link Sample} resources.
     *
     * @param samplesController a reference to a {@link SamplesController}
     */
    public void setSamplesController(SamplesController samplesController) {
        this.samplesController = samplesController;
    }
}
