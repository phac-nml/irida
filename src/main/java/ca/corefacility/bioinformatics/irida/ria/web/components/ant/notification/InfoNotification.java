package ca.corefacility.bioinformatics.irida.ria.web.components.ant.notification;

/**
 * Consumed by the UI to display an information notification.
 */
public class InfoNotification extends Notification{

	public InfoNotification(String message) {
		super(NotificationType.INFO, message);
	}

	public InfoNotification(String message, String description) {
		super(NotificationType.INFO, message, description);
	}
}
