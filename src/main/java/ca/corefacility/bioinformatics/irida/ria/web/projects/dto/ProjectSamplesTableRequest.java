package ca.corefacility.bioinformatics.irida.ria.web.projects.dto;

import ca.corefacility.bioinformatics.irida.ria.web.models.tables.AntTableRequest;
import ca.corefacility.bioinformatics.irida.ria.web.projects.dto.samples.ProjectSamplesFilter;

public class ProjectSamplesTableRequest extends AntTableRequest {
	private ProjectSamplesFilter filters;

	public ProjectSamplesFilter getFilters() {
		return filters != null ? filters : new ProjectSamplesFilter();
	}

	public void setFilters(ProjectSamplesFilter filters) {
		this.filters = filters;
	}
}
