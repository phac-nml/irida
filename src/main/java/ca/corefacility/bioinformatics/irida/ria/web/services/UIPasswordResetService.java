package ca.corefacility.bioinformatics.irida.ria.web.services;

import java.security.Principal;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;

import org.springframework.mail.MailSendException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.exceptions.PasswordReusedException;
import ca.corefacility.bioinformatics.irida.model.user.PasswordReset;
import ca.corefacility.bioinformatics.irida.model.user.Role;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.UserPasswordResetDetails;
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

		if (canCreatePasswordReset(principalUser, user)) {
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

	/**
	 * Create a password reset for the given email address or username
	 *
	 * @param usernameOrEmail The email address or username to create a password reset for
	 * @param locale          The logged in user's locale
	 * @return message indicating if the password reset was successfully created or not
	 */
	public String createAndSendNewPasswordResetEmail(String usernameOrEmail, Locale locale) {
		setAuthentication();

		try {
			/*
			Simple regex to check if an email address or username are provided
			 */
			String EMAIL_PATTERN = "^(.+)@(\\S+)$";
			Pattern pattern = Pattern.compile(EMAIL_PATTERN);
			Matcher matcher = pattern.matcher(usernameOrEmail);
			User user;

			if (matcher.matches()) {
				user = userService.loadUserByEmail(usernameOrEmail);
			} else {
				user = userService.getUserByUsername(usernameOrEmail);
			}

			try {
				createNewPasswordReset(user);
				return messageSource.getMessage("server.ForgotPassword.checkEmail", null, locale);
			} catch (final MailSendException e) {
				SecurityContextHolder.clearContext();
				throw new UIEmailSendException(
						messageSource.getMessage("server.ForgotPassword.errorSendingInstructions", null, locale));
			}
		} catch (EntityNotFoundException ex) {
			SecurityContextHolder.clearContext();
			throw new EntityNotFoundException(
					messageSource.getMessage("server.ForgotPassword.emailOrUsernameNotExist", null, locale));
		}
	}

	/**
	 * Activate the user account
	 *
	 * @param identifier The ID of the {@link PasswordReset}
	 * @return {@link UserPasswordResetDetails}
	 */
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

	/**
	 * Set the password for the {@link User}
	 *
	 * @param resetId  The {@link PasswordReset} identifier
	 * @param password The new password to set for the user
	 * @param model    A model for the page
	 * @param locale   The logged in user's locale
	 * @return message if successful or not
	 */
	public String setNewPassword(String resetId, String password, Model model, Locale locale)
			throws UIConstraintViolationException {
		setAuthentication();
		Map<String, String> errors = new HashMap<>();

		// read the reset to verify it exists first
		PasswordReset passwordReset = passwordResetService.read(resetId);
		User user = passwordReset.getUser();

		// Set the user's authentication to update the password and log them
		// in
		Authentication token = new UsernamePasswordAuthenticationToken(user, password,
				ImmutableList.of(user.getSystemRole()));
		SecurityContextHolder.getContext().setAuthentication(token);

		try {
			userService.changePassword(user.getId(), password);
		} catch (ConstraintViolationException ex) {
			Set<ConstraintViolation<?>> constraintViolations = ex.getConstraintViolations();

			for (ConstraintViolation<?> violation : constraintViolations) {
				logger.debug(violation.getMessage());
				String errorKey = violation.getPropertyPath().toString();
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
		SecurityContextHolder.getContext().setAuthentication(anonymousToken);
	}

	/**
	 * Test if a user should be able to click the password reset button
	 *
	 * @param principalUser The currently logged in principal
	 * @param user          The user being edited
	 * @return true if the principal can create a password reset for the user
	 */
	private boolean canCreatePasswordReset(User principalUser, User user) {
		Role userRole = user.getSystemRole();
		Role principalRole = principalUser.getSystemRole();

		if (principalUser.equals(user)) {
			return false;
		} else if (principalRole.equals(Role.ROLE_ADMIN)) {
			return true;
		} else if (principalRole.equals(Role.ROLE_MANAGER)) {
			if (userRole.equals(Role.ROLE_ADMIN)) {
				return false;
			} else {
				return true;
			}
		}

		return false;
	}

}
