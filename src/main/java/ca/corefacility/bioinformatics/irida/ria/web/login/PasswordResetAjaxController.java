package ca.corefacility.bioinformatics.irida.ria.web.login;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.model.user.PasswordReset;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.ajax.AjaxErrorResponse;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.ajax.AjaxFormErrorResponse;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.ajax.AjaxResponse;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.ajax.AjaxSuccessResponse;
import ca.corefacility.bioinformatics.irida.ria.web.exceptions.UIConstraintViolationException;
import ca.corefacility.bioinformatics.irida.ria.web.exceptions.UIEmailSendException;
import ca.corefacility.bioinformatics.irida.ria.web.services.UIPasswordResetService;

/**
 * Handles asynchronous requests for password resets.
 * Note: This controller is for unauthenticated requests only.
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
	 * Create a password reset for the given email address or username
	 *
	 * @param usernameOrEmail The email address or username to create a password reset for
	 * @param locale          The logged in user's locale
	 * @return message indicating if the password reset was successfully created or not
	 */
	@RequestMapping(method = RequestMethod.POST)
	public ResponseEntity<AjaxResponse> createAndSendNewPasswordResetEmail(@RequestParam String usernameOrEmail,
			Locale locale) {
		try {
			return ResponseEntity.ok(
					new AjaxSuccessResponse(service.createAndSendNewPasswordResetEmail(usernameOrEmail, locale)));
		} catch (EntityNotFoundException | UIEmailSendException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new AjaxErrorResponse(e.getMessage()));
		}
	}

	/**
	 * Activate the user account
	 *
	 * @param identifier The ID of the {@link PasswordReset}
	 * @param locale     The logged in user's locale
	 * @return message indicating if account was successfully activated or not
	 */
	@RequestMapping(value = "/activate_account", method = RequestMethod.POST)
	public ResponseEntity<AjaxResponse> activateAccount(@RequestParam String identifier, Locale locale) {
		try {
			return ResponseEntity.ok(new AjaxSuccessResponse(service.activateAccount(identifier, locale)));
		} catch (EntityNotFoundException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new AjaxErrorResponse(e.getMessage()));
		}
	}

	/**
	 * Update the password for the {@link User}
	 *
	 * @param resetId  The {@link PasswordReset} identifier
	 * @param password The new password to set for the user
	 * @param model    A model for the page
	 * @param locale   The logged in user's locale
	 * @return message indicating if the password reset was successfully created or not
	 */
	@RequestMapping(value = "/update_password", method = RequestMethod.POST)
	public ResponseEntity<AjaxResponse> updatePassword(@RequestParam String resetId, @RequestParam String password,
			Model model, Locale locale) {
		try {
			return ResponseEntity.ok(new AjaxSuccessResponse(service.setNewPassword(resetId, password, model, locale)));
		} catch (UIConstraintViolationException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new AjaxFormErrorResponse(e.getErrors()));
		}
	}

}
