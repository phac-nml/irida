package ca.corefacility.bioinformatics.irida.ria.web.announcements.dto;

import ca.corefacility.bioinformatics.irida.model.announcements.Announcement;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.ria.web.models.tables.TableModel;

/**
 * User interface model for administration of {@link Announcement}
 */
public class AnnouncementTableModel extends TableModel {
	private final User user;
	private final String title;
	private final String message;
	private final Boolean priority;

	public AnnouncementTableModel(Announcement announcement) {
		super(announcement.getId(), announcement.getTitle(), announcement.getCreatedDate(), null);

		title = announcement.getTitle();
		message = announcement.getMessage();
		priority = announcement.getPriority();
		user = announcement.getUser();
	}

	public String getTitle() {
		return title;
	}

	public String getMessage() {
		return message;
	}

	public Boolean getPriority() {
		return priority;
	}

	public User getUser() {
		return user;
	}
}
