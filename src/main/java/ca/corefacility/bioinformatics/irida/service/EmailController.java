package ca.corefacility.bioinformatics.irida.service;

import java.util.List;

import org.springframework.mail.MailSendException;

import ca.corefacility.bioinformatics.irida.model.event.ProjectEvent;
import ca.corefacility.bioinformatics.irida.model.user.PasswordReset;
import ca.corefacility.bioinformatics.irida.model.user.User;

/**
 * Interface describing methods for emailing information to the user
 * 
 */
public interface EmailController {

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
	public void sendWelcomeEmail(User user, User sender, PasswordReset passwordReset) throws MailSendException;

	/**
	 * Send a {@link PasswordReset} link to a {@link User}
	 * 
	 * @param user
	 *            The user for the reset
	 * @param passwordReset
	 *            the reset object
	 */
	public void sendPasswordResetLinkEmail(User user, PasswordReset passwordReset) throws MailSendException;

	/**
	 * Send a subscription email to the given {@link User} containing the given
	 * {@link ProjectEvent}s
	 * 
	 * @param user
	 *            The user to email
	 * @param events
	 *            the events to send to the user
	 */
	public void sendSubscriptionUpdateEmail(User user, List<ProjectEvent> events) throws MailSendException;

	/**
	 * Send an e-mail to the administrative user with an exception when there's a serious storage related exception.
	 * 
	 * @param adminEmailAddress 
	 * 			  the address to which notifications should be sent.
	 * @param rootCause
	 *            the exception to send to the user.
	 */
	public void sendFilesystemExceptionEmail(final String adminEmailAddress, final Exception rootCause) throws MailSendException;

	/**
	 * Is the mail server configured?
	 * 
	 * @return {@link Boolean#TRUE} if configured, {@link Boolean#FALSE} if not.
	 */
	public Boolean isMailConfigured();

}
