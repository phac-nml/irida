package ca.corefacility.bioinformatics.irida.ria.web.ajax.dto;

import ca.corefacility.bioinformatics.irida.model.NcbiExportSubmission;
import ca.corefacility.bioinformatics.irida.model.project.Project;

/**
 * Represents a {@link NcbiExportSubmission} for viewing by an Administrator.
 */
public class NcbiExportSubmissionAdminTableModel extends NcbiExportSubmissionTableModel {
	private final ExportProject project;

	public NcbiExportSubmissionAdminTableModel(NcbiExportSubmission submission) {
		super(submission);
		this.project = new ExportProject(submission.getProject());
	}


	public ExportProject getProject() {
		return project;
	}

	static class ExportProject {
		private final long id;
		private final String name;

		public ExportProject(Project project) {
			this.id = project.getId();
			this.name = project.getName();
		}

		public long getId() {
			return id;
		}

		public String getName() {
			return name;
		}
	}
}
