package ca.corefacility.bioinformatics.irida.ria.web.services;

import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import ca.corefacility.bioinformatics.irida.config.services.IridaApiServicesConfig;
import ca.corefacility.bioinformatics.irida.exceptions.EntityExistsException;
import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.exceptions.InvalidPropertyException;
import ca.corefacility.bioinformatics.irida.exceptions.PasswordReusedException;
import ca.corefacility.bioinformatics.irida.model.user.Role;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.repositories.specification.UserSpecification;
import ca.corefacility.bioinformatics.irida.ria.web.PasswordResetController;
import ca.corefacility.bioinformatics.irida.ria.web.models.tables.TableResponse;
import ca.corefacility.bioinformatics.irida.ria.web.users.dto.AdminUsersTableRequest;
import ca.corefacility.bioinformatics.irida.ria.web.users.dto.UserDetailsModel;
import ca.corefacility.bioinformatics.irida.ria.web.users.dto.UserDetailsResponse;
import ca.corefacility.bioinformatics.irida.service.EmailController;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.user.UserService;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;

/**
 * Handles service call for the the administration of the IRIDA users.
 */
@Component
@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MANAGER')")
public class UIUsersService {
	private final UserService userService;
	private final ProjectService projectService;
	private final EmailController emailController;
	private final MessageSource messageSource;
	private final List<Locale> locales;

	private final List<Role> adminAllowedRoles = Lists.newArrayList(Role.ROLE_ADMIN, Role.ROLE_MANAGER, Role.ROLE_USER,
			Role.ROLE_TECHNICIAN, Role.ROLE_SEQUENCER);

	public UIUsersService(UserService userService, ProjectService projectService, EmailController emailController,
			IridaApiServicesConfig.IridaLocaleList locales, MessageSource messageSource) {
		this.userService = userService;
		this.projectService = projectService;
		this.emailController = emailController;
		this.locales = locales.getLocales();
		this.messageSource = messageSource;
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
			} catch (EntityExistsException | EntityNotFoundException | ConstraintViolationException | InvalidPropertyException e) {
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
						.body(messageSource.getMessage("server.AdminUsersService.error", new Object[] { user.getUsername() }, locale));
			}

		}
		// Should never hit here!
		return ResponseEntity.status(HttpStatus.ALREADY_REPORTED)
				.body("");
	}

	/**
	 * Get the details for a specific user
	 *
	 * @param userId      - the id for the user to show details for
	 * @param mailFailure - if sending a user activation e-mail passed or failed
	 * @param principal   - the currently logged in user
	 * @return user details for a specific user
	 */
	public UserDetailsResponse getUser(Long userId, Boolean mailFailure, Principal principal) {
		UserDetailsResponse response = new UserDetailsResponse();
		User user = userService.read(userId);
		UserDetailsModel userDetails = new UserDetailsModel(user);
		response.setUser(userDetails);
		response.setMailFailure(mailFailure);

		User principalUser = userService.getUserByUsername(principal.getName());

		Locale locale = LocaleContextHolder.getLocale();

		// check if we should show an edit button
		boolean canEditUser = canEditUser(principalUser, user);
		response.setCanEditUser(canEditUser);
		response.setMailConfigured(emailController.isMailConfigured());

		response.setCanCreatePasswordReset(PasswordResetController.canCreatePasswordReset(principalUser, user));

		Map<String, String> localeNames = new HashMap<>();
		for (Locale aLocale : locales) {
			localeNames.put(aLocale.getLanguage(), aLocale.getDisplayName());
		}
		response.setLocales(localeNames);

		Map<String, String> roleNames = new HashMap<>();
		for (Role aRole : adminAllowedRoles) {
			String roleMessageName = "systemrole." + aRole.getName();
			String roleName = messageSource.getMessage(roleMessageName, null, locale);
			roleNames.put(aRole.getName(), roleName);
		}
		response.setAllowedRoles(roleNames);

		String currentRoleName = messageSource.getMessage("systemrole." + user.getSystemRole()
				.getName(), null, locale);
		response.setCurrentRole(currentRoleName);

		return response;
	}

	/**
	 * Check if the logged in user is an Admin
	 *
	 * @param principal The logged in user to check
	 * @return if the user is an admin
	 */
	private boolean isAdmin(Principal principal) {
		User readPrincipal = userService.getUserByUsername(principal.getName());
		return readPrincipal.getAuthorities()
				.contains(Role.ROLE_ADMIN);
	}

	/**
	 * Check if the logged in user is allowed to edit the given user.
	 *
	 * @param principalUser - the currently logged in principal
	 * @param user          - the user to edit
	 * @return boolean if the principal can edit the user
	 */
	private boolean canEditUser(User principalUser, User user) {
		boolean principalAdmin = principalUser.getAuthorities()
				.contains(Role.ROLE_ADMIN);
		boolean usersEqual = user.equals(principalUser);

		return principalAdmin || usersEqual;
	}

	/**
	 * Submit a user edit
	 *
	 * @param userId      The id of the user to edit (required)
	 * @param firstName   The firstname to update
	 * @param lastName    the lastname to update
	 * @param email       the email to update
	 * @param phoneNumber the phone number to update
	 * @param systemRole  the role to update
	 * @param userLocale  The locale the user selected
	 * @param enabled     whether the user account should be enabled or disabled.
	 * @param principal   a reference to the logged in user.
	 * @return The name of the user view
	 */
	public UserDetailsResponse updateUser(Long userId, String firstName, String lastName, String email,
			String phoneNumber, String systemRole, String userLocale, String enabled, Principal principal,
			HttpServletRequest request) {

		UserDetailsResponse response = new UserDetailsResponse();
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

		if (isAdmin(principal)) {
			updatedValues.put("enabled", !Strings.isNullOrEmpty(enabled));

			if (!Strings.isNullOrEmpty(systemRole)) {
				Role newRole = Role.valueOf(systemRole);

				updatedValues.put("systemRole", newRole);
			}
		}

		if (errors.isEmpty()) {
			try {
				userService.updateFields(userId, updatedValues);
			} catch (ConstraintViolationException | DataIntegrityViolationException | PasswordReusedException ex) {
				errors = handleCreateUpdateException(ex, request.getLocale());
				response.setErrors(errors);
			}
		}

		return response;
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
				String errorKey = violation.getPropertyPath()
						.toString();
				errors.put(errorKey, violation.getMessage());
			}
		} else if (ex instanceof DataIntegrityViolationException) {
			DataIntegrityViolationException divx = (DataIntegrityViolationException) ex;
			if (divx.getMessage()
					.contains(User.USER_EMAIL_CONSTRAINT_NAME)) {
				errors.put("email", messageSource.getMessage("user.edit.emailConflict", null, locale));
			}
		} else if (ex instanceof EntityExistsException) {
			EntityExistsException eex = (EntityExistsException) ex;
			errors.put(eex.getFieldName(), eex.getMessage());
		} else if (ex instanceof PasswordReusedException) {
			errors.put("password", messageSource.getMessage("user.edit.passwordReused", null, locale));
		}

		return errors;
	}
}
