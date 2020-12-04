package ca.corefacility.bioinformatics.irida.ria.web.components.ant.notification;

/**
 * Consumed by the UI to display a success notification.
 */
public class SuccessNotification extends Notification{

	public SuccessNotification(String message) {
		super(NotificationType.SUCCESS, message);
	}

	public SuccessNotification(String message, String description) {
		super(NotificationType.SUCCESS, message, description);
	}
}
