package ca.corefacility.bioinformatics.irida.ria.web.sequencingRuns.dto;

import java.util.Date;

import ca.corefacility.bioinformatics.irida.model.run.SequencingRun;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.ria.web.models.TableModel;

/**
 * Model to format a {@link SequencingRun} into a format that can be used in the UI Table.
 */
public class SequencingRunModel extends TableModel {
	private Long id;
	private String sequencerType;
	private String uploadStatus;
	private User user;
	private Date createdDate;

	public SequencingRunModel(SequencingRun run, String uploadStatus) {
		this.id = run.getId();
		this.sequencerType = run.getSequencerType();
		this.uploadStatus = uploadStatus;
		this.user = run.getUser();
		this.createdDate = run.getCreatedDate();
	}

	public Long getId() {
		return id;
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

	public Date getCreatedDate() {
		return createdDate;
	}
}
