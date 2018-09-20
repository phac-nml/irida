package ca.corefacility.bioinformatics.irida.ria.web.linelist.dto;

/**
 * {@link UIMetadataFieldDefault}s are metadata fields that must be present on
 * the line list page.
 */
public class UIMetadataFieldDefault extends AbstractUIMetadataField {

	public UIMetadataFieldDefault(String field, String headerName, String type) {
		super(field, headerName, type, false, false);
	}
}
