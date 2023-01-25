package ca.corefacility.bioinformatics.irida.ria.web.samples.dto;

import java.util.Date;

import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;

/**
 * Used to return analyses ran with {@link Sample} sequencing objects back to the user interface.
 */

public class SampleAnalyses {
	private Long id;
	private String name;
	private String analysisType;
	private Date createdDate;
	private String state;

	public SampleAnalyses(AnalysisSubmission submission, String analysisType) {
		this.id = submission.getId();
		this.name = submission.getName();
		this.analysisType = analysisType;
		this.createdDate = submission.getCreatedDate();
		this.state = submission.getAnalysisState().name();
	}

	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getAnalysisType() {
		return analysisType;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public String getState() {
		return state;
	}
}
