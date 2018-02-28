package ca.corefacility.bioinformatics.irida.ria.web.models;

import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.MetadataTemplateField;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sample.metadata.MetadataEntry;

/**
 * Model for the user interface for a {@link MetadataEntry}
 */
public class UIMetadataEntryModel {
	private Long fieldId;
	private String key;
	private String value;

	public UIMetadataEntryModel(Project project, Sample sample, MetadataEntry entry, MetadataTemplateField field) {
		this.fieldId = field.getId();
		this.key = project.getId() + "-" + sample.getId() + "-" + field.getId();
		this.value = entry.getValue();
	}

	public Long getFieldId() {
		return fieldId;
	}

	public String getKey() {
		return key;
	}

	public String getValue() {
		return value;
	}
}
