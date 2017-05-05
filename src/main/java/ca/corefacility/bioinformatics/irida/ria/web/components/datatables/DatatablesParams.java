package ca.corefacility.bioinformatics.irida.ria.web.components.datatables;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;

import com.google.common.base.Strings;

public class DatatablesParams {
	private static final Logger logger = LoggerFactory.getLogger(DatatablesParams.class);
	private static Pattern columnsPattern = Pattern.compile("columns\\[([0-9]*)?\\]\\[([a-z]*)?\\]");

	private Integer start;
	private Integer length;
	private Integer draw;
	private String searchValue;
	private List<DatatablesColumnDef> datatablesColumnDefs;
	private Sort sort;

	private DatatablesParams(Integer start, Integer length, Integer draw, String searchValue,
			List<DatatablesColumnDef> datatablesColumnDefs, Sort sort) {
		this.start = start;
		this.length = length;
		this.draw = draw;
		this.searchValue = searchValue;
		this.datatablesColumnDefs = datatablesColumnDefs;
		this.sort = sort;
	}

	public static DatatablesParams parseDatatablesParams(HttpServletRequest request) {
		Integer start = Integer.valueOf(request.getParameter("start"));
		Integer length = Integer.valueOf(request.getParameter("length"));
		Integer draw = Integer.valueOf(request.getParameter("draw"));
		String searchValue = request.getParameter("search[value]");
		List<DatatablesColumnDef> datatablesColumnDefs = getColumnDefinitions(request);
		Sort sort = getColumnSortData(request, datatablesColumnDefs);
		return new DatatablesParams(start, length, draw, searchValue, datatablesColumnDefs, sort);
	}

	private static Sort getColumnSortData(HttpServletRequest request, List<DatatablesColumnDef> columnDefs) {
		String orderColumnPattern = "order[0][column]";
		String orderDirectionPattern = "order[0][dir]";
		int columnNumber = Integer.parseInt(request.getParameter(orderColumnPattern));
		String directionString = request.getParameter(orderDirectionPattern);

		List<Sort.Order> sortOrder = new ArrayList<>();
		sortOrder.add(new Sort.Order(getSortDirectionFromString(directionString),
				columnDefs.get(columnNumber).getColumnName()));

		boolean notDone = true;
		for (int i = 1; notDone; i++) {
			directionString = request.getParameter(orderDirectionPattern.replace("0", i + ""));
			if (!Strings.isNullOrEmpty(directionString)) {
				columnNumber = Integer.parseInt(request.getParameter(orderColumnPattern.replace("0", i + "")));
				sortOrder.add(new Sort.Order(getSortDirectionFromString(directionString),
						columnDefs.get(columnNumber).getColumnName()));
			} else {
				notDone = false;
			}
		}

		return new Sort(sortOrder);
	}

	private static Sort.Direction getSortDirectionFromString(String direction) {
		return direction.equals("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
	}

	private static List<DatatablesColumnDef> getColumnDefinitions(HttpServletRequest request) {
		Enumeration<String> parameterNames = request.getParameterNames();
		Map<Integer, DatatablesColumnDef> foundMap = new HashMap<>();

		while (parameterNames.hasMoreElements()) {
			String param = parameterNames.nextElement();
			Matcher matcher = columnsPattern.matcher(param);
			while (matcher.find()) {
				// Group 0 = entire
				// Group 1 = column number
				Integer column = Integer.valueOf(matcher.group(1));
				if (!foundMap.containsKey(column)) {
					foundMap.put(column, DatatablesColumnDef.createColumnDefinition(column, request));
				}
			}
		}

		// Keys should always be 0 - n
		List<DatatablesColumnDef> columnDefList = new ArrayList<>();
		for(int i = 0; i < foundMap.keySet().size(); i++) {
			columnDefList.add(foundMap.get(i));
		}

		return columnDefList;
	}

	public int getDraw() {
		return draw;
	}

	public int getLength() {
		return length;
	}

	public int getCurrentPage() {
		return (int) Math.floor(start / length);
	}

	public String getSearchValue() {
		return searchValue;
	}

	public Sort getSort() {
		return sort;
	}
}
