package ca.corefacility.bioinformatics.irida.ria.web.ajax.dto;

import ca.corefacility.bioinformatics.irida.model.enums.AnalysisState;

/**
 * Used by the UI to to update the state and duration of an analysis.
 */

public class UpdatedAnalysisProgress {
	private AnalysisState analysisState;
	private AnalysisState previousState;
	private Long duration;
	private Boolean treeDefault;

	public UpdatedAnalysisProgress(AnalysisState analysisState,
			AnalysisState previousState, Long duration, Boolean treeDefault) {

		this.analysisState = analysisState;
		this.previousState = previousState;
		this.duration = duration;
		this.treeDefault = treeDefault;
	}


	public AnalysisState getAnalysisState() {
		return analysisState;
	}

	public void setAnalysisState(AnalysisState analysisState) {
		this.analysisState = analysisState;
	}

	public AnalysisState getPreviousState() {
		return previousState;
	}

	public void setPreviousState(AnalysisState previousState) {
		this.previousState = previousState;
	}

	public Long getDuration() {
		return duration;
	}

	public void setDuration(Long duration) {
		this.duration = duration;
	}

	public Boolean getTreeDefault() {
		return treeDefault;
	}

	public void setTreeDefault(Boolean treeDefault) {
		this.treeDefault = treeDefault;
	}
}
