package ca.corefacility.bioinformatics.irida.ria.web.analysis.dto;

import java.util.List;

import ca.corefacility.bioinformatics.irida.model.workflow.analysis.JobError;

/**
 * Used as a response for encapsulating galaxy job errors and the url
 * of the galaxy instance
 */

public class AnalysisJobError {
	private List<JobError> galaxyJobErrors;
	private String galaxyUrl;

	public AnalysisJobError() {
		this.galaxyJobErrors = null;
		this.galaxyUrl = null;
	}

	public AnalysisJobError(List<JobError> galaxyJobErrors, String galaxyUrl) {
		this.galaxyJobErrors = galaxyJobErrors;
		this.galaxyUrl = galaxyUrl;
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

}
