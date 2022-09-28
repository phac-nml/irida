package ca.corefacility.bioinformatics.irida.ria.web.services;

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
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.exceptions.IridaAccountDisabledException;
import ca.corefacility.bioinformatics.irida.exceptions.PasswordReusedException;
import ca.corefacility.bioinformatics.irida.model.user.PasswordReset;
import ca.corefacility.bioinformatics.irida.model.user.Role;
import ca.corefacility.bioinformatics.irida.model.user.User;
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
			} catch (EntityNotFoundException ex) {
				SecurityContextHolder.clearContext();
				throw new EntityNotFoundException(
						messageSource.getMessage("server.ForgotPassword.accountNotFoundOrDisabled", null, locale));
			}
		} catch (EntityNotFoundException | UsernameNotFoundException | IridaAccountDisabledException ex) {
			SecurityContextHolder.clearContext();
			throw new EntityNotFoundException(
					messageSource.getMessage("server.ForgotPassword.accountNotFoundOrDisabled", null, locale));
		}
	}

	/**
	 * Activate the user account
	 *
	 * @param identifier The ID of the {@link PasswordReset}
	 * @param locale     The logged in user's locale
	 * @return message if successful or not
	 */
	public String activateAccount(String identifier, Locale locale) {
		setAuthentication();

		try {
			PasswordReset passwordReset = passwordResetService.read(identifier);
			return passwordReset.getId();
		} catch (EntityNotFoundException e) {
			throw new EntityNotFoundException(
					messageSource.getMessage("ActivateAccount.invalidActivationId", null, locale));
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
	 * @throws UIConstraintViolationException if any constraints are violated
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
	 * @throws IridaAccountDisabledException if the account is disabled
	 */
	private void createNewPasswordReset(User user) throws IridaAccountDisabledException {
		// Only create a password reset for a user that is enabled
		if (user.isEnabled()) {
			PasswordReset passwordReset = new PasswordReset(user);
			passwordResetService.create(passwordReset);

			// send a reset password link to user via email
			emailController.sendPasswordResetLinkEmail(user, passwordReset);
		} else {
			throw new IridaAccountDisabledException("User account is disabled");
		}
	}

	/**
	 * Set an anonymous authentication token
	 */
	private void setAuthentication() {
		AnonymousAuthenticationToken anonymousToken = new AnonymousAuthenticationToken("nobody", "nobody",
				ImmutableList.of(Role.ROLE_ANONYMOUS));
		SecurityContextHolder.getContext().setAuthentication(anonymousToken);
	}

}
