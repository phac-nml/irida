package ca.corefacility.bioinformatics.irida.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.Query;

import ca.corefacility.bioinformatics.irida.model.NcbiExportSubmission;
import ca.corefacility.bioinformatics.irida.model.enums.ExportUploadState;

/**
 * Repository for storing and reading {@link NcbiExportSubmission}s
 */
public interface NcbiExportSubmissionRepository extends IridaJpaRepository<NcbiExportSubmission, Long> {

	/**
	 * Get a List of {@link NcbiExportSubmission} object with the given {@link ExportUploadState}
	 * @param state {@link ExportUploadState} to search for
	 * @return a List of {@link NcbiExportSubmission}
	 */
	@Query("FROM NcbiExportSubmission s WHERE s.uploadState=?1")
	public List<NcbiExportSubmission> getSubmissionsWithState(ExportUploadState state);
}
