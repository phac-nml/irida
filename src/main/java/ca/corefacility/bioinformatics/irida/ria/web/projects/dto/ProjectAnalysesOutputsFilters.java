package ca.corefacility.bioinformatics.irida.ria.web.projects.dto;

import java.util.List;

/**
 * Represents the filters on the Project Analyses Shared
 * and Automated single sample outputs tables.
 */
public class ProjectAnalysesOutputsFilters {
	private String name;

	public String getName() {
		return name;
	}

	public void setName(List<String> name) {
		this.name = name.size() > 0 ?
				name.get(0)
						.trim() :
				null;
	}
}
