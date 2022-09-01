package ca.corefacility.bioinformatics.irida.ria.web.ajax.dto;

import ca.corefacility.bioinformatics.irida.model.NcbiExportSubmission;
import ca.corefacility.bioinformatics.irida.ria.web.models.tables.TableModel;
import ca.corefacility.bioinformatics.irida.ria.web.models.user.UserMinimalModel;

/**
 * Represents a {@link NcbiExportSubmission} for use within the UI.
 */
public class NcbiExportSubmissionTableModel extends TableModel {
	private final int exportedSamples;
	private final String state;
	private final UserMinimalModel submitter;
	private final String bioProjectId;


	public NcbiExportSubmissionTableModel (NcbiExportSubmission submission) {
		super(submission.getId(), null, submission.getCreatedDate(), null);
		this.exportedSamples = submission.getBioSampleFiles().size();
		this.state = submission.getUploadState().toString();
		this.bioProjectId = submission.getBioProjectId();
		this.submitter = new UserMinimalModel(submission.getSubmitter());
	}

	public int getExportedSamples() {
		return exportedSamples;
	}

	public String getState() {
		return state;
	}

	public UserMinimalModel getSubmitter() {
		return submitter;
	}

	public String getBioProjectId() {
		return bioProjectId;
	}
}
