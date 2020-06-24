package ca.corefacility.bioinformatics.irida.ria.web.analysis.dto;

import java.util.List;

import ca.corefacility.bioinformatics.irida.model.workflow.analysis.JobError;

/**
 * Used as a response for encapsulating galaxy job errors, the url
 * of the galaxy instance, and the galaxy history id.
 */

public class AnalysisJobError {
	private List<JobError> galaxyJobErrors;
	private String galaxyUrl;
	private String galaxyHistoryId;

	public AnalysisJobError() {
		this.galaxyJobErrors = null;
		this.galaxyUrl = null;
		this.galaxyHistoryId = null;
	}

	public AnalysisJobError(List<JobError> galaxyJobErrors, String galaxyUrl, String galaxyHistoryId) {
		this.galaxyJobErrors = galaxyJobErrors;
		this.galaxyUrl = galaxyUrl;
		this.galaxyHistoryId = galaxyHistoryId;
	}

	public List<JobError> getGalaxyJobErrors() {
		return galaxyJobErrors;
	}

	public void setGalaxyJobErrors(List<JobError> galaxyJobErrors) {
		this.galaxyJobErrors = galaxyJobErrors;
	}

	public String getGalaxyUrl() {
		return galaxyUrl;
	}

	public void setGalaxyUrl(String galaxyUrl) {
		this.galaxyUrl = galaxyUrl;
	}

	public String getGalaxyHistoryId() {
		return galaxyHistoryId;
	}

	public void setGalaxyHistoryId(String galaxyHistoryId) {
		this.galaxyHistoryId = galaxyHistoryId;
	}
}
