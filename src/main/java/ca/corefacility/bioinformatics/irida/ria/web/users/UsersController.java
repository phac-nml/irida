package ca.corefacility.bioinformatics.irida.ria.web.users;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.service.user.UserService;

/**
 * Controller for all {@link User} related views
 */
@Controller
@RequestMapping(value = "/users")
public class UsersController {
	private static final String USERS_PAGE = "user/list";
	private static final String SPECIFIC_USER_PAGE = "user/account";

	private final UserService userService;

	@Autowired
	public UsersController(UserService userService) {
		this.userService = userService;
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
	 * @param userId identifier for the user
	 * @return The name of the user account page
	 */
	@RequestMapping({ "/{userId}", "/{userId}/*" })
	@PreAuthorize("hasAnyRole('ROLE_ADMIN') or principal.id == #userId")
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

}
