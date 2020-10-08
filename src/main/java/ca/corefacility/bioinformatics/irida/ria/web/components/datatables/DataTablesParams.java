package ca.corefacility.bioinformatics.irida.ria.web.components.datatables;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.springframework.data.domain.Sort;

import ca.corefacility.bioinformatics.irida.ria.web.components.datatables.config.DataTablesRequest;

import com.google.common.base.Strings;

/**
 * Represents all parameters within a DataTables request.
 * <pre>
 * draw                  - Draw counter. This is used by DataTables to ensure that the Ajax returns
 *                         from server-side processing requests are drawn in sequence by DataTables.
 * start                 - Paging first record indicator. This is the start point in the current
 *                         data set (0 index based - i.e. 0 is the first record).
 * length                - Number of records that the table can display in the current draw.
 * search[value]         - Global search value. To be applied to all columns which have
 *                         searchable as true.
 * search[regex]         - true if the global filter should be treated as a regular expression for
 *                         advanced searching, false otherwise.
 * </pre>
 *
 * @see <a href="https://datatables.net/manual/server-side">Server Side</a>
 */
public class DataTablesParams {
	// This is used to match DataTables columns parameter
	// 	e.g. columns[1][name]
	//		 - matching at 1 ==> column number
	//       - matching at 2 ==> which data item (name, seachable, orderable, data)
	private static Pattern columnsPattern = Pattern.compile("columns\\[([0-9]*)?\\]\\[([a-z]*)?\\]");

	private Integer start;
	private Integer length;
	private Integer draw;
	private String searchValue;
	private Sort sort;
	private Map<String, String> searchMap;

	public DataTablesParams() {
	}

	public DataTablesParams(Integer start, Integer length, Integer draw, String searchValue, Sort sort, Map<String, String> searchMap) {
		this.start = start;
		this.length = length;
		this.draw = draw;
		this.searchValue = searchValue;
		this.sort = sort;
		this.searchMap = searchMap;
	}

	/**
	 * Static initializer to get the params for all {@link DataTablesRequest}
	 *
	 * @param request
	 * 		{@link HttpServletRequest} request containing {@link DataTablesParams}
	 * @return {@link DataTablesParams}
	 */
	public static DataTablesParams parseDataTablesParams(HttpServletRequest request) {
		Integer start = Integer.valueOf(request.getParameter("start"));
		Integer length = Integer.valueOf(request.getParameter("length"));
		Integer draw = Integer.valueOf(request.getParameter("draw"));
		String searchValue = request.getParameter("search[value]");
		List<DataTablesColumnDefinitions> dataTablesColumnDefinitions = getColumnDefinitions(request);

		// Get any column specific search data
		Map<String, String> searchMap = new HashMap<>();
		for (DataTablesColumnDefinitions def : dataTablesColumnDefinitions) {
			if (!Strings.isNullOrEmpty(def.getSearchValue())) {
				searchMap.put(def.getColumnName(), def.getSearchValue());
			}
		}

		Sort sort = getColumnSortData(request, dataTablesColumnDefinitions);
		return new DataTablesParams(start, length, draw, searchValue, sort, searchMap);
	}

	/**
	 * Get the DataTables {@link Sort} information from the {@link HttpServletRequest}
	 *
	 * @param request
	 * 		{@link HttpServletRequest} current {@link DataTablesParams} request.
	 * @param columnDefinitions
	 * 		{@link List} of {@link DataTablesColumnDefinitions}
	 * @return {@link Sort} of containing all {@link DataTablesColumnDefinitions} sort information.
	 */
	private static Sort getColumnSortData(HttpServletRequest request,
			List<DataTablesColumnDefinitions> columnDefinitions) {
		String orderColumnPattern = "order[0][column]";
		String orderDirectionPattern = "order[0][dir]";
		int columnNumber = Integer.parseInt(request.getParameter(orderColumnPattern));
		String directionString = request.getParameter(orderDirectionPattern);

		List<Sort.Order> sortOrder = new ArrayList<>();
		sortOrder.add(new Sort.Order(getSortDirectionFromString(directionString),
				columnDefinitions.get(columnNumber).getColumnName()));

		boolean notDone = true;
		for (int i = 1; notDone; i++) {
			directionString = request.getParameter(orderDirectionPattern.replace("0", i + ""));
			if (!Strings.isNullOrEmpty(directionString)) {
				columnNumber = Integer.parseInt(request.getParameter(orderColumnPattern.replace("0", i + "")));
				sortOrder.add(new Sort.Order(getSortDirectionFromString(directionString),
						columnDefinitions.get(columnNumber).getColumnName()));
			} else {
				notDone = false;
			}
		}

		return Sort.by(sortOrder);
	}

	/**
	 * Convert string sort direction information into a {@link Sort.Direction}
	 *
	 * @param direction
	 * 		{@link String} value of the sort.
	 * 		Expect either "asc" ord "desc"
	 * @return {@link Sort.Direction}
	 */
	private static Sort.Direction getSortDirectionFromString(String direction) {
		return direction.equals("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
	}

	/**
	 * Extract the DataTables column definitions from the {@link HttpServletRequest}
	 *
	 * @param request
	 * 		{@link HttpServletRequest} current {@link DataTablesParams} request.
	 * @return {@link List} of {@link DataTablesColumnDefinitions}p
	 */
	private static List<DataTablesColumnDefinitions> getColumnDefinitions(HttpServletRequest request) {
		Enumeration<String> parameterNames = request.getParameterNames();
		Map<Integer, DataTablesColumnDefinitions> foundMap = new HashMap<>();

		// Iterate over all the named parameters.  If it matches what a column parameter should look like
		// Create the column definition.
		while (parameterNames.hasMoreElements()) {
			String param = parameterNames.nextElement();
			Matcher matcher = columnsPattern.matcher(param);
			while (matcher.find()) {
				// Group 0 = entire
				// Group 1 = column number
				Integer column = Integer.valueOf(matcher.group(1));
				if (!foundMap.containsKey(column)) {
					foundMap.put(column, DataTablesColumnDefinitions.createColumnDefinition(column, request));
				}
			}
		}

		// Keys should always be 0 - n
		List<DataTablesColumnDefinitions> dataTablesColumnDefinitions = new ArrayList<>();
		for (int i = 0; i < foundMap.keySet().size(); i++) {
			dataTablesColumnDefinitions.add(foundMap.get(i));
		}

		return dataTablesColumnDefinitions;
	}

	/**
	 * Get the draw counter.  This is used by DataTables to ensure that the Ajax returns from server-side
	 * processing requests are drawn in sequence by DataTables (Ajax requests are asynchronous and thus can return
	 * out of sequence). This is used as part of the draw return parameter.
	 *
	 * @return {@link Integer} draw counter
	 */
	public int getDraw() {
		return draw;
	}

	/**
	 * Number of records that the table can display in the current draw.
	 *
	 * @return {@link Integer} number of records in the current page.
	 */
	public int getLength() {
		return length;
	}

	/**
	 * Get the currently viewed page in the DataTable.
	 *
	 * @return {@link Integer} Current page
	 */
	public int getCurrentPage() {
		return (int) Math.floor(start / length);
	}

	/**
	 * Get the value from the global search.
	 *
	 * @return {@link String} search value
	 */
	public String getSearchValue() {
		return searchValue;
	}

	/**
	 * Get the current table sort properties.
	 *
	 * @return {@link Sort} current table sort.
	 */
	public Sort getSort() {
		return sort;
	}

	/**
	 * Get all the searches on this columns in this table
	 * @return {@link Map}
	 */
	public Map<String, String> getSearchMap() {
		return searchMap;
	}
}
