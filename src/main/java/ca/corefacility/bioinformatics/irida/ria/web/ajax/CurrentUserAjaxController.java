package ca.corefacility.bioinformatics.irida.ria.web.ajax;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.CurrentUser;
import ca.corefacility.bioinformatics.irida.service.user.UserService;

/**
 * UI Controller to get information about the current user
 */
@Controller
@RequestMapping("/ajax/users/current")
public class CurrentUserAjaxController {
	private final UserService userService;

	@Autowired
	public CurrentUserAjaxController(UserService userService) {
		this.userService = userService;
	}

	/**
	 * Get information about the current user
	 *
	 * @return {@link CurrentUser}
	 */
	@GetMapping("")
	public ResponseEntity<CurrentUser> getCurrentUserDetails() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.getUserByUsername(authentication.getName());
		return ResponseEntity.ok(new CurrentUser(user));
	}
}
