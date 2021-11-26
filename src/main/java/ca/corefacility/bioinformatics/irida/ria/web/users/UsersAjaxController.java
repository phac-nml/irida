package ca.corefacility.bioinformatics.irida.ria.web.users;

import java.security.Principal;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.ria.web.models.tables.TableResponse;
import ca.corefacility.bioinformatics.irida.ria.web.services.UIUsersService;
import ca.corefacility.bioinformatics.irida.ria.web.users.dto.AdminUsersTableRequest;
import ca.corefacility.bioinformatics.irida.ria.web.users.dto.UserDetailsModel;
import ca.corefacility.bioinformatics.irida.ria.web.users.dto.UserDetailsResponse;

/**
 * Handles asynchronous requests for the administration users table.
 */
@RestController
@RequestMapping("/ajax/users")
@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MANAGER')")
public class UsersAjaxController {

	private final UIUsersService UIUsersService;

	@Autowired
	public UsersAjaxController(UIUsersService UIUsersService) {
		this.UIUsersService = UIUsersService;
	}

	/**
	 * Get a paged listing of users for the administration user.  This can be filtered and sorted.
	 *
	 * @param request - the information about the current page of users to return
	 * @return {@link TableResponse}
	 */
	@RequestMapping("/list")
	public TableResponse<UserDetailsModel> getUsersPagedList(@RequestBody AdminUsersTableRequest request) {
		return UIUsersService.getUsersPagedList(request);
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
	public ResponseEntity<String> updateUserStatus(@RequestParam Long id, @RequestParam boolean isEnabled,
			Locale locale) {
		return UIUsersService.updateUserStatus(id, isEnabled, locale);
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
	@RequestMapping(value = "/{userId}/edit", method = RequestMethod.POST)
	public ResponseEntity updateUser(@PathVariable Long userId, @RequestParam(required = false) String firstName,
			@RequestParam(required = false) String lastName, @RequestParam(required = false) String email,
			@RequestParam(required = false) String phoneNumber, @RequestParam(required = false) String systemRole,
			@RequestParam(required = false, name = "locale") String userLocale,
			@RequestParam(required = false) String enabled, Principal principal, HttpServletRequest request) {

		UserDetailsResponse response = UIUsersService.updateUser(userId, firstName, lastName, email, phoneNumber,
				systemRole, userLocale, enabled, principal, request);

		if (response.hasErrors())
			return ResponseEntity.status(HttpStatus.CONFLICT)
					.body(response.getErrors());
		else
			return ResponseEntity.ok(response);

	}

	/**
	 * Get the details for a specific user
	 *
	 * @param userId      - the id for the user to show details for
	 * @param mailFailure - if sending a user activation e-mail passed or failed
	 * @param principal   - the currently logged in user
	 * @return a {@link UserDetailsResponse} containing the details for a specific user
	 */
	@RequestMapping("/{userId}")
	public ResponseEntity<UserDetailsResponse> getUserDetails(@PathVariable("userId") Long userId,
			@RequestParam(value = "mailFailure", required = false, defaultValue = "false") final Boolean mailFailure,
			Principal principal) {
		return ResponseEntity.ok(UIUsersService.getUser(userId, mailFailure, principal));
	}

}
