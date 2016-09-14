package ca.corefacility.bioinformatics.irida.ria.web.components.datatables.models;

import java.util.Date;

import com.google.common.base.Strings;

/**
 * Parent class of all export model to hold utility functions.
 */
public abstract class AbstractExportModel {

	protected String checkNullId(Long id) {
		return id != null ? Long.toString(id) : "";
	}

	protected String checkNullStrings(String item) {
		return Strings.isNullOrEmpty(item) ? "" : item;
	}

	protected String checkNullDate(Date date) {
		return date != null ? date.toString() : "";
	}
}
