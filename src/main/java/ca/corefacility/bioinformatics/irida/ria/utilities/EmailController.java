package ca.corefacility.bioinformatics.irida.ria.utilities;

import java.util.Locale;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Profile;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import ca.corefacility.bioinformatics.irida.model.user.PasswordReset;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.ria.config.WebEmailConfig.ConfigurableJavaMailSender;

/**
 * This class is responsible for all email sent to the server that are templated
 * with Thymeleaf.
 * 
 */
@Component
@Profile({ "prod", "dev" })
public class EmailController {
	private static final Logger logger = LoggerFactory.getLogger(EmailController.class);

	public static final String WELCOME_TEMPLATE = "welcome-email";
	public static final String RESET_TEMPLATE = "password-reset-link";
	
	private @Value("${mail.server.email}") String serverEmail;

	private @Value("${server.base.url}") String serverURL;

	private ConfigurableJavaMailSender javaMailSender;
	private TemplateEngine templateEngine;
	private MessageSource messageSource;

	@Autowired
	public EmailController(final ConfigurableJavaMailSender javaMailSender,
			@Qualifier("emailTemplateEngine") TemplateEngine templateEngine, MessageSource messageSource) {
		this.javaMailSender = javaMailSender;
		this.templateEngine = templateEngine;
		this.messageSource = messageSource;
	}

	/**
	 * Send welcome email to a user who joined the platform
	 * 
	 * @param user
	 *            The {@link User} that was just created
	 * @param sender
	 *            The {@link User} that created the new user
	 * @param passwordReset
	 *            A {@link PasswordReset} object to send an activation link
	 */
	public void sendWelcomeEmail(User user, User sender, PasswordReset passwordReset) {
		logger.debug("Sending user creation email to " + user.getEmail());

		Locale locale = LocaleContextHolder.getLocale();

		final Context ctx = new Context(locale);
		ctx.setVariable("ngsEmail", serverEmail);
		ctx.setVariable("serverURL", serverURL);

		ctx.setVariable("creator", sender);
		ctx.setVariable("user", user);
		ctx.setVariable("passwordReset", passwordReset);

		final MimeMessage mimeMessage = this.javaMailSender.createMimeMessage();
		final MimeMessageHelper message = new MimeMessageHelper(mimeMessage, "UTF-8");
		try {
			message.setSubject(messageSource.getMessage("email.welcome.subject", null, locale));
			message.setFrom(serverEmail);
			message.setTo(user.getEmail());

			final String htmlContent = templateEngine.process(WELCOME_TEMPLATE, ctx);
			message.setText(htmlContent, true);
			javaMailSender.send(mimeMessage);
		} catch (MessagingException e) {
			logger.error("User creation email failed to send", e);
		}
	}

	/**
	 * Send a {@link PasswordReset} link to a {@link User}
	 * 
	 * @param user
	 *            The user for the reset
	 * @param passwordReset
	 *            the reset object
	 */
	public void sendPasswordResetLinkEmail(User user, PasswordReset passwordReset) {
		logger.debug("Sending password reset email to " + user.getEmail());
		final Context ctx = new Context();
		ctx.setVariable("ngsEmail", serverEmail);
		ctx.setVariable("serverURL", serverURL);

		Locale locale = LocaleContextHolder.getLocale();

		// add the reset information
		ctx.setVariable("passwordReset", passwordReset);
		ctx.setVariable("user", user);

		try {
			final MimeMessage mimeMessage = this.javaMailSender.createMimeMessage();
			final MimeMessageHelper message = new MimeMessageHelper(mimeMessage, "UTF-8");
			message.setSubject(messageSource.getMessage("email.reset.subject", null, locale));
			message.setFrom(serverEmail);
			message.setTo(user.getEmail());

			final String htmlContent = templateEngine.process(RESET_TEMPLATE, ctx);
			message.setText(htmlContent, true);

			javaMailSender.send(mimeMessage);
		} catch (MessagingException e) {
			logger.error("Error trying to send a password reset link email.", e);
		}
	}
	
	/**
	 * Is the mail server configured?
	 * 
	 * @return {@value Boolean#TRUE} if configured, {@value Boolean#FALSE} if
	 *         not.
	 */
	public Boolean isMailConfigured() {
		return javaMailSender.isConfigured();
	}
}
