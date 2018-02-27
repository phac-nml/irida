package ca.corefacility.bioinformatics.irida.ria.web.models;

import ca.corefacility.bioinformatics.irida.model.sample.MetadataTemplateField;
import ca.corefacility.bioinformatics.irida.model.sample.metadata.MetadataEntry;

/**
 * Model for the user interface for a {@link MetadataEntry}
 */
public class UIMetadataEntryModel {
	private Long fieldId;
	private String value;

	public UIMetadataEntryModel(MetadataEntry entry, MetadataTemplateField field) {
		this.fieldId = field.getId();
		this.value = entry.getValue();
	}

	public Long getFieldId() {
		return fieldId;
	}

	public String getValue() {
		return value;
	}
}
