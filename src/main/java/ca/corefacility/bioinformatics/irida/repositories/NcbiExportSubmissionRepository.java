package ca.corefacility.bioinformatics.irida.repositories;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.Query;

import ca.corefacility.bioinformatics.irida.model.NcbiExportSubmission;
import ca.corefacility.bioinformatics.irida.model.enums.ExportUploadState;
import ca.corefacility.bioinformatics.irida.model.project.Project;

/**
 * Repository for storing and reading {@link NcbiExportSubmission}s
 */
public interface NcbiExportSubmissionRepository extends IridaJpaRepository<NcbiExportSubmission, Long> {

	/**
	 * Get a List of {@link NcbiExportSubmission} object with the given
	 * {@link ExportUploadState}
	 * 
	 * @param state
	 *            {@link ExportUploadState} to search for
	 * @return a List of {@link NcbiExportSubmission}
	 */
	@Query("FROM NcbiExportSubmission s WHERE s.uploadState IN ?1")
	public List<NcbiExportSubmission> getSubmissionsWithState(Set<ExportUploadState> state);

	/**
	 * Get a List of {@link NcbiExportSubmission} for the given {@link Project}
	 * 
	 * @param project
	 *            The {@link Project} for the submission
	 * @return a List of {@link NcbiExportSubmission}
	 */
	@Query("FROM NcbiExportSubmission s WHERE s.project=?1")
	public List<NcbiExportSubmission> getSubmissionsForProject(Project project);
}
