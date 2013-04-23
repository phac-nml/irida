package ca.corefacility.bioinformatics.irida.web.controller;

import ca.corefacility.bioinformatics.irida.exceptions.EntityExistsException;
import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.model.Project;
import ca.corefacility.bioinformatics.irida.model.User;
import ca.corefacility.bioinformatics.irida.model.roles.impl.UserIdentifier;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.UserService;
import ca.corefacility.bioinformatics.irida.web.assembler.resource.project.ProjectCollectionResource;
import ca.corefacility.bioinformatics.irida.web.assembler.resource.project.ProjectResource;
import ca.corefacility.bioinformatics.irida.web.assembler.resource.user.UserResource;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.ExceptionHandler;

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
    private static final String USER_PROJECTS_REL = "user/projects";

    @Autowired
    public UsersController(UserService userService, ProjectService projectService) {
        super(userService, User.class, UserResource.class);
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
     * Get an individual user from the database.
     *
     * @param username the username for the desired user.
     * @return a model containing the appropriate resource.
     */
    @RequestMapping(value = "/{username}", method = RequestMethod.GET)
    public ModelAndView getUser(@PathVariable String username) {
        ModelAndView mav = new ModelAndView("users/user");
        UserResource u = new UserResource(userService().getUserByUsername(username));
        u.add(linkTo(UsersController.class).slash(username).slash("projects").withRel(USER_PROJECTS_REL));
        u.add(linkTo(UsersController.class).slash(username).withSelfRel());
        mav.addObject("user", u);
        return mav;
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
        User u = userService().getUserByUsername(username);
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

    /**
     * Handle {@link EntityNotFoundException}.
     *
     * @param e the exception as thrown by the service.
     * @return an appropriate HTTP response.
     */
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<String> handleNotFoundException(EntityNotFoundException e) {
        return new ResponseEntity<>("No such user found.", HttpStatus.NOT_FOUND);
    }

    /**
     * Handle {@link ConstraintViolationException}.
     *
     * @param e the exception as thrown by the service.
     * @return an appropriate HTTP response.
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<String> handleConstraintViolations(ConstraintViolationException e) {
        Set<ConstraintViolation<?>> constraintViolations = e.getConstraintViolations();
        return new ResponseEntity<>(validationMessages(constraintViolations), HttpStatus.BAD_REQUEST);
    }

    /**
     * Handle {@link EntityExistsException}.
     *
     * @param e the exception as thrown by the service.
     * @return an appropriate HTTP response.
     */
    @ExceptionHandler(EntityExistsException.class)
    public ResponseEntity<String> handleExistsException(EntityExistsException e) {
        return new ResponseEntity<>("An entity already exists with that identifier.", HttpStatus.CONFLICT);
    }

    /**
     * Render a collection of constraint violations as a JSON object.
     *
     * @param failures the set of constraint violations.
     * @return the constraint violations as a JSON object.
     */
    private String validationMessages(Set<ConstraintViolation<?>> failures) {
        Map<String, List<String>> mp = new HashMap();
        for (ConstraintViolation<?> failure : failures) {
            logger.debug(failure.getPropertyPath().toString() + ": " + failure.getMessage());
            String property = failure.getPropertyPath().toString();
            if (mp.containsKey(property)) {
                mp.get(failure.getPropertyPath().toString()).add(failure.getMessage());
            } else {
                List<String> list = new ArrayList<>();
                list.add(failure.getMessage());
                mp.put(property, list);
            }
        }
        Gson g = new Gson();
        return g.toJson(mp);
    }

    private UserService userService() {
        return (UserService) crudService;
    }
}