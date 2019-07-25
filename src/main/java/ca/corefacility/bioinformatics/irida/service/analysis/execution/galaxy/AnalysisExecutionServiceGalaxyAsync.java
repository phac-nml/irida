package ca.corefacility.bioinformatics.irida.service.analysis.execution.galaxy;

import ca.corefacility.bioinformatics.irida.exceptions.*;
import ca.corefacility.bioinformatics.irida.model.enums.AnalysisState;
import ca.corefacility.bioinformatics.irida.model.workflow.IridaWorkflow;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.Analysis;
import ca.corefacility.bioinformatics.irida.model.workflow.execution.galaxy.PreparedWorkflowGalaxy;
import ca.corefacility.bioinformatics.irida.model.workflow.execution.galaxy.WorkflowInputsGalaxy;
import ca.corefacility.bioinformatics.irida.model.workflow.structure.IridaWorkflowStructure;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;
import ca.corefacility.bioinformatics.irida.pipeline.results.AnalysisSubmissionSampleProcessor;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyWorkflowService;
import ca.corefacility.bioinformatics.irida.service.AnalysisService;
import ca.corefacility.bioinformatics.irida.service.AnalysisSubmissionService;
import ca.corefacility.bioinformatics.irida.service.analysis.annotations.RunAsUser;
import ca.corefacility.bioinformatics.irida.service.analysis.workspace.galaxy.AnalysisWorkspaceServiceGalaxy;
import ca.corefacility.bioinformatics.irida.service.workflow.IridaWorkflowsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.concurrent.Future;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Service for executing {@link AnalysisSubmission} stages within a Galaxy
 * execution manager asynchronously.
 * 
 */
@Async("analysisTaskExecutor")
public class AnalysisExecutionServiceGalaxyAsync {

	private static final Logger logger = LoggerFactory.getLogger(AnalysisExecutionServiceGalaxyAsync.class);

	private final AnalysisSubmissionService analysisSubmissionService;
	private final AnalysisService analysisService;
	private final AnalysisWorkspaceServiceGalaxy workspaceService;
	private final GalaxyWorkflowService galaxyWorkflowService;
	private final IridaWorkflowsService iridaWorkflowsService;
	private final AnalysisSubmissionSampleProcessor analysisSubmissionSampleProcessor;

	/**
	 * Builds a new {@link AnalysisExecutionServiceGalaxyAsync} with the given
	 * information.
	 * 
	 * @param analysisSubmissionService
	 *            A service for analysis submissions.
	 * @param analysisService
	 *            A service for analysis results.
	 * @param galaxyWorkflowService
	 *            A service for Galaxy workflows.
	 * @param workspaceService
	 *            A service for a workflow workspace.
	 * @param iridaWorkflowsService
	 *            A service for loading up {@link IridaWorkflow}s.
	 * @param analysisSubmissionSampleService
	 *            A service to updating samples associated with a submission
	 *            with the analysis results.
	 */
	@Autowired
	public AnalysisExecutionServiceGalaxyAsync(AnalysisSubmissionService analysisSubmissionService,
			AnalysisService analysisService, GalaxyWorkflowService galaxyWorkflowService,
			AnalysisWorkspaceServiceGalaxy workspaceService, IridaWorkflowsService iridaWorkflowsService,
			AnalysisSubmissionSampleProcessor analysisSubmissionSampleService) {
		this.analysisSubmissionService = analysisSubmissionService;
		this.analysisService = analysisService;
		this.galaxyWorkflowService = galaxyWorkflowService;
		this.workspaceService = workspaceService;
		this.iridaWorkflowsService = iridaWorkflowsService;
		this.analysisSubmissionSampleProcessor = analysisSubmissionSampleService;
	}

	/**
	 * Prepares the given {@link AnalysisSubmission} to be executed within an
	 * execution manager. This will persist the submission within the database.
	 * 
	 * @param analysisSubmission
	 *            The {@link AnalysisSubmission} to prepare.
	 * @return A {@link Future} with an {@link AnalysisSubmission} for the
	 *         analysis submitted.
	 * @throws IridaWorkflowNotFoundException
	 *             If there was an issue getting a workflow.
	 * @throws IOException
	 *             If there was an issue reading the workflow.
	 * @throws ExecutionManagerException
	 *             If there was an issue preparing a workspace for the workflow.
	 */
	@Transactional
	public Future<AnalysisSubmission> prepareSubmission(final AnalysisSubmission analysisSubmission)
			throws IridaWorkflowNotFoundException, IOException, ExecutionManagerException {
		checkNotNull(analysisSubmission, "analysisSubmission is null");
		checkNotNull(analysisSubmission.getId(), "analysisSubmission id is null");
		checkArgument(null == analysisSubmission.getRemoteAnalysisId(), "remote analyis id should be null");
		checkArgument(null == analysisSubmission.getRemoteWorkflowId(), "remoteWorkflowId should be null");

		IridaWorkflow iridaWorkflow = iridaWorkflowsService.getIridaWorkflow(analysisSubmission.getWorkflowId());
		IridaWorkflowStructure workflowStructure = iridaWorkflow.getWorkflowStructure();

		logger.debug("Preparing submission for " + analysisSubmission);

		String workflowId = galaxyWorkflowService.uploadGalaxyWorkflow(workflowStructure.getWorkflowFile());
		analysisSubmission.setRemoteWorkflowId(workflowId);
		logger.trace("Uploaded workflow for " + analysisSubmission + " to workflow with id=" + workflowId);

		String analysisId = workspaceService.prepareAnalysisWorkspace(analysisSubmission);

		logger.trace("Created Galaxy history for analysis " + " id=" + analysisId + ", " + analysisSubmission);

		analysisSubmission.setAnalysisState(AnalysisState.PREPARED);
		analysisSubmission.setRemoteWorkflowId(workflowId);
		analysisSubmission.setRemoteAnalysisId(analysisId);
		AnalysisSubmission analysisPrepared = analysisSubmissionService.update(analysisSubmission);

		return new AsyncResult<>(analysisPrepared);
	}

	/**
	 * Executes the passed prepared {@link AnalysisSubmission} in an execution
	 * manager.
	 * 
	 * @param analysisSubmission
	 *            The {@link AnalysisSubmission} to execute.
	 * @return A {@link Future} with an {@link AnalysisSubmission} for the
	 *         analysis submitted.
	 * @throws ExecutionManagerException
	 *             If there was an exception submitting the analysis to the
	 *             execution manager.
	 * @throws IridaWorkflowException If there was an issue with the IRIDA workflow.
	 * @throws IOException If there was an error reading some of the input files.
	 */
	@RunAsUser("#analysisSubmission.getSubmitter()")
	public Future<AnalysisSubmission> executeAnalysis(AnalysisSubmission analysisSubmission)
			throws ExecutionManagerException, IridaWorkflowException, IOException {
		checkNotNull(analysisSubmission, "analysisSubmission is null");
		checkNotNull(analysisSubmission.getRemoteAnalysisId(), "remote analyis id is null");
		checkNotNull(analysisSubmission.getWorkflowId(), "workflowId is null");

		logger.debug("Running submission for " + analysisSubmission);

		logger.trace("Preparing files for " + analysisSubmission);
		PreparedWorkflowGalaxy preparedWorkflow = workspaceService.prepareAnalysisFiles(analysisSubmission);
		WorkflowInputsGalaxy input = preparedWorkflow.getWorkflowInputs();
		String libraryId = preparedWorkflow.getRemoteDataId();

		logger.trace("Executing " + analysisSubmission);
		galaxyWorkflowService.runWorkflow(input);

		analysisSubmission.setAnalysisState(AnalysisState.RUNNING);
		analysisSubmission.setRemoteInputDataId(libraryId);
		analysisSubmission = analysisSubmissionService.update(analysisSubmission);

		return new AsyncResult<>(analysisSubmission);
	}

	/**
	 * Downloads and saves the results of an {@link AnalysisSubmission} that was
	 * previously submitted from an execution manager.
	 * 
	 * @param submittedAnalysis
	 *            An {@link AnalysisSubmission} that was previously submitted.
	 * @return A {@link Future} with an {@link AnalysisSubmission} object
	 *         containing information about the particular analysis.
	 * @throws ExecutionManagerException
	 *             If there was an issue with the execution manager.
	 * @throws IridaWorkflowNotFoundException
	 *             If the workflow for this submission could not be found in
	 *             IRIDA.
	 * @throws IOException
	 *             If there was an error loading the analysis results from an
	 *             execution manager.
	 * @throws IridaWorkflowAnalysisTypeException
	 *             If there was an issue building an {@link Analysis} object.
	 */
	@Transactional
	public Future<AnalysisSubmission> transferAnalysisResults(AnalysisSubmission submittedAnalysis)
			throws ExecutionManagerException, IOException, IridaWorkflowNotFoundException,
			IridaWorkflowAnalysisTypeException {
		checkNotNull(submittedAnalysis, "submittedAnalysis is null");
		checkNotNull(submittedAnalysis.getRemoteAnalysisId(), "remoteAnalysisId is null");
		if (!analysisSubmissionService.exists(submittedAnalysis.getId())) {
			throw new EntityNotFoundException("Could not find analysis submission for " + submittedAnalysis);
		}

		logger.debug("Getting results for " + submittedAnalysis);
		Analysis analysisResults = workspaceService.getAnalysisResults(submittedAnalysis);

		logger.trace("Saving results for " + submittedAnalysis);
		Analysis savedAnalysis = analysisService.create(analysisResults);

		// if samples should be updated, set to TRANSFERRED.  Otherwise just complete.
		if (submittedAnalysis.getUpdateSamples()) {
			submittedAnalysis.setAnalysisState(AnalysisState.TRANSFERRED);
		} else {
			submittedAnalysis.setAnalysisState(AnalysisState.COMPLETED);
		}

		try {
			submittedAnalysis.setAnalysis(savedAnalysis);
		} catch (AnalysisAlreadySetException e) {
			throw new ExecutionManagerException("Analysis already set", e);
		}

		AnalysisSubmission completedSubmission = analysisSubmissionService.update(submittedAnalysis);

		return new AsyncResult<>(completedSubmission);
	}

	/**
	 * Calls the {@link AnalysisSubmissionSampleProcessor} to perform any post processing required on a given {@link AnalysisSubmission}
	 *
	 * @param analysisSubmission the {@link AnalysisSubmission} to process
	 * @return a Future {@link AnalysisSubmission}
	 */
	@Transactional
	@RunAsUser("#analysisSubmission.getSubmitter()")
	public Future<AnalysisSubmission> postProcessResults(AnalysisSubmission analysisSubmission) {
		checkNotNull(analysisSubmission, "submittedAnalysis is null");

		if (analysisSubmission.getUpdateSamples()) {
			try {
				analysisSubmissionSampleProcessor.updateSamples(analysisSubmission);
			} catch (Exception e) {
				logger.error("Error updating corresponding samples with analysis results for AnalysisSubmission = ["
						+ analysisSubmission.getId() + "]. Skipping this step.", e);
			}
		}

		analysisSubmission.setAnalysisState(AnalysisState.COMPLETED);

		analysisSubmission = analysisSubmissionService.update(analysisSubmission);

		return new AsyncResult<>(analysisSubmission);
	}
}
