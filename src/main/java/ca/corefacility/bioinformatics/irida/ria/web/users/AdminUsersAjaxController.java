package ca.corefacility.bioinformatics.irida.ria.web.users;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.ria.web.models.tables.TableResponse;
import ca.corefacility.bioinformatics.irida.ria.web.services.AdminUsersService;
import ca.corefacility.bioinformatics.irida.ria.web.users.dto.AdminUsersTableRequest;
import ca.corefacility.bioinformatics.irida.ria.web.users.dto.UserTableModel;

/**
 * Handles asynchronous requests for the administration users table.
 */
@RestController
@RequestMapping("/ajax/users")
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class AdminUsersAjaxController {

	private final AdminUsersService adminUsersService;

	@Autowired
	public AdminUsersAjaxController(AdminUsersService adminUsersService) {
		this.adminUsersService = adminUsersService;
	}

	/**
	 * Get a paged listing of users for the administration user.  This can be filtered and sorted.
	 *
	 * @param request - the information about the current page of users to return
	 * @return {@link TableResponse}
	 */
	@RequestMapping("/list")
	public TableResponse<UserTableModel> getUsersPagedList(@RequestBody AdminUsersTableRequest request) {
		return adminUsersService.getUsersPagedList(request);
	}

	/**
	 * Update a user status (if the user is enabled within IRIDA).
	 *
	 * @param id        - the identifier for the {@link User} whom status is being updated.
	 * @param isEnabled - {@link Boolean} value whether the {@link User} should be enabled or not.
	 */
	@RequestMapping("/edit")
	public ResponseEntity<String> updateUserStatus(@RequestParam Long id, @RequestParam boolean isEnabled,
			Locale locale) {
		return adminUsersService.updateUserStatus(id, isEnabled, locale);
	}
}
