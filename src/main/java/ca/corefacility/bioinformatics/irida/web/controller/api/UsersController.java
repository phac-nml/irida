package ca.corefacility.bioinformatics.irida.web.controller.api;

import ca.corefacility.bioinformatics.irida.model.Project;
import ca.corefacility.bioinformatics.irida.model.User;
import ca.corefacility.bioinformatics.irida.model.roles.impl.UserIdentifier;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.UserService;
import ca.corefacility.bioinformatics.irida.web.assembler.resource.ResourceCollection;
import ca.corefacility.bioinformatics.irida.web.assembler.resource.project.ProjectResource;
import ca.corefacility.bioinformatics.irida.web.assembler.resource.user.UserResource;
import ca.corefacility.bioinformatics.irida.web.controller.api.projects.ProjectsController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import static org.springframework.hateoas.core.DummyInvocationUtils.methodOn;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

/**
 * Controller for managing users.
 *
 * @author Josh Adam <josh.adam@phac-aspc.gc.ca>
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 */
@Controller
@RequestMapping(value = "/users")
public class UsersController extends GenericController<UserIdentifier, User, UserResource> {

    /**
     * rel for the first page of the users document.
     */
    public static final String REL_USERS_FIRST_PAGE = "users/pages/first";
    /**
     * logger
     */
    private static final Logger logger = LoggerFactory.getLogger(UsersController.class);
    /**
     * a rel for getting a handle on the projects that a user belongs to.
     */
    private static final String USER_PROJECTS_REL = "user/projects";
    /**
     * Reference to the {@link ProjectService}.
     */
    private final ProjectService projectService;
    /**
     * Reference to the {@link UserService}
     */
    private final UserService userService;

    /**
     * Constructor, requires a reference to a {@link UserService} and a {@link ProjectService}.
     *
     * @param userService    the {@link UserService} that this controller uses.
     * @param projectService the {@link ProjectService} that this controller uses.
     */
    @Autowired
    public UsersController(UserService userService, ProjectService projectService) {
        super(userService, User.class, UserIdentifier.class, UserResource.class);
        this.userService = userService;
        this.projectService = projectService;
    }

    protected UsersController() {
        projectService = null;
        userService = null;
    }

    /**
     * Get all users in the application.
     *
     * @return
     */
    @RequestMapping(value = "/all", method = RequestMethod.GET)
    public ModelMap getAllUsers() {
        List<User> users = userService.list();
        ResourceCollection<UserResource> userResources = new ResourceCollection<>(users.size());
        for (User u : users) {
            UserResource ur = new UserResource(u);
            ur.add(linkTo(UsersController.class).slash(u.getUsername()).withSelfRel());
            userResources.add(ur);
        }

        userResources.add(linkTo(methodOn(UsersController.class).getAllUsers()).withSelfRel());
        userResources.add(linkTo(UsersController.class).withRel(REL_USERS_FIRST_PAGE));
        userResources.setTotalResources(users.size());

        ModelMap model = new ModelMap();
        model.addAttribute(GenericController.RESOURCE_NAME, userResources);
        return model;
    }

    /**
     * Get the collection of projects for a specific user.
     *
     * @param username the username for the desired user.
     * @return a model containing the collection of projects for that user.
     */
    @RequestMapping(value = "/{username}/projects", method = RequestMethod.GET)
    public ModelMap getUserProjects(@PathVariable String username) {
        logger.debug("Loading projects for user [" + username + "]");
        ModelMap mav = new ModelMap();


        // get the appropriate user from the database
        User u = userService.getUserByUsername(username);

        // get all of the projects that this user belongs to
        ResourceCollection<ProjectResource> resources = new ResourceCollection<>();
        Collection<Project> projects = projectService.getProjectsForUser(u);
        ControllerLinkBuilder linkBuilder = linkTo(ProjectsController.class);

        // add the project and a self-rel link to the project representation
        for (Project project : projects) {
            ProjectResource resource = new ProjectResource(project);
            resource.add(linkBuilder.slash(project.getIdentifier().getUUID()).withSelfRel());
            resources.add(resource);
        }

        // add the resources to the response
        mav.addAttribute("projectResources", resources);

        // respond to the user
        return mav;
    }

    /**
     * Map an instance of {@link UserResource} to {@link User}.
     *
     * @param ur the {@link UserResource} to map.
     * @return an instance of {@link User}.
     */
    @Override
    public User mapResourceToType(UserResource ur) {
        return new User(ur.getUsername(), ur.getEmail(), ur.getPassword(),
                ur.getFirstName(), ur.getLastName(), ur.getPhoneNumber());
    }

    /**
     * A collection of custom links for a specific {@link User}.
     *
     * @param u the {@link User} to create links for.
     * @return the links for this {@link User}.
     */
    @Override
    public Collection<Link> constructCustomResourceLinks(User u) {
        Collection<Link> links = new HashSet<>();
        links.add(linkTo(UsersController.class).slash(u.getUsername()).
                slash("projects").withRel(USER_PROJECTS_REL));
        return links;
    }
}
