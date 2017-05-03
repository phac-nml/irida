package ca.corefacility.bioinformatics.irida.ria.web.components.datatables;

import javax.servlet.http.HttpServletRequest;

import org.springframework.data.domain.Sort;

/**
 * Created by josh on 2017-05-02.
 */
public class DatatablesColumnOrder {
	private Sort.Direction direction;
	private Integer column;

	private DatatablesColumnOrder(Integer column, Sort.Direction direction) {
		this.direction = direction;
		this.column = column;
	}

	public Sort.Direction getDirection() {
		return direction;
	}

	public Integer getColumn() {
		return column;
	}

	public static DatatablesColumnOrder createColumnOrder(HttpServletRequest request) {
		// TODO: this should be changes once the paging is changes to be an array of columns that are ordered
		String sortString = request.getParameter("order[0][dir]");
		Sort.Direction direction = sortString.equals("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
		Integer column = Integer.valueOf(request.getParameter("order[0][column]"));
		return new DatatablesColumnOrder(column, direction);
	}
}
