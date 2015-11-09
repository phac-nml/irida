package ca.corefacility.bioinformatics.irida.ria.web.components.datatables;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.github.dandelion.datatables.core.ajax.ColumnDef;
import com.github.dandelion.datatables.extras.spring3.ajax.DatatablesParams;
import com.google.common.collect.ImmutableSet;

/**
 * Static methods to help create Searching criteria for the projects datatable.
 */
public class ProjectsDatatableUtils extends DatatablesUtils {
	private static final Set<String> filterableProjectAttributes = ImmutableSet.of(
			"name",
			"organism"
	);


	/**
	 * Generate a {@link Map} of search criteria.
	 *
	 * @param columnDefs
	 * 		{@link List} {@link DatatablesParams} list of column definitions
	 *
	 * @return {@link Map} of search criteria
	 */
	public static HashMap<String, String> generateSearchMap(List<ColumnDef> columnDefs) {
		HashMap<String, String> searchMap = new HashMap<>();

		columnDefs.stream().filter(def -> def.isFiltered() && filterableProjectAttributes.contains(def.getName()))
				.forEach(def -> searchMap.put(def.getName(), def.getSearch()));
		return searchMap;
	}
}
