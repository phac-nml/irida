package ca.corefacility.bioinformatics.irida.ria.web.announcements.dto;

/**
 * Data transfer object for requesting information about an
 * announcement.
 */
public class AnnouncementRequest {
	private Long id;
	private String title;
	private String message;
	private Boolean priority;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Boolean getPriority() {
		return priority;
	}

	public void setPriority(Boolean priority) {
		this.priority = priority;
	}
}
