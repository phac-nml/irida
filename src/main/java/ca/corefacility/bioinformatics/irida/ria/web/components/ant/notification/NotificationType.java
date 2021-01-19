package ca.corefacility.bioinformatics.irida.ria.web.components.ant.notification;

/**
 * Different types of notification found in Ant Design
 */
public enum NotificationType {
	SUCCESS("success"),
	ERROR("error"),
	WARN("warn"),
	INFO("info");

	public final String label;

	private NotificationType(String label) {
		this.label = label;
	}

	@Override
	public String toString() {
		return this.label;
	}
}
