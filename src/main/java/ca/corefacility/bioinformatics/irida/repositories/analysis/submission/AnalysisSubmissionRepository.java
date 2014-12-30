package ca.corefacility.bioinformatics.irida.repositories.analysis.submission;

import java.util.List;

import org.springframework.data.jpa.repository.Query;

import ca.corefacility.bioinformatics.irida.model.enums.AnalysisState;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;
import ca.corefacility.bioinformatics.irida.repositories.IridaJpaRepository;

/**
 * A repository for managing {@link AnalysisSubmission} objects.
 * 
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 */
public interface AnalysisSubmissionRepository extends
		IridaJpaRepository<AnalysisSubmission, Long> {

	/**
	 * Loads up a list of @{link AnalysisSubmission}s with the given state.
	 * 
	 * @param state
	 *            The state of the analyses to search for.
	 * @return An List of @{link AnalysisSubmission} objects with the given
	 *         state.
	 */
	@Query("select s from AnalysisSubmission s where s.analysisState = ?1")
	public List<AnalysisSubmission> findByAnalysisState(AnalysisState state);
}
