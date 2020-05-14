package ca.corefacility.bioinformatics.irida.ria.web.analysis.dto;

import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;
import ca.corefacility.bioinformatics.irida.ria.web.models.tables.TableModel;

/**
 * Used to represent an {@link AnalysisSubmission} for consumption by the user interface.
 */
public class AnalysisModel extends TableModel {
	private String submitter;
	private AnalysisStateModel state;
	private String type;
	private long duration;
	private boolean modifiable;

	public AnalysisModel(AnalysisSubmission submission, AnalysisStateModel state, Long duration, String type,
			boolean modifiable) {
		super(submission.getId(), submission.getLabel(), submission.getCreatedDate(), submission.getModifiedDate());
		this.submitter = submission.getSubmitter()
				.getLabel();
		this.state = state;
		this.type = type;
		this.duration = duration;
		this.modifiable = modifiable;
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
