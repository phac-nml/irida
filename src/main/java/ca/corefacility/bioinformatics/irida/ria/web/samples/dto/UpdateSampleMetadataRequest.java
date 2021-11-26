package ca.corefacility.bioinformatics.irida.ria.web.samples.dto;

/**
 * Used to handle requests from the UI to update a sample metadata field/entry/restriction.
 */
public class UpdateSampleMetadataRequest {
	private Long projectId;
	private Long metadataFieldId;
	private String metadataField;
	private Long metadataEntryId;
	private String metadataEntry;
	private String metadataRestriction;

	public UpdateSampleMetadataRequest() {
	}

	public UpdateSampleMetadataRequest(Long projectId, Long metadataFieldId, String metadataField, Long metadataEntryId,
			String metadataEntry, String metadataRestriction) {
		this.projectId = projectId;
		this.metadataFieldId = metadataFieldId;
		this.metadataField = metadataField;
		this.metadataEntryId = metadataEntryId;
		this.metadataEntry = metadataEntry;
		this.metadataRestriction = metadataRestriction;
	}

	public Long getProjectId() {
		return projectId;
	}

	public void setProjectId(Long projectId) {
		this.projectId = projectId;
	}

	public Long getMetadataFieldId() {
		return metadataFieldId;
	}

	public void setMetadataFieldId(Long metadataFieldId) {
		this.metadataFieldId = metadataFieldId;
	}

	public String getMetadataField() {
		return metadataField;
	}

	public void setMetadataField(String metadataField) {
		this.metadataField = metadataField;
	}

	public Long getMetadataEntryId() {
		return metadataEntryId;
	}

	public void setMetadataEntryId(Long metadataEntryId) {
		this.metadataEntryId = metadataEntryId;
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
