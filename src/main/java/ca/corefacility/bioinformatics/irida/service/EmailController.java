package ca.corefacility.bioinformatics.irida.service;

import java.util.List;

import ca.corefacility.bioinformatics.irida.model.project.Project;
import org.springframework.mail.MailSendException;

import ca.corefacility.bioinformatics.irida.model.event.ProjectEvent;
import ca.corefacility.bioinformatics.irida.model.user.PasswordReset;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;

/**
 * Interface describing methods for emailing information to the user
 */
public interface EmailController {

	/**
	 * Send welcome email to a user who joined the platform
	 *
	 * @param user          The {@link User} that was just created
	 * @param sender        The {@link User} that created the new user
	 * @param passwordReset A {@link PasswordReset} object to send an activation link
	 * @throws MailSendException if the email failed to send
	 */
	public void sendWelcomeEmail(User user, User sender, PasswordReset passwordReset) throws MailSendException;

	/**
	 * Send a {@link PasswordReset} link to a {@link User}
	 *
	 * @param user          The user for the reset
	 * @param passwordReset the reset object
	 * @throws MailSendException if the email failed to send
	 */
	public void sendPasswordResetLinkEmail(User user, PasswordReset passwordReset) throws MailSendException;

	/**
	 * Send a subscription email to the given {@link User} containing the given {@link ProjectEvent}s
	 *
	 * @param user   The user to email
	 * @param events the events to send to the user
	 * @throws MailSendException if the email failed to send
	 */
	public void sendSubscriptionUpdateEmail(User user, List<ProjectEvent> events) throws MailSendException;

	/**
	 * Send an e-mail to the administrative user with an exception when there's a serious storage related exception.
	 *
	 * @param adminEmailAddress the address to which notifications should be sent.
	 * @param rootCause         the exception to send to the user.
	 * @throws MailSendException if the email failed to send
	 */
	public void sendFilesystemExceptionEmail(final String adminEmailAddress, final Exception rootCause)
			throws MailSendException;

	/**
	 * Send an email to the administrators with an exception when there's an error uploading data or getting upload
	 * status from NCBI's SRA.
	 *
	 * @param adminEmailAddress Address of the admin to email to
	 * @param rootCause         exception to display in the email
	 * @param submissionId      the ID of the NCBI export submission that failed
	 * @throws MailSendException If there's an error sending the message
	 */
	public void sendNCBIUploadExceptionEmail(final String adminEmailAddress, final Exception rootCause,
			Long submissionId) throws MailSendException;

	/**
	 * Is the mail server configured?
	 *
	 * @return {@link Boolean#TRUE} if configured, {@link Boolean#FALSE} if not.
	 */
	public Boolean isMailConfigured();

	/**
	 * Send pipeline status email to a user when a pipeline that they have launched is completed or has an error
	 *
	 * @param submission The {@link AnalysisSubmission} that the pipeline status email will be sent for
	 * @throws MailSendException if the email failed to send
	 */
	public void sendPipelineStatusEmail(AnalysisSubmission submission) throws MailSendException;

	/**
	 * Send a notification email to the given {@link User} for the given analysis {@link String}
	 *
	 * @param user   The user to email
	 * @param analysisName the name of the analysis
	 * @throws MailSendException if the email failed to send
	 */
	public void sendEndOfAnalysisEmail(String recipientEmailAddresses, String analysisName, String sampleCode, String sampleSpecies, String clusterId, String clusters, String jsonstring) throws MailSendException;

	/**
	 * Email user responsible for a synchronized project when the sync job's credentials expire
	 *
	 * @param project The project that sync failed for
	 */
	public void sendProjectSyncUnauthorizedEmail(Project project);
}
