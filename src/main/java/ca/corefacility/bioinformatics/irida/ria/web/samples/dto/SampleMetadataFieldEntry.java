package ca.corefacility.bioinformatics.irida.ria.web.samples.dto;

public class SampleMetadataFieldEntry {
	private Long fieldId;
	private String metadataTemplateField;
	private String metadataEntry;
	private Long entryId;

	public SampleMetadataFieldEntry(Long fieldId, String metadataTemplateField, String metadataEntry, Long entryId) {
		this.fieldId = fieldId;
		this.metadataTemplateField = metadataTemplateField;
		this.metadataEntry = metadataEntry;
		this.entryId = entryId;
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
}
