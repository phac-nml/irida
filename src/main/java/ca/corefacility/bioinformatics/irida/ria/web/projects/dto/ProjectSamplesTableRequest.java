package ca.corefacility.bioinformatics.irida.ria.web.projects.dto;

import java.util.List;

import ca.corefacility.bioinformatics.irida.ria.web.models.tables.AntTableRequest;

public class ProjectSamplesTableRequest extends AntTableRequest {
	private List<Long> associated;

	public List<Long> getAssociated() {
		return associated;
	}

	public void setAssociated(List<Long> associated) {
		this.associated = associated;
	}
}
