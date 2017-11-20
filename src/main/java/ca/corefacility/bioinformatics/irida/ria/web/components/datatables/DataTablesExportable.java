package ca.corefacility.bioinformatics.irida.ria.web.components.datatables;

import java.util.List;
import java.util.Locale;

import org.springframework.context.MessageSource;

public interface DataTablesExportable {
	/**
	 * Convert the attribute of the class into a ordered list.
	 *
	 * @return List of values in order for the specific datatable.
	 */
	List<String> toTableRow();

	/**
	 * Get an ordered list of internationalized table headers for the datatable.
	 *
	 * @param messageSource {@link MessageSource}
	 * @param locale        {@link Locale} for the current user.
	 * @return List of table headers.
	 */
	List<String> getTableHeaders(MessageSource messageSource, Locale locale);
}
