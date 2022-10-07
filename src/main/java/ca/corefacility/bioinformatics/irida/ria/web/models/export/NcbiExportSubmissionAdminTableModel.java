package ca.corefacility.bioinformatics.irida.ria.web.models.export;

import ca.corefacility.bioinformatics.irida.model.NcbiExportSubmission;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.NcbiExportSubmissionTableModel;
import ca.corefacility.bioinformatics.irida.ria.web.models.project.ProjectMinimalModel;

/**
 * Represents a {@link NcbiExportSubmission} for viewing by an Administrator.
 */
public class NcbiExportSubmissionAdminTableModel extends NcbiExportSubmissionTableModel {
	private final ProjectMinimalModel project;

	public NcbiExportSubmissionAdminTableModel(NcbiExportSubmission submission) {
		super(submission);
		this.project = new ProjectMinimalModel(submission.getProject());
	}

	public ProjectMinimalModel getProject() {
		return project;
	}
}
