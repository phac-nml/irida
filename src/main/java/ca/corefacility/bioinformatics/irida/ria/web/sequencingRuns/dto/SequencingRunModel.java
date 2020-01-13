package ca.corefacility.bioinformatics.irida.ria.web.sequencingRuns.dto;

import ca.corefacility.bioinformatics.irida.model.run.SequencingRun;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.ria.web.models.tables.TableModel;

/**
 * Model to format a {@link SequencingRun} into a format that can be used in the UI Table.
 */
public class SequencingRunModel extends TableModel {
	private String sequencerType;
	private String uploadStatus;
	private User user;

	public SequencingRunModel(SequencingRun run, String uploadStatus) {
		super(run.getId(), run.getLabel(), run.getCreatedDate(), run.getModifiedDate());
		this.sequencerType = run.getSequencerType();
		this.uploadStatus = uploadStatus;
		this.user = run.getUser();
	}

	public String getSequencerType() {
		return sequencerType;
	}

	public String getUploadStatus() {
		return uploadStatus;
	}

	public User getUser() {
		return user;
	}

}
