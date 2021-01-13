package ca.corefacility.bioinformatics.irida.ria.web.announcements.dto;

import ca.corefacility.bioinformatics.irida.model.announcements.Announcement;
import ca.corefacility.bioinformatics.irida.model.announcements.AnnouncementUserJoin;

/**
 * Data transfer object for retrieving announcement details from a sql query.
 */

public class AnnouncementUserReadDetails {

	private Announcement announcement;
	private AnnouncementUserJoin announcementUserJoin;

	public AnnouncementUserReadDetails(Announcement announcement, AnnouncementUserJoin announcementUserJoin) {
		this.announcement = announcement;
		this.announcementUserJoin = announcementUserJoin;
	}

	public Announcement getAnnouncement() {
		return announcement;
	}

	public void setAnnouncement(Announcement announcement) {
		this.announcement = announcement;
	}

	public AnnouncementUserJoin getAnnouncementUserJoin() {
		return announcementUserJoin;
	}

	public void setAnnouncementUserJoin(AnnouncementUserJoin announcementUserJoin) {
		this.announcementUserJoin = announcementUserJoin;
	}

}

