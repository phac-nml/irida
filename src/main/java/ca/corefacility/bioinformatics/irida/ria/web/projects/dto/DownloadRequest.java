package ca.corefacility.bioinformatics.irida.ria.web.projects.dto;

import java.util.List;

/**
 * Request information to download samples from the project samples table
 */
public class DownloadRequest {
	private List<Long> sampleIds;

	public List<Long> getSampleIds() {
		return sampleIds;
	}
}
