package ca.corefacility.bioinformatics.irida.ria.web.components.datatables;

import org.springframework.data.domain.Sort;

import com.github.dandelion.datatables.core.ajax.ColumnDef;
import com.github.dandelion.datatables.core.ajax.DatatablesCriterias;

/**
 * Created by josh on 2015-10-22.
 */
public class DatatablesUtils {
	public static Sort.Direction getSortDirection(DatatablesCriterias criterias) {
		ColumnDef sortedColumn = criterias.getSortedColumnDefs().get(0);
		return sortedColumn.getSortDirection().equals(ColumnDef.SortDirection.ASC) ? Sort.Direction.ASC : Sort.Direction.DESC;
	}

	public static int getCurrentPage(DatatablesCriterias criterias) {
		int pageSize = criterias.getLength() > 0 ? criterias.getLength() : 20;
		return (int) Math.floor(criterias.getStart() / pageSize);
	}
}
