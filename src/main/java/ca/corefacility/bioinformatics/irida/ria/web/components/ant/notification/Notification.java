package ca.corefacility.bioinformatics.irida.ria.web.components.ant.notification;

/**
 * Abstract class to represent an Ant Design UI notification.
 */
public abstract class Notification {
	private final NotificationType type;
	private final String message;
	private String description;

	public Notification(NotificationType type, String message) {
		this.type = type;
		this.message = message;
	}

	public Notification(NotificationType type, String message, String description) {
		this.type = type;
		this.message = message;
		this.description = description;
	}

	public String getType() {
		return type.toString();
	}

	public String getMessage() {
		return message;
	}

	public String getDescription() {
		return description;
	}
}
