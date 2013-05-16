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
package ca.corefacility.bioinformatics.irida.web.controller;

import ca.corefacility.bioinformatics.irida.model.Project;
import ca.corefacility.bioinformatics.irida.model.Relationship;
import ca.corefacility.bioinformatics.irida.model.Sample;
import ca.corefacility.bioinformatics.irida.model.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.enums.Order;
import ca.corefacility.bioinformatics.irida.model.roles.impl.Identifier;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.RelationshipService;
import ca.corefacility.bioinformatics.irida.service.UserService;
import ca.corefacility.bioinformatics.irida.web.assembler.resource.project.ProjectResource;
import ca.corefacility.bioinformatics.irida.web.controller.links.LabelledRelationshipResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.*;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

/**
 * Controller for managing {@link Project}s in the database.
 *
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 */
@Controller
@RequestMapping(value = "/projects")
public class ProjectsController extends GenericController<Identifier, Project, ProjectResource> {

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
     * Reference to {@link RelationshipService} for getting related resources.
     */
    private RelationshipService relationshipService;

    /**
     * Constructor for {@link ProjectsController}, requires a reference to a {@link ProjectService}.
     *
     * @param projectService the {@link ProjectService} to be used by this controller.
     */
    @Autowired
    public ProjectsController(ProjectService projectService, UserService userService, RelationshipService relationshipService) {
        super(projectService, Identifier.class, ProjectResource.class);
        this.userService = userService;
        this.relationshipService = relationshipService;
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
    @SuppressWarnings("unchecked")
    @Override
    protected Map<String, Collection<LabelledRelationshipResource>> constructRelatedResourceCollections(Project project) {
        Map<String, Collection<LabelledRelationshipResource>> resources = new HashMap<>();

        resources.put(PROJECT_USERS_MAP_LABEL, getUsersForProject(project));
        resources.put(PROJECT_SAMPLES_MAP_LABEL,
                getRelatedResourcesForProject(project, Sample.class, SamplesController.class));
        resources.put(PROJECT_SEQUENCE_FILES_MAP_LABEL,
                getRelatedResourcesForProject(project, SequenceFile.class, SequenceFileController.class));

        return resources;
    }

    private Collection<LabelledRelationshipResource> getRelatedResourcesForProject(Project project, Class<?> relatedClass, Class<?> controller) {
        Collection<Relationship> relationships = relationshipService.getRelationshipsForEntity(project.getIdentifier(),
                Project.class, relatedClass);
        List<LabelledRelationshipResource> resources = new ArrayList<>(relationships.size());
        for (Relationship r : relationships) {
            LabelledRelationshipResource resource = new LabelledRelationshipResource("relationship", r);
            resource.add(linkTo(controller).slash(r.getObject()).withSelfRel());
            resources.add(resource);
        }
        return resources;
    }

    /**
     * Get the users for this project as a collection of {@link ca.corefacility.bioinformatics.irida.web.controller.links.LabelledRelationshipResource}.
     *
     * @param project the project to get the users for.
     * @return labels and identifiers for the users attached to the project.
     */
    private Collection<LabelledRelationshipResource> getUsersForProject(Project project) {
        Collection<Relationship> relationships = userService.getUsersForProject(project.getIdentifier());
        List<LabelledRelationshipResource> userResources = new ArrayList<>(relationships.size());
        for (Relationship r : relationships) {
            Identifier userIdentifier = r.getSubject();
            LabelledRelationshipResource resource = new LabelledRelationshipResource(userIdentifier.getLabel(), r);
            resource.add(linkTo(UsersController.class).slash(userIdentifier.getIdentifier()).withSelfRel());
            userResources.add(resource);
        }
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
