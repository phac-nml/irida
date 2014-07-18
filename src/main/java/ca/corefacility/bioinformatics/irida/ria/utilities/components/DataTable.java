package ca.corefacility.bioinformatics.irida.ria.utilities.components;

import org.springframework.data.domain.Sort;

/**
 * Static string used in the ResponseBody for DataTables calls.
 *
 * @author Josh Adam <josh.adam@phac-aspc.gc.ca>
 */
public class DataTable {
	/**
	 * Request Parameters
	 */
	public static final String REQUEST_PARAM_START = "start";
	public static final String REQUEST_PARAM_LENGTH = "length";
	public static final String REQUEST_PARAM_DRAW = "draw";
	public static final String REQUEST_PARAM_SEARCH_VALUE = "search[value]";
	public static final String REQUEST_PARAM_SORT_COLUMN = "order[0][column]";
	public static final String REQUEST_PARAM_SORT_DIRECTION = "order[0][dir]";

	/**
	 * Response Parameters
	 */
	public static final String RESPONSE_PARAM_DRAW = REQUEST_PARAM_DRAW;
	public static final String RESPONSE_PARAM_SORT_COLUMN = REQUEST_PARAM_SORT_COLUMN;
	public static final String RESPONSE_PARAM_SORT_DIRECTION = REQUEST_PARAM_SORT_DIRECTION;
	public static final String RESPONSE_PARAM_DATA = "data";
	public static final String RESPONSE_PARAM_RECORDS_TOTAL = "recordsTotal";
	public static final String RESPONSE_PARAM_RECORDS_FILTERED = "recordsFiltered";

	/**
	 * Helper method to get the sort direction for the column.
	 * 
	 * @param directionString
	 *            Expect either "asc" or "desc" for direction of sort.
	 * @return {@link org.springframework.data.domain.Sort.Direction}
	 */
	public static Sort.Direction getSortDirection(String directionString) {
		return directionString.equals("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
	}

	/**
	 * Helper method to get the page number based on the pageSize of the list and
	 * the current start
	 * 
	 * @param start The first object at the start of the currently display DataTables row.
	 * @param length The length of the DataTables page.
	 * @return Current page number
	 */
	public static int getPageNumber(int start, int length) {
		return start / length;
	}
}
