package ca.corefacility.bioinformatics.irida.ria.web.components.datatables.models;

import java.util.Date;

import com.google.common.base.Strings;

/**
 * Parent class of all export model to hold utility functions.
 */
public abstract class AbstractExportModel {

	/**
	 * Check if the ID of the object is null.  If null, return empty string.
	 *
	 * @param id the ID to check
	 * @return the value or empty string
	 */
	protected String checkNullId(Long id) {
		return id != null ? Long.toString(id) : "";
	}

	/**
	 * Check if a string is null or empty.  If null, return empty string.
	 *
	 * @param item the item to check
	 * @return the value or empty string
	 */
	protected String checkNullStrings(String item) {
		return Strings.isNullOrEmpty(item) ? "" : item;
	}

	/**
	 * Check if a date is null.  If null, return empty string.
	 *
	 * @param date the date to check.
	 * @return the date as a string or an empty string.
	 */
	protected String checkNullDate(Date date) {
		return date != null ? date.toString() : "";
	}
}
