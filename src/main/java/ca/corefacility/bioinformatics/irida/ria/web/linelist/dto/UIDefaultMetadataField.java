package ca.corefacility.bioinformatics.irida.ria.web.linelist.dto;

/**
 * {@link UIDefaultMetadataField}s are metadata fields that must be present on
 * the line list page.
 */
public class UIDefaultMetadataField extends UIMetadataField {

	public UIDefaultMetadataField(String field, String headerName, String type) {
		super(field, headerName, type, false, false);
	}
}
