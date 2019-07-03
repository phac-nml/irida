package ca.corefacility.bioinformatics.irida.ria.web.analysis.dto;

import java.util.Date;

import ca.corefacility.bioinformatics.irida.model.workflow.analysis.Analysis;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;

public class AnalysisModel {
	private Long id;
	private String name;
	private Date createdDate;

	public AnalysisModel(AnalysisSubmission submission) {
		Analysis analysis = submission.getAnalysis();
		this.id = analysis.getId();
		this.name = analysis.getLabel();
		this.createdDate = analysis.getCreatedDate();
	}

	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public Date getCreatedDate() {
		return createdDate;
	}
}
