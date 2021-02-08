package ca.corefacility.bioinformatics.irida.ria.web.announcements.dto;

import ca.corefacility.bioinformatics.irida.model.announcements.Announcement;
import ca.corefacility.bioinformatics.irida.model.announcements.AnnouncementUserJoin;
import com.google.common.collect.ComparisonChain;

/**
 * Data transfer object for retrieving announcement details from a sql query.
 */

public class AnnouncementUserReadDetails implements Comparable<AnnouncementUserReadDetails>{

	private Announcement announcement;
	private AnnouncementUserJoin announcementUserJoin;

	public AnnouncementUserReadDetails(Announcement announcement, AnnouncementUserJoin announcementUserJoin) {
		this.announcement = announcement;
		this.announcementUserJoin = announcementUserJoin;
	}

	@Override
	public int compareTo(AnnouncementUserReadDetails other) {
		return ComparisonChain.start().compareTrueFirst(isRead(), other.isRead()).compareTrueFirst(announcement.getPriority(), other.getAnnouncement().getPriority()).compare(other.getAnnouncement().getCreatedDate(), announcement.getCreatedDate()).result();
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

	public boolean isRead() {
		return announcementUserJoin != null;
	}

}

