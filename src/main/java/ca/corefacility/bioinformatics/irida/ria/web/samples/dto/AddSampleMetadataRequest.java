package ca.corefacility.bioinformatics.irida.ria.web.samples.dto;

/**
 * Used to handle requests from the UI to add a sample metadata.
 */
public class AddSampleMetadataRequest {
	private Long projectId;
	private String metadataField;
	private String metadataEntry;
	private String metadataRestriction;

	public AddSampleMetadataRequest() {
	}

	public AddSampleMetadataRequest(Long projectId, String metadataField, String metadataEntry,
			String metadataRestriction) {
		this.projectId = projectId;
		this.metadataField = metadataField;
		this.metadataEntry = metadataEntry;
		this.metadataRestriction = metadataRestriction;
	}

	public Long getProjectId() {
		return projectId;
	}

	public void setProjectId(Long projectId) {
		this.projectId = projectId;
	}

	public String getMetadataField() {
		return metadataField;
	}

	public void setMetadataField(String metadataField) {
		this.metadataField = metadataField;
	}

	public String getMetadataEntry() {
		return metadataEntry;
	}

	public void setMetadataEntry(String metadataEntry) {
		this.metadataEntry = metadataEntry;
	}

	public String getMetadataRestriction() {
		return metadataRestriction;
	}

	public void setMetadataRestriction(String metadataRestriction) {
		this.metadataRestriction = metadataRestriction;
	}
}
