package ca.corefacility.bioinformatics.irida.ria.web.projects.dto.samples;

import java.util.List;

/**
 * DTO to handle filtering samples in a project
 */
public class ProjectSamplesFilter {
	private List<Long> associated;

	public List<Long> getAssociated() {
		return associated;
	}

	public void setAssociated(List<Long> associated) {
		this.associated = associated;
	}
}
