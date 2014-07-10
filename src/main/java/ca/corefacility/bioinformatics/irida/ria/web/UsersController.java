package ca.corefacility.bioinformatics.irida.ria.web;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
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
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectUserJoin;
import ca.corefacility.bioinformatics.irida.model.user.Role;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.ria.utilities.DataTable;
import ca.corefacility.bioinformatics.irida.ria.utilities.Formats;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.user.UserService;

import com.google.common.base.Strings;
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

	private final List<Role> allowedRoles = Lists.newArrayList(Role.ROLE_ADMIN, Role.ROLE_MANAGER, Role.ROLE_USER,
			Role.ROLE_SEQUENCER);

	private MessageSource messageSource;

	@Autowired
	public UsersController(UserService userService, ProjectService projectService, MessageSource messageSource) {
		this.userService = userService;
		this.projectService = projectService;
		this.messageSource = messageSource;
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
	@RequestMapping(value = "/{userId}", method = RequestMethod.GET)
	public String getUserSpecificPage(@PathVariable("userId") Long userId, final Model model, Principal principal) {
		logger.debug("Getting project information for [User " + userId + "]");
		String page;
		User user = userService.read(userId);
		model.addAttribute("user", user);

		Locale locale = LocaleContextHolder.getLocale();

		String roleMessageName = "systemrole." + user.getSystemRole().getName();
		String systemRole = messageSource.getMessage(roleMessageName, null, locale);

		boolean canEditUser = canEditUser(principal, user);
		model.addAttribute("canEditUser", canEditUser);

		model.addAttribute("systemRole", systemRole);

		// TODO: Only display this for ADMIN users and the currently logged
		// in user
		List<Join<Project, User>> projectsForUser = projectService.getProjectsForUser(user);
		int totalProjects = projectsForUser.size();
		// Trimming down the number of projects if there are too many
		if (totalProjects > MAX_DISPLAY_PROJECTS) {
			projectsForUser = projectsForUser.subList(0, MAX_DISPLAY_PROJECTS);
		}

		List<String> projectRoles = new ArrayList<>();
		for (Join<Project, User> join : projectsForUser) {
			ProjectUserJoin pujoin = (ProjectUserJoin) join;

			String proleMessageName = "projectRole." + pujoin.getProjectRole().toString();
			String projectRole = messageSource.getMessage(proleMessageName, null, locale);

			projectRoles.add(projectRole);
		}

		model.addAttribute("projects", projectsForUser);
		model.addAttribute("projectRoles", projectRoles);
		model.addAttribute("totalProjects", totalProjects);

		page = SPECIFIC_USER_PAGE;

		return page;
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
	@RequestMapping(value = "/{userId}/edit", method = RequestMethod.POST)
	public String updateUser(@PathVariable Long userId, @RequestParam(required = false) String firstName,
			@RequestParam(required = false) String lastName, @RequestParam(required = false) String email,
			@RequestParam(required = false) String systemRole, @RequestParam(required = false) String password,
			@RequestParam(required = false) String confirmPassword, Model model) {
		logger.debug("Updating user " + userId);

		Map<String, String> errors = new HashMap<>();

		Map<String, Object> updatedValues = new HashMap<>();

		if (!Strings.isNullOrEmpty(firstName)) {
			updatedValues.put("firstName", firstName);
		}

		if (!Strings.isNullOrEmpty(lastName)) {
			updatedValues.put("lastName", lastName);
		}

		if (!Strings.isNullOrEmpty(email)) {
			updatedValues.put("email", email);
		}

		if (!Strings.isNullOrEmpty(systemRole)) {
			Role newRole = Role.valueOf(systemRole);

			updatedValues.put("systemRole", newRole);
		}

		if (!Strings.isNullOrEmpty(password) || !Strings.isNullOrEmpty(confirmPassword)) {
			if (!password.equals(confirmPassword)) {
				errors.put("password", "Passwords do not match.");
			}
		}

		try {
			userService.update(userId, updatedValues);
		} catch (ConstraintViolationException ex) {
			logger.debug("User provided data threw ConstrainViolation");
			Set<ConstraintViolation<?>> constraintViolations = ex.getConstraintViolations();

			for (ConstraintViolation<?> violation : constraintViolations) {
				logger.debug(violation.getMessage());
				String errorKey = violation.getPropertyPath().toString();
				errors.put(errorKey, violation.getMessage());
			}
		}

		String returnView = getEditUserPage(userId, model);
		if (!errors.isEmpty()) {
			model.addAttribute("errors", errors);

		} else {
			returnView = "redirect:/users/" + userId;
		}

		return returnView;
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

		Locale locale = LocaleContextHolder.getLocale();

		Map<String, String> roleNames = new HashMap<>();
		for (Role role : allowedRoles) {
			String roleMessageName = "systemrole." + role.getName();
			String roleName = messageSource.getMessage(roleMessageName, null, locale);
			roleNames.put(role.getName(), roleName);
		}

		model.addAttribute("allowedRoles", roleNames);

		if (!model.containsAttribute("errors")) {
			model.addAttribute("errors", new HashMap<String, String>());
		}

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

		Locale locale = LocaleContextHolder.getLocale();
		List<List<String>> usersData = new ArrayList<>();
		for (User user : userPage) {
			// getting internationalized system role from the message source
			String roleMessageName = "systemrole." + user.getSystemRole().getName();
			String systemRole = messageSource.getMessage(roleMessageName, null, locale);

			List<String> row = new ArrayList<>();
			row.add(user.getId().toString());
			row.add(user.getUsername());
			row.add(user.getLastName());
			row.add(user.getFirstName());
			row.add(user.getEmail());
			row.add(systemRole);
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
	public String handleAccessDenied(Exception e) {
		logger.error(e.getMessage());
		return ERROR_PAGE;
	}

	/**
	 * Check if the logged in user is allowed to edit the given user.
	 * 
	 * @param principal
	 *            The currently logged in principal
	 * @param user
	 *            The user to edit
	 * @return boolean if the principal can edit the user
	 */
	private boolean canEditUser(Principal principal, User user) {
		String principalName = principal.getName();
		User readPrincipal = userService.getUserByUsername(principalName);

		return user.equals(readPrincipal) || readPrincipal.getAuthorities().contains(Role.ROLE_ADMIN);
	}

}
