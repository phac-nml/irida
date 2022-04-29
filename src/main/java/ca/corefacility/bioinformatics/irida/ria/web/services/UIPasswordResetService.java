package ca.corefacility.bioinformatics.irida.ria.web.services;

import java.security.Principal;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.mail.MailSendException;
import org.springframework.stereotype.Component;

import ca.corefacility.bioinformatics.irida.model.user.PasswordReset;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.ria.web.PasswordResetController;
import ca.corefacility.bioinformatics.irida.ria.web.exceptions.UIEmailSendException;
import ca.corefacility.bioinformatics.irida.service.EmailController;
import ca.corefacility.bioinformatics.irida.service.user.PasswordResetService;
import ca.corefacility.bioinformatics.irida.service.user.UserService;

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
}
