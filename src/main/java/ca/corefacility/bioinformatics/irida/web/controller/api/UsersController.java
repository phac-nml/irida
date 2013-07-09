package ca.corefacility.bioinformatics.irida.web.controller.api;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

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

import ca.corefacility.bioinformatics.irida.model.Project;
import ca.corefacility.bioinformatics.irida.model.Relationship;
import ca.corefacility.bioinformatics.irida.model.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.User;
import ca.corefacility.bioinformatics.irida.model.roles.impl.Identifier;
import ca.corefacility.bioinformatics.irida.model.roles.impl.UserIdentifier;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.RelationshipService;
import ca.corefacility.bioinformatics.irida.service.UserService;
import ca.corefacility.bioinformatics.irida.web.assembler.resource.LabelledRelationshipResource;
import ca.corefacility.bioinformatics.irida.web.assembler.resource.ResourceCollection;
import ca.corefacility.bioinformatics.irida.web.assembler.resource.project.ProjectResource;
import ca.corefacility.bioinformatics.irida.web.assembler.resource.user.UserResource;
import ca.corefacility.bioinformatics.irida.web.controller.api.projects.ProjectsController;

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
     * logger
     */
    private static final Logger logger = LoggerFactory.getLogger(UsersController.class);
    /**
     * rel for all users.
     */
    public static final String REL_ALL_USERS = "users/all";
    /**
     * rel for the first page of the users document.
     */
    public static final String REL_USERS_FIRST_PAGE = "users/pages/first";
    /**
     * a rel for getting a handle on the projects that a user belongs to.
     */
    public static final String REL_USER_PROJECTS = "user/projects";
    /**
     * a map label for the projects associated with a user.
     */
    public static final String USER_PROJECTS_MAP_LABEL = "projects";
    /**
     * Reference to the {@link ProjectService}.
     */
    private final ProjectService projectService;
    /**
     * Reference to the {@link UserService}
     */
    private final UserService userService;
    /**
     * Reference to the {@link RelationshipService}
     */
    private final RelationshipService relationshipService;

    protected UsersController() {
        this.projectService = null;
        this.userService = null;
        this.relationshipService = null;
    }

    /**
     * Constructor, requires a reference to a {@link UserService} and a {@link ProjectService}.
     *
     * @param userService    the {@link UserService} that this controller uses.
     * @param projectService the {@link ProjectService} that this controller uses.
     */
    @Autowired
    public UsersController(UserService userService, ProjectService projectService, RelationshipService relationshipService) {
        super(userService, User.class, UserIdentifier.class, UserResource.class);
        this.userService = userService;
        this.projectService = projectService;
        this.relationshipService = relationshipService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Map<String, ResourceCollection<LabelledRelationshipResource>> constructCustomRelatedResourceCollections(User user) {
        Map<String, ResourceCollection<LabelledRelationshipResource>> resources = new HashMap<>();

        resources.put(USER_PROJECTS_MAP_LABEL, getProjectsForUser(user));

        return resources;
    }

    /**
     * A collection of custom links for the users resource collection.
     *
     * @return a collection of links for all users.
     */
    @Override
    protected Collection<Link> constructCustomResourceCollectionLinks() {
        Collection<Link> links = new HashSet<>();

        links.add(linkTo(methodOn(UsersController.class).getAllUsers()).withRel(REL_ALL_USERS));

        return links;
    }

    /**
     * A collection of custom links for a specific {@link User}.
     *
     * @param u the {@link User} to create links for.
     * @return the links for this {@link User}.
     */
    @Override
    protected Collection<Link> constructCustomResourceLinks(User u) {
        Collection<Link> links = new HashSet<>();
        links.add(linkTo(UsersController.class).slash(u.getUsername()).
                slash("projects").withRel(REL_USER_PROJECTS));
        return links;
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
     * Get the {@link SequenceFile} entities related to this {@link Project}.
     *
     * @param project the {@link Project} to load {@link SequenceFile} entities for.
     * @return labelled relationships
     */
    private ResourceCollection<LabelledRelationshipResource> getProjectsForUser(User user) {
    	
    	Collection<Relationship> relationships = relationshipService.getRelationshipsForEntity(user.getIdentifier(), User.class, Project.class);
    	ResourceCollection<LabelledRelationshipResource> projectResources = new ResourceCollection<>(relationships.size());
    	
    	String userId = user.getIdentifier().getIdentifier();
    	
    	for (Relationship r : relationships) {
    		Identifier projectId = r.getObject();
    		LabelledRelationshipResource resource = new LabelledRelationshipResource(projectId.getLabel(), r);
    		resource.add(linkTo(ProjectsController.class).slash(projectId.getIdentifier()).withSelfRel());
    		projectResources.add(resource);
    	}
    			
    	projectResources.add(linkTo(methodOn(UsersController.class).getUserProjects(userId)).withRel(REL_USER_PROJECTS));
    	projectResources.setTotalResources(relationships.size());

        return projectResources;
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
    protected User mapResourceToType(UserResource ur) {
        return new User(ur.getUsername(), ur.getEmail(), ur.getPassword(),
                ur.getFirstName(), ur.getLastName(), ur.getPhoneNumber());
    }
}
