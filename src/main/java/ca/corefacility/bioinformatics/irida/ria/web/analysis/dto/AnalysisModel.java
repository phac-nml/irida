package ca.corefacility.bioinformatics.irida.ria.web.analysis.dto;

import java.util.Date;

import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;
import ca.corefacility.bioinformatics.irida.ria.web.models.TableModel;

/**
 * Used to represent an {@link AnalysisSubmission} for consumption by the user interface.
 */
public class AnalysisModel extends TableModel {
	private Long id;
	private String name;
	private Date createdDate;
	private String submitter;
	private AnalysisStateModel state;
	private String type;
	private long duration;
	private boolean modifiable;

	public AnalysisModel(AnalysisSubmission submission, AnalysisStateModel state, Long duration, String type,
			boolean modifiable) {
		this.id = submission.getId();
		this.name = submission.getLabel();
		this.createdDate = submission.getCreatedDate();
		this.submitter = submission.getSubmitter()
				.getLabel();
		this.state = state;
		this.type = type;
		this.duration = duration;
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

	public AnalysisStateModel getState() {
		return state;
	}

	public String getSubmitter() {
		return submitter;
	}

	public String getType() {
		return type;
	}

	public long getDuration() {
		return duration;
	}

	public boolean isModifiable() {
		return modifiable;
	}
}
