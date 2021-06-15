package ca.corefacility.bioinformatics.irida.ria.web.ajax.dto;

import ca.corefacility.bioinformatics.irida.model.workflow.analysis.AnalysisOutputFile;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;
import ca.corefacility.bioinformatics.irida.ria.web.models.tables.TableModel;

/**
 * Data transfer objected used by the UI on the shared and automated
 * single sample outputs tables
 */

public class ProjectAnalysesSampleOutputModel extends TableModel {
	private String submitter;
	private String sampleName;
	private String analysisType;
	private AnalysisSubmission analysisSubmission;
	private String pipelineName;


	public ProjectAnalysesSampleOutputModel(AnalysisSubmission analysisSubmission, AnalysisOutputFile analysisOutputFile, String sampleName, String analysisType, String pipelineName) {
		super(analysisOutputFile.getId(), analysisOutputFile.getLabel(), analysisOutputFile.getCreatedDate(), null);
		this.submitter = analysisSubmission.getSubmitter().getLabel();
		this.analysisType = analysisType;
		this.sampleName = sampleName;
		this.analysisSubmission = analysisSubmission;
		this.pipelineName = pipelineName;
	}

	public String getSubmitter() {
		return submitter;
	}

	public void setSubmitter(String submitter) {
		this.submitter = submitter;
	}

	public String getSampleName() {
		return sampleName;
	}

	public void setSampleName(String sampleName) {
		this.sampleName = sampleName;
	}

	public String getAnalysisType() {
		return analysisType;
	}

	public void setAnalysisType(String analysisType) {
		this.analysisType = analysisType;
	}

	public AnalysisSubmission getAnalysisSubmission() {
		return analysisSubmission;
	}

	public void setAnalysisSubmission(AnalysisSubmission analysisSubmission) {
		this.analysisSubmission = analysisSubmission;
	}

	public String getPipelineName() {
		return pipelineName;
	}

	public void setPipelineName(String pipelineName) {
		this.pipelineName = pipelineName;
	}
}
