package ca.corefacility.bioinformatics.irida.ria.web.analysis.dto;

import java.util.List;

/**
 * UI Response for the current page of the Analyses Table.
 */
public class AnalysesListResponse {
	private List<AnalysisModel> analyses;
	private Long total;

	public AnalysesListResponse(List<AnalysisModel> analyses, Long total) {
		this.analyses = analyses;
		this.total = total;
	}

	public List<AnalysisModel> getAnalyses() {
		return analyses;
	}

	public Long getTotal() {
		return total;
	}
}
