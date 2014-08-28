package ca.corefacility.bioinformatics.irida.repositories.analysis.submission;

import org.springframework.data.jpa.repository.Query;

import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;
import ca.corefacility.bioinformatics.irida.repositories.IridaJpaRepository;

/**
 * A repository for managing {@link AnalysisSubmission} objects.
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 */
public interface AnalysisSubmissionRepository extends IridaJpaRepository<AnalysisSubmission, String> {
	/**
	 * Load up an @{link AnalysisSubmission} by an id for the passed type of AnalysisSubmission.
	 * @param analysisId  The id of the AnalysisSubmission to load.
	 * @param analysisType  The type of the analysis to load.
	 * @return  An AnalysisSubmission of the specified type by with the given id.
	 */
	@Query("select s from AnalysisSubmission s where s.remoteAnalysisId = ?1 and type(s) = ?2")
	public <T extends AnalysisSubmission> T getByType(String analysisId, Class<T> analysisType);
}
