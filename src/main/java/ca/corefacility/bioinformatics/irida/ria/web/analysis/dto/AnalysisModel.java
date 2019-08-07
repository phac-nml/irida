package ca.corefacility.bioinformatics.irida.ria.web.analysis.dto;

import java.util.Date;

import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;

/**
 * Used to represent an {@link AnalysisSubmission} for consumption by the user interface.
 */
public class AnalysisModel {
	private Long id;
	private String name;
	private Date createdDate;
	private String submitter;
	private String state;
	private String type;
	private float percentage;
	private long duration;
	private boolean modifiable;

	public AnalysisModel(AnalysisSubmission submission, String state, Long duration, String type, float percentage,
			boolean modifiable) {
		this.id = submission.getId();
		this.name = submission.getLabel();
		this.createdDate = submission.getCreatedDate();
		this.submitter = submission.getSubmitter()
				.getLabel();
		this.state = state;
		this.type = type;
		this.duration = duration;
		this.percentage = percentage;
		this.modifiable = modifiable;
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

	public String getState() {
		return state;
	}

	public String getSubmitter() {
		return submitter;
	}

	public String getType() {
		return type;
	}

	public float getPercentage() {
		return percentage;
	}

	public long getDuration() {
		return duration;
	}

	public boolean isModifiable() {
		return modifiable;
	}
}
