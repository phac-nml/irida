package ca.corefacility.bioinformatics.irida.ria.web.components.datatables;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.dandelion.datatables.core.ajax.ColumnDef;
import com.github.dandelion.datatables.extras.spring3.ajax.DatatablesParams;

/**
 * Static methods to help create Searching criteria for the projects datatable.
 */
public class ProjectsDatatableUtils extends DatatablesUtils  {
	public static final int NAME_COLUMN = 1;
	public static final int ORGANISM_COLUMN = 2;

	/**
	 * Generate a {@link Map<String, String>} of search criteria.
	 *
	 * @param columnDefs {@link List<ColumnDef>} {@link DatatablesParams} list of column definitions
	 * @return {@link Map<String, String>} of search criteria
	 */
	public static HashMap<String, String> generateSearchMap(List<ColumnDef> columnDefs) {
		HashMap<String, String> searchMap = new HashMap<>();
		// 1. Name
		if (columnDefs.get(NAME_COLUMN).isFiltered()) {
			searchMap.put("name", columnDefs.get(1).getSearch());
		}
		// 2. Organism
		if (columnDefs.get(ORGANISM_COLUMN).isFiltered()) {
			searchMap.put("organism", columnDefs.get(2).getSearch());
		}
		return searchMap;
	}
}
