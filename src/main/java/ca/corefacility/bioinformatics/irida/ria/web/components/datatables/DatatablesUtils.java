package ca.corefacility.bioinformatics.irida.ria.web.components.datatables;

import java.util.Map;

import org.springframework.data.domain.Sort;

import com.github.dandelion.datatables.core.ajax.ColumnDef;
import com.github.dandelion.datatables.core.ajax.DatatablesCriterias;
import com.google.common.collect.ImmutableMap;

/**
 * Handles decipher requests made using {@link DatatablesCriterias}
 */
public class DatatablesUtils {
	public static String SORT_DIRECTION = "direction";
	public static String SORT_STRING = "sort_string";

	/**
	 * Switch the {@link DatatablesCriterias} {@link ColumnDef.SortDirection} for a {@link Sort.Direction}
	 *
	 * @param criterias {@link DatatablesCriterias}
	 * @return {@link Sort.Direction}
	 */
	public static Map<String, Object> getSortProperties(DatatablesCriterias criterias) {
		ColumnDef sortedColumn = criterias.getSortedColumnDefs().get(0);
		return ImmutableMap.of(
				SORT_DIRECTION, sortedColumn.getSortDirection().equals(ColumnDef.SortDirection.ASC) ?
						Sort.Direction.ASC :
						Sort.Direction.DESC,
				SORT_STRING, sortedColumn.getName()
		);
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
