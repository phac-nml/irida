package ca.corefacility.bioinformatics.irida.ria.web.analysis.dto;

import java.util.List;

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
