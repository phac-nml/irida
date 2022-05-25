package ca.corefacility.bioinformatics.irida.ria.web.login;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import ca.corefacility.bioinformatics.irida.model.user.PasswordReset;
import ca.corefacility.bioinformatics.irida.model.user.Role;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.service.user.PasswordResetService;


import com.google.common.collect.ImmutableList;

/**
 * Controller for handling password reset flow
 */
@Controller
@RequestMapping(value = "/password_reset")
public class PasswordResetController {
	public static final String PASSWORD_RESET_PAGE = "password/password_reset";
	private final PasswordResetService passwordResetService;


	@Autowired
	public PasswordResetController(PasswordResetService passwordResetService) {
		this.passwordResetService = passwordResetService;
	}

	/**
	 * Get the password reset page
	 *
	 * @param resetId The ID of the {@link PasswordReset}
	 * @param expired indicates whether we're showing the reset page because of an
	 *                expired password or a reset request.
	 * @param model   A model for the page
	 * @return The string name of the page
	 */
	@RequestMapping(value = "/{resetId}", method = RequestMethod.GET)
	public String getResetPage(@PathVariable String resetId,
			@RequestParam(required = false, defaultValue = "false") boolean expired, Model model) {
		setAuthentication();

		PasswordReset passwordReset = passwordResetService.read(resetId);
		User user = passwordReset.getUser();

		model.addAttribute("user", user);
		model.addAttribute("passwordReset", passwordReset);
		if (expired) {
			model.addAttribute("expired", true);
		}

		if (!model.containsAttribute("errors")) {
			model.addAttribute("errors", new HashMap<>());
		}

		return PASSWORD_RESET_PAGE;
	}

	/**
	 * Set an anonymous authentication token
	 */
	private void setAuthentication() {
		AnonymousAuthenticationToken anonymousToken = new AnonymousAuthenticationToken("nobody", "nobody",
				ImmutableList.of(Role.ROLE_ANONYMOUS));
		SecurityContextHolder.getContext()
				.setAuthentication(anonymousToken);
	}

}
