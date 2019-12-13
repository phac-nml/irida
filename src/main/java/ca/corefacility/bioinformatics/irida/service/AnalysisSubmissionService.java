package ca.corefacility.bioinformatics.irida.service;

import java.util.*;

import ca.corefacility.bioinformatics.irida.service.impl.analysis.submission.AnalysisSubmissionServiceImpl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

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
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.JobError;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.ProjectSampleAnalysisOutputInfo;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmissionTemplate;
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
	 * Gets all {@link AnalysisSubmissionService}s accessible by the current
	 * user matching one of the workflow ids.
	 * 
	 * @param workflowIds
	 *            The workflow ids to match.
	 * @return A list of {@link AnalysisSubmission}s matching one of the
	 *         workflow ids.
	 */
	public List<AnalysisSubmission> getAnalysisSubmissionsAccessibleByCurrentUserByWorkflowIds(
			Collection<UUID> workflowIds);

	/**
	 * Delete multiple {@link AnalysisSubmission}s
	 *
	 * @param ids the collection of IDs to delete
	 */
	public void deleteMultiple(Collection<Long> ids);

	/**
	 * Submit {@link AnalysisSubmission} for workflows allowing multiple one
	 * {@link SequenceFile} or {@link SequenceFilePair}
	 *
	 * @param workflow              {@link IridaWorkflow} that the files will be run on
	 * @param ref                   {@link Long} id for a {@link ReferenceFile}
	 * @param sequenceFiles         {@link List} of {@link SequenceFile} to run on the workflow
	 * @param sequenceFilePairs     {@link List} of {@link SequenceFilePair} to run on the
	 *                              workflow
	 * @param unnamedParameters     {@link Map} of parameters specific for the pipeline
	 * @param namedParameters       the named parameters to use for the workflow.
	 * @param name                  {@link String} the name for the analysis
	 * @param analysisDescription   {@link String} the description of the analysis being submitted
	 * @param projectsToShare       A list of {@link Project}s to share analysis results with
	 * @param writeResultsToSamples If true, results of this pipeline will be saved back to the
	 *                              samples on successful completion.
	 * @param emailPipelineResult   If true, user will be emailed if a pipelines successfully
	 *                              completes or if it errors
	 * @return the {@link AnalysisSubmission} created for the files.
	 */
	public AnalysisSubmission createMultipleSampleSubmission(IridaWorkflow workflow, Long ref,
			List<SingleEndSequenceFile> sequenceFiles, List<SequenceFilePair> sequenceFilePairs,
			Map<String, String> unnamedParameters, IridaWorkflowNamedParameters namedParameters, String name,
			String analysisDescription, List<Project> projectsToShare, boolean writeResultsToSamples, boolean emailPipelineResult);

	/**
	 * Submit {@link AnalysisSubmission} for workflows requiring only one
	 * {@link SequenceFile} or {@link SequenceFilePair}
	 *
	 * @param workflow              {@link IridaWorkflow} that the files will be run on
	 * @param ref                   {@link Long} id for a {@link ReferenceFile}
	 * @param sequenceFiles         {@link List} of {@link SequenceFile} to run on the workflow
	 * @param sequenceFilePairs     {@link List} of {@link SequenceFilePair} to run on the
	 *                              workflow
	 * @param unnamedParameters     {@link Map} of parameters specific for the pipeline
	 * @param namedParameters       the named parameters to use for the workflow.
	 * @param name                  {@link String} the name for the analysis
	 * @param analysisDescription   {@link String} the description of the analysis being submitted
	 * @param projectsToShare       A list of {@link Project}s to share analysis results with
	 * @param writeResultsToSamples If true, results of this pipeline will be saved back to the
	 *                              samples on successful completion.
	 * @param emailPipelineResult   If true, user will be emailed if a pipelines successfully
	 *                              completes or if it errors
	 * @return the {@link Collection} of {@link AnalysisSubmission} created for
	 * the supplied files.
	 */
	public Collection<AnalysisSubmission> createSingleSampleSubmission(IridaWorkflow workflow, Long ref,
			List<SingleEndSequenceFile> sequenceFiles, List<SequenceFilePair> sequenceFilePairs,
			Map<String, String> unnamedParameters, IridaWorkflowNamedParameters namedParameters, String name,
			String analysisDescription, List<Project> projectsToShare, boolean writeResultsToSamples, boolean emailPipelineResult);

	/**
	 * Create a new {@link AnalysisSubmissionTemplate} for a project with the given settings
	 *
	 * @param workflow              {@link IridaWorkflow} that the files will be run on
	 * @param referenceFileId       {@link Long} id for a {@link ReferenceFile}
	 * @param params                {@link Map} of parameters specific for the pipeline
	 * @param namedParameters       the named parameters to use for the workflow.
	 * @param submissionName        {@link String} the name for the analysis
	 * @param statusMessage         A status message for the submission template
	 * @param analysisDescription   {@link String} the description of the analysis being submitted
	 * @param projectsToShare       The {@link Project} to save the analysis to
	 * @param writeResultsToSamples If true, results of this pipeline will be saved back to the samples on successful
	 *                              completion.
	 * @param emailPipelineResults  Whether or not to email the pipeline results to the user
	 * @return the newly created {@link AnalysisSubmissionTemplate}
	 */
	public AnalysisSubmissionTemplate createSingleSampleSubmissionTemplate(IridaWorkflow workflow, Long referenceFileId,
			Map<String, String> params, IridaWorkflowNamedParameters namedParameters, String submissionName,
			String statusMessage, String analysisDescription, Project projectsToShare, boolean writeResultsToSamples,
			boolean emailPipelineResults);

	/**
	 * Get all the {@link AnalysisSubmissionTemplate}s for a given {@link Project}
	 *
	 * @param project the {@link Project} to get templates for
	 * @return a list of all {@link AnalysisSubmissionTemplate}s
	 */
	public List<AnalysisSubmissionTemplate> getAnalysisTemplatesForProject(Project project);

	/**
	 * Get an {@link AnalysisSubmissionTemplate} on the given {@link Project}
	 *
	 * @param id      the {@link AnalysisSubmissionTemplate} id
	 * @param project the {@link Project} to get templates for
	 * @return the found {@link AnalysisSubmissionTemplate}
	 */
	public AnalysisSubmissionTemplate readAnalysisSubmissionTemplateForProject(Long id, Project project);

	/**
	 * Delete an {@link AnalysisSubmissionTemplate} from the given {@link Project}
	 *
	 * @param id      The id of an {@link AnalysisSubmissionTemplate}.
	 * @param project the {@link Project} to delete from
	 */
	public void deleteAnalysisSubmissionTemplateForProject(Long id, Project project);

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
	 * Get the {@link JobError} objects for a {@link AnalysisSubmission} id
	 * @param id {@link AnalysisSubmission} id
	 * @return {@link JobError} objects for a {@link AnalysisSubmission}
	 * @throws EntityNotFoundException If no such {@link AnalysisSubmission} exists.
	 * @throws ExecutionManagerException If there was an issue contacting the execution manager.
	 */
	List<JobError> getJobErrors(Long id) throws EntityNotFoundException, ExecutionManagerException;

	/**
	 * Get first {@link JobError} for a {@link AnalysisSubmission} id
	 * @param id {@link AnalysisSubmission} id
	 * @return {@link JobError} object
	 * @throws EntityNotFoundException If no such {@link AnalysisSubmission} exists.
	 * @throws ExecutionManagerException If there was an issue contacting the execution manager.
	 */
	JobError getFirstJobError(Long id) throws  EntityNotFoundException, ExecutionManagerException;

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

	/**
	 * Get a page of the {@link AnalysisSubmission}s shared with a project.
	 *
	 * @param search      basic search string
	 * @param name        analysis submission name
	 * @param states       Set of {@link AnalysisState} of the submission to search
	 * @param workflowIds set of workflow UUIDs to search
	 * @param project     {@link Project} to search in
	 * @param pageRequest a {@link PageRequest} for the results to show
	 * @return a page of {@link AnalysisSubmission}
	 */
	public Page<AnalysisSubmission> listSubmissionsForProject(String search, String name, Set<AnalysisState> states,
			Set<UUID> workflowIds, Project project, PageRequest pageRequest);

	/**
	 * Get a page of all {@link AnalysisSubmission}s in the system
	 *
	 * @param search      basic search string
	 * @param name        analysis submission name
	 * @param states       Set of {@link AnalysisState} of the submission to search
	 * @param workflowIds set of workflow UUIDs to search
	 * @param pageRequest a {@link PageRequest} for the results to show
	 * @return a page of {@link AnalysisSubmission}
	 */
	public Page<AnalysisSubmission> listAllSubmissions(String search, String name, Set<AnalysisState> states,
			Set<UUID> workflowIds, PageRequest pageRequest);

	/**
	 * Get a page of {@link AnalysisSubmission}s the given user has submitted.
	 *
	 * @param search      basic search string
	 * @param name        analysis submission name
	 * @param states      Set of {@link AnalysisState} of the submission to search
	 * @param user        the {@link User} to get submissions for
	 * @param workflowIds set of workflow UUIDs to search
	 * @param pageRequest a {@link PageRequest} for the restults to show
	 * @return a page of {@link AnalysisSubmission}s for the given user
	 */
	public Page<AnalysisSubmission> listSubmissionsForUser(String search, String name, Set<AnalysisState> states, User user,
			Set<UUID> workflowIds, PageRequest pageRequest);

	/**
	 * Update the priority of an {@link AnalysisSubmission}
	 *
	 * @param submission the submission to update
	 * @param priority   the new priority
	 * @return the updated submission
	 */
	public AnalysisSubmission updatePriority(AnalysisSubmission submission, AnalysisSubmission.Priority priority);

	/**
	 * Get all {@link User} generated {@link ca.corefacility.bioinformatics.irida.model.workflow.analysis.AnalysisOutputFile} info
	 * @param user {@link User}
	 * @return List of {@link ca.corefacility.bioinformatics.irida.model.workflow.analysis.AnalysisOutputFile} info
	 */
	List<ProjectSampleAnalysisOutputInfo> getAllUserAnalysisOutputInfo(User user);

	/**
	 * Get all {@link ProjectSampleAnalysisOutputInfo} for a {@link Project}.
	 *
	 * @param projectId {@link Project} id
	 * @return a list of {@link ProjectSampleAnalysisOutputInfo}
	 */
	List<ProjectSampleAnalysisOutputInfo> getAllAnalysisOutputInfoSharedWithProject(Long projectId);

	/**
	 * Get all automated {@link ProjectSampleAnalysisOutputInfo} for a {@link Project}.
	 *
	 * @param projectId {@link Project} id
	 * @return a list of {@link ProjectSampleAnalysisOutputInfo}
	 */
	List<ProjectSampleAnalysisOutputInfo> getAllAutomatedAnalysisOutputInfoForAProject(Long projectId);

	public AnalysisServiceStatus getAnalysisServiceStatus();

	public class AnalysisServiceStatus {
		private Long running;
		private Long queued;

		public AnalysisServiceStatus(Long running, Long queued) {
			this.running = running;
			this.queued = queued;
		}

		public Long getRunning() {
			return running;
		}

		public Long getQueued() {
			return queued;
		}

		@Override
		public String toString() {
			return "Running: " + running + ", Queued: " + queued;
		}
	}
}
