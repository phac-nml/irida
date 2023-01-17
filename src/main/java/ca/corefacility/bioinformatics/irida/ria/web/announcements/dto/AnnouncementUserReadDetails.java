package ca.corefacility.bioinformatics.irida.ria.web.announcements.dto;

import java.util.Comparator;
import java.util.Date;

import com.google.common.collect.ComparisonChain;

/**
 * Data transfer object for retrieving announcement details from a sql query.
 */

public class AnnouncementUserReadDetails implements Comparable<AnnouncementUserReadDetails> {

	private Long announcementID;
	private String title;
	private boolean priority;
	private Date createdDate;
	private Date readDate;

	public AnnouncementUserReadDetails(Long announcementID, String title, boolean priority, Date createdDate,
			Date readDate) {
		this.announcementID = announcementID;
		this.title = title;
		this.priority = priority;
		this.createdDate = createdDate;
		this.readDate = readDate;
	}

	@Override
	public int compareTo(AnnouncementUserReadDetails other) {
		return ComparisonChain.start()
				.compareFalseFirst(isRead(), other.isRead())
				.compareTrueFirst(getPriority(), other.getPriority())
				.compare(other.getCreatedDate(), getCreatedDate(), Comparator.nullsLast(Comparator.naturalOrder()))
				.result();
	}

	public Long getAnnouncementID() {
		return announcementID;
	}

	public String getTitle() {
		return title;
	}

	public boolean getPriority() {
		return priority;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public Date getReadDate() {
		return readDate;
	}

	public boolean isRead() {
		return readDate != null;
	}

}