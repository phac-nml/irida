package ca.corefacility.bioinformatics.irida.ria.web.announcements.dto;

import ca.corefacility.bioinformatics.irida.model.announcements.Announcement;
import ca.corefacility.bioinformatics.irida.model.announcements.AnnouncementUserJoin;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.ria.web.models.tables.TableModel;

import java.util.Date;

/**
 * User interface model for viewing {@link Announcement}
 */

public class AnnouncementUserTableModel extends TableModel {
	private Long id;
	private final User user;
	private Date dateRead;

	public AnnouncementUserTableModel(User user, AnnouncementUserJoin join) {
		super(user.getId(), user.getUsername(), user.getCreatedDate(), user.getModifiedDate());

		this.id = user.getId();
		this.user = user;
		if (join != null) {
			this.dateRead = join.getCreatedDate();
		}
	}

	@Override
	public Long getId() {
		return this.id;
	}

	public User getUser() {
		return user;
	}

	public Date getDateRead() {
		return dateRead;
	}
}



