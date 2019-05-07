package ca.corefacility.bioinformatics.irida.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

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
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.google.common.collect.ImmutableMap;

import ca.corefacility.bioinformatics.irida.config.services.WebEmailConfig.ConfigurableJavaMailSender;
import ca.corefacility.bioinformatics.irida.model.event.DataAddedToSampleProjectEvent;
import ca.corefacility.bioinformatics.irida.model.event.ProjectEvent;
import ca.corefacility.bioinformatics.irida.model.event.SampleAddedProjectEvent;
import ca.corefacility.bioinformatics.irida.model.event.UserRemovedProjectEvent;
import ca.corefacility.bioinformatics.irida.model.event.UserRoleSetProjectEvent;
import ca.corefacility.bioinformatics.irida.model.user.PasswordReset;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;
import ca.corefacility.bioinformatics.irida.service.EmailController;
import ca.corefacility.bioinformatics.irida.model.enums.AnalysisState;
/**
 * This class is responsible for all email sent to the server that are templated
 * with Thymeleaf.
 *
 */
@Component
@Profile({ "prod", "dev" , "web", "analysis", "ncbi", "processing", "sync" })
public class EmailControllerImpl implements EmailController {
	private static final Logger logger = LoggerFactory.getLogger(EmailControllerImpl.class);

	public static final String WELCOME_TEMPLATE = "welcome-email";
	public static final String RESET_TEMPLATE = "password-reset-link";
	public static final String SUBSCRIPTION_TEMPLATE = "subscription-email";
	public static final String PIPELINE_STATUS_TEMPLATE = "pipeline-status-email";

	private @Value("${mail.server.email}") String serverEmail;

	private @Value("${server.base.url}") String serverURL;

	private ConfigurableJavaMailSender javaMailSender;
	private TemplateEngine templateEngine;
	private MessageSource messageSource;

	public static final Map<Class<? extends ProjectEvent>, String> FRAGMENT_NAMES = ImmutableMap.of(
			UserRoleSetProjectEvent.class, "user-role-event", UserRemovedProjectEvent.class, "user-removed-event",
			SampleAddedProjectEvent.class, "sample-added-event", DataAddedToSampleProjectEvent.class,
			"data-added-event");

	@Autowired
	public EmailControllerImpl(final ConfigurableJavaMailSender javaMailSender,
			@Qualifier("emailTemplateEngine") TemplateEngine templateEngine, MessageSource messageSource) {
		this.javaMailSender = javaMailSender;
		this.templateEngine = templateEngine;
		this.messageSource = messageSource;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void sendWelcomeEmail(User user, User sender, PasswordReset passwordReset) throws MailSendException {
		logger.debug("Sending user creation email to " + user.getEmail());

		Locale locale = LocaleContextHolder.getLocale();

		final Context ctx = new Context(locale);
		ctx.setVariable("ngsEmail", serverEmail);
		ctx.setVariable("serverURL", serverURL);

		ctx.setVariable("creator", sender);
		ctx.setVariable("user", user);
		ctx.setVariable("passwordReset", passwordReset);

		try {
			final MimeMessage mimeMessage = this.javaMailSender.createMimeMessage();
			final MimeMessageHelper message = new MimeMessageHelper(mimeMessage, "UTF-8");
			message.setSubject(messageSource.getMessage("email.welcome.subject", null, locale));
			message.setFrom(serverEmail);
			message.setTo(user.getEmail());

			final String htmlContent = templateEngine.process(WELCOME_TEMPLATE, ctx);
			message.setText(htmlContent, true);
			javaMailSender.send(mimeMessage);
		} catch (final Exception e) {
			logger.error("User creation email failed to send", e);
			throw new MailSendException("Failed to send e-mail when creating user account.", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void sendPasswordResetLinkEmail(User user, PasswordReset passwordReset) throws MailSendException {
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
		} catch (Exception e) {
			logger.error("Error trying to send a password reset link email.", e);
			throw new MailSendException("Failed to send e-mail when doing password reset.", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void sendSubscriptionUpdateEmail(User user, List<ProjectEvent> events) {
		logger.debug("Sending subscription email to " + user.getEmail());
		final Context ctx = new Context();
		ctx.setVariable("ngsEmail", serverEmail);
		ctx.setVariable("serverURL", serverURL);
		ctx.setVariable("user", user);

		Locale locale = Locale.forLanguageTag(user.getLocale());

		ctx.setVariable("dateFormat", messageSource.getMessage("locale.date.long", null, locale));

		List<Map<String, Object>> eventsList = buildEventsListFromCollection(events);
		ctx.setVariable("events", eventsList);

		final String htmlContent = templateEngine.process(SUBSCRIPTION_TEMPLATE, ctx);

		try {
			final MimeMessage mimeMessage = this.javaMailSender.createMimeMessage();
			final MimeMessageHelper message = new MimeMessageHelper(mimeMessage, "UTF-8");
			message.setSubject(messageSource.getMessage("email.subscription.title", null, locale));
			message.setFrom(serverEmail);
			message.setTo(user.getEmail());

			message.setText(htmlContent, true);

			javaMailSender.send(mimeMessage);
		} catch (Exception e) {
			logger.error("Error trying to send subcription email.", e);
			throw new MailSendException("Failed to send e-mail for project event subscription.", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Boolean isMailConfigured() {
		return javaMailSender.isConfigured();
	}

	/**
	 * Convert the Page of events to the list expected in the model
	 *
	 * @param events
	 *            Page of {@link ProjectEvent}s
	 * @return A List<Map<String,Object>> containing the events and fragment
	 *         names
	 */
	private List<Map<String, Object>> buildEventsListFromCollection(Collection<ProjectEvent> events) {
		List<Map<String, Object>> eventInfo = new ArrayList<>();
		for (ProjectEvent e : events) {
			if (FRAGMENT_NAMES.containsKey(e.getClass())) {
				Map<String, Object> info = new HashMap<>();
				info.put("name", FRAGMENT_NAMES.get(e.getClass()));
				info.put("event", e);
				eventInfo.add(info);
			}
		}

		return eventInfo;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void sendFilesystemExceptionEmail(final String adminEmailAddress, final Exception rootCause)
			throws MailSendException {
		final MimeMessage mimeMessage = javaMailSender.createMimeMessage();
		final MimeMessageHelper message = new MimeMessageHelper(mimeMessage);
		try {
			message.setSubject("IRIDA Storage Exception: " + rootCause.getMessage());
			message.setTo(adminEmailAddress);
			message.setFrom(serverEmail);
			message.setText("An exeption related to storage has occurred that requires your attention, stack as follows: "
					+ rootCause);

			javaMailSender.send(mimeMessage);
		} catch (final MessagingException e) {
			logger.error("Error trying to send exception email. (Ack.)", e);
			throw new MailSendException("Failed to send e-mail for storage related-exception.", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void sendNCBIUploadExceptionEmail(String adminEmailAddress, Exception rootCause, Long submissionId)
			throws MailSendException {
		final MimeMessage mimeMessage = javaMailSender.createMimeMessage();
		final MimeMessageHelper message = new MimeMessageHelper(mimeMessage);
		try {
			message.setSubject("IRIDA NCBI Upload Exception: " + rootCause.getMessage());
			message.setTo(adminEmailAddress);
			message.setFrom(serverEmail);
			message.setText("An exeption occurred when attempting to communicate with NCBI's SRA.  Submission " + submissionId
					+ " had an error:" + rootCause);

			javaMailSender.send(mimeMessage);
		} catch (final MessagingException e) {
			logger.error("Error trying to send exception email.", e);
			throw new MailSendException("Failed to send e-mail for NCBI SRA related-exception.", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void sendPipelineStatusEmail(AnalysisSubmission submission) throws MailSendException {
		logger.debug("Sending pipeline status email to " + submission.getSubmitter()
				.getEmail());

		Locale locale = LocaleContextHolder.getLocale();

		final Context ctx = new Context(locale);
		String pipelineStatus = submission.getAnalysisState()
				.equals(AnalysisState.ERROR) ?
				messageSource.getMessage("email.pipeline.ERROR", null, locale) :
				messageSource.getMessage("email.pipeline.COMPLETED", null, locale);
		String emailSubject = messageSource.getMessage("email.pipeline.subject",
				new Object[] { submission.getName(), pipelineStatus }, locale);

		ctx.setVariable("serverURL", serverURL);
		ctx.setVariable("pipelineStatus", pipelineStatus);
		ctx.setVariable("pipelineName", submission.getName());
		ctx.setVariable("analysisSubmissionURL", (serverURL + "/analysis/" + (submission.getId())));

		try {
			final MimeMessage mimeMessage = this.javaMailSender.createMimeMessage();
			final MimeMessageHelper message = new MimeMessageHelper(mimeMessage, "UTF-8");
			message.setSubject(emailSubject);
			message.setFrom(serverEmail);
			message.setTo(submission.getSubmitter()
					.getEmail());

			final String htmlContent = templateEngine.process(PIPELINE_STATUS_TEMPLATE, ctx);
			message.setText(htmlContent, true);
			javaMailSender.send(mimeMessage);
		} catch (final Exception e) {
			logger.error("Pipeline status email failed to send", e);
			throw new MailSendException("Failed to send pipeline status e-mail .", e);
		}
	}
}
