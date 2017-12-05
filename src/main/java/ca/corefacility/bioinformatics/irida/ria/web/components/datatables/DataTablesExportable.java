package ca.corefacility.bioinformatics.irida.ria.web.components.datatables;

import java.util.List;
import java.util.Locale;

import org.springframework.context.MessageSource;

/**
 * This interface is responsible for enforcing that classes implementing
 * {@link ca.corefacility.bioinformatics.irida.ria.web.components.datatables.models.DataTablesResponseModel} and
 * need to be exportable will be able to explicitly state which attributes and in which order they need to be exported.
 */
public interface DataTablesExportable {
	/**
	 * Convert the attribute of the class into a ordered list.
	 *
	 * @return List of values in order for the specific datatable.
	 */
	List<String> getExportableTableRow();

	/**
	 * Get an ordered list of internationalized table headers for the datatable.
	 *
	 * @param messageSource {@link MessageSource}
	 * @param locale        {@link Locale} for the current user.
	 * @return List of table headers.
	 */
	List<String> getExportableTableHeaders(MessageSource messageSource, Locale locale);
}
