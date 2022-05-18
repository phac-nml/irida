package ca.corefacility.bioinformatics.irida.ria.web.services;

import java.security.Principal;
import java.util.*;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailSendException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.exceptions.PasswordReusedException;
import ca.corefacility.bioinformatics.irida.model.user.PasswordReset;
import ca.corefacility.bioinformatics.irida.model.user.Role;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.ria.web.PasswordResetController;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.UserPasswordResetDetails;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.ajax.AjaxErrorResponse;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.ajax.AjaxResponse;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.ajax.AjaxSuccessResponse;
import ca.corefacility.bioinformatics.irida.ria.web.exceptions.UIConstraintViolationException;
import ca.corefacility.bioinformatics.irida.ria.web.exceptions.UIEmailSendException;
import ca.corefacility.bioinformatics.irida.service.EmailController;
import ca.corefacility.bioinformatics.irida.service.user.PasswordResetService;
import ca.corefacility.bioinformatics.irida.service.user.UserService;

import com.google.common.collect.ImmutableList;

/**
 * Handles service calls for password resets.
 */
@Component
public class UIPasswordResetService {
	private static final Logger logger = LoggerFactory.getLogger(UIPasswordResetService.class);
	private final UserService userService;
	private final PasswordResetService passwordResetService;
	private final EmailController emailController;
	private final MessageSource messageSource;

	@Autowired
	public UIPasswordResetService(UserService userService, PasswordResetService passwordResetService,
			EmailController emailController, MessageSource messageSource) {
		this.userService = userService;
		this.passwordResetService = passwordResetService;
		this.emailController = emailController;
		this.messageSource = messageSource;
	}

	/**
	 * Create a new {@link PasswordReset} for the given {@link User}
	 *
	 * @param userId    The ID of the {@link User}
	 * @param principal a reference to the logged in user.
	 * @param locale    a reference to the locale specified by the browser.
	 * @return text to display to the user about the result of creating a password reset.
	 * @throws UIEmailSendException if there is an error emailing the password reset.
	 */
	public String adminNewPasswordReset(Long userId, Principal principal, Locale locale) throws UIEmailSendException {
		User user = userService.read(userId);
		User principalUser = userService.getUserByUsername(principal.getName());

		if (PasswordResetController.canCreatePasswordReset(principalUser, user)) {
			try {
				createNewPasswordReset(user);
			} catch (final MailSendException e) {
				logger.error("Failed to send password reset e-mail.");
				throw new UIEmailSendException(
						messageSource.getMessage("server.password.reset.error.message", null, locale));
			}
		} else {
			throw new UIEmailSendException(
					messageSource.getMessage("server.password.reset.error.message", null, locale));
		}

		return messageSource.getMessage("server.password.reset.success.message", new Object[] { user.getFirstName() },
				locale);
	}

	public String createAndSendNewPasswordResetEmail(String email) {
		setAuthentication();

		try {
			User user = userService.loadUserByEmail(email);

			try {
				createNewPasswordReset(user);
				return "Check your email for password reset instructions";
//				page = CREATED_REDIRECT + Base64.getEncoder()
//						.encodeToString(email.getBytes());
			} catch (final MailSendException e) {
				SecurityContextHolder.clearContext();
				throw new UIEmailSendException("There was an error sending password reset instructions. Please contact the system administrator");
			}
		} catch (EntityNotFoundException ex) {
			SecurityContextHolder.clearContext();
			throw new EntityNotFoundException("If a user with the provided email address exists you will receive an email with password reset instructions");
		}
	}

	public UserPasswordResetDetails activateAccount(String identifier) {
		setAuthentication();

		try {
			PasswordReset passwordReset = passwordResetService.read(identifier);
			User user = passwordReset.getUser();
			return new UserPasswordResetDetails(identifier, user);
		} catch (EntityNotFoundException e) {
			throw new EntityNotFoundException(e.getMessage());
		}
	}


	public String setNewPassword(String resetId, String password, Model model, Locale locale) throws UIConstraintViolationException {
		setAuthentication();
		Map<String, String> errors = new HashMap<>();

		// read the reset to verify it exists first
		PasswordReset passwordReset = passwordResetService.read(resetId);
		User user = passwordReset.getUser();

//		if (!password.equals(confirmPassword)) {
//			errors.put("password", messageSource.getMessage("server.user.edit.password.match", null, locale));
//		}


		// Set the user's authentication to update the password and log them
		// in
		Authentication token = new UsernamePasswordAuthenticationToken(user, password,
				ImmutableList.of(user.getSystemRole()));
		SecurityContextHolder.getContext()
				.setAuthentication(token);

		try {
			userService.changePassword(user.getId(), password);
		} catch (ConstraintViolationException ex) {
			Set<ConstraintViolation<?>> constraintViolations = ex.getConstraintViolations();

			for (ConstraintViolation<?> violation : constraintViolations) {
				logger.debug(violation.getMessage());
				String errorKey = violation.getPropertyPath()
						.toString();
				errors.put(errorKey, violation.getMessage());
			}
		} catch (PasswordReusedException ex) {
			errors.put("password", messageSource.getMessage("server.user.edit.passwordReused", null, locale));
		}


		if (!errors.isEmpty()) {
			throw new UIConstraintViolationException(errors);
		} else {
			passwordResetService.delete(resetId);

			User currUser = userService.loadUserByEmail(user.getEmail());
			model.addAttribute("user", currUser);
			return "success";
		}
	}

	/**
	 * Create a new password reset for a given {@link User} and send a reset password link via email
	 *
	 * @param user The user to create the reset for
	 */
	public void createNewPasswordReset(User user) {
		PasswordReset passwordReset = new PasswordReset(user);
		passwordResetService.create(passwordReset);

		// send a reset password link to user via email
		emailController.sendPasswordResetLinkEmail(user, passwordReset);
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
