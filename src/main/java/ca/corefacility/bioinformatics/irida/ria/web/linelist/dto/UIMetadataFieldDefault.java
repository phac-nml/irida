package ca.corefacility.bioinformatics.irida.ria.web.linelist.dto;

import ca.corefacility.bioinformatics.irida.ria.web.components.agGrid.AgGridColumn;

/**
 * Represents metadata fields that are not part of the sample metadata, but are included
 * on the line list page.
 */
public class UIMetadataFieldDefault extends AgGridColumn {
	private String field;

	public UIMetadataFieldDefault(String field, String headerName, String type) {
		super(headerName, type, false, false);
		this.setLockPinned(true);
		this.setLockPosition(true);
		this.field = field;
	}

	@Override
	public String getField() {
		return field;
	}
}
