package ca.corefacility.bioinformatics.irida.ria.web.analysis.dto;

import ca.corefacility.bioinformatics.irida.model.enums.AnalysisState;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.type.AnalysisType;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;

/**
 *  Used by UI to encapsulate analysis information
 */

public class AnalysisInfo {
	private AnalysisSubmission analysis;
	private String analysisName;
	private AnalysisState analysisState;
	private String analysisType;
	private String analysisViewer;
	private boolean isAdmin;
	private boolean mailConfigured;
	private AnalysisState previousState;
	private Long duration;
	private boolean isCompleted;
	private boolean isError;
	private boolean treeDefault;

	public AnalysisInfo(AnalysisSubmission analysis, String analysisName, AnalysisState analysisState,
			String analysisType, String analysisViewer, boolean isAdmin, boolean mailConfigured,
			AnalysisState previousState, Long duration, boolean isCompleted, boolean isError, boolean treeDefault) {
		this.analysis = analysis;
		this.analysisName = analysisName;
		this.analysisState = analysisState;
		this.analysisType = analysisType;
		this.analysisViewer = analysisViewer;
		this.isAdmin = isAdmin;
		this.mailConfigured = mailConfigured;
		this.previousState = previousState;
		this.duration = duration;
		this.isCompleted = isCompleted;
		this.isError = isError;
		this.treeDefault = treeDefault;
	}

	public AnalysisSubmission getAnalysis() {
		return analysis;
	}

	public void setAnalysis(AnalysisSubmission analysis) {
		this.analysis = analysis;
	}

	public String getAnalysisName() {
		return analysisName;
	}

	public void setAnalysisName(String analysisName) {
		this.analysisName = analysisName;
	}

	public AnalysisState getAnalysisState() {
		return analysisState;
	}

	public void setAnalysisState(AnalysisState analysisState) {
		this.analysisState = analysisState;
	}

	public String getAnalysisType() {
		return analysisType;
	}

	public void setAnalysisType(String analysisType) {
		this.analysisType = analysisType;
	}

	public String getAnalysisViewer() {
		return analysisViewer;
	}

	public void setAnalysisViewer(String analysisViewer) {
		this.analysisViewer = analysisViewer;
	}

	public boolean isAdmin() {
		return isAdmin;
	}

	public void setAdmin(boolean admin) {
		isAdmin = admin;
	}

	public boolean isMailConfigured() {
		return mailConfigured;
	}

	public void setMailConfigured(boolean mailConfigured) {
		this.mailConfigured = mailConfigured;
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

	public boolean isCompleted() {
		return isCompleted;
	}

	public void setCompleted(boolean completed) {
		this.isCompleted = completed;
	}

	public boolean isError() {
		return isError;
	}

	public void setError(boolean error) {
		this.isError = error;
	}

	public boolean treeDefault() {
		return treeDefault;
	}

	public void setTreeDefault(boolean treeDefault) {
		this.treeDefault = treeDefault;
	}
}
