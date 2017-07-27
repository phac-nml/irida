package ca.corefacility.bioinformatics.irida.service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.exceptions.ExecutionManagerException;
import ca.corefacility.bioinformatics.irida.exceptions.NoPercentageCompleteException;
import ca.corefacility.bioinformatics.irida.model.enums.AnalysisState;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.project.ReferenceFile;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFilePair;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SingleEndSequenceFile;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.model.workflow.IridaWorkflow;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.IridaWorkflowNamedParameters;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.ProjectAnalysisSubmissionJoin;

/**
 * A service for AnalysisSubmissions.
 * 
 *
 */
public interface AnalysisSubmissionService extends CRUDService<Long, AnalysisSubmission> {

	/**
	 * Given an analysis submission id, gets the state of this analysis.
	 * 
	 * @param analysisSubmissionId
	 *            The id of this analysis.
	 * @return The state of this analysis.
	 * @throws EntityNotFoundException
	 *             If the corresponding analysis cannot be found.
	 */
	public AnalysisState getStateForAnalysisSubmission(Long analysisSubmissionId) throws EntityNotFoundException;
	
	/**
	 * Gets a {@link Set} of {@link AnalysisSubmission}s for a {@link User}.
	 * 
	 * @param user
	 *            The {@link User} to find all submissions for.
	 * @return A {@link Set} of {@link AnalysisSubmission}s for a user.
	 */
	public Set<AnalysisSubmission> getAnalysisSubmissionsForUser(User user);

	/**
	 * Gets a {@link Set} of {@link AnalysisSubmission}s for the current
	 * {@link User}.
	 * 
	 * @return A {@link Set} of {@link AnalysisSubmission}s for the current
	 *         user.
	 */
	public Set<AnalysisSubmission> getAnalysisSubmissionsForCurrentUser();
	
	/**
	 * Submit {@link AnalysisSubmission} for workflows allowing multiple one
	 * {@link SequenceFile} or {@link SequenceFilePair}
	 *
	 * @param workflow
	 *            {@link IridaWorkflow} that the files will be run on
	 * @param ref
	 *            {@link Long} id for a {@link ReferenceFile}
	 * @param sequenceFiles
	 *            {@link List} of {@link SequenceFile} to run on the workflow
	 * @param sequenceFilePairs
	 *            {@link List} of {@link SequenceFilePair} to run on the
	 *            workflow
	 * @param unnamedParameters
	 *            {@link Map} of parameters specific for the pipeline
	 * @param namedParameters
	 *            the named parameters to use for the workflow.
	 * @param name
	 *            {@link String} the name for the analysis
	 * @param analysisDescription
	 *            {@link String} the description of the analysis being submitted
	 * @param projectsToShare
	 *            A list of {@link Project}s to share analysis results with
	 * @return the {@link AnalysisSubmission} created for the files.
	 */
	public AnalysisSubmission createMultipleSampleSubmission(IridaWorkflow workflow, Long ref,
			List<SingleEndSequenceFile> sequenceFiles, List<SequenceFilePair> sequenceFilePairs,
			Map<String, String> unnamedParameters, IridaWorkflowNamedParameters namedParameters, String name,
			String analysisDescription, List<Project> projectsToShare);

	/**
	 * Submit {@link AnalysisSubmission} for workflows requiring only one
	 * {@link SequenceFile} or {@link SequenceFilePair}
	 *
	 * @param workflow
	 *            {@link IridaWorkflow} that the files will be run on
	 * @param ref
	 *            {@link Long} id for a {@link ReferenceFile}
	 * @param sequenceFiles
	 *            {@link List} of {@link SequenceFile} to run on the workflow
	 * @param sequenceFilePairs
	 *            {@link List} of {@link SequenceFilePair} to run on the
	 *            workflow
	 * @param unnamedParameters
	 *            {@link Map} of parameters specific for the pipeline
	 * @param namedParameters
	 *            the named parameters to use for the workflow.
	 * @param name
	 *            {@link String} the name for the analysis
	 * @param analysisDescription
	 *            {@link String} the description of the analysis being submitted
	 * @param projectsToShare
	 *            A list of {@link Project}s to share analysis results with
	 * @return the {@link Collection} of {@link AnalysisSubmission} created for
	 *         the supplied files.
	 */
	public Collection<AnalysisSubmission> createSingleSampleSubmission(IridaWorkflow workflow, Long ref,
			List<SingleEndSequenceFile> sequenceFiles, List<SequenceFilePair> sequenceFilePairs,
			Map<String, String> unnamedParameters, IridaWorkflowNamedParameters namedParameters, String name,
			String analysisDescription, List<Project> projectsToShare);

	/**
	 * Given the id of an {@link AnalysisSubmission} gets the percentage
	 * complete.
	 * 
	 * @param id
	 *            The id of an {@link AnalysisSubmission}.
	 * @return The percentage complete for this {@link AnalysisSubmission}.
	 * @throws NoPercentageCompleteException
	 *             An exception that indicates there is no percentage complete
	 *             for the submission.
	 * @throws ExecutionManagerException
	 *             If there was an issue when contacting the execution manager.
	 * @throws EntityNotFoundException
	 *             If no such corresponding submission exists.
	 */
	public float getPercentCompleteForAnalysisSubmission(Long id) throws EntityNotFoundException,
			NoPercentageCompleteException, ExecutionManagerException;
	
	/**
	 * Share an {@link AnalysisSubmission} with a given {@link Project}
	 * 
	 * @param submission
	 *            {@link AnalysisSubmission} to share
	 * @param project
	 *            {@link Project} to share with
	 * @return a {@link ProjectAnalysisSubmissionJoin} describing the
	 *         relationship
	 */
	public ProjectAnalysisSubmissionJoin shareAnalysisSubmissionWithProject(AnalysisSubmission submission,
			Project project);

	/**
	 * Cancel the share of an {@link AnalysisSubmission} with a given
	 * {@link Project}
	 * 
	 * @param submission
	 *            the {@link AnalysisSubmission} to stop sharing
	 * @param project
	 *            the {@link Project} to stop sharing with
	 */
	public void removeAnalysisProjectShare(AnalysisSubmission submission, Project project);
	
	/**
	 * Get a list of all {@link AnalysisSubmission}s with a given
	 * {@link AnalysisState}
	 * 
	 * @param states
	 *            A list of {@link AnalysisState} to find
	 *            {@link AnalysisSubmission}s for
	 * @return a Collection of {@link AnalysisSubmission}
	 */
	public Collection<AnalysisSubmission> findAnalysesByState(Collection<AnalysisState> states);

	/**
	 * Get a collection of all {@link AnalysisSubmission}s shared with a
	 * {@link Project}.
	 * 
	 * @param project
	 *            The {@link Project} to search.
	 * @return A collection of {@link AnalysisSubmission}s.
	 */
	public Collection<AnalysisSubmission> getAnalysisSubmissionsSharedToProject(Project project);
}
