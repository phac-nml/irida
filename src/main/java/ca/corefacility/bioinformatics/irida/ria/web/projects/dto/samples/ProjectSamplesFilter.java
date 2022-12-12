package ca.corefacility.bioinformatics.irida.ria.web.projects.dto.samples;

import java.util.ArrayList;
import java.util.List;

/**
 * DTO to handle filtering samples in a project
 */
public class ProjectSamplesFilter {
	private List<Long> associated;

	public List<Long> getAssociated() {
		return associated != null ? associated : new ArrayList<>();
	}

	public void setAssociated(List<Long> associated) {
		this.associated = associated;
	}
}
