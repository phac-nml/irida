package ca.corefacility.bioinformatics.irida.ria.web.projects.dto;

import ca.corefacility.bioinformatics.irida.ria.web.models.tables.TableRequest;

/**
 * Used to handle a UI Request for the Project Analyses Outputs pages.
 * This is the basic information required to create the table.
 */
public class ProjectAnalysesOutputsRequest extends TableRequest {
	private ProjectAnalysesOutputsFilters filters;

	public ProjectAnalysesOutputsRequest() {
	}

	public ProjectAnalysesOutputsFilters getFilters() {
		return filters;
	}

	public void setFilters(ProjectAnalysesOutputsFilters projectAnalysesOutputsFilters) {
		this.filters = projectAnalysesOutputsFilters;
	}
}
