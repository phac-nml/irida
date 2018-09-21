package ca.corefacility.bioinformatics.irida.ria.web.components.agGrid;

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
		return headerName.replaceAll(FIND, REPLACEMENT);
	}

	/**
	 * Convert the field back to the original text.
	 *
	 * @param field {@link String} the AgGrid field
	 * @return {@link String} the original header name
	 */
	public static String convertFieldToHeaderName(String field) {
		return field.replaceAll(REPLACEMENT, FIND);
	}
}
