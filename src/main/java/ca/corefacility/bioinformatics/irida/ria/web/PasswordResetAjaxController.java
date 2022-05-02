package ca.corefacility.bioinformatics.irida.ria.web;

import java.security.Principal;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import ca.corefacility.bioinformatics.irida.model.user.PasswordReset;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.ajax.AjaxErrorResponse;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.ajax.AjaxResponse;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.ajax.AjaxSuccessResponse;
import ca.corefacility.bioinformatics.irida.ria.web.exceptions.UIEmailSendException;
import ca.corefacility.bioinformatics.irida.ria.web.services.UIPasswordResetService;

/**
 * Handles asynchronous requests for password resets.
 */
@RestController
@RequestMapping("/ajax/password_reset")
public class PasswordResetAjaxController {
	private final UIPasswordResetService UIPasswordResetService;

	@Autowired
	public PasswordResetAjaxController(UIPasswordResetService UIPasswordResetService) {
		this.UIPasswordResetService = UIPasswordResetService;
	}

	/**
	 * Create a new {@link PasswordReset} for the given {@link User}
	 *
	 * @param userId    The ID of the {@link User}
	 * @param principal a reference to the logged in user.
	 * @param locale    a reference to the locale specified by the browser.
	 * @return text to display to the user about the result of creating a password reset.
	 */
	@RequestMapping(value = "/create/{userId}", method = RequestMethod.POST)
	@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MANAGER')")
	public ResponseEntity<AjaxResponse> adminNewPasswordReset(@PathVariable Long userId, Principal principal,
			Locale locale) {
		try {
			return ResponseEntity.ok(
					new AjaxSuccessResponse(UIPasswordResetService.adminNewPasswordReset(userId, principal, locale)));
		} catch (UIEmailSendException e) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body(new AjaxErrorResponse(e.getMessage()));
		}
	}

}
