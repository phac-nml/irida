package ca.corefacility.bioinformatics.irida.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.MailSendException;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;

import ca.corefacility.bioinformatics.irida.config.services.WebEmailConfig.ConfigurableJavaMailSender;
import ca.corefacility.bioinformatics.irida.model.event.ProjectEvent;
import ca.corefacility.bioinformatics.irida.model.user.PasswordReset;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;

/**
 * Email controller used for testing.  Does not really send emails.
 */
@Component
@Profile({ "it", "test" })
public class TestEmailController extends EmailControllerImpl {
	private static final Logger logger = LoggerFactory.getLogger(TestEmailController.class);

	@Autowired
	public TestEmailController(ConfigurableJavaMailSender javaMailSender,
			@Qualifier("emailTemplateEngine") TemplateEngine templateEngine, MessageSource messageSource) {
		super(javaMailSender, templateEngine, messageSource);
		logger.info("TestEmailController overriding EmailController");
	}

	public TestEmailController() {
		super(null, null, null);
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

	@Override
	public void sendSubscriptionUpdateEmail(User user, List<ProjectEvent> events) {
		logger.info("TestEmailController#sendSubscriptionUpdateEmail called for " + user + " and " + events.size()
				+ " events");
	}

	@Override
	public void sendFilesystemExceptionEmail(final String adminEmailAddress, final Exception rootCause)
			throws MailSendException {
		logger.info("TestEmailController#sendFilesystemExceptionEmail called.");
	}

	@Override
	public void sendNCBIUploadExceptionEmail(String adminEmailAddress, Exception rootCause, Long submissionId)
			throws MailSendException {
		logger.info("TestEmailController#sendNCBIUploadExceptionEmail called.");
	}

	@Override
	public void sendPipelineStatusEmail(AnalysisSubmission submission) {
		logger.info("TestEmailController#sendPipelineStatusEmail called for " + submission.getSubmitter().getEmail());
	}

	@Override
	public Boolean isMailConfigured() {
		return true;
	}
}
