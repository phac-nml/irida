package ca.corefacility.bioinformatics.irida.ria.web.ajax.dto;

import ca.corefacility.bioinformatics.irida.ria.web.analysis.dto.AnalysisStateModel;

/**
 * Used by the UI to to update the state and duration of an analysis.
 */

public class UpdatedAnalysisTableProgress {
	private AnalysisStateModel analysisStateModel;
	private Long duration;
	private boolean isCompleted;
	private boolean isError;

	public UpdatedAnalysisTableProgress(AnalysisStateModel analysisStateModel, Long duration, boolean isCompleted, boolean isError) {
		this.analysisStateModel = analysisStateModel;
		this.duration = duration;
		this.isCompleted = isCompleted;
		this.isError = isError;
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

	public boolean isCompleted() {
		return isCompleted;
	}

	public void setCompleted(boolean completed) {
		isCompleted = completed;
	}

	public boolean isError() {
		return isError;
	}

	public void setError(boolean error) {
		isError = error;
	}
}
