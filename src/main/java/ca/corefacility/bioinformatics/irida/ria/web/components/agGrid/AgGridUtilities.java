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
		Converting to camelcase to help with class attribute in {@link UISampleMetadata},
		most importantly to ensure that createdDate and modifiedDate will match up properly
		with what is store in a metadata template.
		 */
		return CaseUtils.toCamelCase(headerName.replaceAll(FIND, REPLACEMENT), false);
	}
}
