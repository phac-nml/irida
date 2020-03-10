package ca.corefacility.bioinformatics.irida.ria.web.users;

import java.security.Principal;
import java.security.SecureRandom;
import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.mail.MailSendException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import ca.corefacility.bioinformatics.irida.config.services.IridaApiServicesConfig;
import ca.corefacility.bioinformatics.irida.exceptions.EntityExistsException;
import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.exceptions.PasswordReusedException;
import ca.corefacility.bioinformatics.irida.model.enums.ProjectRole;
import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectUserJoin;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.user.PasswordReset;
import ca.corefacility.bioinformatics.irida.model.user.Role;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.ria.config.UserSecurityInterceptor;
import ca.corefacility.bioinformatics.irida.ria.web.PasswordResetController;
import ca.corefacility.bioinformatics.irida.ria.web.components.datatables.DataTablesParams;
import ca.corefacility.bioinformatics.irida.ria.web.components.datatables.DataTablesResponse;
import ca.corefacility.bioinformatics.irida.service.EmailController;
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
	private static final String ROLE_MESSAGE_PREFIX = "systemrole.";
	private static final Logger logger = LoggerFactory.getLogger(UsersController.class);

	private final List<Locale> locales;

	private final UserService userService;
	private final ProjectService projectService;
	private final PasswordResetService passwordResetService;
	private final EmailController emailController;

	private final List<Role> adminAllowedRoles = Lists.newArrayList(Role.ROLE_ADMIN, Role.ROLE_MANAGER, Role.ROLE_USER,
			Role.ROLE_TECHNICIAN, Role.ROLE_SEQUENCER);

	private final MessageSource messageSource;

	@Autowired
	public UsersController(UserService userService, ProjectService projectService,
			PasswordResetService passwordResetService, EmailController emailController, MessageSource messageSource,
			IridaApiServicesConfig.IridaLocaleList locales) {
		this.userService = userService;
		this.projectService = projectService;
		this.passwordResetService = passwordResetService;
		this.emailController = emailController;
		this.messageSource = messageSource;
		this.locales = locales.getLocales();
	}

	/**
	 * Request for the page to display a list of all projects available to the
	 * currently logged in user.
	 *
	 * @return The name of the page.
	 */
	@RequestMapping
	@PreAuthorize("hasRole('ROLE_ADMIN')")
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
	 * @param mailFailure
	 * 			  if sending a user activation e-mail passed or failed
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
			map.put("isManager", pujoin.getProjectRole().equals(ProjectRole.PROJECT_OWNER));
			map.put("subscribed" , pujoin.isEmailSubscription());

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
	 * @param userId          The id of the user to edit (required)
	 * @param firstName       The firstname to update
	 * @param lastName        the lastname to update
	 * @param email           the email to update
	 * @param phoneNumber     the phone number to update
	 * @param systemRole      the role to update
	 * @param userLocale      The locale the user selected
	 * @param password        the password to update
	 * @param confirmPassword password confirmation
	 * @param model           The model to work on
	 * @param enabled         whether the user account should be enabled or disabled.
	 * @param principal       a reference to the logged in user.
	 * @param request         the request
	 * @return The name of the user view
	 */
	@RequestMapping(value = "/{userId}/edit", method = RequestMethod.POST)
	public String updateUser(@PathVariable Long userId, @RequestParam(required = false) String firstName,
			@RequestParam(required = false) String lastName, @RequestParam(required = false) String email,
			@RequestParam(required = false) String phoneNumber, @RequestParam(required = false) String systemRole,
			@RequestParam(required = false, name = "locale") String userLocale,
			@RequestParam(required = false) String password, @RequestParam(required = false) String enabled,
			@RequestParam(required = false) String confirmPassword, Model model, Principal principal,
			HttpServletRequest request) {
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

		if (!Strings.isNullOrEmpty(phoneNumber)) {
			updatedValues.put("phoneNumber", phoneNumber);
		}

		if (!Strings.isNullOrEmpty(userLocale)) {
			updatedValues.put("locale", userLocale);
		}

		if (!Strings.isNullOrEmpty(password) || !Strings.isNullOrEmpty(confirmPassword)) {
			if (!password.equals(confirmPassword)) {
				errors.put("password", messageSource.getMessage("user.edit.password.match", null, request.getLocale()));
			} else {
				updatedValues.put("password", password);
			}
		}

		if (isAdmin(principal)) {
			logger.debug("User is admin");
			updatedValues.put("enabled", !Strings.isNullOrEmpty(enabled));

			if (!Strings.isNullOrEmpty(systemRole)) {
				Role newRole = Role.valueOf(systemRole);

				updatedValues.put("systemRole", newRole);
			}
		}

		String returnView;
		if (errors.isEmpty()) {
			try {
				User user = userService.updateFields(userId, updatedValues);
				returnView = "redirect:/users/" + userId;

				// If the user is updating their account make sure you update it in the sesion variable
				// this will update the users gravatar!
				if (user != null && principal.getName()
						.equals(user.getUsername())) {
					HttpSession session = request.getSession();
					session.setAttribute(UserSecurityInterceptor.CURRENT_USER_DETAILS, user);
				}

			} catch (ConstraintViolationException | DataIntegrityViolationException | PasswordReusedException ex) {
				errors = handleCreateUpdateException(ex, request.getLocale());

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

		model.addAttribute("locales", locales);

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

	/**
	 * Get the user creation view
	 * @param model Model for the view
	 * @return user creation view
	 */
	@RequestMapping(value = "/create", method = RequestMethod.GET)
	@PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_MANAGER')")
	public String createUserPage(Model model) {

		Locale locale = LocaleContextHolder.getLocale();

		model.addAttribute("locales", locales);

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
	 * @param user              User to create as a motel attribute
	 * @param systemRole        The system role to give to the user
	 * @param confirmPassword   Password confirmation
	 * @param requireActivation Checkbox whether the user account needs to be activated
	 * @param model             Model for the view
	 * @param principal         The user creating the object
	 * @param locale            The logged in user's request locale
	 * @return A redirect to the user details view
	 */
	@RequestMapping(value = "/create", method = RequestMethod.POST)
	@PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_MANAGER')")
	public String submitCreateUser(@ModelAttribute User user, @RequestParam String systemRole,
			@RequestParam String confirmPassword, @RequestParam(required = false) String requireActivation, Model model,
			Principal principal, Locale locale) {

		Map<String, String> errors = new HashMap<>();

		String returnView = null;

		User creator = userService.getUserByUsername(principal.getName());

		// check if we need to generate a password
		boolean generateActivation = !Strings.isNullOrEmpty(requireActivation);
		if (generateActivation) {
			user.setPassword(generatePassword());
			confirmPassword = user.getPassword();
			user.setCredentialsNonExpired(false);
		}

		// check validity of password
		if (!user.getPassword()
				.equals(confirmPassword)) {
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
			model.addAttribute("given_role", user.getSystemRole());
			model.addAttribute("given_requireActivation", generateActivation);

			returnView = createUserPage(model);
		}

		return returnView;
	}

	/**
	 * Get a list of users based on search criteria.
	 *
	 * @param params {@link DataTablesParams} for the current Users DataTables.
	 * @param locale {@link Locale}
	 * @return {@link DataTablesResponse} of the filtered users list.
	 */
	//	@RequestMapping(value = "/ajax/list", produces = MediaType.APPLICATION_JSON_VALUE)
	//	public @ResponseBody
	//	DataTablesResponse getAjaxUserList(@DataTablesRequest DataTablesParams params, Locale locale) {
	//
	//		Page<User> userPage = userService.search(UserSpecification.searchUser(params.getSearchValue()),
	//				PageRequest.of(params.getCurrentPage(), params.getLength(), params.getSort()));
	//
	//		List<DataTablesResponseModel> usersData = new ArrayList<>();
	//		for (User user : userPage) {
	//			// getting internationalized system role from the message source
	//			String roleMessageName = "systemrole." + user.getSystemRole()
	//					.getName();
	//			String systemRole = messageSource.getMessage(roleMessageName, null, locale);
	//
	//			usersData.add(new DTUser(user.getId(), user.getUsername(), user.getFirstName(), user.getLastName(),
	//					user.getEmail(), systemRole, user.getCreatedDate(), user.getModifiedDate(), user.getLastLogin()));
	//		}
	//
	//		return new DataTablesResponse(params, userPage, usersData);
	//	}

	/**
	 * Check that username not already taken
	 *
	 * @param username Username to check existence of
	 * @return true if username not taken
	 */
	@RequestMapping(value = "/validate-username", method = RequestMethod.GET)
	@ResponseBody
	public Boolean usernameExists(@RequestParam String username) {
		try {
			userService.getUserByUsername(username);
			return false;
		} catch (UsernameNotFoundException e) {
			return true;
		}
	}

	/**
	 * Check that email not already taken
	 * @param email Email address to check existence of
	 * @return true if email not taken
	 */
	@RequestMapping(value = "/validate-email", method = RequestMethod.GET)
	@ResponseBody
	public Boolean emailExists(@RequestParam String email) {
		try {
			userService.loadUserByEmail(email);
			return false;
		} catch (EntityNotFoundException e) {
			return true;
		}
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
		else if(ex instanceof PasswordReusedException){
			errors.put("password", messageSource.getMessage("user.edit.passwordReused", null, locale));
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
	 * @param principalUser
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
		String SPECIAL_CHARS = "!@#$%^&*()+?/<>=.\\{}";

		List<Character> pwdArray = new ArrayList<>(PASSWORD_LENGTH);
		SecureRandom random = new SecureRandom();

		// 1. Create 1 random uppercase.
		pwdArray.add((char) ('A' + random.nextInt(ALPHABET_SIZE)));

		// 2. Create 1 random lowercase.
		pwdArray.add((char) ('a' + random.nextInt(ALPHABET_SIZE)));

		// 3. Create 1 random number.
		pwdArray.add((char) ('0' + random.nextInt(SINGLE_DIGIT_SIZE)));

		// 4. Add 1 special character
		pwdArray.add(SPECIAL_CHARS.charAt(random.nextInt(SPECIAL_CHARS.length())));

		// 5. Create 5 random.
		int c = 'A';
		int rand;
		for (int i = 0; i < RANDOM_LENGTH; i++) {
			rand = random.nextInt(4);
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
			case 3:
				c = SPECIAL_CHARS.charAt(random.nextInt(SPECIAL_CHARS.length()));
				break;
			}
			pwdArray.add((char) c);
		}

		// 6. Shuffle.
		Collections.shuffle(pwdArray, random);

		// 7. Create string.
		Joiner joiner = Joiner.on("");
		return joiner.join(pwdArray);
	}

}
