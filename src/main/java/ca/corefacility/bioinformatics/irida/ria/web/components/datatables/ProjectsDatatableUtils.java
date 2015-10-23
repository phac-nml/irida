package ca.corefacility.bioinformatics.irida.ria.web.components.datatables;

import java.util.HashMap;
import java.util.List;

import com.github.dandelion.datatables.core.ajax.ColumnDef;

/**
 * Created by josh on 2015-10-22.
 */
public class ProjectsDatatableUtils extends DatatablesUtils  {
	public static final int NAME_COLUMN = 1;
	public static final int ORGANISM_COLUMN = 2;

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
