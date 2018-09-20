package ca.corefacility.bioinformatics.irida.ria.web.linelist.dto;

import ca.corefacility.bioinformatics.irida.ria.web.components.agGrid.AgGridUtilities;

public class AbstractUIMetadataField {
	private String field;
	private String headerName; // The name to render in the column header.
	private String type; // TODO: This needs to be added to the MetadataTemplateField and removed from the Entry
	private boolean hide; // If this field should be visible in the table.
	private boolean editable; // If the field can be edited through the linelist table.

	public AbstractUIMetadataField() {
	}

	AbstractUIMetadataField(String field, String headerName, String type, boolean hide, boolean editable) {
		this.field = field;
		this.headerName = headerName;
		this.field = field;
		this.type = type;
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

	public boolean isHide() {
		return hide;
	}

	public boolean isEditable() {
		return editable;
	}
}
