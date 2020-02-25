package ca.corefacility.bioinformatics.irida.repositories.analysis.submission;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.data.jpa.repository.Query;

import ca.corefacility.bioinformatics.irida.model.enums.AnalysisCleanedState;
import ca.corefacility.bioinformatics.irida.model.enums.AnalysisState;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.project.ReferenceFile;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequencingObject;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.Analysis;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.ProjectSampleAnalysisOutputInfo;
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
	 * Loads up a list of {@link AnalysisSubmission}s with the given state.
	 * 
	 * @param state
	 *            A collection of states to search for.
	 * @return A {@link List} of {@link AnalysisSubmission} objects with the
	 *         given state.
	 */
	@Query("select s from AnalysisSubmission s where s.analysisState in ?1")
	public List<AnalysisSubmission> findByAnalysisState(Collection<AnalysisState> state);

	/**
	 * Get the analysis submissions that are currently in the given list of states
	 *
	 * @param state the list of states to get analyses fro
	 * @return the number of analyses with that state
	 */
	@Query("select count(s.id) from AnalysisSubmission s where s.analysisState in ?1")
	public Long countByAnalysisState(Collection<AnalysisState> state);

	/**
	 * Loads up a list of {@link AnalysisSubmission}s with the given states.
	 * 
	 * @param analysisState
	 *            The {@link AnalysisState} of the analyses to search for.
	 * @param analysisCleanedState
	 *            The {@link AnalysisCleanedState} of the analyses to search
	 *            for.
	 * @return A {@link List} of {@link AnalysisSubmission} objects with the
	 *         given states.
	 */
	@Query("select s from AnalysisSubmission s where s.analysisState = ?1 and s.analysisCleanedState = ?2")
	public List<AnalysisSubmission> findByAnalysisState(AnalysisState analysisState,
			AnalysisCleanedState analysisCleanedState);
	
	/**
	 * Finds all {@link AnalysisSubmission}s corresponding to the given workflow
	 * ids.
	 * 
	 * @param workflowIds
	 *            The workflow ids to match.
	 * @return A list of {@link AnalysisSubmission}s matching one of the
	 *         workflow ids.
	 */
	@Query("select s from AnalysisSubmission s where s.workflowId in ?1")
	public List<AnalysisSubmission> findByWorkflowIds(Collection<UUID> workflowIds);

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

	/**
	 * Finds the {@link AnalysisSubmission} that caused the passed
	 * {@link Analysis} to be created.
	 * 
	 * @param analysis
	 *            the analysis to find the submission for
	 * @return the submission for the analysis
	 */
	@Query("select s from AnalysisSubmission s where s.analysis = ?1")
	public AnalysisSubmission findByAnalysis(final Analysis analysis);

	/**
	 * Get the Set of {@link AnalysisSubmission}s which use a given
	 * {@link SequencingObject}
	 * 
	 * @param object
	 *            The {@link SequencingObject} to get submissions for
	 * @return Set of {@link AnalysisSubmission}
	 */
	@Query("FROM AnalysisSubmission s WHERE ?1 IN elements(s.inputFiles)")
	public Set<AnalysisSubmission> findAnalysisSubmissionsForSequecingObject(SequencingObject object);

	/**
	 * Get the Set of {@link AnalysisSubmission}s making use of the given
	 * {@link ReferenceFile}.
	 * 
	 * @param file
	 *            The {@link ReferenceFile}.
	 * @return A Set of {@link AnalysisSubmission}s.
	 */
	@Query("FROM AnalysisSubmission s WHERE ?1 = referenceFile")
	public Set<AnalysisSubmission> findByReferenceFile(ReferenceFile file);

	/**
	 * Get all {@link ca.corefacility.bioinformatics.irida.model.user.User} generated analysis output information.
	 *
	 * @param userId {@link ca.corefacility.bioinformatics.irida.model.user.User} id
	 * @return a list of {@link ProjectSampleAnalysisOutputInfo}
	 */
	List<ProjectSampleAnalysisOutputInfo> getAllUserAnalysisOutputInfo(Long userId);

	/**
	 * Get all {@link ProjectSampleAnalysisOutputInfo} shared with a {@link Project}.
	 *
	 * @param projectId {@link Project} id
	 * @param workflowIds Workflow UUIDs of workflow pipelines to get output files for
	 * @return a list of {@link ProjectSampleAnalysisOutputInfo}
	 */
	List<ProjectSampleAnalysisOutputInfo> getAllAnalysisOutputInfoSharedWithProject(Long projectId, Set<UUID> workflowIds);

	/**
	 * Get all automated {@link ProjectSampleAnalysisOutputInfo} for a {@link Project}.
	 *
	 * @param projectId {@link Project} id
	 * @param workflowIds Workflow UUIDs of workflow pipelines to get output files for
	 * @return a list of {@link ProjectSampleAnalysisOutputInfo}
	 */
	List<ProjectSampleAnalysisOutputInfo> getAllAutomatedAnalysisOutputInfoForAProject(Long projectId,
			Set<UUID> workflowIds);
}
