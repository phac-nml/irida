package ca.corefacility.bioinformatics.irida.ria.web.users;

import java.security.Principal;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import ca.corefacility.bioinformatics.irida.model.user.PasswordReset;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.ajax.AjaxErrorResponse;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.ajax.AjaxFormErrorResponse;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.ajax.AjaxResponse;
import ca.corefacility.bioinformatics.irida.ria.web.exceptions.UIEmailSendException;
import ca.corefacility.bioinformatics.irida.ria.web.exceptions.UIUserFormException;
import ca.corefacility.bioinformatics.irida.ria.web.exceptions.UIUserStatusException;
import ca.corefacility.bioinformatics.irida.ria.web.models.tables.TableResponse;
import ca.corefacility.bioinformatics.irida.ria.web.services.UIUsersService;
import ca.corefacility.bioinformatics.irida.ria.web.users.dto.*;

/**
 * Handles asynchronous requests for the administration users table.
 */
@RestController
@RequestMapping("/ajax/users")
public class UsersAjaxController {
	private final UIUsersService uiUsersService;

	@Autowired
	public UsersAjaxController(UIUsersService uiUsersService) {
		this.uiUsersService = uiUsersService;
	}

	/**
	 * Get a paged listing of users for the administration user. This can be filtered and sorted.
	 *
	 * @param request - the information about the current page of users to return
	 * @return {@link TableResponse}
	 */
	@RequestMapping("/list")
	public TableResponse<UserDetailsModel> getUsersPagedList(@RequestBody AdminUsersTableRequest request) {
		return uiUsersService.getUsersPagedList(request);
	}

	/**
	 * Create a new user
	 *
	 * @param userCreateRequest a {@link UserCreateRequest} containing details about a specific user
	 * @param principal         a reference to the logged in user
	 * @param locale            the logged in user's request locale
	 * @return the id of the new user
	 */
	@RequestMapping(value = "/create", method = RequestMethod.POST)
	public ResponseEntity<AjaxResponse> createUser(@RequestBody UserCreateRequest userCreateRequest,
			Principal principal, Locale locale) {
		try {
			return ResponseEntity.ok(uiUsersService.createUser(userCreateRequest, principal, locale));
		} catch (UIEmailSendException e) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body(new AjaxErrorResponse(e.getMessage()));
		} catch (UIUserFormException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new AjaxFormErrorResponse(e.getErrors()));
		}
	}

	/**
	 * Update a user status (if the user is enabled within IRIDA).
	 *
	 * @param id        - the identifier for the {@link User} whom status is being updated.
	 * @param isEnabled - {@link Boolean} value whether the {@link User} should be enabled or not.
	 * @param locale    - the {@link Locale} of the current user.
	 * @return {@link ResponseEntity} internationalized response to the update
	 */
	@RequestMapping("/edit")
	public ResponseEntity<AjaxResponse> updateUserStatus(@RequestParam Long id, @RequestParam boolean isEnabled,
			Locale locale) {
		try {
			return ResponseEntity.status(HttpStatus.OK).body(uiUsersService.updateUserStatus(id, isEnabled, locale));
		} catch (UIUserStatusException e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new AjaxErrorResponse(e.getMessage()));
		}
	}

	/**
	 * Submit a user edit
	 *
	 * @param userId          The id of the user to edit (required)
	 * @param userEditRequest a {@link UserEditRequest} containing details about a specific user
	 * @param principal       a reference to the logged in user
	 * @param request         the request
	 * @param locale          the logged in user's request locale
	 * @return a status message
	 */
	@RequestMapping(value = "/{userId}/edit", method = RequestMethod.POST)
	public ResponseEntity<AjaxResponse> updateUser(@PathVariable Long userId,
			@RequestBody UserEditRequest userEditRequest, Principal principal, HttpServletRequest request,
			Locale locale) {
		try {
			return ResponseEntity.ok(uiUsersService.updateUser(userId, userEditRequest, principal, request, locale));
		} catch (UIUserFormException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new AjaxFormErrorResponse(e.getErrors()));
		}
	}

	/**
	 * Change the password for a user
	 *
	 * @param userId      The id of the user to edit (required)
	 * @param oldPassword The old password of the user for password change
	 * @param newPassword The new password of the user for password change
	 * @param principal   a reference to the logged in user
	 * @param request     the request
	 * @param locale      the logged in user's request locale
	 * @return a status message
	 */
	@RequestMapping(value = "/{userId}/changePassword", method = RequestMethod.POST)
	public ResponseEntity<AjaxResponse> changeUserPassword(@PathVariable Long userId,
			@RequestParam(required = false) String oldPassword, @RequestParam String newPassword, Principal principal,
			HttpServletRequest request, Locale locale) {
		try {
			return ResponseEntity.ok(
					uiUsersService.changeUserPassword(userId, oldPassword, newPassword, principal, request, locale));
		} catch (UIUserFormException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new AjaxFormErrorResponse(e.getErrors()));
		}
	}

	/**
	 * Get the details for a specific user
	 *
	 * @param userId    - the id for the user to show details for
	 * @param principal - the currently logged in user
	 * @return a {@link UserDetailsResponse} containing the details for a specific user
	 */
	@RequestMapping("/{userId}")
	public ResponseEntity<UserDetailsResponse> getUserDetails(@PathVariable("userId") Long userId,
			Principal principal) {
		return ResponseEntity.ok(uiUsersService.getUser(userId, principal));
	}

	/**
	 * Create a new {@link PasswordReset} for the given {@link User}
	 *
	 * @param userId    The ID of the {@link User}
	 * @param principal a reference to the logged in user.
	 * @param locale    a reference to the locale specified by the browser.
	 * @return text to display to the user about the result of creating a password reset.
	 */
	@RequestMapping(value = "/{userId}/reset-password", method = RequestMethod.POST)
	@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MANAGER')")
	public ResponseEntity<AjaxResponse> adminNewPasswordReset(@PathVariable Long userId, Principal principal,
			Locale locale) {
		try {
			return ResponseEntity.ok(uiUsersService.adminNewPasswordReset(userId, principal, locale));
		} catch (UIEmailSendException e) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body(new AjaxErrorResponse(e.getMessage()));
		}
	}
}
