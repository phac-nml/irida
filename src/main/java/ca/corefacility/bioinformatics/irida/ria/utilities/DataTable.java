package ca.corefacility.bioinformatics.irida.ria.utilities;

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
	public static final String RESPONSE_PARAM_DRAW = "draw";
	public static final String RESPONSE_PARAM_DATA = "data";
	public static final String RESPONSE_PARAM_RECORDS_TOTAL = "recordsTotal";
	public static final String RESPONSE_PARAM_RECORDS_FILTERED = "recordsFiltered";
}
