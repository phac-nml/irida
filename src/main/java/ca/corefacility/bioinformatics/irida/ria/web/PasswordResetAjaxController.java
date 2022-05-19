package ca.corefacility.bioinformatics.irida.ria.web;

import java.security.Principal;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.model.user.PasswordReset;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.UserPasswordResetDetails;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.ajax.AjaxErrorResponse;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.ajax.AjaxResponse;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.ajax.AjaxSuccessResponse;
import ca.corefacility.bioinformatics.irida.ria.web.exceptions.UIConstraintViolationException;
import ca.corefacility.bioinformatics.irida.ria.web.exceptions.UIEmailSendException;
import ca.corefacility.bioinformatics.irida.ria.web.services.UIPasswordResetService;
import ca.corefacility.bioinformatics.irida.ria.web.users.dto.PasswordResetResponse;

/**
 * Handles asynchronous requests for password resets.
 */
@RestController
@RequestMapping("/ajax/password_reset")
public class PasswordResetAjaxController {
	private final UIPasswordResetService service;

	@Autowired
	public PasswordResetAjaxController(UIPasswordResetService UIPasswordResetService) {
		this.service = UIPasswordResetService;
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
					new AjaxSuccessResponse(service.adminNewPasswordReset(userId, principal, locale)));
		} catch (UIEmailSendException e) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body(new AjaxErrorResponse(e.getMessage()));
		}
	}

	/**
	 * Create a password reset for the given email address
	 *
	 * @param usernameOrEmail The email address or username to create a password reset for
	 * @return Reset created page if the email exists in the system
	 */
	@RequestMapping(method = RequestMethod.POST)
	public ResponseEntity<AjaxResponse> createAndSendNewPasswordResetEmail(@RequestParam String usernameOrEmail, Locale locale) {
		try {
			return ResponseEntity.ok(new AjaxSuccessResponse(service.createAndSendNewPasswordResetEmail(usernameOrEmail, locale)));
		} catch (EntityNotFoundException e) {
			return ResponseEntity.ok(new AjaxSuccessResponse(e.getMessage()));
		} catch (UIEmailSendException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new AjaxErrorResponse(e.getMessage()));
		}
	}

	@RequestMapping(value = "/activate_account", method = RequestMethod.POST)
	public  ResponseEntity<UserPasswordResetDetails> activateAccount(@RequestParam String identifier) {
		try {
			return ResponseEntity.ok(
					service.activateAccount(identifier));
		} catch (UIEmailSendException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
		}
	}

	@RequestMapping(value = "/update_password", method = RequestMethod.POST)
	public ResponseEntity<PasswordResetResponse> updatePassword(@RequestParam String resetId, @RequestParam String password, Model model, Locale locale) {
		try {
			return ResponseEntity.ok(new PasswordResetResponse(service.setNewPassword(resetId, password, model, locale), null));
		} catch (UIConstraintViolationException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new PasswordResetResponse("error",e.getErrors()));
		}
	}



}
