package ca.corefacility.bioinformatics.irida.ria.web.admin.dto;

/**
 * Used by the UI to to get updated analyses statistics.
 */

public class AnalysesStatistics {
	private Long numAnalyses;

	public AnalysesStatistics(Long numAnalyses) {
		this.numAnalyses = numAnalyses;
	}

	public Long getNumAnalyses() {
		return numAnalyses;
	}

	public void setNumAnalyses(Long numAnalyses) {
		this.numAnalyses = numAnalyses;
	}
}
