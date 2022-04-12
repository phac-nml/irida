package ca.corefacility.bioinformatics.irida.ria.web.projects.dto;

import java.util.List;

public class DownloadRequest {
	private List<Long> sampleIds;

	public List<Long> getSampleIds() {
		return sampleIds;
	}
}
