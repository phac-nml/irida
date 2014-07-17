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
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import ca.corefacility.bioinformatics.irida.model.user.PasswordReset;
import ca.corefacility.bioinformatics.irida.model.user.User;

/**
 * This class is responsible for all email sent to the server that are templated
 * with Thymeleaf.
 * 
 * @author Josh Adam <josh.adam@phac-aspc.gc.ca>
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 */
@Component
public class SpringEmail {
	private static final Logger logger = LoggerFactory.getLogger(SpringEmail.class);
	private static String RESET_ENDPOINT = "/password_reset/";

	private @Value("${mail.server.email}") String serverEmail;

	private @Value("${mail.server.url}") String serverURL;

	private JavaMailSender javaMailSender;
	private TemplateEngine templateEngine;
	private MessageSource messageSource;

	@Autowired
	public SpringEmail(final JavaMailSender javaMailSender,
			@Qualifier("emailTemplateEngine") TemplateEngine templateEngine, MessageSource messageSource) {
		this.javaMailSender = javaMailSender;
		this.templateEngine = templateEngine;
		this.messageSource = messageSource;

	}

	public void sendWelcomeEmail(User user, User sender, PasswordReset passwordReset)
			throws MessagingException {
		Locale locale = LocaleContextHolder.getLocale();

		final Context ctx = new Context(locale);
		ctx.setVariable("ngsEmail", serverEmail);
		ctx.setVariable("serverURL", serverURL);

		ctx.setVariable("creator", sender);
		ctx.setVariable("user", user);
		ctx.setVariable("passwordReset", passwordReset);

		final MimeMessage mimeMessage = this.javaMailSender.createMimeMessage();
		final MimeMessageHelper message = new MimeMessageHelper(mimeMessage, "UTF-8");
		message.setSubject(messageSource.getMessage("email.welcome.subject", null, locale));
		message.setFrom(serverEmail);
		message.setTo(user.getEmail());

		final String htmlContent = templateEngine.process("welcome-email.html", ctx);
		message.setText(htmlContent, true);

		this.javaMailSender.send(mimeMessage);
	}

	public void sendPasswordResetLinkEmail(User user, String linkId) {

		final Context ctx = new Context();
		ctx.setVariable("ngsEmail", serverEmail);

		Locale locale = LocaleContextHolder.getLocale();

		// Add information about who created this user
		String url = serverURL + RESET_ENDPOINT + linkId;
		ctx.setVariable("resetLink", url);

		try {
			final MimeMessage mimeMessage = this.javaMailSender.createMimeMessage();
			final MimeMessageHelper message = new MimeMessageHelper(mimeMessage, "UTF-8");
			message.setSubject(messageSource.getMessage("welcome.email.subject", null, locale));
			message.setFrom(serverEmail);
			message.setTo(user.getEmail());

			final String htmlContent = this.templateEngine.process("password-reset-link.html", ctx);
			message.setText(htmlContent, true);

			this.javaMailSender.send(mimeMessage);
		} catch (MessagingException e) {
			logger.error("Error trying to send a password reset link email. Stack trace to follow: ", e);
		}
	}
}
