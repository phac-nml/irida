package ca.corefacility.bioinformatics.irida.ria.web.projects.dto.samples;

import java.util.List;

public class SampleIdsRequest {
	private List<Long> associated;

	public List<Long> getAssociated() {
		return associated;
	}

	public void setAssociated(List<Long> associated) {
		this.associated = associated;
	}
}
