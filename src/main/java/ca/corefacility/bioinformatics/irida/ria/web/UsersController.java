package ca.corefacility.bioinformatics.irida.ria.web;

import java.security.Principal;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
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
import org.springframework.mail.MailSendException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import ca.corefacility.bioinformatics.irida.exceptions.EntityExistsException;
import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.model.enums.ProjectRole;
import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectUserJoin;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.user.PasswordReset;
import ca.corefacility.bioinformatics.irida.model.user.Role;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.repositories.specification.UserSpecification;
import ca.corefacility.bioinformatics.irida.ria.utilities.EmailController;
import ca.corefacility.bioinformatics.irida.ria.utilities.Formats;
import ca.corefacility.bioinformatics.irida.ria.utilities.components.DataTable;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.user.PasswordResetService;
import ca.corefacility.bioinformatics.irida.service.user.UserService;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;

/**
 * Controller for all {@link User} related views
 *
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
	private static final Logger logger = LoggerFactory.getLogger(UsersController.class);

	private final UserService userService;
	private final ProjectService projectService;
	private final PasswordResetService passwordResetService;
	private final EmailController emailController;

	private final List<String> SORT_COLUMNS = Lists.newArrayList(SORT_BY_ID, "username", "email", "lastName",
			"firstName", "systemRole", "createdDate", "modifiedDate");

	private final List<Role> adminAllowedRoles = Lists.newArrayList(Role.ROLE_ADMIN, Role.ROLE_MANAGER, Role.ROLE_USER,
			Role.ROLE_SEQUENCER);

	private final MessageSource messageSource;

	@Autowired
	public UsersController(UserService userService, ProjectService projectService,
			PasswordResetService passwordResetService, EmailController emailController, MessageSource messageSource) {
		this.userService = userService;
		this.projectService = projectService;
		this.passwordResetService = passwordResetService;
		this.emailController = emailController;
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
	 *
	 * @return The name of the user/details page
	 */

	@RequestMapping(value = "/{userId}", method = RequestMethod.GET)
	public String getUserSpecificPage(@PathVariable("userId") Long userId,
									  @RequestParam(value = "mailFailure", required = false, defaultValue = "false") final Boolean mailFailure,
			final Model model, Principal principal) {
		logger.debug("Getting project information for [User " + userId + "]");

		// add the user to the model
		User user = userService.read(userId);
		model.addAttribute("user", user);
		model.addAttribute("mailFailure", mailFailure);

		User principalUser = userService.getUserByUsername(principal.getName());

		Locale locale = LocaleContextHolder.getLocale();

		// add the user's role to the model
		String roleMessageName = "systemrole." + user.getSystemRole().getName();
		String systemRole = messageSource.getMessage(roleMessageName, null, locale);
		model.addAttribute("systemRole", systemRole);

		// check if we should show an edit button
		boolean canEditUser = canEditUser(principalUser, user);
		model.addAttribute("canEditUser", canEditUser);
		model.addAttribute("mailConfigured", emailController.isMailConfigured());

		model.addAttribute("canCreatePasswordReset",
				PasswordResetController.canCreatePasswordReset(principalUser, user));

		// show the user's projects
		List<Join<Project, User>> projectsForUser = projectService.getProjectsForUser(user);

		// add the projects to the model list
		List<Map<String, Object>> projects = new ArrayList<>();
		for (Join<Project, User> join : projectsForUser) {
			ProjectUserJoin pujoin = (ProjectUserJoin) join;
			Project project = join.getSubject();
			Map<String, Object> map = new HashMap<>();
			map.put("identifier", project.getId());
			map.put("name", project.getName());
			map.put("isManager", pujoin.getProjectRole().equals(ProjectRole.PROJECT_OWNER) ? true : false);

			String proleMessageName = "projectRole." + pujoin.getProjectRole().toString();
			map.put("role", messageSource.getMessage(proleMessageName, null, locale));
			map.put("date", pujoin.getCreatedDate());
			projects.add(map);
		}
		model.addAttribute("projects", projects);

		return SPECIFIC_USER_PAGE;
	}

	/**
	 * Get the currently logged in user's page
	 *
	 * @param model
	 *            The model to pass on
	 * @param principal
	 *            The currently logged in user
	 *
	 * @return getUserSpecificPage for the currently logged in user
	 */
	@RequestMapping("/current")
	public String getLoggedInUserPage(Model model, Principal principal) {
		User readPrincipal = userService.getUserByUsername(principal.getName());

		return getUserSpecificPage(readPrincipal.getId(), false, model, principal);
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
	 * @param phoneNumber
	 *            the phone number to update
	 * @param systemRole
	 *            the role to update
	 * @param password
	 *            the password to update
	 * @param confirmPassword
	 *            password confirmation
	 * @param model
	 *            The model to work on
	 * @param enabled
	 *            whether the user account should be enabled or disabled.
	 * @param principal
	 *            a reference to the logged in user.
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

		if (isAdmin(principal)) {
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
		if (errors.isEmpty()) {
			try {
				userService.update(userId, updatedValues);
				returnView = "redirect:/users/" + userId;
			} catch (ConstraintViolationException | DataIntegrityViolationException ex) {
				errors = handleCreateUpdateException(ex, locale);

				model.addAttribute("errors", errors);

				returnView = getEditUserPage(userId, model);
			}
		} else {
			model.addAttribute("errors", errors);
			returnView = getEditUserPage(userId, model);
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
	 *
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
		for (Role role : adminAllowedRoles) {
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
		if (!model.containsAttribute("given_requireActivation")) {
			model.addAttribute("given_requireActivation", true);
		}

		if (!model.containsAttribute("errors")) {
			model.addAttribute("errors", new HashMap<String, String>());
		}

		model.addAttribute("emailConfigured", emailController.isMailConfigured());

		return CREATE_USER_PAGE;
	}

	/**
	 * Create a new user object
	 *
	 * @param user
	 *            User to create as a motel attribute
	 * @param systemRole
	 *            The system role to give to the user
	 * @param confirmPassword
	 *            Password confirmation
	 * @param requireActivation
	 *            Checkbox whether the user account needs to be activated
	 * @param model
	 *            Model for the view
	 * @param principal
	 *            The user creating the object
	 *
	 * @return A redirect to the user details view
	 */
	@RequestMapping(value = "/create", method = RequestMethod.POST)
	@PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_MANAGER')")
	public String submitCreateUser(@ModelAttribute User user, @RequestParam String systemRole,
			@RequestParam String confirmPassword, @RequestParam(required = false) String requireActivation, Model model,
			Principal principal) {

		Map<String, String> errors = new HashMap<>();

		String returnView = null;

		Locale locale = LocaleContextHolder.getLocale();

		User creator = userService.getUserByUsername(principal.getName());

		// check if we need to generate a password
		boolean generateActivation = !Strings.isNullOrEmpty(requireActivation);
		if (generateActivation) {
			user.setPassword(generatePassword());
			confirmPassword = user.getPassword();
			user.setCredentialsNonExpired(false);
		}

		// check validity of password
		if (!user.getPassword().equals(confirmPassword)) {
			errors.put("password", messageSource.getMessage("user.edit.password.match", null, locale));
		}

		// Check if there are any errors for the user creation
		if (errors.isEmpty()) {
			if (isAdmin(principal)) {
				user.setSystemRole(Role.valueOf(systemRole));
			} else {
				user.setSystemRole(Role.ROLE_USER);
			}

			try {
				user = userService.create(user);
				Long userId = user.getId();
				returnView = "redirect:/users/" + userId;

				// if the password isn't set, we'll generate a password reset
				PasswordReset passwordReset = null;
				if (generateActivation) {
					passwordReset = passwordResetService.create(new PasswordReset(user));
					logger.trace("Created password reset for activation");
				}

				emailController.sendWelcomeEmail(user, creator, passwordReset);
			} catch (ConstraintViolationException | DataIntegrityViolationException | EntityExistsException ex) {
				errors = handleCreateUpdateException(ex, locale);
			} catch (final MailSendException e) {
				logger.error("Failed to send user activation e-mail.", e);
				model.addAttribute("mailFailure", true);
			}
		}

		if (!errors.isEmpty()) {
			model.addAttribute("errors", errors);

			model.addAttribute("given_username", user.getUsername());
			model.addAttribute("given_firstName", user.getFirstName());
			model.addAttribute("given_lastName", user.getLastName());
			model.addAttribute("given_email", user.getEmail());
			model.addAttribute("given_phoneNumber", user.getPhoneNumber());
			model.addAttribute("given_requireActivation", generateActivation);

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
	 *            a WET-specific variable.
	 * @param sortColumn
	 *            The column to sort on
	 * @param direction
	 *            The direction to sort
	 * @param searchValue
	 *            The value to search with
	 *
	 * @return A Model {@code Map<String,Object>} containing the users to list
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

		Page<User> userPage = userService.search(UserSpecification.searchUser(searchValue), pageNum, length,
				sortDirection, sortString);

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
			row.add(user.getModifiedDate().toString());
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
	 *
	 * @param ex
	 *            an exception to handle
	 * @param locale
	 *            The locale to work with
	 *
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
	 *
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
	 *
	 * @return boolean if the principal can edit the user
	 */
	private boolean canEditUser(User principalUser, User user) {
		boolean principalAdmin = principalUser.getAuthorities().contains(Role.ROLE_ADMIN);
		boolean usersEqual = user.equals(principalUser);

		return principalAdmin || usersEqual;
	}

	/**
	 * Check if the logged in user is an Admin
	 *
	 * @param principal
	 *            The logged in user to check
	 *
	 * @return if the user is an admin
	 */
	private boolean isAdmin(Principal principal) {
		logger.trace("Checking if user is admin");
		User readPrincipal = userService.getUserByUsername(principal.getName());
		return readPrincipal.getAuthorities().contains(Role.ROLE_ADMIN);
	}

	/**
	 * Generate a temporary password for a user
	 *
	 * @return A temporary password
	 */
	private static String generatePassword() {
		int PASSWORD_LENGTH = 32;
		int ALPHABET_SIZE = 26;
		int SINGLE_DIGIT_SIZE = 10;
		int RANDOM_LENGTH = PASSWORD_LENGTH - 3;

		List<Character> pwdArray = new ArrayList<>(PASSWORD_LENGTH);
		SecureRandom random = new SecureRandom();

		// 1. Create 1 random uppercase.
		pwdArray.add((char) ('A' + random.nextInt(ALPHABET_SIZE)));

		// 2. Create 1 random lowercase.
		pwdArray.add((char) ('a' + random.nextInt(ALPHABET_SIZE)));

		// 3. Create 1 random number.
		pwdArray.add((char) ('0' + random.nextInt(SINGLE_DIGIT_SIZE)));

		// 4. Create 5 random.
		int c = 'A';
		int rand = 0;
		for (int i = 0; i < RANDOM_LENGTH; i++) {
			rand = random.nextInt(3);
			switch (rand) {
			case 0:
				c = '0' + random.nextInt(SINGLE_DIGIT_SIZE);
				break;
			case 1:
				c = 'a' + random.nextInt(ALPHABET_SIZE);
				break;
			case 2:
				c = 'A' + random.nextInt(ALPHABET_SIZE);
				break;
			}
			pwdArray.add((char) c);
		}

		// 5. Shuffle.
		Collections.shuffle(pwdArray, random);

		// 6. Create string.
		Joiner joiner = Joiner.on("");
		return joiner.join(pwdArray);
	}

}
