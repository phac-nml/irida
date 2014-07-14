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
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import ca.corefacility.bioinformatics.irida.exceptions.EntityExistsException;
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
	private static final String CREATE_USER_PAGE = "user/create";
	private static final String ERROR_PAGE = "error";
	private static final String SORT_BY_ID = "id";
	private static final String SORT_ASCENDING = "asc";
	private static final String ROLE_MESSAGE_PREFIX = "systemrole.";
	private static final int MAX_DISPLAY_PROJECTS = 10;
	private static final Logger logger = LoggerFactory.getLogger(UsersController.class);

	private final UserService userService;
	private final ProjectService projectService;

	private final List<String> SORT_COLUMNS = Lists.newArrayList(SORT_BY_ID, "username", "email", "lastName",
			"firstName", "systemRole", "createdDate", "modifiedDate");

	private final List<Role> adminAllowedRoles = Lists.newArrayList(Role.ROLE_ADMIN, Role.ROLE_MANAGER, Role.ROLE_USER,
			Role.ROLE_SEQUENCER);

	private final MessageSource messageSource;

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
	 *            Spring model to populate the html page
	 * @param principal
	 *            the currently logged in user
	 * @return The name of the user/details page
	 */

	@RequestMapping(value = "/{userId}", method = RequestMethod.GET)
	public String getUserSpecificPage(@PathVariable("userId") Long userId, final Model model, Principal principal) {
		logger.debug("Getting project information for [User " + userId + "]");

		// add the user to the model
		User user = userService.read(userId);
		model.addAttribute("user", user);

		Locale locale = LocaleContextHolder.getLocale();

		// add the user's role to the model
		String roleMessageName = "systemrole." + user.getSystemRole().getName();
		String systemRole = messageSource.getMessage(roleMessageName, null, locale);
		model.addAttribute("systemRole", systemRole);

		// check if we should show an edit button
		boolean canEditUser = canEditUser(principal, user);
		model.addAttribute("canEditUser", canEditUser);

		// show the user's projects
		List<Join<Project, User>> projectsForUser = projectService.getProjectsForUser(user);
		int totalProjects = projectsForUser.size();
		// Trimming down the number of projects if there are too many
		if (totalProjects > MAX_DISPLAY_PROJECTS) {
			projectsForUser = projectsForUser.subList(0, MAX_DISPLAY_PROJECTS);
		}

		// add the projects to the model list
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

		return SPECIFIC_USER_PAGE;
	}

	/**
	 * Get the currently logged in user's page
	 * 
	 * @param model
	 *            The model to pass on
	 * @param principal
	 *            The currently logged in user
	 * @return getUserSpecificPage for the currently logged in user
	 */
	@RequestMapping("/current")
	public String getLoggedInUserPage(Model model, Principal principal) {
		User readPrincipal = userService.getUserByUsername(principal.getName());

		return getUserSpecificPage(readPrincipal.getId(), model, principal);
	}

	/**
	 * Submit a user edit
	 * 
	 * @param userId
	 *            The id of the user to edit (required)
	 * @param firstName
	 *            The firstname to update
	 * @param lastName
	 *            the lastname to update
	 * @param email
	 *            the email to update
	 * @param systemRole
	 *            the role to update
	 * @param password
	 *            the password to update
	 * @param confirmPassword
	 *            password confirmation
	 * @param model
	 *            The model to work on
	 * @return The name of the user view
	 */
	@RequestMapping(value = "/{userId}/edit", method = RequestMethod.POST)
	public String updateUser(@PathVariable Long userId, @RequestParam(required = false) String firstName,
			@RequestParam(required = false) String lastName, @RequestParam(required = false) String email,
			@RequestParam(required = false) String phoneNumber, @RequestParam(required = false) String systemRole,
			@RequestParam(required = false) String password, @RequestParam(required = false) String enabled,
			@RequestParam(required = false) String confirmPassword, Model model, Principal principal) {
		logger.debug("Updating user " + userId);

		Locale locale = LocaleContextHolder.getLocale();

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
		
		if (!Strings.isNullOrEmpty(phoneNumber)) {
			updatedValues.put("phoneNumber", phoneNumber);
		}

		if (!Strings.isNullOrEmpty(password) || !Strings.isNullOrEmpty(confirmPassword)) {
			if (!password.equals(confirmPassword)) {

				errors.put("password", messageSource.getMessage("user.edit.password.match", null, locale));
			} else {
				updatedValues.put("password", password);
			}
		}

		if (isAdmin) {
			logger.debug("User is admin");
			if (!Strings.isNullOrEmpty(enabled)) {
				updatedValues.put("enabled", true);
			} else {
				updatedValues.put("enabled", false);
			}

			if (!Strings.isNullOrEmpty(systemRole)) {
				Role newRole = Role.valueOf(systemRole);

				updatedValues.put("systemRole", newRole);
			}
		}

		String returnView;
		try {
			userService.update(userId, updatedValues);
			returnView = "redirect:/users/" + userId;
		} catch (ConstraintViolationException | DataIntegrityViolationException ex) {
			errors = handleCreateUpdateException(ex, locale);

			model.addAttribute("errors", errors);

			returnView = getEditUserPage(userId, model);
		}

		return returnView;
	}
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
	@PreAuthorize("hasPermission(#userId, 'canUpdateUser')")
	public String getEditUserPage(@PathVariable Long userId, Model model) {
		logger.trace("Getting edit project page for [User " + userId + "]");
		User user = userService.read(userId);
		model.addAttribute("user", user);

		Locale locale = LocaleContextHolder.getLocale();

		Map<String, String> roleNames = new HashMap<>();
		for (Role role : allowedRoles) {
			if (!role.equals(user.getSystemRole())) {
				String roleMessageName = ROLE_MESSAGE_PREFIX + role.getName();
				String roleName = messageSource.getMessage(roleMessageName, null, locale);
				roleNames.put(role.getName(), roleName);
			}
		}

		model.addAttribute("allowedRoles", roleNames);

		String currentRoleName = messageSource.getMessage(ROLE_MESSAGE_PREFIX + user.getSystemRole().getName(), null,
				locale);

		model.addAttribute("currentRole", currentRoleName);

		if (!model.containsAttribute("errors")) {
			model.addAttribute("errors", new HashMap<String, String>());
		}

		return EDIT_USER_PAGE;
	}

	@RequestMapping(value = "/create", method = RequestMethod.GET)
	@PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_MANAGER')")
	public String createUserPage(Model model) {

		Locale locale = LocaleContextHolder.getLocale();

		Map<String, String> roleNames = new HashMap<>();
		for (Role role : adminAllowedRoles) {
			String roleMessageName = "systemrole." + role.getName();
			String roleName = messageSource.getMessage(roleMessageName, null, locale);
			roleNames.put(role.getName(), roleName);
		}

		model.addAttribute("allowedRoles", roleNames);

		if (!model.containsAttribute("errors")) {
			model.addAttribute("errors", new HashMap<String, String>());
		}

		return CREATE_USER_PAGE;
	}

	@RequestMapping(value = "/create", method = RequestMethod.POST)
	@PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_MANAGER')")
	public String submitCreateUser(@RequestParam String username, @RequestParam String firstName,
			@RequestParam String lastName, @RequestParam String email, @RequestParam String phoneNumber,
			@RequestParam(defaultValue = "ROLE_USER") String systemRole, @RequestParam String password,
			@RequestParam String confirmPassword, Model model) {

		User user = new User(username, email, password, firstName, lastName, phoneNumber);
		user.setSystemRole(Role.valueOf(systemRole));

		Map<String, String> errors;

		// if there are errors, add them and return the edit page
		String returnView;

		Locale locale = LocaleContextHolder.getLocale();

		try {
			user = userService.create(user);

			Long userId = user.getId();
			returnView = "redirect:/users/" + userId;
		} catch (ConstraintViolationException | DataIntegrityViolationException | EntityExistsException ex) {
			errors = handleCreateUpdateException(ex, locale);

			model.addAttribute("errors", errors);

			model.addAttribute("given_username", username);
			model.addAttribute("given_firstName", firstName);
			model.addAttribute("given_lastName", lastName);
			model.addAttribute("given_email", email);
			model.addAttribute("given_phoneNumber", phoneNumber);

			returnView = createUserPage(model);
		}

		return returnView;
	}

	/**
	 * Get the listing of users
	 * 
	 * @param principal
	 *            The logged in user
	 * @param start
	 *            The start page
	 * @param length
	 *            The length of a page
	 * @param draw
	 * @param sortColumn
	 *            The column to sort on
	 * @param direction
	 *            The direction to sort
	 * @param searchValue
	 *            The value to search with
	 * @return A Model Map<String,Object> containing the users to list
	 */
	@RequestMapping(value = "/ajax/list", produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Map<String, Object> getAjaxUserList(final Principal principal,
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

	/**
	 * Handle exceptions for the create and update pages
	 * @param ex an exception to handle
	 * @param locale The locale to work with
	 * @return A Map<String,String> of errors to render
	 */
	private Map<String, String> handleCreateUpdateException(Exception ex, Locale locale) {
		Map<String, String> errors = new HashMap<>();
		if (ex instanceof ConstraintViolationException) {
			ConstraintViolationException cvx = (ConstraintViolationException) ex;
			logger.debug("User provided data threw ConstrainViolation");
			Set<ConstraintViolation<?>> constraintViolations = cvx.getConstraintViolations();

			for (ConstraintViolation<?> violation : constraintViolations) {
				logger.debug(violation.getMessage());
				String errorKey = violation.getPropertyPath().toString();
				errors.put(errorKey, violation.getMessage());
			}
		} else if (ex instanceof DataIntegrityViolationException) {
			DataIntegrityViolationException divx = (DataIntegrityViolationException) ex;
			logger.debug(divx.getMessage());
			if (divx.getMessage().contains(User.USER_EMAIL_CONSTRAINT_NAME)) {
				errors.put("email", messageSource.getMessage("user.edit.emailConflict", null, locale));
			}
		} else if (ex instanceof EntityExistsException) {
			EntityExistsException eex = (EntityExistsException) ex;
			errors.put(eex.getFieldName(), eex.getMessage());
		}

		return errors;
	}

	/**
	 * Handle {@link AccessDeniedException} and {@link EntityNotFoundException}
	 * 
	 * @param e
	 *            THe exception to handle
	 * @return An error page
	 */
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
		User readPrincipal = userService.getUserByUsername(principal.getName());

		boolean principalAdmin = readPrincipal.getAuthorities().contains(Role.ROLE_ADMIN);
		boolean usersEqual = user.equals(readPrincipal);

		return principalAdmin || usersEqual;
	}

	/**
	 * Check if the logged in user is an Admin
	 * 
	 * @param principal
	 *            The logged in user to check
	 * @return if the user is an admin
	 */
	private boolean isAdmin(Principal principal) {
		logger.trace("Checking if user is admin");
		User readPrincipal = userService.getUserByUsername(principal.getName());
		return readPrincipal.getAuthorities().contains(Role.ROLE_ADMIN);
	}

}
