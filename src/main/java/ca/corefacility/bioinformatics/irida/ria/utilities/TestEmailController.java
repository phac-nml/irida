package ca.corefacility.bioinformatics.irida.ria.utilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;

import ca.corefacility.bioinformatics.irida.model.user.PasswordReset;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.ria.config.WebEmailConfig.ConfigurableJavaMailSender;

@Component
@Profile({ "it", "test" })
public class TestEmailController extends EmailController {
	private static final Logger logger = LoggerFactory.getLogger(TestEmailController.class);

	@Autowired
	public TestEmailController(ConfigurableJavaMailSender javaMailSender, TemplateEngine templateEngine, MessageSource messageSource) {
		super(javaMailSender, templateEngine, messageSource);
		logger.info("TestEmailController overriding EmailController");
	}

	@Override
	public void sendPasswordResetLinkEmail(User user, PasswordReset passwordReset) {
		logger.info("TestEmailController#sendPasswordResetLinkEmail called for " + user + " " + passwordReset);
	}

	@Override
	public void sendWelcomeEmail(User user, User sender, PasswordReset passwordReset) {
		logger.info("TestEmailController#sendWelcomeEmail called for " + user + " " + sender + " " + passwordReset);
	}

}
