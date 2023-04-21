package ca.corefacility.bioinformatics.irida.ria.web.services;

import java.security.Principal;
import java.security.SecureRandom;
import java.util.*;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.mail.MailSendException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import ca.corefacility.bioinformatics.irida.exceptions.EntityExistsException;
import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.exceptions.InvalidPropertyException;
import ca.corefacility.bioinformatics.irida.exceptions.PasswordReusedException;
import ca.corefacility.bioinformatics.irida.model.user.PasswordReset;
import ca.corefacility.bioinformatics.irida.model.user.Role;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.repositories.specification.UserSpecification;
import ca.corefacility.bioinformatics.irida.ria.config.UserSecurityInterceptor;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.ajax.AjaxCreateItemSuccessResponse;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.ajax.AjaxSuccessResponse;
import ca.corefacility.bioinformatics.irida.ria.web.exceptions.UIEmailSendException;
import ca.corefacility.bioinformatics.irida.ria.web.exceptions.UIUserFormException;
import ca.corefacility.bioinformatics.irida.ria.web.exceptions.UIUserStatusException;
import ca.corefacility.bioinformatics.irida.ria.web.models.tables.TableResponse;
import ca.corefacility.bioinformatics.irida.ria.web.users.dto.*;
import ca.corefacility.bioinformatics.irida.ria.web.utilities.RoleUtilities;
import ca.corefacility.bioinformatics.irida.service.EmailController;
import ca.corefacility.bioinformatics.irida.service.user.PasswordResetService;
import ca.corefacility.bioinformatics.irida.service.user.UserService;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;

/**
 * Handles service call for the administration of the IRIDA users.
 */
@Component
public class UIUsersService {
	private static final Logger logger = LoggerFactory.getLogger(UIUsersService.class);
	private final UserService userService;
	private final EmailController emailController;
	private final MessageSource messageSource;
	private final PasswordEncoder passwordEncoder;
	private final PasswordResetService passwordResetService;

	@Autowired
	public UIUsersService(UserService userService, EmailController emailController, MessageSource messageSource,
			PasswordEncoder passwordEncoder, PasswordResetService passwordResetService) {
		this.userService = userService;
		this.emailController = emailController;
		this.messageSource = messageSource;
		this.passwordEncoder = passwordEncoder;
		this.passwordResetService = passwordResetService;
	}

	/**
	 * Get a paged listing of users for the administration user. This can be filtered and sorted.
	 *
	 * @param request - the information about the current page of users to return
	 * @return {@link TableResponse}
	 */
	public TableResponse<UserDetailsModel> getUsersPagedList(AdminUsersTableRequest request) {
		Specification<User> specification = UserSpecification.searchUser(request.getSearch());
		PageRequest pageRequest = PageRequest.of(request.getCurrent(), request.getPageSize(), request.getSort());
		Page<User> userPage = userService.search(specification, pageRequest);

		List<UserDetailsModel> users = userPage.getContent()
				.stream()
				.map(UserDetailsModel::new)
				.collect(Collectors.toList());

		return new TableResponse<>(users, userPage.getTotalElements());
	}

	/**
	 * Submit a new user
	 *
	 * @param userCreateRequest a {@link UserCreateRequest} containing details about a specific user
	 * @param principal         a reference to the logged in user
	 * @param locale            The logged in user's request locale
	 * @return The id of the new user
	 * @throws UIEmailSendException if there is an error emailing the password reset
	 * @throws UIUserFormException  if there are errors creating the new user
	 */
	public AjaxCreateItemSuccessResponse createUser(UserCreateRequest userCreateRequest, Principal principal,
			Locale locale) throws UIEmailSendException, UIUserFormException {
		Map<String, String> errors = new HashMap<>();

		User user = new User();
		if (!Strings.isNullOrEmpty(userCreateRequest.getUsername())) {
			user.setUsername(userCreateRequest.getUsername());
		}

		if (!Strings.isNullOrEmpty(userCreateRequest.getFirstName())) {
			user.setFirstName(userCreateRequest.getFirstName());
		}

		if (!Strings.isNullOrEmpty(userCreateRequest.getLastName())) {
			user.setLastName(userCreateRequest.getLastName());
		}

		if (!Strings.isNullOrEmpty(userCreateRequest.getEmail())) {
			user.setEmail(userCreateRequest.getEmail());
		}

		if (!Strings.isNullOrEmpty(userCreateRequest.getPhoneNumber())) {
			user.setPhoneNumber(userCreateRequest.getPhoneNumber());
		} else {
			user.setPhoneNumber(null);
		}

		if (!Strings.isNullOrEmpty(userCreateRequest.getLocale())) {
			user.setLocale(userCreateRequest.getLocale());
		}

		// Check if we need to generate a password
		boolean generateActivation = userCreateRequest.getActivate();
		if (generateActivation) {
			user.setPassword(generatePassword());
			user.setCredentialsNonExpired(false);
		} else {
			if (!Strings.isNullOrEmpty(userCreateRequest.getPassword())) {
				user.setPassword(userCreateRequest.getPassword());
			} else {
				errors.put("password", messageSource.getMessage("server.user.edit.password.empty", null, locale));
			}
		}

		if (errors.isEmpty()) {
			if (!Strings.isNullOrEmpty(userCreateRequest.getRole()) && isAdmin(principal)) {
				user.setSystemRole(Role.valueOf(userCreateRequest.getRole()));
			} else {
				user.setSystemRole(Role.ROLE_USER);
			}

			PasswordReset passwordReset = null;
			try {
				user = userService.create(user);

				// Generate a password reset
				if (generateActivation) {
					logger.trace("Created password reset for activation");
					passwordReset = passwordResetService.create(new PasswordReset(user));
				}

				//Send welcome email
				if (emailController.isMailConfigured()) {
					User creator = userService.getUserByUsername(principal.getName());
					emailController.sendWelcomeEmail(user, creator, passwordReset);
				}
			} catch (ConstraintViolationException | DataIntegrityViolationException | EntityExistsException ex) {
				errors = handleCreateUpdateException(ex, locale);
				throw new UIUserFormException(errors);
			} catch (final MailSendException e) {
				//Undo user creation if activation email fails, so the user can try again with setting the password manually
				if (generateActivation) {
					logger.error("Failed to send user activation e-mail.", e);
					passwordResetService.delete(passwordReset.getId());
					userService.delete(user.getId());
					throw new UIEmailSendException(
							messageSource.getMessage("server.password.reset.error.message", null, locale));
				}
			}
		} else {
			throw new UIUserFormException(errors);
		}

		return new AjaxCreateItemSuccessResponse(user.getId());
	}

	/**
	 * Update a user status (if the user is enabled within IRIDA).
	 *
	 * @param id        - identifier for an {@link User}
	 * @param isEnabled - whether the user should be enabled.
	 * @param locale    - users {@link Locale}
	 * @return a success message
	 * @throws UIUserStatusException if there is an error updating the user status
	 */
	public AjaxSuccessResponse updateUserStatus(Long id, boolean isEnabled, Locale locale)
			throws UIUserStatusException {
		User user = userService.read(id);

		try {
			userService.updateFields(id, ImmutableMap.of("enabled", isEnabled));
			String key = isEnabled ? "server.AdminUsersService.enabled" : "server.AdminUsersService.disabled";
			return new AjaxSuccessResponse(messageSource.getMessage(key, new Object[] { user.getUsername() }, locale));
		} catch (EntityExistsException | EntityNotFoundException | ConstraintViolationException
				| InvalidPropertyException e) {
			throw new UIUserStatusException(messageSource.getMessage("server.AdminUsersService.error",
					new Object[] { user.getUsername() }, locale));
		}
	}

	/**
	 * Get the details for a specific user
	 *
	 * @param userId    - the id for the user to show details for
	 * @param principal - the currently logged in user
	 * @return {@link UserDetailsResponse} that contains user details for a specific user
	 */
	public UserDetailsResponse getUser(Long userId, Principal principal) {
		User user = userService.read(userId);
		UserDetailsModel userDetails = new UserDetailsModel(user);
		User principalUser = userService.getUserByUsername(principal.getName());
		boolean isAdmin = RoleUtilities.isAdmin(principalUser);
		boolean canEditUserInfo = canEditUserInfo(principalUser, user);
		boolean canEditUserStatus = canEditUserStatus(principalUser, user);
		boolean isOwnAccount = isOwnAccount(principalUser, user);
		boolean canCreatePasswordReset = canCreatePasswordReset(principalUser, user);

		return new UserDetailsResponse(userDetails, isAdmin, canEditUserInfo, canEditUserStatus, isOwnAccount,
				canCreatePasswordReset);
	}

	/**
	 * Submit a user edit
	 *
	 * @param userId          The id of the user to edit (required)
	 * @param userEditRequest a {@link UserEditRequest} containing details about a specific user
	 * @param principal       a reference to the logged in user
	 * @param request         the request
	 * @param locale          logged in users {@link Locale}
	 * @return a success message
	 * @throws UIUserFormException if there are errors updating the user
	 */
	public AjaxSuccessResponse updateUser(Long userId, UserEditRequest userEditRequest, Principal principal,
			HttpServletRequest request, Locale locale) throws UIUserFormException {
		User principalUser = userService.getUserByUsername(principal.getName());
		Map<String, Object> updatedValues = new HashMap<>();

		if (!Strings.isNullOrEmpty(userEditRequest.getFirstName())) {
			updatedValues.put("firstName", userEditRequest.getFirstName());
		}

		if (!Strings.isNullOrEmpty(userEditRequest.getLastName())) {
			updatedValues.put("lastName", userEditRequest.getLastName());
		}

		if (!Strings.isNullOrEmpty(userEditRequest.getEmail())) {
			updatedValues.put("email", userEditRequest.getEmail());
		}

		if (!Strings.isNullOrEmpty(userEditRequest.getPhoneNumber())) {
			updatedValues.put("phoneNumber", userEditRequest.getPhoneNumber());
		} else {
			updatedValues.put("phoneNumber", null);
		}

		if (!Strings.isNullOrEmpty(userEditRequest.getLocale())) {
			updatedValues.put("locale", userEditRequest.getLocale());
		}

		if (RoleUtilities.isAdmin(principalUser)) {
			updatedValues.put("enabled", userEditRequest.getEnabled());

			if (!Strings.isNullOrEmpty(userEditRequest.getRole())) {
				Role newRole = Role.valueOf(userEditRequest.getRole());
				updatedValues.put("systemRole", newRole);
			}
		}
		updateUser(userId, principal, request, updatedValues);

		return new AjaxSuccessResponse(messageSource.getMessage("server.user.edit.success", null, locale));
	}

	/**
	 * Change the password of a user
	 *
	 * @param userId      The id of the user to edit (required)
	 * @param oldPassword The old password of the user for password change
	 * @param newPassword The new password of the user for password change
	 * @param principal   a reference to the logged in user
	 * @param request     the request
	 * @param locale      logged in users {@link Locale}
	 * @return a success message
	 * @throws UIUserFormException if there is an error changing the password
	 */
	public AjaxSuccessResponse changeUserPassword(Long userId, String oldPassword, String newPassword,
			Principal principal, HttpServletRequest request, Locale locale) throws UIUserFormException {
		User user = userService.read(userId);
		User principalUser = userService.getUserByUsername(principal.getName());
		boolean principalAdmin = principalUser.getAuthorities().contains(Role.ROLE_ADMIN);
		boolean usersEqual = user.equals(principalUser);
		Map<String, Object> updatedValues = new HashMap<>();
		Map<String, String> errors = new HashMap<>();

		if (usersEqual) {
			//check both oldPassword & newPassword exist if a user is updating their own password
			if (Strings.isNullOrEmpty(oldPassword)) {
				errors.put("oldPassword",
						messageSource.getMessage("server.user.edit.password.old.required", null, request.getLocale()));
			} else if (Strings.isNullOrEmpty(newPassword)) {
				errors.put("newPassword",
						messageSource.getMessage("server.user.edit.password.new.required", null, request.getLocale()));
			} else {
				if (!passwordEncoder.matches(oldPassword, principalUser.getPassword())) {
					errors.put("oldPassword", messageSource.getMessage("server.user.edit.password.old.incorrect", null,
							request.getLocale()));
				} else {
					updatedValues.put("password", newPassword);
				}
			}
		} else {
			//only check newPassword exists if an admin is updating another user's password
			if (principalAdmin) {
				if (Strings.isNullOrEmpty(newPassword)) {
					errors.put("newPassword", messageSource.getMessage("server.user.edit.password.new.required", null,
							request.getLocale()));
				} else {
					updatedValues.put("password", newPassword);
				}
			}
		}

		if (errors.isEmpty()) {
			updateUser(userId, principal, request, updatedValues);
		} else {
			try {
				User updatedUser = userService.updateFields(userId, updatedValues);

				// If the user is updating their account make sure you update it in the session variable
				if (updatedUser != null && usersEqual) {
					HttpSession session = request.getSession();
					session.setAttribute(UserSecurityInterceptor.CURRENT_USER_DETAILS, (UserDetails) updatedUser);
				}
			} catch (ConstraintViolationException | DataIntegrityViolationException | PasswordReusedException ex) {
				errors = handleCreateUpdateException(ex, request.getLocale());
			}
			throw new UIUserFormException(errors);
		}

		return new AjaxSuccessResponse(messageSource.getMessage("server.user.edit.password.success", null, locale));
	}

	/**
	 * Create a new {@link PasswordReset} for the given {@link User}
	 *
	 * @param userId    The ID of the {@link User}
	 * @param principal a reference to the logged in user.
	 * @param locale    a reference to the locale specified by the browser.
	 * @return text to display to the user about the result of creating a password reset.
	 * @throws UIEmailSendException if there is an error emailing the password reset.
	 */
	public AjaxSuccessResponse adminNewPasswordReset(Long userId, Principal principal, Locale locale)
			throws UIEmailSendException {
		User user = userService.read(userId);
		User principalUser = userService.getUserByUsername(principal.getName());

		if (canCreatePasswordReset(principalUser, user)) {
			try {
				createNewPasswordReset(user);
			} catch (final MailSendException e) {
				throw new UIEmailSendException(
						messageSource.getMessage("server.password.reset.error.message", null, locale));
			}
		} else {
			throw new UIEmailSendException(
					messageSource.getMessage("server.password.reset.error.message", null, locale));
		}

		return new AjaxSuccessResponse(messageSource.getMessage("server.password.reset.success.message",
				new Object[] { user.getFirstName() }, locale));
	}

	/**
	 * Create a new password reset for a given {@link User} and send a reset password link via email
	 *
	 * @param user The user to create the reset for
	 */
	private void createNewPasswordReset(User user) {
		PasswordReset passwordReset = new PasswordReset(user);
		passwordResetService.create(passwordReset);

		// send a reset password link to user via email
		emailController.sendPasswordResetLinkEmail(user, passwordReset);
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
			Set<ConstraintViolation<?>> constraintViolations = cvx.getConstraintViolations();

			for (ConstraintViolation<?> violation : constraintViolations) {
				String errorKey = violation.getPropertyPath().toString();
				errors.put(errorKey, violation.getMessage());
			}
		} else if (ex instanceof DataIntegrityViolationException) {
			DataIntegrityViolationException divx = (DataIntegrityViolationException) ex;
			if (divx.getMessage().contains(User.USER_EMAIL_CONSTRAINT_NAME)) {
				errors.put("email", messageSource.getMessage("server.user.edit.emailConflict", null, locale));
			}
		} else if (ex instanceof EntityExistsException) {
			EntityExistsException eex = (EntityExistsException) ex;
			errors.put(eex.getFieldName(), eex.getMessage());
		} else if (ex instanceof PasswordReusedException) {
			errors.put("newPassword", messageSource.getMessage("server.user.edit.passwordReused", null, locale));
		}

		return errors;
	}

	/**
	 * Check if the logged in user is allowed to edit user information for the given user.
	 *
	 * @param principalUser - the currently logged in principal
	 * @param user          - the user to edit
	 * @return boolean if the principal can edit the user information
	 */
	private boolean canEditUserInfo(User principalUser, User user) {
		boolean principalAdmin = principalUser.getAuthorities().contains(Role.ROLE_ADMIN);
		boolean usersEqual = user.equals(principalUser);

		return principalAdmin || usersEqual;
	}

	/**
	 * Check if the logged in user is allowed to edit user status for the given user.
	 *
	 * @param principalUser - the currently logged in principal
	 * @param user          - the user to edit
	 * @return boolean if the principal can edit the user status
	 */
	private boolean canEditUserStatus(User principalUser, User user) {
		boolean principalAdmin = principalUser.getAuthorities().contains(Role.ROLE_ADMIN);
		boolean usersEqual = user.equals(principalUser);

		return !(principalAdmin && usersEqual);
	}

	/**
	 * Check if the logged in user is modifying their own account.
	 *
	 * @param principalUser - the currently logged in principal
	 * @param user          - the user to edit
	 * @return boolean if the principal can change their password
	 */
	private boolean isOwnAccount(User principalUser, User user) {
		boolean usersEqual = user.equals(principalUser);
		return usersEqual;
	}

	/**
	 * Check if the logged in user is an Admin
	 *
	 * @param principal The logged in user to check
	 * @return if the user is an admin
	 */
	private boolean isAdmin(Principal principal) {
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

	/**
	 * Test if a user should be able to click the password reset button
	 *
	 * @param principalUser The currently logged in principal
	 * @param user          The user being edited
	 * @return true if the principal can create a password reset for the user
	 */
	private boolean canCreatePasswordReset(User principalUser, User user) {
		Role userRole = user.getSystemRole();
		Role principalRole = principalUser.getSystemRole();

		if (principalUser.equals(user)) {
			return false;
		} else if (principalRole.equals(Role.ROLE_ADMIN)) {
			return true;
		} else if (principalRole.equals(Role.ROLE_MANAGER)) {
			return !userRole.equals(Role.ROLE_ADMIN);
		}

		return false;
	}

	/**
	 * Update the {@link User} and in the session variable
	 *
	 * @param userId        The id of the user to edit (required)
	 * @param principal     a reference to the logged in user
	 * @param request       the request
	 * @param updatedValues the user values to be updated
	 * @throws UIUserFormException if there is an error emailing the password reset.
	 */
	private void updateUser(Long userId, Principal principal, HttpServletRequest request,
			Map<String, Object> updatedValues) throws UIUserFormException {
		Map<String, String> errors;
		try {
			userService.updateFields(userId, updatedValues);
		} catch (ConstraintViolationException | DataIntegrityViolationException | PasswordReusedException ex) {
			errors = handleCreateUpdateException(ex, request.getLocale());
			throw new UIUserFormException(errors);
		}
	}

}
