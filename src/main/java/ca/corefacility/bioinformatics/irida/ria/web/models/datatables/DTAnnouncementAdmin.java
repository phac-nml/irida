package ca.corefacility.bioinformatics.irida.ria.web.models.datatables;

import java.util.Date;

import ca.corefacility.bioinformatics.irida.model.announcements.Announcement;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.ria.web.components.datatables.models.DataTablesResponseModel;

/**
 * User interface model for DataTables for administration of {@link Announcement}
 */
public class DTAnnouncementAdmin implements DataTablesResponseModel {
	private Long id;
	private String message;
	private Date createdDate;
	private User user;

	public DTAnnouncementAdmin(Announcement announcement) {
		this.id = announcement.getId();
		// Only display the first line of the message.
		this.message = announcement.getMessage().split("\\r?\\n")[0];
		if(this.message.length() > 80){
			// If the message is still really long just take a substring of it.
			this.message = this.message.substring(0, 79) + " ...";
		}
		this.createdDate = announcement.getCreatedDate();
		this.user = announcement.getUser();
	}

	@Override
	public Long getId() {
		return id;
	}

	public String getMessage() {
		return message;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public User getUser() {
		return user;
	}
}
