package ca.corefacility.bioinformatics.irida.ria.web.samples.dto;

import java.util.List;

public class SampleNameCheckRequest {
	List<String> names;
	List<Long> projectIds;

	public List<String> getNames() {
		return names;
	}

	public List<Long> getProjectIds() {
		return projectIds;
	}
}
