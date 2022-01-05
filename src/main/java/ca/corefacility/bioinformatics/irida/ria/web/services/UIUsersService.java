package ca.corefacility.bioinformatics.irida.ria.web.services;

import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
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
import ca.corefacility.bioinformatics.irida.ria.config.UserSecurityInterceptor;
import ca.corefacility.bioinformatics.irida.ria.web.PasswordResetController;
import ca.corefacility.bioinformatics.irida.ria.web.models.tables.TableResponse;
import ca.corefacility.bioinformatics.irida.ria.web.users.dto.*;
import ca.corefacility.bioinformatics.irida.service.EmailController;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.user.UserService;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;

/**
 * Handles service call for the administration of the IRIDA users.
 */
@Component
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
	@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MANAGER')")
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
	@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MANAGER')")
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
						.body(messageSource.getMessage("server.AdminUsersService.error",
								new Object[] { user.getUsername() }, locale));
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
	 * @return {@link UserDetailsResponse} that contains user details for a specific user
	 */
	public UserDetailsResponse getUser(Long userId, Boolean mailFailure, Principal principal) {
		User user = userService.read(userId);
		UserDetailsModel userDetails = new UserDetailsModel(user);
		User principalUser = userService.getUserByUsername(principal.getName());
		Locale locale = LocaleContextHolder.getLocale();
		Boolean mailConfigured = emailController.isMailConfigured();
		boolean isAdmin = isAdmin(principal);
		boolean canEditUserInfo = canEditUserInfo(principalUser, user);
		boolean canEditUserStatus = canEditUserStatus(principalUser, user);
		boolean canCreatePasswordReset = PasswordResetController.canCreatePasswordReset(principalUser, user);

		List<UserDetailsLocale> localeNames = new ArrayList<>();
		for (Locale aLocale : locales) {
			localeNames.add(new UserDetailsLocale(aLocale.getLanguage(), aLocale.getDisplayName()));
		}

		List<UserDetailsRole> roleNames = new ArrayList<>();
		for (Role aRole : adminAllowedRoles) {
			String roleMessageName = "systemrole." + aRole.getName();
			String roleName = messageSource.getMessage(roleMessageName, null, locale);
			roleNames.add(new UserDetailsRole(aRole.getName(), roleName));
		}

		String currentRoleName = messageSource.getMessage("systemrole." + user.getSystemRole()
				.getName(), null, locale);

		return new UserDetailsResponse(userDetails, currentRoleName, mailConfigured, mailFailure, isAdmin,
				canEditUserInfo, canEditUserStatus, canCreatePasswordReset, localeNames, roleNames);
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
		}

		if (!Strings.isNullOrEmpty(userEditRequest.getUserLocale())) {
			updatedValues.put("locale", userEditRequest.getUserLocale());
		}

		if (isAdmin(principal)) {
			if (!Strings.isNullOrEmpty(userEditRequest.getEnabled())) {
				updatedValues.put("enabled", userEditRequest.getEnabled());
			}

			if (!Strings.isNullOrEmpty(userEditRequest.getSystemRole())) {
				Role newRole = Role.valueOf(userEditRequest.getSystemRole());

				updatedValues.put("systemRole", newRole);
			}
		}

		if (errors.isEmpty()) {
			try {
				User user = userService.updateFields(userId, updatedValues);

				// If the user is updating their account make sure you update it in the session variable
				if (user != null && principal.getName()
						.equals(user.getUsername())) {
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

	/**
	 * Check if the logged in user is allowed to edit user information for the given user.
	 *
	 * @param principalUser - the currently logged in principal
	 * @param user          - the user to edit
	 * @return boolean if the principal can edit the user information
	 */
	private boolean canEditUserInfo(User principalUser, User user) {
		boolean principalAdmin = principalUser.getAuthorities()
				.contains(Role.ROLE_ADMIN);
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
		boolean principalAdmin = principalUser.getAuthorities()
				.contains(Role.ROLE_ADMIN);
		boolean usersEqual = user.equals(principalUser);

		return !(principalAdmin && usersEqual);
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
}
