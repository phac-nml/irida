package ca.corefacility.bioinformatics.irida.ria.web.ajax.dto;

import ca.corefacility.bioinformatics.irida.ria.web.analysis.dto.AnalysisStateModel;

public class UpdatedAnalysisTableProgress {
	private AnalysisStateModel analysisStateModel;
	private Long duration;

	public UpdatedAnalysisTableProgress(AnalysisStateModel analysisStateModel, Long duration) {
		this.analysisStateModel = analysisStateModel;
		this.duration = duration;
	}

	public AnalysisStateModel getAnalysisStateModel() {
		return analysisStateModel;
	}

	public void setAnalysisStateModel(AnalysisStateModel analysisStateModel) {
		this.analysisStateModel = analysisStateModel;
	}

	public Long getDuration() {
		return duration;
	}

	public void setDuration(Long duration) {
		this.duration = duration;
	}
}
