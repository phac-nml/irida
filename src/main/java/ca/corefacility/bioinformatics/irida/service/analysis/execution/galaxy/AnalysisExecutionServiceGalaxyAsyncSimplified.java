package ca.corefacility.bioinformatics.irida.service.analysis.execution.galaxy;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.util.concurrent.Future;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;

import ca.corefacility.bioinformatics.irida.exceptions.ExecutionManagerException;
import ca.corefacility.bioinformatics.irida.exceptions.IridaWorkflowNotFoundException;
import ca.corefacility.bioinformatics.irida.exceptions.WorkflowException;
import ca.corefacility.bioinformatics.irida.model.enums.AnalysisState;
import ca.corefacility.bioinformatics.irida.model.workflow.IridaWorkflow;
import ca.corefacility.bioinformatics.irida.model.workflow.IridaWorkflowStructure;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisExecutionWorker;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyHistoriesService;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyWorkflowService;
import ca.corefacility.bioinformatics.irida.service.AnalysisService;
import ca.corefacility.bioinformatics.irida.service.AnalysisSubmissionService;
import ca.corefacility.bioinformatics.irida.service.analysis.workspace.galaxy.AnalysisWorkspaceServiceGalaxySimplified;
import ca.corefacility.bioinformatics.irida.service.workflow.IridaWorkflowsService;

import com.google.common.collect.ImmutableMap;

/**
 * Service for executing analysis stages within a Galaxy execution manager
 * asynchronously.
 * 
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 */
public class AnalysisExecutionServiceGalaxyAsyncSimplified {

	private static final Logger logger = LoggerFactory.getLogger(AnalysisExecutionServiceGalaxyAsyncSimplified.class);

	private final AnalysisSubmissionService analysisSubmissionService;
	private final AnalysisService analysisService;
	private final AnalysisWorkspaceServiceGalaxySimplified workspaceService;
	private final GalaxyHistoriesService galaxyHistoriesService;
	private final GalaxyWorkflowService galaxyWorkflowService;
	private final IridaWorkflowsService iridaWorkflowsService;

	/**
	 * Builds a new {@link AnalysisExecutionServiceGalaxyAsyncSimplified} with
	 * the given information.
	 * 
	 * @param analysisSubmissionService
	 *            A service for analysis submissions.
	 * @param analysisService
	 *            A service for analysis results.
	 * @param galaxyWorkflowService
	 *            A service for Galaxy workflows.
	 * @param galaxyHistoriesService
	 *            A service for Galaxy histories.
	 * @param workspaceService
	 *            A service for a workflow workspace.
	 * @param iridaWorkflowsService
	 *            A service for loading up {@link IridaWorkflow}s.
	 */
	@Autowired
	public AnalysisExecutionServiceGalaxyAsyncSimplified(AnalysisSubmissionService analysisSubmissionService,
			AnalysisService analysisService, GalaxyWorkflowService galaxyWorkflowService,
			GalaxyHistoriesService galaxyHistoriesService, AnalysisWorkspaceServiceGalaxySimplified workspaceService,
			IridaWorkflowsService iridaWorkflowsService) {
		this.analysisSubmissionService = analysisSubmissionService;
		this.analysisService = analysisService;
		this.galaxyWorkflowService = galaxyWorkflowService;
		this.galaxyHistoriesService = galaxyHistoriesService;
		this.workspaceService = workspaceService;
		this.iridaWorkflowsService = iridaWorkflowsService;
	}

	/**
	 * /** Prepares the given {@link AnalysisSubmission} to be executed within
	 * an execution manager. This will persist the submission within the
	 * database.
	 * 
	 * @param analysisSubmission
	 *            The {@link AnalysisSubmission} to prepare.
	 * @return An {@link AnalysisExecutionWorker} for the thread doing the
	 *         preparation of submissions.
	 * @throws IridaWorkflowNotFoundException
	 * @throws IOException
	 * @throws ExecutionManagerException
	 * @throws WorkflowException
	 */
	@Transactional
	@Async("analysisTaskExecutor")
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

		AnalysisSubmission analysisPrepared = analysisSubmissionService.update(analysisSubmission.getId(), ImmutableMap
				.of("remoteAnalysisId", analysisId, "remoteWorkflowId", workflowId, "analysisState",
						AnalysisState.PREPARED));

		return new AsyncResult<>(analysisPrepared);
	}
}
