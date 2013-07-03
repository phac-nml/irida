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
package ca.corefacility.bioinformatics.irida.web.controller.api.projects;

import ca.corefacility.bioinformatics.irida.model.Project;
import ca.corefacility.bioinformatics.irida.model.Relationship;
import ca.corefacility.bioinformatics.irida.model.Sample;
import ca.corefacility.bioinformatics.irida.model.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.enums.Order;
import ca.corefacility.bioinformatics.irida.model.roles.impl.Identifier;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.RelationshipService;
import ca.corefacility.bioinformatics.irida.service.UserService;
import ca.corefacility.bioinformatics.irida.web.assembler.resource.ResourceCollection;
import ca.corefacility.bioinformatics.irida.web.assembler.resource.project.ProjectResource;
import ca.corefacility.bioinformatics.irida.web.controller.api.GenericController;
import ca.corefacility.bioinformatics.irida.web.controller.api.UsersController;
import ca.corefacility.bioinformatics.irida.web.assembler.resource.LabelledRelationshipResource;
import ca.corefacility.bioinformatics.irida.web.controller.api.samples.SampleSequenceFilesController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.ExposesResourceFor;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

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
@RequestMapping(value = "/projects")
public class ProjectsController extends GenericController<Identifier, Project, ProjectResource> {

    /**
     * rel used for accessing an individual project.
     */
    public static final String REL_PROJECT = "project";
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
     * Reference to {@link RelationshipService}.
     */
    private RelationshipService relationshipService;

    /**
     * Default constructor. Should not be used.
     */
    protected ProjectsController() {
    }

    /**
     * Constructor for {@link ProjectsController}, requires a reference to a {@link ProjectService}.
     *
     * @param projectService the {@link ProjectService} to be used by this controller.
     */
    @Autowired
    public ProjectsController(ProjectService projectService, UserService userService,
                              RelationshipService relationshipService) {
        super(projectService, Project.class, Identifier.class, ProjectResource.class);
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
    protected Map<String, ResourceCollection<LabelledRelationshipResource>> constructCustomRelatedResourceCollections(Project project) {
        Map<String, ResourceCollection<LabelledRelationshipResource>> resources = new HashMap<>();

        resources.put(PROJECT_USERS_MAP_LABEL, getUsersForProject(project));
        resources.put(PROJECT_SAMPLES_MAP_LABEL, getSamplesForProject(project));
        resources.put(PROJECT_SEQUENCE_FILES_MAP_LABEL, getSequenceFilesForProject(project));

        return resources;
    }

    /**
     * Get the {@link SequenceFile} entities related to this {@link Project}.
     *
     * @param project the {@link Project} to load {@link SequenceFile} entities for.
     * @return labelled relationships
     */
    private ResourceCollection<LabelledRelationshipResource> getSequenceFilesForProject(Project project) {
        Collection<Relationship> relationships = relationshipService.getRelationshipsForEntity(project.getIdentifier(),
                Project.class, SequenceFile.class);
        ResourceCollection<LabelledRelationshipResource> sequenceFileResources = new ResourceCollection<>(relationships.size());
        String projectId = project.getIdentifier().getIdentifier();
        for (Relationship r : relationships) {
            Identifier sequenceFileIdentifier = r.getObject();
            LabelledRelationshipResource resource = new LabelledRelationshipResource(sequenceFileIdentifier.getLabel(), r);
            resource.add(linkTo(methodOn(ProjectSequenceFilesController.class).getProjectSequenceFile(projectId,
                    sequenceFileIdentifier.getIdentifier())).withSelfRel());
            Link fastaLink = linkTo(methodOn(ProjectSequenceFilesController.class).getProjectSequenceFile(projectId,
                    sequenceFileIdentifier.getIdentifier())).withRel(ProjectSequenceFilesController.REL_PROJECT_SEQUENCE_FILE_FASTA);
            // we need to add the fasta suffix manually to the end, so that web-based clients can find the file.
            resource.add(new Link(fastaLink.getHref() + ".fasta", ProjectSequenceFilesController.REL_PROJECT_SEQUENCE_FILE_FASTA));
            sequenceFileResources.add(resource);
        }

        sequenceFileResources.add(linkTo(methodOn(ProjectSequenceFilesController.class)
                .getProjectSequenceFiles(projectId)).withRel(ProjectSequenceFilesController.REL_PROJECT_SEQUENCE_FILES));
        sequenceFileResources.setTotalResources(relationships.size());

        return sequenceFileResources;
    }

    /**
     * Get the samples for this project.
     *
     * @param project the project to get samples for.
     * @return the labels and identifiers for the samples attached to this project.
     */
    private ResourceCollection<LabelledRelationshipResource> getSamplesForProject(Project project) {
        Collection<Relationship> relationships = relationshipService.getRelationshipsForEntity(project.getIdentifier(),
                Project.class, Sample.class);
        ResourceCollection<LabelledRelationshipResource> sampleResources = new ResourceCollection<>(relationships.size());
        String projectId = project.getIdentifier().getIdentifier();
        for (Relationship r : relationships) {
            Identifier sampleIdentifier = r.getObject();
            LabelledRelationshipResource resource = new LabelledRelationshipResource(sampleIdentifier.getLabel(), r);
            // add a link to get the specific sample from the project
            resource.add(linkTo(methodOn(ProjectSamplesController.class)
                    .getProjectSample(projectId, sampleIdentifier.getIdentifier())).withSelfRel());
            // add a link to add sequence files to the sample
            resource.add(linkTo(methodOn(SampleSequenceFilesController.class)
                    .getSampleSequenceFiles(projectId, sampleIdentifier.getIdentifier()))
                    .withRel(SampleSequenceFilesController.REL_SAMPLE_SEQUENCE_FILES));
            sampleResources.add(resource);
        }
        sampleResources.add(linkTo(methodOn(ProjectSamplesController.class).getProjectSamples(projectId))
                .withRel(ProjectSamplesController.REL_PROJECT_SAMPLES));
        sampleResources.setTotalResources(relationships.size());

        return sampleResources;
    }

    /**
     * Get the users for this project as a collection of {@link ca.corefacility.bioinformatics.irida.web.assembler.resource.LabelledRelationshipResource}.
     *
     * @param project the project to get the users for.
     * @return labels and identifiers for the users attached to the project.
     */
    private ResourceCollection<LabelledRelationshipResource> getUsersForProject(Project project) {
        Collection<Relationship> relationships = userService.getUsersForProject(project.getIdentifier());
        ResourceCollection<LabelledRelationshipResource> userResources = new ResourceCollection<>(relationships.size());
        String projectId = project.getIdentifier().getIdentifier();
        for (Relationship r : relationships) {
            Identifier userIdentifier = r.getObject();

            LabelledRelationshipResource resource = new LabelledRelationshipResource(userIdentifier.getLabel(), r);
            // rel pointing at the user instance
            resource.add(linkTo(UsersController.class).slash(userIdentifier.getIdentifier()).withSelfRel());
            // rel telling the client how to delete the relationship between the user and the project.
            resource.add(linkTo(methodOn(ProjectUsersController.class).removeUserFromProject(projectId,
                    userIdentifier.getIdentifier())).withRel(REL_RELATIONSHIP));
            userResources.add(resource);
        }

        userResources.add(linkTo(methodOn(ProjectUsersController.class, String.class).getUsersForProject(
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
        String projectId = p.getIdentifier().getIdentifier();
        links.add(linkTo(ProjectsController.class).
                slash(p.getIdentifier().getIdentifier()).slash("users").
                withRel(PROJECT_USERS_REL));
        links.add(linkTo(methodOn(ProjectSamplesController.class).getProjectSamples(projectId))
                .withRel(ProjectSamplesController.REL_PROJECT_SAMPLES));
        links.add(linkTo(methodOn(ProjectSequenceFilesController.class).getProjectSequenceFiles(projectId))
                .withRel(ProjectSequenceFilesController.REL_PROJECT_SEQUENCE_FILES));
        return links;
    }
}
