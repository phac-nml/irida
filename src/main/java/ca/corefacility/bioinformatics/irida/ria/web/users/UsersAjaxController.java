package ca.corefacility.bioinformatics.irida.ria.web.users;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.CurrentUser;
import ca.corefacility.bioinformatics.irida.ria.web.models.tables.TableResponse;
import ca.corefacility.bioinformatics.irida.ria.web.services.UIUsersService;
import ca.corefacility.bioinformatics.irida.ria.web.users.dto.AdminUsersTableRequest;
import ca.corefacility.bioinformatics.irida.ria.web.users.dto.UserTableModel;

/**
 * Handles asynchronous requests for the administration users table.
 */
@RestController
@RequestMapping("/ajax/users")
@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MANAGER')")
public class UsersAjaxController {

	private final UIUsersService usersService;

	@Autowired
	public UsersAjaxController(UIUsersService uiUsersService) {
		this.usersService = uiUsersService;
	}

	/**
	 * Get a paged listing of users for the administration user.  This can be filtered and sorted.
	 *
	 * @param request - the information about the current page of users to return
	 * @return {@link TableResponse}
	 */
	@GetMapping("/list")
	public TableResponse<UserTableModel> getUsersPagedList(@RequestBody AdminUsersTableRequest request) {
		return usersService.getUsersPagedList(request);
	}

	/**
	 * Update a user status (if the user is enabled within IRIDA).
	 *
	 * @param id        - the identifier for the {@link User} whom status is being updated.
	 * @param isEnabled - {@link Boolean} value whether the {@link User} should be enabled or not.
	 * @param locale    - the {@link Locale} of the current user.
	 * @return {@link ResponseEntity} internationalized response to the update
	 */
	@GetMapping("/edit")
	public ResponseEntity<String> updateUserStatus(@RequestParam Long id, @RequestParam boolean isEnabled,
			Locale locale) {
		return usersService.updateUserStatus(id, isEnabled, locale);
	}

	/**
	 * Get information about the current user
	 *
	 * @param projectId Identifier for a project (not required)
	 * @return {@link CurrentUser}
	 */
	@GetMapping("/current")
	public ResponseEntity<CurrentUser> getCurrentUserDetails(@RequestParam(required = false) long projectId) {
		return ResponseEntity.ok(usersService.getCurrentUserDetails(projectId));
	}
}
