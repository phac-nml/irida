package ca.corefacility.bioinformatics.irida.ria.web.components.ant.notification;

/**
 * Consumed by the UI to display a warning notification.
 */
public class WarnNotification extends Notification{

	public WarnNotification(String message) {
		super(NotificationType.WARN, message);
	}

	public WarnNotification(String message, String description) {
		super(NotificationType.WARN, message, description);
	}
}
