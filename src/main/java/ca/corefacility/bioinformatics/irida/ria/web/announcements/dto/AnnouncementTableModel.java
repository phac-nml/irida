package ca.corefacility.bioinformatics.irida.ria.web.announcements.dto;

import ca.corefacility.bioinformatics.irida.model.announcements.Announcement;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.ria.web.models.tables.TableModel;

/**
 * User interface model for DataTables for administration of {@link Announcement}
 */
public class AnnouncementTableModel extends TableModel {
	private String message;
	private User user;

	public AnnouncementTableModel(Announcement announcement) {
		super(announcement.getId(), announcement.getLabel(), announcement.getCreatedDate(), null);
		// Only display the first line of the message.
		this.message = announcement.getMessage().split("\\r?\\n")[0];
		if(this.message.length() > 80){
			// If the message is still really long just take a substring of it.
			this.message = this.message.substring(0, 79) + " ...";
		}
		this.user = announcement.getUser();
	}

	public String getMessage() {
		return message;
	}

	public User getUser() {
		return user;
	}
}
