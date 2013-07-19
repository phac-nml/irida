package ca.corefacility.bioinformatics.irida.web.controller.api.projects;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import ca.corefacility.bioinformatics.irida.model.Project;
import ca.corefacility.bioinformatics.irida.model.Sample;
import ca.corefacility.bioinformatics.irida.model.User;
import ca.corefacility.bioinformatics.irida.model.enums.Order;
import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.SampleService;
import ca.corefacility.bioinformatics.irida.service.UserService;
import ca.corefacility.bioinformatics.irida.web.assembler.resource.LabelledRelationshipResource;
import ca.corefacility.bioinformatics.irida.web.assembler.resource.ResourceCollection;
import ca.corefacility.bioinformatics.irida.web.assembler.resource.project.ProjectResource;
import ca.corefacility.bioinformatics.irida.web.controller.api.GenericController;
import ca.corefacility.bioinformatics.irida.web.controller.api.UsersController;
import ca.corefacility.bioinformatics.irida.web.controller.api.samples.SampleSequenceFilesController;

/**
 * Controller for managing {@link Project}s in the database.
 *
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 */
@Controller
@RequestMapping(value = "/projects")
public class ProjectsController extends GenericController<Project, ProjectResource> {

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
     * Reference to {@link UserService} for getting users associated with a project.
     */
    private UserService userService;
    /**
     * Reference to {@link SampleService} for getting samples associated with a project.
     */
    private SampleService sampleService;

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
    public ProjectsController(ProjectService projectService, UserService userService, SampleService sampleService) {
        super(projectService, Project.class, ProjectResource.class);
        this.userService = userService;
        this.sampleService = sampleService;
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
     * {@inheritDoc}
     */
    @Override
    protected Map<String, ResourceCollection<?>> constructCustomRelatedResourceCollections(Project project) {
        Map<String, ResourceCollection<?>> resources = new HashMap<>();

        resources.put(PROJECT_USERS_MAP_LABEL, getUsersForProject(project));
        resources.put(PROJECT_SAMPLES_MAP_LABEL, getSamplesForProject(project));

        return resources;
    }

    /**
     * Get the samples for this project.
     *
     * @param project the project to get samples for.
     * @return the labels and identifiers for the samples attached to this project.
     */
    private ResourceCollection<LabelledRelationshipResource<Project, Sample>> getSamplesForProject(Project project) {
    	List<Join<Project, Sample>> relationships = sampleService.getSamplesForProject(project);
        ResourceCollection<LabelledRelationshipResource<Project, Sample>> sampleResources = new ResourceCollection<>(relationships.size());
        Long projectId = project.getId();
        
        for (Join<Project, Sample> r : relationships) {
        	Sample s = r.getObject();
        	            
            LabelledRelationshipResource<Project, Sample> resource = new LabelledRelationshipResource<>(s.getLabel(), r);
            // add a link to get the specific sample from the project
            resource.add(linkTo(methodOn(ProjectSamplesController.class)
                    .getProjectSample(projectId, s.getId())).withSelfRel());
            // add a link to add sequence files to the sample
            resource.add(linkTo(methodOn(SampleSequenceFilesController.class)
                    .getSampleSequenceFiles(projectId, s.getId()))
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
    private ResourceCollection<LabelledRelationshipResource<Project, User>> getUsersForProject(Project project) {
    	Collection<Join<Project, User>> users = userService.getUsersForProject(project);
        ResourceCollection<LabelledRelationshipResource<Project, User>> userResources = new ResourceCollection<>(users.size());

        for (Join<Project, User> join : users) {
        	User u = join.getObject();
            LabelledRelationshipResource<Project, User> resource = new LabelledRelationshipResource<>(u.getLabel(), join);
            // rel pointing at the user instance
            resource.add(linkTo(UsersController.class).slash(u.getId()).withSelfRel());
            // rel telling the client how to delete the relationship between the user and the project.
            resource.add(linkTo(methodOn(ProjectUsersController.class).removeUserFromProject(project.getId(),
                    u.getUsername())).withRel(REL_RELATIONSHIP));
            userResources.add(resource);
        }

        userResources.add(linkTo(methodOn(ProjectUsersController.class, String.class).getUsersForProject(
                project.getId())).withRel(PROJECT_USERS_REL));
        userResources.setTotalResources(users.size());

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
        Long projectId = p.getId();
        links.add(linkTo(ProjectsController.class).
                slash(p.getId()).slash("users").
                withRel(PROJECT_USERS_REL));
        links.add(linkTo(methodOn(ProjectSamplesController.class).getProjectSamples(projectId))
                .withRel(ProjectSamplesController.REL_PROJECT_SAMPLES));
        return links;
    }
}
