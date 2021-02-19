package ca.corefacility.bioinformatics.irida.repositories.analysis.submission;

import java.util.List;

import org.springframework.data.jpa.repository.Query;

import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmissionTempFile;
import ca.corefacility.bioinformatics.irida.repositories.IridaJpaRepository;

/**
 * A repository for managing {@link AnalysisSubmissionTempFile} objects.
 */

public interface AnalysisSubmissionTempFileRepository extends IridaJpaRepository<AnalysisSubmissionTempFile, Long> {

	/**
	 * Get all {@link AnalysisSubmissionTempFile} objects by submission id.
	 *
	 * @param analysisSubmissionId The analysis submission id
	 * @return a list of {@link AnalysisSubmissionTempFile}
	 */
	@Query("FROM AnalysisSubmissionTempFile f WHERE f.analysisSubmissionId = ?1")
	List<AnalysisSubmissionTempFile> findAllByAnalysisSubmissionId(Long analysisSubmissionId);
}
