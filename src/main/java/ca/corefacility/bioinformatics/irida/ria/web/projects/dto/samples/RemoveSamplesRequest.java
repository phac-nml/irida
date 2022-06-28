package ca.corefacility.bioinformatics.irida.ria.web.projects.dto.samples;

import java.util.List;

/**
 * DTO for removing samples from a project
 */
public class RemoveSamplesRequest {
	private List<Long> sampleIds;

	public List<Long> getSampleIds() {
		return sampleIds;
	}
}
