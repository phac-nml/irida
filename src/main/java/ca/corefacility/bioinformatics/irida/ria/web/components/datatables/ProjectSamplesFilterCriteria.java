package ca.corefacility.bioinformatics.irida.ria.web.components.datatables;

import java.util.List;

import com.github.dandelion.datatables.core.ajax.ColumnDef;

/**
 * Handles which fields in the Project Samples Table are filterable.
 */
public class ProjectSamplesFilterCriteria {
	private String name = "";

	public ProjectSamplesFilterCriteria(List<ColumnDef> defs) {
		for (ColumnDef def : defs) {
			switch (def.getName()) {
			case "name":
				this.name = def.getSearch();
				break;
			}
		}
	}

	public String getName() {
		return this.name;
	}
}
