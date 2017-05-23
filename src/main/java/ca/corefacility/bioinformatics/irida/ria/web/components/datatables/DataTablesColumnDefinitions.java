package ca.corefacility.bioinformatics.irida.ria.web.components.datatables;

import javax.servlet.http.HttpServletRequest;

import org.springframework.data.domain.Sort;

import ca.corefacility.bioinformatics.irida.ria.web.components.datatables.config.DataTablesRequest;
import com.google.common.base.Strings;

/**
 * Responsible for extracting and representing DataTables Columns
 * <pre>
 * 	columns[n][data]:0					- Column's data source (https://datatables.net/reference/option/columns.data)
 *  columns[n][name]:					- Column's name (https://datatables.net/reference/option/columns.name)
 *  columns[n][searchable]:true			- Flag to indicate if this column is globally searchable (true) or not (false).
 *  columns[n][orderable]:false			- Flag to indicate if this column is orderable (true) or not (false).
 *  columns[n][search][value]:			- Search value to apply to this specific column.
 *  columns[n][search][regex]:false 	- Flag to indicate if the search term for this column should be treated as
 *  									  regular expression (true) or not (false).
 * </pre>
 *
 * @see <a href="https://datatables.net/manual/server-side">Server-side processing</a>
 */
public class DataTablesColumnDefinitions {
	private String name;
	private boolean orderable;
	private boolean searchable;
	private boolean filtered;
	private String regex;
	private String search;
	private String searchFrom;
	private String searchTo;
	private Sort.Direction sortDirection;

	private DataTablesColumnDefinitions(String name, boolean searchable, boolean orderable) {
		this.name = name;
		this.searchable = searchable;
		this.orderable = orderable;
	}

	/**
	 * Static initializer.  Parses {@link HttpServletRequest} to get the DataTables parameters for the Column.
	 *
	 * @param index
	 * 		{@link Integer} index for the current column.
	 * @param request
	 * 		{@link HttpServletRequest} current server request, containing a {@link DataTablesRequest}
	 *
	 * @return {@link DataTablesColumnDefinitions} definition for the column at the index.
	 */
	static DataTablesColumnDefinitions createColumnDefinition(Integer index, HttpServletRequest request) {
		String prefix = "columns[" + index + "]";
		String name = request.getParameter(prefix + "[name]");
		if (Strings.isNullOrEmpty(name)) {
			name = request.getParameter(prefix + "[data]");
		}
		boolean searchable = Boolean.parseBoolean(request.getParameter(prefix + "[searchable]"));
		boolean orderable = Boolean.parseBoolean(request.getParameter(prefix + "[orderable]"));
		return new DataTablesColumnDefinitions(name, searchable, orderable);
	}

	/**
	 * Column Name
	 *
	 * @return {@link String}
	 * @see <a href="https://datatables.net/reference/option/columns.name">columns.name</a>
	 */
	public String getColumnName() {
		return name;
	}

	/**
	 * Column Orderable: end user's ability to order this column.
	 *
	 * @return {@link Boolean} true if the column is orderable.
	 * @see <a href="https://datatables.net/reference/option/columns.orderable">columns.orderable</a>
	 */
	public boolean isOrderable() {
		return orderable;
	}

	/**
	 * Column searchable
	 * Defined if DataTables should include this column in the filterable data in the table.
	 *
	 * @return {@link Boolean} true if the column is searchable
	 * @see <a href="https://datatables.net/reference/option/columns.searchable">solumns.searchable</a>
	 */
	public boolean isSearchable() {
		return searchable;
	}
}
