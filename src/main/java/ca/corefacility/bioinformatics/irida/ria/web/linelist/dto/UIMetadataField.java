package ca.corefacility.bioinformatics.irida.ria.web.linelist.dto;

/**
 * This is a generic class to represent all possible headers (MetadataFields)
 * in a line list.
 */
public class UIMetadataField extends AbstractTableItem {
	private String headerName; // The name to render in the column header.
	private String type; // TODO: This needs to be added to the MetadataTemplateField and removed from the Entry
	private boolean hide; // If this field should be visible in the table.
	private boolean editable; // If the field can be edited through the linelist table.

	public UIMetadataField() {
		super("");
	}

	public UIMetadataField(String field, String headerName, String type, boolean hide, boolean editable) {
		super(field);
		this.headerName = headerName;
		this.type = type;
		this.hide = hide;
		this.editable = editable;
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
