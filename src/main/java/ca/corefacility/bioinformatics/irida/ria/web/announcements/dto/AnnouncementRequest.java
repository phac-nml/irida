package ca.corefacility.bioinformatics.irida.ria.web.announcements.dto;

/**
 * Data transfer object for requesting information about an
 * announcement.
 */
public class AnnouncementRequest {
	private Long id;
	private String message;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
