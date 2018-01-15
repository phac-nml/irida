package ca.corefacility.bioinformatics.irida.ria.web.models.datatables;

import java.util.Date;

import ca.corefacility.bioinformatics.irida.model.workflow.analysis.JobError;
import ca.corefacility.bioinformatics.irida.ria.web.components.datatables.models.DataTablesResponseModel;

/**
 * User interface model for DataTables for an Analysis
 */
public class DTAnalysis implements DataTablesResponseModel {
	private Long id;
	private String name;
	private String submitter;
	private float percentComplete;
	private Date createdDate;
	private String workflow;
	private String state;
	private final JobError jobError;
	private Long duration;
	boolean updatePermission;

	public DTAnalysis(Long id, String name, String submitter, float percentComplete, Date createdDate, String workflow,
			String state, JobError jobError, Long duration, boolean updatePermission) {
		this.id = id;
		this.name = name;
		this.submitter = submitter;
		this.percentComplete = percentComplete;
		this.createdDate = createdDate;
		this.workflow = workflow;
		this.state = state;
		this.jobError = jobError;
		this.duration = duration;
		this.updatePermission = updatePermission;
	}

	@Override
	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getSubmitter() {
		return submitter;
	}

	public float getPercentComplete() {
		return percentComplete;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public String getWorkflow() {
		return workflow;
	}

	public String getState() {
		return state;
	}

	public Long getDuration() {
		return duration;
	}

	public boolean isUpdatePermission() {
		return updatePermission;
	}

	public JobError getJobError() {
		return jobError;
	}
}
