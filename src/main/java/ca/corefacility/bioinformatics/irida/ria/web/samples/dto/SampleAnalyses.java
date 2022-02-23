package ca.corefacility.bioinformatics.irida.ria.web.samples.dto;

import java.util.Date;

import ca.corefacility.bioinformatics.irida.model.sample.Sample;

/**
 * Used to return analyses ran with {@link Sample} sequencing objects back to the user interface.
 */

public class SampleAnalyses {
	private Long id;
	private String name;
	private String analysisType;
	private Date createdDate;
	private String state;

	public SampleAnalyses(Long id, String name, String analysisType, Date createdDate, String state) {
		this.id = id;
		this.name = name;
		this.analysisType = analysisType;
		this.createdDate = createdDate;
		this.state = state;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAnalysisType() {
		return analysisType;
	}

	public void setAnalysisType(String analysisType) {
		this.analysisType = analysisType;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public String getState() { return state; }

	public void setState(String state) { this.state = state; }
}
