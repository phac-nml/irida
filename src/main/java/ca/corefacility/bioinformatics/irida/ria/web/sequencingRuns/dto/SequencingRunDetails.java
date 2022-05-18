package ca.corefacility.bioinformatics.irida.ria.web.sequencingRuns.dto;

import java.util.Date;
import java.util.Map;
import java.util.Objects;

import ca.corefacility.bioinformatics.irida.model.run.SequencingRun;

/**
 * Data transfer object for retrieving sequencing run details from a sql query.
 */
public class SequencingRunDetails {
	private String description;
	private Date createdDate;
	private String sequencerType;
	private String uploadStatus;
	private Map<String, String> optionalProperties;
	private Long userID;
	private String userName;

	public SequencingRunDetails(SequencingRun run) {
		this.description = run.getDescription();
		this.createdDate = run.getCreatedDate();
		this.sequencerType = run.getSequencerType();
		this.uploadStatus = run.getUploadStatus().toString();
		this.optionalProperties = run.getOptionalProperties();
		this.userID = run.getUser().getId();
		this.userName = run.getUser().getLabel();
	}

	public String getDescription() {
		return description;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public String getSequencerType() {
		return sequencerType;
	}

	public String getUploadStatus() {
		return uploadStatus;
	}

	public Map<String, String> getOptionalProperties() {
		return optionalProperties;
	}

	public Long getUserID() {
		return userID;
	}

	public String getUserName() {
		return userName;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		SequencingRunDetails that = (SequencingRunDetails) o;
		return Objects.equals(description, that.description) && Objects.equals(createdDate, that.createdDate)
				&& Objects.equals(sequencerType, that.sequencerType) && Objects.equals(uploadStatus, that.uploadStatus)
				&& Objects.equals(optionalProperties, that.optionalProperties) && Objects.equals(userID, that.userID)
				&& Objects.equals(userName, that.userName);
	}

	@Override
	public int hashCode() {
		return Objects.hash(description, createdDate, sequencerType, uploadStatus, optionalProperties, userID,
				userName);
	}
}
