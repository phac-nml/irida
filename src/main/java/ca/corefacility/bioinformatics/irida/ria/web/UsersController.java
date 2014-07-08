package ca.corefacility.bioinformatics.irida.ria.web;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.model.Project;
import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.ria.utilities.DataTable;
import ca.corefacility.bioinformatics.irida.ria.utilities.Formats;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.user.UserService;

import com.google.common.collect.Lists;

/**
 * Controller for all {@link User} related views
 * 
 * @author Josh Adam <josh.adam@phac-aspc.gc.ca>
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 */
@Controller
@RequestMapping(value = "/users")
public class UsersController {
	private static final String USERS_PAGE = "user/list";
	private static final String SPECIFIC_USER_PAGE = "user/user_details";
	private static final String EDIT_USER_PAGE = "user/edit";
	private static final String ERROR_PAGE = "error";
	private static final String SORT_BY_ID = "id";
	private static final String SORT_ASCENDING = "asc";
	private static final int MAX_DISPLAY_PROJECTS = 10;
	private static final Logger logger = LoggerFactory.getLogger(UsersController.class);

	private final UserService userService;
	private ProjectService projectService;

	private final List<String> SORT_COLUMNS = Lists.newArrayList(SORT_BY_ID, "username", "email", "lastName",
			"firstName", "systemRole", "createdDate", "modifiedDate");

	@Autowired
	public UsersController(UserService userService, ProjectService projectService) {
		this.userService = userService;
		this.projectService = projectService;
	}

	/**
	 * Request for the page to display a list of all projects available to the
	 * currently logged in user.
	 * 
	 * @return The name of the page.
	 */
	@RequestMapping
	public String getUsersPage() {
		return USERS_PAGE;
	}

	/**
	 * Request for a specific user details page.
	 * 
	 * @param userId
	 *            The id for the user to show details for.
	 * @param model
	 *            Spring model to populate the html page.
	 * @return The name of the user details page.
	 */
	@RequestMapping(value = "/{userId}")
	public String getUserSpecificPage(@PathVariable Long userId, final Model model) {
		logger.debug("Getting project information for [User " + userId + "]");
		String page;
		User user = userService.read(userId);
		model.addAttribute("user", user);

		// TODO: Only display this for ADMIN users and the currently logged
		// in user
		List<Join<Project, User>> projectsForUser = projectService.getProjectsForUser(user);
		int totalProjects = projectsForUser.size();
		// Trimming down the number of projects if there are too many
		if (totalProjects > MAX_DISPLAY_PROJECTS) {
			projectsForUser = projectsForUser.subList(0, MAX_DISPLAY_PROJECTS);
		}

		model.addAttribute("projects", projectsForUser);
		model.addAttribute("totalProjects", totalProjects);

		page = SPECIFIC_USER_PAGE;

		return page;
	}

	/**
	 * Get the user edit page
	 * 
	 * @param userId
	 *            The ID of the user to get
	 * @param model
	 *            The model for the returned view
	 * @return The user edit view
	 */
	@RequestMapping(value = "/{userId}/edit", method = RequestMethod.GET)
	public String getEditUserPage(@PathVariable Long userId, Model model) {
		logger.trace("Getting edit project page for [User " + userId + "]");
		String page;
		User user = userService.read(userId);
		model.addAttribute("user", user);

		page = EDIT_USER_PAGE;

		return page;
	}

	/**
	 * Handles AJAX request for getting a list of projects available to the
	 * logged in user. Produces JSON.
	 * 
	 * @param principal
	 *            The currently logged in user.
	 * @param request
	 *            Contains the parameters for the datatable.
	 * @return JSON value of the projects.
	 */
	@RequestMapping(value = "/ajax/list", produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Map<String, Object> getAjaxProjectList(final Principal principal,
			@RequestParam(DataTable.REQUEST_PARAM_START) Integer start,
			@RequestParam(DataTable.REQUEST_PARAM_LENGTH) Integer length,
			@RequestParam(DataTable.REQUEST_PARAM_DRAW) Integer draw,
			@RequestParam(value = DataTable.REQUEST_PARAM_SORT_COLUMN, defaultValue = "0") Integer sortColumn,
			@RequestParam(value = DataTable.REQUEST_PARAM_SORT_DIRECTION, defaultValue = "asc") String direction,
			@RequestParam(DataTable.REQUEST_PARAM_SEARCH_VALUE) String searchValue) {

		String sortString;

		try {
			sortString = SORT_COLUMNS.get(sortColumn);
		} catch (IndexOutOfBoundsException ex) {
			sortString = SORT_BY_ID;
		}

		Sort.Direction sortDirection = direction.equals(SORT_ASCENDING) ? Sort.Direction.ASC : Sort.Direction.DESC;

		int pageNum = start / length;

		Page<User> userPage = userService.searchUser(searchValue, pageNum, length, sortDirection, sortString);

		List<List<String>> usersData = new ArrayList<>();
		for (User user : userPage) {
			List<String> row = new ArrayList<>();
			row.add(user.getId().toString());
			row.add(user.getUsername());
			row.add(user.getLastName());
			row.add(user.getFirstName());
			row.add(user.getEmail());
			row.add(user.getSystemRole().getDisplayName());
			row.add(Formats.DATE.format(user.getCreatedDate()));
			row.add(Formats.DATE.format(user.getModifiedDate()));
			usersData.add(row);
		}

		Map<String, Object> map = new HashMap<>();
		map.put(DataTable.RESPONSE_PARAM_DRAW, draw);
		map.put(DataTable.RESPONSE_PARAM_RECORDS_TOTAL, userPage.getTotalElements());
		map.put(DataTable.RESPONSE_PARAM_RECORDS_FILTERED, userPage.getTotalElements());

		map.put(DataTable.RESPONSE_PARAM_DATA, usersData);
		return map;
	}

	@ExceptionHandler({ AccessDeniedException.class, EntityNotFoundException.class })
	public String handleAccessDenied() {
		return ERROR_PAGE;
	}

}
