package ca.corefacility.bioinformatics.irida.service.export;

import java.util.Collection;
import java.util.List;

import ca.corefacility.bioinformatics.irida.model.NcbiExportSubmission;
import ca.corefacility.bioinformatics.irida.model.enums.ExportUploadState;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.service.CRUDService;

/**
 * Service for exporting {@link SequenceFile} data to NCBI
 */
public interface NcbiExportSubmissionService extends CRUDService<Long, NcbiExportSubmission> {
	/**
	 * Get a List of {@link NcbiExportSubmission} object with the given
	 * {@link ExportUploadState}
	 * 
	 * @param state
	 *            {@link ExportUploadState} to search for
	 * @return a List of {@link NcbiExportSubmission}
	 */
	public List<NcbiExportSubmission> getSubmissionsWithState(ExportUploadState state);

	/**
	 * Get a List of {@link NcbiExportSubmission} objects which have one of the
	 * given {@link ExportUploadState}s
	 * 
	 * @param states
	 *            collection of {@link ExportUploadState} to search for
	 * @return a List of {@link NcbiExportSubmission}
	 */
	public List<NcbiExportSubmission> getSubmissionsWithState(Collection<ExportUploadState> states);

	/**
	 * Get a List of {@link NcbiExportSubmission} for the given {@link Project}
	 * 
	 * @param project
	 *            The {@link Project} for the submission
	 * @return a List of {@link NcbiExportSubmission}
	 */
	public List<NcbiExportSubmission> getSubmissionsForProject(Project project);
}
