package ca.corefacility.bioinformatics.irida.service.impl;

import ca.corefacility.bioinformatics.irida.config.services.WebEmailConfig.ConfigurableJavaMailSender;
import ca.corefacility.bioinformatics.irida.model.enums.AnalysisState;
import ca.corefacility.bioinformatics.irida.model.event.*;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.user.PasswordReset;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;
import ca.corefacility.bioinformatics.irida.service.EmailController;
import com.google.common.collect.ImmutableMap;
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

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.*;

/* ISS */
import javax.mail.util.ByteArrayDataSource;
import javax.mail.internet.InternetAddress;

/**
 * This class is responsible for all email sent to the server that are templated with Thymeleaf.
 */
@Component
@Profile({ "prod", "dev", "web", "analysis", "ncbi", "processing", "sync" })
public class EmailControllerImpl implements EmailController {
	private static final Logger logger = LoggerFactory.getLogger(EmailControllerImpl.class);

	public static final String WELCOME_TEMPLATE = "welcome-email";
	public static final String RESET_TEMPLATE = "password-reset-link";
	public static final String SUBSCRIPTION_TEMPLATE = "subscription-email";
	public static final String PIPELINE_STATUS_TEMPLATE = "pipeline-status-email";
	public static final String ANALYSIS_TEMPLATE = "endofanalysis-email";
	public static final String SYNC_EXPIRED_TEMPLATE = "sync-expired";

	@Value("${mail.server.email}")
	private String serverEmail;

	@Value("${server.base.url}")
	private String serverURL;

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
	 * @param events Page of {@link ProjectEvent}s
	 * @return A List<Map<String,Object>> containing the events and fragment names
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
			message.setText(
					"An exeption related to storage has occurred that requires your attention, stack as follows: "
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
			message.setText(
					"An exeption occurred when attempting to communicate with NCBI's SRA.  Submission " + submissionId
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

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void sendEndOfAnalysisEmail(String recipients, String analysisName, String sampleCode, String sampleSpecies, String clusterId, String clusterCriterium, String clusters) throws MailSendException {
		logger.debug("Sending end-of-analysis email for " + sampleCode + " to " + recipients);

		Locale locale = LocaleContextHolder.getLocale();
        int msgpriority;
        String header;

		String sampleSpeciesShort = sampleSpecies;
		switch (sampleSpecies) {
			case "Shiga toxin-producing Escherichia coli": 
				 sampleSpeciesShort = "STEC";
				 break;
			case "Listeria monocytogenes": 
				 sampleSpeciesShort = "Listeria";
				 break;
			case "Coronavirus": 
				 sampleSpeciesShort = "SARS-CoV-2";
				 break;
		}

		analysisName = analysisName.replace(", dummy.fastq", "");
		final Context ctx = new Context(locale);
		ctx.setVariable("ngsEmail", serverEmail);
		ctx.setVariable("serverURL", serverURL);
		ctx.setVariable("analysisName", analysisName);
		if (sampleSpecies.equals("Coronavirus")) {
			//clusterId = Notifica; clusterCriterium=jsonstring; clusters=lineage,clade,variante,sprotein,nconsensus
			String[] variants = clusters.split("/");
			String strClusters = "";
			String strHeader = "";
			if (clusterCriterium.contains("Sanger")) {
				//Sanger
				strClusters = "Il campione " + sampleCode + " appartiene al lignaggio " + variants[0] + " e al clade " + variants[1] + ". (Le sequenze del solo gene S potrebbero non consentire un'attribuzione accurata del lignaggio). Le mutazioni nella proteina S sono: " + variants[3];
				header = sampleSpeciesShort + ": " + variants[0] + " - " + variants[1];
			}
			else {
				strClusters = "Il campione " + sampleCode + " appartiene al lignaggio " + variants[0] + " e al clade " + variants[1] + ". Le mutazioni nella proteina S sono: " + variants[3];
				float n_perc = Float.parseFloat(variants[4].substring(variants[4].lastIndexOf("(") + 1, variants[4].length() - 2));
				if (n_perc > 5.0) { 
					strClusters = strClusters + " - Attenzione, analisi incerta: il numero di N nel consenso supera il 5%! " + variants[4].substring(variants[4].lastIndexOf("("));
				}
				header = sampleSpeciesShort + ": " + variants[0] + " - " + variants[1];
			}

			if (clusterId.equals("-")) {
				msgpriority = 3;
			}
			else {
				msgpriority = 1;
				String headerVariante = "";
				if (!variants[2].equals("-")) { headerVariante = variants[2]; }
				if (!clusterId.equals("Si")) { headerVariante = clusterId; }
				header = sampleSpeciesShort + ": Attenzione " + headerVariante + "! " + variants[0] + " - " + variants[1];
			}
			ctx.setVariable("header", header);
			ctx.setVariable("clusters", strClusters);
			ctx.setVariable("disclaimer", "L’ultima versione di Pangolin  v4.0, adottata per le analisi corse sulla piattaforma I-Co-Gen, ha introdotto importanti modifiche agli algoritmi per l’assegnazione dei lignaggi su base filogenetica. Il fatto che la nuova strategia di analisi non sia ancora completamente consolidata, può portare a fluttuazioni temporanee nelle identificazioni delle varianti.<br/>Alla luce della situazione attuale di possibile circolazione di ceppi virali ricombinanti è inoltre <u>importante ricordare la necessità di sequenziare l’intero genoma</u>. Per le sequenze del solo gene S così come nel caso di sequenze genomiche frammentate e/o con uno score di qualità “failed” (elevato numero di N), l’esito della corretta classificazione di un ceppo ricombinante potrebbe avere un incerto supporto statistico.");
		}
		else {
			if (!clusterId.equals("-")) {
				if (clusterId.contains("_ext")) {
					msgpriority = 2;
					String[] neighbours = clusters.split(",");
					if (clusterId.equals("-_ext")) {
						header = sampleSpeciesShort + ": Vicino ad altri campioni";
						String strNeighbours = neighbours[0] + " (" + neighbours[1].trim() + "), " + neighbours[2] + " (" + neighbours[3].trim() + "), " + neighbours[4] + " (" + neighbours[5].trim() + ")";
						ctx.setVariable("header", header);
						ctx.setVariable("clusters", "Il campione " + sampleCode + " dista " + neighbours[6].trim() + " o meno alleli da altri campioni. I tre campioni più vicini con il numero di alleli di differenza sono: " + strNeighbours + ".");
						ctx.setVariable("disclaimer","");
					} else { 
						header = sampleSpeciesShort + ": Vicino ad un cluster";
						ctx.setVariable("header", header);
						ctx.setVariable("clusters", "Il campione " + sampleCode + " dista " + neighbours[6].trim() + " o meno alleli dal cluster " + clusterId + ".");
						ctx.setVariable("disclaimer","");
					}
				} else { 
					msgpriority = 1;
					header = sampleSpeciesShort + ": Cluster!";
					ctx.setVariable("header", header);
					ctx.setVariable("clusters", "Il campione " + sampleCode + " fa parte del cluster " + clusterId + " insieme ai seguenti campioni distanti " + clusterCriterium + " alleli o meno: " + clusters + ".");
					ctx.setVariable("disclaimer","");
				}
			}
			else {
				msgpriority = 3;
				String[] neighbours = clusters.split(",");
				if (neighbours[0].equals("ERROR")) {
					header = sampleSpeciesShort + ": Errore";
					ctx.setVariable("header", header);
					ctx.setVariable("clusters", "Errore durante le analisi dei cluster");
					ctx.setVariable("disclaimer","");
				}
				else {
					if (neighbours[0].equals("-")) {
						header = sampleSpeciesShort + ": Primo campione del sierogruppo";
						ctx.setVariable("header", header);
						ctx.setVariable("clusters", "Primo campione del sierogruppo");
						ctx.setVariable("disclaimer","");
					}
					else {
						if (neighbours[0].equals("RERUN")) {
							header = sampleSpeciesShort + ": cgMLST errore mapping";
							ctx.setVariable("header", header);
							ctx.setVariable("clusters", "cgMLST ha mappato meno del 80% dei loci, il campione non è stato incluso nella cluster analysis");
							ctx.setVariable("disclaimer","");
						}
						else {
							String strNeighbours = neighbours[0] + " (" + neighbours[1].trim() + "), " + neighbours[2] + " (" + neighbours[3].trim() + "), " + neighbours[4] + " (" + neighbours[5].trim() + ")";
							header = sampleSpeciesShort + ": No cluster";
							ctx.setVariable("header", header);
							ctx.setVariable("clusters", "Il campione " + sampleCode + " non fa parte di nessun cluster. I tre campioni più vicini con il numero di alleli di differenza sono: " + strNeighbours + ".");
							ctx.setVariable("disclaimer","");
						}
					}
				}
			}
		}
		try {
			final MimeMessage mimeMessage = this.javaMailSender.createMimeMessage();
			final MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true, "UTF-8");
			message.setSubject(messageSource.getMessage("email.analysis.subject", null, locale) + " " + header);
			message.setFrom(serverEmail);
			message.setTo(InternetAddress.parse("aries@iss.it"));
			message.setBcc(InternetAddress.parse(recipients));
            message.setPriority(msgpriority);
			if (sampleSpecies.equals("Coronavirus")) {
				final ByteArrayDataSource attach = new ByteArrayDataSource(clusterCriterium, "application/json");
				message.addAttachment("irida-aries_" + sampleCode + ".json", attach);
			}

			final String htmlContent = templateEngine.process(ANALYSIS_TEMPLATE, ctx);
			message.setText(htmlContent, true);
			javaMailSender.send(mimeMessage);
		} catch (final Exception e) {
			logger.error("End-of-analysis email failed to send", e);
			throw new MailSendException("Failed to send e-mail.", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void sendProjectSyncUnauthorizedEmail(Project project) {
		User syncUser = null;
		try {
			syncUser = project.getRemoteStatus()
					.getReadBy();
		} catch (NullPointerException e) {
			logger.error("Tried to email a sync user for a non-remote project", e);
		}

		Locale locale = LocaleContextHolder.getLocale();

		String projectSettingsUrl = serverURL + "/projects/" + project.getId() + "/settings/remote";

		final Context ctx = new Context(locale);

		ctx.setVariable("failedProject", project.getName());
		ctx.setVariable("user", syncUser.getUsername());
		ctx.setVariable("settingsLink", projectSettingsUrl);

		try {
			final MimeMessage mimeMessage = this.javaMailSender.createMimeMessage();
			final MimeMessageHelper message = new MimeMessageHelper(mimeMessage, "UTF-8");
			message.setSubject(
					messageSource.getMessage("email.syncexpired.subject", new Object[] { project.getName() }, locale));
			message.setFrom(serverEmail);
			message.setTo(syncUser.getEmail());

			final String htmlContent = templateEngine.process(SYNC_EXPIRED_TEMPLATE, ctx);
			message.setText(htmlContent, true);

			javaMailSender.send(mimeMessage);

			message.setText(htmlContent, true);

			javaMailSender.send(mimeMessage);
		} catch (Exception e) {
			logger.error("Error trying to send sync failed email.", e);
			throw new MailSendException("Failed to send e-mail for sync failure.", e);
		}
	}
}
