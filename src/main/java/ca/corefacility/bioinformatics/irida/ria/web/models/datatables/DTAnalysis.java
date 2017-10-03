package ca.corefacility.bioinformatics.irida.ria.web.models.datatables;

import ca.corefacility.bioinformatics.irida.ria.web.components.datatables.models.DataTablesResponseModel;

import java.util.Date;

public class DTAnalysis implements DataTablesResponseModel {
	private Long id;
	private String name;
	private String submitter;
	private float percentComplete;
	private Date createdDate;
	private String workflow;
	private String state;
	private Long duration;

	public DTAnalysis(Long id, String name, String submitter, float percentComplete, Date createdDate, String workflow,
			String state, Long duration) {
		this.id = id;
		this.name = name;
		this.submitter = submitter;
		this.percentComplete = percentComplete;
		this.createdDate = createdDate;
		this.workflow = workflow;
		this.state = state;
		this.duration = duration;
	}

	@Override
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

	public String getSubmitter() {
		return submitter;
	}

	public void setSubmitter(String submitter) {
		this.submitter = submitter;
	}

	public float getPercentComplete() {
		return percentComplete;
	}

	public void setPercentComplete(float percentComplete) {
		this.percentComplete = percentComplete;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public String getWorkflow() {
		return workflow;
	}

	public void setWorkflow(String workflow) {
		this.workflow = workflow;
	}

	public Long getDuration() {
		return duration;
	}

	public String getState() {
		return state;
	}

	public void setDuration(Long duration) {
		this.duration = duration;
	}
}
