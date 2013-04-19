package ca.corefacility.bioinformatics.irida.web.controller;

import ca.corefacility.bioinformatics.irida.exceptions.user.UserNotFoundException;
import ca.corefacility.bioinformatics.irida.model.Project;
import ca.corefacility.bioinformatics.irida.model.User;
import ca.corefacility.bioinformatics.irida.model.enums.Order;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.UserService;
import ca.corefacility.bioinformatics.irida.web.assembler.resource.project.ProjectCollectionResource;
import ca.corefacility.bioinformatics.irida.web.assembler.resource.project.ProjectResource;
import ca.corefacility.bioinformatics.irida.web.assembler.resource.user.UserResource;
import ca.corefacility.bioinformatics.irida.web.assembler.resource.user.UserCollectionResource;
import ca.corefacility.bioinformatics.irida.web.controller.links.PageableControllerLinkBuilder;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static ca.corefacility.bioinformatics.irida.web.controller.links.PageableControllerLinkBuilder.pageLinksFor;
import ca.corefacility.bioinformatics.irida.web.exceptions.EntityNotFoundException;
import java.util.Collection;
import org.springframework.http.MediaType;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
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
public class UsersController {

    private static final Logger logger = LoggerFactory.getLogger(UsersController.class);
    private final UserService userService;
    private final ProjectService projectService;
    private static final String USER_PROJECTS_REL = "user/projects";

    @Autowired
    public UsersController(UserService userService, ProjectService projectService) {
        this.userService = userService;
        this.projectService = projectService;
    }

    @RequestMapping(method = RequestMethod.GET)
    public String showUsersPage(Model model,
            @RequestParam(value = PageableControllerLinkBuilder.REQUEST_PARAM_PAGE, defaultValue = "1") int page,
            @RequestParam(value = PageableControllerLinkBuilder.REQUEST_PARAM_SIZE, defaultValue = "20") int size,
            @RequestParam(value = PageableControllerLinkBuilder.REQUEST_PARAM_SORT_COLUMN, defaultValue = "username") String sortColumn,
            @RequestParam(value = PageableControllerLinkBuilder.REQUEST_PARAM_SORT_ORDER, defaultValue = "ASCENDING") Order sortOrder) {

        List<User> users = userService.list(page, size, sortColumn, sortOrder);
        ControllerLinkBuilder linkBuilder = linkTo(UsersController.class);
        int totalUsers = userService.count();
        UserCollectionResource resources = new UserCollectionResource();

        for (User u : users) {
            UserResource resource = new UserResource(u);
            resource.add(linkBuilder.slash(resource.getUsername()).withSelfRel());
            resources.add(resource);
        }

        resources.add(pageLinksFor(UsersController.class, page, size, totalUsers, sortColumn, sortOrder));
        resources.setTotalUsers(totalUsers);

        model.addAttribute("userResources", resources);
        model.addAttribute("users", true);
        model.addAttribute("pageTitle", "Users");
        return "users/index";
    }

    /**
     *
     * @param response
     * @param body
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody String create(HttpServletResponse response, @RequestBody UserResource ur){
           
        User user = new User(ur.getUsername(), ur.getEmail(), ur.getEmail(), ur.getFirstName(),ur.getLastName(), ur.getPhoneNumber());
        logger.debug(user.toString());
        try {
            userService.create(user);
        } catch (ConstraintViolationException e) {
            Set<ConstraintViolation<?>> constraintViolations = e.getConstraintViolations();
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return validationMessages(constraintViolations);
        } catch (IllegalArgumentException e) {
            return "{error: {username: 'username already exists'}}";
        }
        response.setStatus(HttpServletResponse.SC_CREATED);
        return "success";
    }
    
    @RequestMapping(value = "/partials/{name}", method = RequestMethod.GET)
    public String getHTMLPartials(@PathVariable String name, Model model){
        return "users/partials/" + name;
    }

    @RequestMapping(value = "/{username}", method = RequestMethod.GET)
    public String getUser(HttpServletResponse response, @PathVariable String username, Model model) {
        try {
            UserResource u = new UserResource(userService.getUserByUsername(username));
            u.add(linkTo(UsersController.class).slash(username).slash("projects").withRel(USER_PROJECTS_REL));
            u.add(linkTo(UsersController.class).slash(username).withSelfRel());
            model.addAttribute("user", u);
        } catch (UserNotFoundException e) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return "exceptions/404";
        }
        return "users/user";
    }

    @RequestMapping(value = "/{username}/projects", method = RequestMethod.GET)
    public ModelAndView getUserProjects(@PathVariable String username) {
        ModelAndView mav = new ModelAndView("users/user");
        try {
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
        } catch (UserNotFoundException e) {
            throw new EntityNotFoundException();
        }
        return mav;
    }

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
}
