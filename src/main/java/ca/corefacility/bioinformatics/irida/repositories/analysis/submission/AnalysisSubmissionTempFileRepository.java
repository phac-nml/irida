package ca.corefacility.bioinformatics.irida.repositories.analysis.submission;

import java.util.List;

import org.springframework.data.jpa.repository.Query;

import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmissionTempFile;
import ca.corefacility.bioinformatics.irida.repositories.IridaJpaRepository;

/**
 * A repository for managing {@link AnalysisSubmissionTempFile} objects.
 */

public interface AnalysisSubmissionTempFileRepository extends IridaJpaRepository<AnalysisSubmissionTempFile, Long> {

	/**
	 * Get all {@link AnalysisSubmissionTempFile} objects by submission id.
	 *
	 * @param analysisSubmission The {@link AnalysisSubmission}
	 * @return a list of {@link AnalysisSubmissionTempFile}
	 */
	@Query("FROM AnalysisSubmissionTempFile f WHERE f.analysisSubmission = ?1")
	List<AnalysisSubmissionTempFile> findAllByAnalysisSubmission(AnalysisSubmission analysisSubmission);
}
