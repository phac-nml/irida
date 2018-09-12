package ca.corefacility.bioinformatics.irida.ria.web.models;

import ca.corefacility.bioinformatics.irida.model.sample.MetadataTemplateField;

/**
 * Represents a {@link MetadataTemplateField} (column header) in the Line List table.
 */
public class UIMetadataTemplateField {
	private String field; // The field in the Sample metadata to get the cells data from
	private String headerName; // The name to render in the column header.
	private String type; // TODO: This needs to be added to the MetadataTemplateField and removed from the Entry
	private boolean hide; // If this field should be visible in the table.
	private boolean editable; // If the field can be edited through the linelist table.

	public UIMetadataTemplateField() {
	}

	public UIMetadataTemplateField(MetadataTemplateField field, String headerName, boolean editable, boolean hide) {
		this.field = field.getLabel();
		this.headerName = headerName;
		this.type = field.getType();
		this.hide = hide;
		this.editable = editable;
	}

	public String getField() {
		return field;
	}

	public String getHeaderName() {
		return headerName;
	}

	public String getType() {
		return type;
	}

	public boolean isEditable() {
		return editable;
	}

	public boolean isHide() {
		return hide;
	}
}
