package ca.corefacility.bioinformatics.irida.web.controller;

import ca.corefacility.bioinformatics.irida.exceptions.user.UserNotFoundException;
import ca.corefacility.bioinformatics.irida.model.User;
import ca.corefacility.bioinformatics.irida.model.enums.Order;
import ca.corefacility.bioinformatics.irida.service.UserService;
import ca.corefacility.bioinformatics.irida.web.assembler.resource.user.UserResource;
import ca.corefacility.bioinformatics.irida.web.assembler.resource.user.UserCollectionResource;
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
import org.springframework.hateoas.Link;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.*;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

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
    private static final String PAGE_PARAM = "page";
    private static final String SIZE_PARAM = "size";
    private static final String SORT_COLUMN_PARAM = "sortColumn";
    private static final String SORT_ORDER_PARAM = "sortOrder";
    private static final String USER_PROJECTS_REL = "user/projects";

    @Autowired
    public UsersController(UserService userService) {
        this.userService = userService;
    }

    @RequestMapping(method = RequestMethod.GET)
    public String showUsersPage(Model model,
            @RequestParam(value = PAGE_PARAM, defaultValue = "1") int page,
            @RequestParam(value = SIZE_PARAM, defaultValue = "20") int size,
            @RequestParam(value = SORT_COLUMN_PARAM, defaultValue = "username") String sortColumn,
            @RequestParam(value = SORT_ORDER_PARAM, defaultValue = "DESCENDING") Order sortOrder) {
        List<User> users = userService.list(page, size, sortColumn, sortOrder);
        UserCollectionResource resources = new UserCollectionResource();
        ControllerLinkBuilder linkBuilder = linkTo(UsersController.class);
        for (User u : users) {
            UserResource resource = new UserResource(u);
            resource.add(linkBuilder.slash(resource.getUsername()).withSelfRel());
            resources.add(resource);
        }

        resources.add(getPageLinks(linkBuilder.withSelfRel().getHref(), page, size, sortColumn, sortOrder));
        resources.setTotalUsers(userService.count());

        model.addAttribute("userResources", resources);
        return "users/index";
    }

    /**
     * Get a collection of page {@link Link} objects to add to a collection of
     * resources.
     *
     * @param baseUrl the URL used as the base for all of the links.
     * @param page the current page.
     * @param size the current size of requested resources.
     * @param sortColumn the column that should be used to sort the resources.
     * @param sortOrder the order that the column should be sorted on.
     * @return A collection of links to assist with page navigation.
     */
    private Iterable<Link> getPageLinks(String baseUrl, int page, int size, String sortColumn, Order sortOrder) {
        List<Link> links = new ArrayList<>();
        int totalEntities = userService.count();
        int lastPage = (totalEntities / size) + 1;
        int nextPage = page == lastPage ? lastPage : page + 1;
        int prevPage = page > 2 ? page - 1 : 1;
        int firstPage = 1;

        if (!baseUrl.endsWith("?")) {
            baseUrl = baseUrl + "?";
        }

        links.add(new Link(baseUrl + pageParams(firstPage, size, sortColumn, sortOrder), Link.REL_FIRST));
        links.add(new Link(baseUrl + pageParams(prevPage, size, sortColumn, sortOrder), Link.REL_PREVIOUS));
        links.add(new Link(baseUrl + pageParams(nextPage, size, sortColumn, sortOrder), Link.REL_NEXT));
        links.add(new Link(baseUrl + pageParams(lastPage, size, sortColumn, sortOrder), Link.REL_LAST));

        return links;
    }

    /**
     * Construct the paging parameter lists for appending to a URL.
     *
     * @param page the page that the link should point to.
     * @param size the size of the result set in the page.
     * @param sortColumn the column that the result set should be sorted by.
     * @param sortOrder the order of the sort.
     * @return the parameter section of the URL.
     */
    private String pageParams(int page, int size, String sortColumn, Order sortOrder) {
        StringBuilder sb = new StringBuilder();
        sb.append(PAGE_PARAM).append("=").append(page).append("&");
        sb.append(SIZE_PARAM).append("=").append(size).append("&");
        sb.append(SORT_COLUMN_PARAM).append("=").append(sortColumn).append("&");
        sb.append(SORT_ORDER_PARAM).append("=").append(sortOrder);
        return sb.toString();
    }

    @RequestMapping(method = RequestMethod.POST)
    public @ResponseBody
    String create(HttpServletResponse response,
            @RequestParam(value = "username", required = false) String username,
            @RequestParam(value = "firstName", required = false) String firstName,
            @RequestParam(value = "lastName", required = false) String lastName,
            @RequestParam(value = "email", required = false) String email,
            @RequestParam(value = "phoneNumber", required = false) String phoneNumber,
            @RequestParam(value = "password", required = false) String password) {
        User user = new User(username, email, password, firstName, lastName, phoneNumber);
        try {
            userService.create(user);
        } catch (ConstraintViolationException e) {
            Set<ConstraintViolation<?>> constraintViolations = e.getConstraintViolations();
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return validationMessages(constraintViolations);
        }
        response.setStatus(HttpServletResponse.SC_CREATED);
        return "success";
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
    public String getUserProjects(@PathVariable String username, Model model) {
        return "users/user";
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
