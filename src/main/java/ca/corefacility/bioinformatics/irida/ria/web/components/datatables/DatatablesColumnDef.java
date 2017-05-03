package ca.corefacility.bioinformatics.irida.ria.web.components.datatables;

import javax.servlet.http.HttpServletRequest;

import org.springframework.data.domain.Sort;

import com.google.common.base.Strings;

public class DatatablesColumnDef {
	private String name;
	private boolean orderable;
	private boolean searchable;
	private boolean filtered;
	private String regex;
	private String search;
	private String searchFrom;
	private String searchTo;
	private Sort.Direction sortDirection;

	private DatatablesColumnDef(String name) {
		this.name = name;
	}

	public String getColumnName() {
		return name;
	}

	public static DatatablesColumnDef createColumnDefinition(Integer column, HttpServletRequest request) {
		String prefix = "columns[" + column + "]";
		String name = request.getParameter(prefix + "[name]");
		if (Strings.isNullOrEmpty(name)) {
			name = request.getParameter(prefix + "[data]");
		}
//		Boolean orderable = Boolean.valueOf(request.getParameter(prefix + "[orderable]"));
//		Boolean searchable = Boolean.valueOf(request.getParameter(prefix + "[searchable]"));
		return new DatatablesColumnDef(name);
	}
}
