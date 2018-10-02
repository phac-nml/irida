package ca.corefacility.bioinformatics.irida.ria.web.components.agGrid;

import org.apache.commons.text.CaseUtils;

/**
 * Utility functions for the UI Ag Grid.
 */
public class AgGridUtilities {
	private final static String FIND = "\\.";
	private final static String REPLACEMENT = "----";

	/**
	 * Fields should not have periods in them (unless the field is nested inside a JSON object).
	 *
	 * @param headerName {@link String} the header text for the column
	 * @return {@link String}
	 */
	public static String convertHeaderNameToField(String headerName) {
		/*
		Fields are attributes on AgGrid column definitions which tells the table which field in the
		row object (UISampleMetadata) to use as the data for that particular column.  By converting
		the header to camel case it facilitate the direct change from the MetadataTemplateField label
		to the field definition for AgGrid.
		E.g. The label "Created Date" will be converted into the field "createdDate" which would match
		with a UISampleMetadata object attribute "createdDate" when serialized to JSON.
		 */
		return CaseUtils.toCamelCase(headerName.replaceAll(FIND, REPLACEMENT), false);
	}
}
