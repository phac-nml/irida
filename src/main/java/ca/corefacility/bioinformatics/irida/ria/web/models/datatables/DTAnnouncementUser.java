package ca.corefacility.bioinformatics.irida.ria.web.models.datatables;

import java.util.Date;

import ca.corefacility.bioinformatics.irida.model.announcements.Announcement;
import ca.corefacility.bioinformatics.irida.model.announcements.AnnouncementUserJoin;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.ria.web.components.datatables.models.DataTablesResponseModel;

/**
 * User interface model for DataTables for viewing {@link Announcement} User interface model for DataTables for viewing
 * {@link Announcement}
 */
public class DTAnnouncementUser implements DataTablesResponseModel {
	private Long id;
	private User user;
	private Date dateRead;

	public DTAnnouncementUser(User user, AnnouncementUserJoin join) {
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
