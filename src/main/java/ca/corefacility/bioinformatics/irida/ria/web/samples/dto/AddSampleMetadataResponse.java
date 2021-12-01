package ca.corefacility.bioinformatics.irida.ria.web.samples.dto;

/**
 * Used to handle responses to the UI to add a piece of sample metadata.
 */
public class AddSampleMetadataResponse {
	private Long fieldId;
	private String metadataTemplateField;
	private String metadataEntry;
	private Long entryId;
	private String metadataRestriction;
	private String responseMessage;

	public AddSampleMetadataResponse(Long fieldId, String metadataTemplateField, String metadataEntry, Long entryId,
			String metadataRestriction, String responseMessage) {
		this.fieldId = fieldId;
		this.metadataTemplateField = metadataTemplateField;
		this.metadataEntry = metadataEntry;
		this.entryId = entryId;
		this.metadataRestriction = metadataRestriction;
		this.responseMessage = responseMessage;
	}

	public Long getFieldId() {
		return fieldId;
	}

	public void setFieldId(Long fieldId) {
		this.fieldId = fieldId;
	}

	public String getMetadataTemplateField() {
		return metadataTemplateField;
	}

	public void setMetadataTemplateField(String metadataTemplateField) {
		this.metadataTemplateField = metadataTemplateField;
	}

	public String getMetadataEntry() {
		return metadataEntry;
	}

	public void setMetadataEntry(String metadataEntry) {
		this.metadataEntry = metadataEntry;
	}

	public Long getEntryId() {
		return entryId;
	}

	public void setEntryId(Long entryId) {
		this.entryId = entryId;
	}

	public String getMetadataRestriction() {
		return metadataRestriction;
	}

	public void setMetadataRestriction(String metadataRestriction) {
		this.metadataRestriction = metadataRestriction;
	}

	public String getResponseMessage() {
		return responseMessage;
	}

	public void setResponseMessage(String responseMessage) {
		this.responseMessage = responseMessage;
	}
}
