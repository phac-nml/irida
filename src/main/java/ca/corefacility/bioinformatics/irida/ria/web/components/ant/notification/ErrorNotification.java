package ca.corefacility.bioinformatics.irida.ria.web.components.ant.notification;

/**
 * Consumed by the UI to display an error notification.
 */
public class ErrorNotification extends Notification {

	public ErrorNotification(String message) {
		super(NotificationType.ERROR, message);
	}

	public ErrorNotification(String message, String description) {
		super(NotificationType.ERROR, message, description);
	}
}
