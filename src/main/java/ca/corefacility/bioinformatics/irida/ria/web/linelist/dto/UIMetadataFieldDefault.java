package ca.corefacility.bioinformatics.irida.ria.web.linelist.dto;

import ca.corefacility.bioinformatics.irida.ria.web.components.agGrid.AgGridColumn;

/**
 * Represents metadata fields that are not part of the sample metadata, but are included
 * on the line list page.
 */
public class UIMetadataFieldDefault extends AgGridColumn {
	public UIMetadataFieldDefault(String headerName, String field, String type) {
		super(headerName, field, type, false, false);
	}
}
