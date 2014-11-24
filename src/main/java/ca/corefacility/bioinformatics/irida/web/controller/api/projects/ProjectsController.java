package ca.corefacility.bioinformatics.irida.web.controller.api.projects;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.util.Collection;
import java.util.HashSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.web.assembler.resource.project.ProjectResource;
import ca.corefacility.bioinformatics.irida.web.controller.api.GenericController;

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
    public ProjectsController(ProjectService projectService) {
        super(projectService, Project.class, ProjectResource.class);
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
