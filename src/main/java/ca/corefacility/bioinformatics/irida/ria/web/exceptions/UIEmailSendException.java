package ca.corefacility.bioinformatics.irida.ria.web.exceptions;

import org.springframework.mail.MailSendException;

/**
 * Exception to be thrown by the UI when an email failed to send.
 */
public class UIEmailSendException extends MailSendException {
	public UIEmailSendException(String errorMessage) {
		super(errorMessage);
	}
}
