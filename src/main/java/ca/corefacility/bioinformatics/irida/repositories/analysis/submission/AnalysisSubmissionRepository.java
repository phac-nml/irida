package ca.corefacility.bioinformatics.irida.repositories.analysis.submission;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.Query;

import ca.corefacility.bioinformatics.irida.model.enums.AnalysisCleanedState;
import ca.corefacility.bioinformatics.irida.model.enums.AnalysisState;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;
import ca.corefacility.bioinformatics.irida.repositories.IridaJpaRepository;

/**
 * A repository for managing {@link AnalysisSubmission} objects.
 * 
 *
 */
public interface AnalysisSubmissionRepository extends IridaJpaRepository<AnalysisSubmission, Long> {

	/**
	 * Loads up a list of {@link AnalysisSubmission}s with the given state.
	 * 
	 * @param state
	 *            The state of the analyses to search for.
	 * @return A {@link List} of {@link AnalysisSubmission} objects with the
	 *         given state.
	 */
	@Query("select s from AnalysisSubmission s where s.analysisState = ?1")
	public List<AnalysisSubmission> findByAnalysisState(AnalysisState state);
	
	/**
	 * Loads up a list of {@link AnalysisSubmission}s with the given states.
	 * 
	 * @param analysisState
	 *            The {@link AnalysisState} of the analyses to search for.
	 * @param analysisCleanupState
	 *            The {@link AnalysisCleanedState} of the analyses to search
	 *            for.
	 * @return A {@link List} of {@link AnalysisSubmission} objects with the
	 *         given states.
	 */
	@Query("select s from AnalysisSubmission s where s.analysisState = ?1 and s.analysisCleanedState = ?2")
	public List<AnalysisSubmission> findByAnalysisState(AnalysisState analysisState,
			AnalysisCleanedState analysisCleanedState);

	/**
	 * Loads up all {@link AnalysisSubmission}s by the submitted {@link User}.
	 * 
	 * @param submitter
	 *            The {@link User} who submitted the analysis.
	 * @return A {@link List} of {@link AnalysisSubmission}s by the {@link User}
	 *         .
	 */
	@Query("select s from AnalysisSubmission s where s.submitter = ?1")
	public Set<AnalysisSubmission> findBySubmitter(User submitter);
}
