package ca.corefacility.bioinformatics.irida.web.controller;

import ca.corefacility.bioinformatics.irida.model.Project;
import ca.corefacility.bioinformatics.irida.model.User;
import ca.corefacility.bioinformatics.irida.model.roles.impl.UserIdentifier;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.UserService;
import ca.corefacility.bioinformatics.irida.web.assembler.resource.project.ProjectCollectionResource;
import ca.corefacility.bioinformatics.irida.web.assembler.resource.project.ProjectResource;
import ca.corefacility.bioinformatics.irida.web.assembler.resource.user.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import com.google.common.net.HttpHeaders;
import java.util.Collection;
import java.util.HashSet;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.servlet.ModelAndView;

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

    private static final Logger logger = LoggerFactory.getLogger(UsersController.class);
    private final ProjectService projectService;
    private final UserService userService;
    private static final String USER_PROJECTS_REL = "user/projects";

    @Autowired
    public UsersController(UserService userService, ProjectService projectService) {
        super(userService, User.class, UserResource.class, UserIdentifier.class);
        this.userService = userService;
        this.projectService = projectService;
    }

    /**
     * Create a new user resource in the database.
     *
     * @param ur the user resource template to be used.
     * @return the status of the creation request.
     */
    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> create(@RequestBody UserResource ur) {
        User user = new User(ur.getUsername(), ur.getEmail(), ur.getPassword(), ur.getFirstName(), ur.getLastName(), ur.getPhoneNumber());
        user = crudService.create(user);
        String location = linkTo(UsersController.class).slash(user.getUsername()).withSelfRel().getHref();
        MultiValueMap<String, String> responseHeaders = new LinkedMultiValueMap();
        responseHeaders.add(HttpHeaders.LOCATION, location);
        ResponseEntity<String> response = new ResponseEntity<>("success", responseHeaders, HttpStatus.CREATED);
        return response;
    }

    @RequestMapping(value = "/partials/{name}", method = RequestMethod.GET)
    public String getHTMLPartials(@PathVariable String name) {
        return "users/partials/" + name;
    }

    /**
     * Get the collection of projects for a specific user.
     *
     * @param username the username for the desired user.
     * @return a model containing the collection of projects for that user.
     */
    @RequestMapping(value = "/{username}/projects", method = RequestMethod.GET)
    public ModelAndView getUserProjects(@PathVariable String username) {
        ModelAndView mav = new ModelAndView("users/user");
        User u = userService.getUserByUsername(username);
        ProjectCollectionResource resources = new ProjectCollectionResource();
        Collection<Project> projects = projectService.getProjectsForUser(u);
        ControllerLinkBuilder linkBuilder = linkTo(ProjectsController.class);
        for (Project project : projects) {
            ProjectResource resource = new ProjectResource(project);
            resource.add(linkBuilder.slash(project.getIdentifier().getUUID()).withSelfRel());
            resources.add(resource);
        }

        mav.addObject("projectResources", resources);
        return mav;
    }

    @Override
    public Collection<Link> constructCustomResourceLinks(User u) {
        Collection<Link> links = new HashSet<>();
        links.add(linkTo(UsersController.class).slash(u.getUsername()).
                slash("projects").withRel(USER_PROJECTS_REL));
        return links;
    }
}