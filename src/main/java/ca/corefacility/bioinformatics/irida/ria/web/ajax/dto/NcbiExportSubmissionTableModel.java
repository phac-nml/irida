package ca.corefacility.bioinformatics.irida.ria.web.ajax.dto;

import ca.corefacility.bioinformatics.irida.model.NcbiExportSubmission;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.ria.web.models.tables.TableModel;

/**
 * Represents a {@link NcbiExportSubmission} for use within the UI.
 */
public class NcbiExportSubmissionTableModel extends TableModel {
	private final int exportedSamples;
	private final String state;
	private final Submitter submitter;
	private final String bioProjectId;


	public NcbiExportSubmissionTableModel (NcbiExportSubmission submission) {
		super(submission.getId(), null, submission.getCreatedDate(), null);
		this.exportedSamples = submission.getBioSampleFiles().size();
		this.state = submission.getUploadState().toString();
		this.bioProjectId = submission.getBioProjectId();
		this.submitter = new Submitter(submission.getSubmitter());
	}

	public int getExportedSamples() {
		return exportedSamples;
	}

	public String getState() {
		return state;
	}

	public Submitter getSubmitter() {
		return submitter;
	}

	public String getBioProjectId() {
		return bioProjectId;
	}

	static class Submitter {
		private final long id;
		private final String name;

		public Submitter(User user) {
			this.id = user.getId();
			this.name = user.getLabel();
		}

		 public long getId() {
			 return id;
		 }

		 public String getName() {
			 return name;
		 }
	 }
}
