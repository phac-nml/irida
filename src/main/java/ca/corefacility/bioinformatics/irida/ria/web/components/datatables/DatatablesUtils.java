package ca.corefacility.bioinformatics.irida.ria.web.components.datatables;

import org.springframework.data.domain.Sort;

import com.github.dandelion.datatables.core.ajax.ColumnDef;
import com.github.dandelion.datatables.core.ajax.DatatablesCriterias;

/**
 * Handles decipher requests made using {@link DatatablesCriterias}
 */
public class DatatablesUtils {

	/**
	 * Switch the {@link DatatablesCriterias} {@link ColumnDef.SortDirection} for a {@link Sort.Direction}
	 *
	 * @param criterias {@link DatatablesCriterias}
	 * @return {@link Sort.Direction}
	 */
	public static Sort.Direction getSortDirection(DatatablesCriterias criterias) {
		ColumnDef sortedColumn = criterias.getSortedColumnDefs().get(0);
		return sortedColumn.getSortDirection().equals(ColumnDef.SortDirection.ASC) ? Sort.Direction.ASC : Sort.Direction.DESC;
	}

	/**
	 * Determine the current page based on {@link DatatablesCriterias}
	 *
	 * @param criterias {@link DatatablesCriterias}
	 * @return {@link Integer} the current page of the datatable
	 */
	public static int getCurrentPage(DatatablesCriterias criterias) {
		int pageSize = criterias.getLength() > 0 ? criterias.getLength() : 20;
		return (int) Math.floor(criterias.getStart() / pageSize);
	}
}
