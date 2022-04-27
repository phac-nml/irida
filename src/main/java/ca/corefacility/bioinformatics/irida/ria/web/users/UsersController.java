package ca.corefacility.bioinformatics.irida.ria.web.users;

import java.security.Principal;
import java.security.SecureRandom;
import java.util.*;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.mail.MailSendException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import ca.corefacility.bioinformatics.irida.config.services.IridaApiServicesConfig;
import ca.corefacility.bioinformatics.irida.exceptions.EntityExistsException;
import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.exceptions.PasswordReusedException;
import ca.corefacility.bioinformatics.irida.model.user.PasswordReset;
import ca.corefacility.bioinformatics.irida.model.user.Role;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.service.EmailController;
import ca.corefacility.bioinformatics.irida.service.user.PasswordResetService;
import ca.corefacility.bioinformatics.irida.service.user.UserService;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;

/**
 * Controller for all {@link User} related views
 */
@Controller
@RequestMapping(value = "/users")
public class UsersController {
	private static final String USERS_PAGE = "user/list";
	private static final String SPECIFIC_USER_PAGE = "user/account";
	private static final String CREATE_USER_PAGE = "user/create";
	private static final Logger logger = LoggerFactory.getLogger(UsersController.class);

	private final List<Locale> locales;

	private final UserService userService;
	private final PasswordResetService passwordResetService;
	private final EmailController emailController;

	private final List<Role> adminAllowedRoles = Lists.newArrayList(Role.ROLE_ADMIN, Role.ROLE_MANAGER, Role.ROLE_USER,
			Role.ROLE_TECHNICIAN, Role.ROLE_SEQUENCER);

	private final MessageSource messageSource;

	@Autowired
	public UsersController(UserService userService, PasswordResetService passwordResetService,
			EmailController emailController, MessageSource messageSource,
			IridaApiServicesConfig.IridaLocaleList locales) {
		this.userService = userService;
		this.passwordResetService = passwordResetService;
		this.emailController = emailController;
		this.messageSource = messageSource;
		this.locales = locales.getLocales();
	}

	/**
	 * Request for the page to display a list of all projects available to the currently logged in user.
	 *
	 * @return The name of the page.
	 */
	@RequestMapping
	@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MANAGER')")
	public String getUsersPage() {
		return USERS_PAGE;
	}

	/**
	 * Request for a specific user details page.
	 *
	 * @return The name of the user account page
	 */
	@RequestMapping({ "/{userId}", "/{userId}/*" })
	@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MANAGER') or principal.id == #userId")
	public String getUserDetailsPage(@PathVariable Long userId) {
		return SPECIFIC_USER_PAGE;
	}

	/**
	 * Get the currently logged in user's page
	 *
	 * @param principal a reference to the logged in user.
	 * @return getUserSpecificPage for the currently logged in user
	 */
	@RequestMapping({ "/current" })
	public String getLoggedInUserPage(Principal principal) {
		User readPrincipal = userService.getUserByUsername(principal.getName());
		Long id = readPrincipal.getId();
		return "redirect:/users/" + id;
	}

	/**
	 * Get the user creation view
	 *
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
		if (!user.getPassword().equals(confirmPassword)) {
			errors.put("password", messageSource.getMessage("server.user.edit.password.match", null, locale));
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
	 *
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
	 * @param ex     an exception to handle
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
				errors.put("email", messageSource.getMessage("server.user.edit.emailConflict", null, locale));
			}
		} else if (ex instanceof EntityExistsException) {
			EntityExistsException eex = (EntityExistsException) ex;
			errors.put(eex.getFieldName(), eex.getMessage());
		} else if (ex instanceof PasswordReusedException) {
			errors.put("password", messageSource.getMessage("server.user.edit.passwordReused", null, locale));
		}

		return errors;
	}

	/**
	 * Check if the logged in user is an Admin
	 *
	 * @param principal The logged in user to check
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
