package ca.corefacility.bioinformatics.irida.ria.web.linelist.dto;

import ca.corefacility.bioinformatics.irida.model.sample.MetadataTemplateField;
import ca.corefacility.bioinformatics.irida.model.sample.metadata.MetadataEntry;

/**
 * {@link MetadataEntry} displayed in the line list table must be in the format of
 * {field: value}.  The 'field' must match that of the {@link MetadataTemplateField}'s
 * AgGrid does not allow for special characters in this, so this is really a helper class
 * to ensure that the field is formatted properly.
 */
public class UIMetadataEntry extends AbstractTableItem {
	private String value;

	public UIMetadataEntry(MetadataTemplateField field, MetadataEntry entry) {
		super(field.getLabel());
		this.value = entry.getValue();
	}

	public String getValue() {
		return value;
	}
}
