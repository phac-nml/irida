package ca.corefacility.bioinformatics.irida.ria.web.models.datatables;

import java.util.Date;

import ca.corefacility.bioinformatics.irida.model.announcements.AnnouncementUserJoin;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.ria.web.components.datatables.models.DataTablesResponseModel;

public class DTAnnouncementUser implements DataTablesResponseModel, Comparable<DTAnnouncementUser> {
	private Long id;
	private final String username;
	private final AnnouncementUserJoin join;
	private final Date createdDate;
	private final boolean hasRead;

	public DTAnnouncementUser(User user, final AnnouncementUserJoin join) {
		this.id = user.getId();
		this.username = user.getUsername();
		this.join = join;
		if (join != null) {
			this.createdDate = join.getCreatedDate();
			hasRead = true;
		} else {
			createdDate = new Date(0);
			hasRead = false;
		}
	}

	@Override
	public Long getId() {
		return this.id;
	}

	public String getUsername() {
		return username;
	}

	public AnnouncementUserJoin getJoin() {
		return join;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public boolean getHasRead() {
		return hasRead;
	}

	/**
	 * Comparator method to compare dates for each read receipt
	 * @param response
	 *      The object to compare to
	 * @return
	 *      -1 if this object is newer than {@param response}
	 *      0 if they have the same date
	 *      1 if {@param response} is newer than this object
	 */
	public int compareTo(DTAnnouncementUser response) {
		return this.createdDate.compareTo(response.createdDate);
	}
}
