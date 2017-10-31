package ca.corefacility.bioinformatics.irida.ria.web.models.datatables;

import ca.corefacility.bioinformatics.irida.model.NcbiExportSubmission;
import ca.corefacility.bioinformatics.irida.model.enums.ExportUploadState;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.ria.web.components.datatables.models.DataTablesResponseModel;

import java.util.Date;

/**
 * Datatables representation of a {@link NcbiExportSubmission}
 */
public class DTExportSubmission implements DataTablesResponseModel {
	private NcbiExportSubmission submission;

	public DTExportSubmission(NcbiExportSubmission submission) {
		this.submission = submission;
	}

	@Override
	public Long getId() {
		return submission.getId();
	}

	public int getSampleCount() {
		return submission.getBioSampleFiles().size();
	}

	public ExportUploadState getUploadState() {
		return submission.getUploadState();
	}

	public Date getCreatedDate() {
		return submission.getCreatedDate();
	}

	public Project getProject() {
		return submission.getProject();
	}
}
