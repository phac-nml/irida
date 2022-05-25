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
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailSendException;
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
import ca.corefacility.bioinformatics.irida.ria.web.PasswordResetController;
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
	private final PasswordResetService passwordResetService;
	private final EmailController emailController;
	private final MessageSource messageSource;
	private final PasswordEncoder passwordEncoder;

	@Autowired
	public UIUsersService(UserService userService, PasswordResetService passwordResetService,
			EmailController emailController, MessageSource messageSource, PasswordEncoder passwordEncoder) {
		this.userService = userService;
		this.passwordResetService = passwordResetService;
		this.emailController = emailController;
		this.messageSource = messageSource;
		this.passwordEncoder = passwordEncoder;
	}

	/**
	 * Get a paged listing of users for the administration user.  This can be filtered and sorted.
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
	 * @return The name of the user view
	 */
	public UserDetailsResponse createUser(UserCreateRequest userCreateRequest, Principal principal, Locale locale) {
		boolean mailFailure = false;
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
		boolean generateActivation = !Strings.isNullOrEmpty(userCreateRequest.getActivate());
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

			try {
				user = userService.create(user);

				// if the password isn't set, we'll generate a password reset
				PasswordReset passwordReset = null;
				if (generateActivation) {
					passwordReset = passwordResetService.create(new PasswordReset(user));
					logger.trace("Created password reset for activation");
				}

				User creator = userService.getUserByUsername(principal.getName());
				emailController.sendWelcomeEmail(user, creator, passwordReset);
			} catch (ConstraintViolationException | DataIntegrityViolationException | EntityExistsException ex) {
				errors = handleCreateUpdateException(ex, locale);
			} catch (final MailSendException e) {
				logger.error("Failed to send user activation e-mail.", e);
				mailFailure = true;
			}
		}

		return new UserDetailsResponse(mailFailure, errors);
	}

	/**
	 * Update a user status (if the user is enabled within IRIDA).
	 *
	 * @param id        - identifier for an {@link User}
	 * @param isEnabled - whether the user should be enabled.
	 * @param locale    - users {@link Locale}
	 * @return {@link ResponseEntity}
	 */
	public ResponseEntity<String> updateUserStatus(Long id, boolean isEnabled, Locale locale) {
		User user = userService.read(id);
		if (user.isEnabled() != isEnabled) {
			try {
				userService.updateFields(id, ImmutableMap.of("enabled", isEnabled));
				String key = isEnabled ? "server.AdminUsersService.enabled" : "server.AdminUsersService.disabled";
				return ResponseEntity.status(HttpStatus.OK)
						.body(messageSource.getMessage(key, new Object[] { user.getUsername() }, locale));
			} catch (EntityExistsException | EntityNotFoundException | ConstraintViolationException |
					 InvalidPropertyException e) {
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
						.body(messageSource.getMessage("server.AdminUsersService.error",
								new Object[] { user.getUsername() }, locale));
			}

		}
		// Should never hit here!
		return ResponseEntity.status(HttpStatus.ALREADY_REPORTED).body("");
	}

	/**
	 * Get the details for a specific user
	 *
	 * @param userId      - the id for the user to show details for
	 * @param mailFailure - if sending a user activation e-mail passed or failed
	 * @param principal   - the currently logged in user
	 * @return {@link UserDetailsResponse} that contains user details for a specific user
	 */
	public UserDetailsResponse getUser(Long userId, Boolean mailFailure, Principal principal) {
		User user = userService.read(userId);
		UserDetailsModel userDetails = new UserDetailsModel(user);
		User principalUser = userService.getUserByUsername(principal.getName());
		Locale locale = LocaleContextHolder.getLocale();
		boolean isAdmin = RoleUtilities.isAdmin(principalUser);
		boolean canEditUserInfo = canEditUserInfo(principalUser, user);
		boolean canEditUserStatus = canEditUserStatus(principalUser, user);
		boolean canChangePassword = canChangePassword(principalUser, user);
		boolean canCreatePasswordReset = PasswordResetController.canCreatePasswordReset(principalUser, user);

		String currentRoleName = messageSource.getMessage("systemRole." + user.getSystemRole().getName(), null, locale);

		return new UserDetailsResponse(userDetails, currentRoleName, mailFailure, isAdmin, canEditUserInfo,
				canEditUserStatus, canChangePassword, canCreatePasswordReset);
	}

	/**
	 * Submit a user edit
	 *
	 * @param userId          The id of the user to edit (required)
	 * @param userEditRequest a {@link UserEditRequest} containing details about a specific user
	 * @param principal       a reference to the logged in user
	 * @param request         the request
	 * @return The name of the user view
	 */
	public UserDetailsResponse updateUser(Long userId, UserEditRequest userEditRequest, Principal principal,
			HttpServletRequest request) {
		User principalUser = userService.getUserByUsername(principal.getName());
		Map<String, Object> updatedValues = new HashMap<>();
		Map<String, String> errors = new HashMap<>();

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
			if (!Strings.isNullOrEmpty(userEditRequest.getEnabled())) {
				updatedValues.put("enabled", userEditRequest.getEnabled());
			}

			if (!Strings.isNullOrEmpty(userEditRequest.getRole())) {
				Role newRole = Role.valueOf(userEditRequest.getRole());

				updatedValues.put("systemRole", newRole);
			}
		}

		if (errors.isEmpty()) {
			try {
				User user = userService.updateFields(userId, updatedValues);

				// If the user is updating their account make sure you update it in the session variable
				if (user != null && principal.getName().equals(user.getUsername())) {
					HttpSession session = request.getSession();
					session.setAttribute(UserSecurityInterceptor.CURRENT_USER_DETAILS, user);
				}
			} catch (ConstraintViolationException | DataIntegrityViolationException | PasswordReusedException ex) {
				errors = handleCreateUpdateException(ex, request.getLocale());
			}
		}

		return new UserDetailsResponse(errors);
	}

	/**
	 * Change the password of a user
	 *
	 * @param userId      The id of the user to edit (required)
	 * @param oldPassword The old password of the user for password change
	 * @param newPassword The new password of the user for password change
	 * @param principal   a reference to the logged in user
	 * @param request     the request
	 * @return The name of the user view
	 */
	public UserDetailsResponse changeUserPassword(Long userId, String oldPassword, String newPassword,
			Principal principal, HttpServletRequest request) {
		User principalUser = userService.getUserByUsername(principal.getName());
		Map<String, Object> updatedValues = new HashMap<>();
		Map<String, String> errors = new HashMap<>();

		if (!Strings.isNullOrEmpty(oldPassword) || !Strings.isNullOrEmpty(newPassword)) {
			if (!passwordEncoder.matches(oldPassword, principalUser.getPassword())) {
				errors.put("oldPassword",
						messageSource.getMessage("server.user.edit.password.old.incorrect", null, request.getLocale()));
			} else {
				updatedValues.put("password", newPassword);
			}
		}

		if (errors.isEmpty()) {
			try {
				User user = userService.updateFields(userId, updatedValues);

				// If the user is updating their account make sure you update it in the session variable
				if (user != null && principal.getName().equals(user.getUsername())) {
					HttpSession session = request.getSession();
					session.setAttribute(UserSecurityInterceptor.CURRENT_USER_DETAILS, user);
				}
			} catch (ConstraintViolationException | DataIntegrityViolationException | PasswordReusedException ex) {
				errors = handleCreateUpdateException(ex, request.getLocale());
			}
		}

		return new UserDetailsResponse(errors);
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
			errors.put("password", messageSource.getMessage("server.user.edit.passwordReused", null, locale));
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
	 * Check if the logged in user is allowed to change their password.
	 *
	 * @param principalUser - the currently logged in principal
	 * @param user          - the user to edit
	 * @return boolean if the principal can change their password
	 */
	private boolean canChangePassword(User principalUser, User user) {
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

}
