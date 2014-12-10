package ca.corefacility.bioinformatics.irida.service.analysis.execution.galaxy;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.exceptions.ExecutionManagerException;
import ca.corefacility.bioinformatics.irida.exceptions.IridaWorkflowNotFoundException;
import ca.corefacility.bioinformatics.irida.exceptions.WorkflowException;
import ca.corefacility.bioinformatics.irida.exceptions.galaxy.WorkflowChecksumInvalidException;
import ca.corefacility.bioinformatics.irida.model.enums.AnalysisState;
import ca.corefacility.bioinformatics.irida.model.workflow.WorkflowStatus;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.Analysis;
import ca.corefacility.bioinformatics.irida.model.workflow.galaxy.PreparedWorkflowGalaxy;
import ca.corefacility.bioinformatics.irida.model.workflow.galaxy.RemoteWorkflowGalaxy;
import ca.corefacility.bioinformatics.irida.model.workflow.galaxy.WorkflowInputsGalaxy;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyHistoriesService;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyWorkflowService;
import ca.corefacility.bioinformatics.irida.service.AnalysisService;
import ca.corefacility.bioinformatics.irida.service.AnalysisSubmissionService;
import ca.corefacility.bioinformatics.irida.service.analysis.execution.AnalysisExecutionServiceSimplified;
import ca.corefacility.bioinformatics.irida.service.analysis.workspace.galaxy.AnalysisWorkspaceServiceGalaxySimplified;

import com.google.common.collect.ImmutableMap;

/**
 * Service for performing analyses within a Galaxy execution manager.
 * 
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 */
public class AnalysisExecutionServiceGalaxySimplified implements AnalysisExecutionServiceSimplified {

	private static final Logger logger = LoggerFactory.getLogger(AnalysisExecutionServiceGalaxySimplified.class);

	private AnalysisSubmissionService analysisSubmissionService;

	private AnalysisService analysisService;

	private AnalysisWorkspaceServiceGalaxySimplified workspaceService;

	private GalaxyHistoriesService galaxyHistoriesService;
	private GalaxyWorkflowService galaxyWorkflowService;

	/**
	 * Builds a new {@link AnalysisExecutionServiceGalaxySimplified} with the
	 * given information.
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
	 */
	@Autowired
	public AnalysisExecutionServiceGalaxySimplified(AnalysisSubmissionService analysisSubmissionService,
			AnalysisService analysisService, GalaxyWorkflowService galaxyWorkflowService,
			GalaxyHistoriesService galaxyHistoriesService, AnalysisWorkspaceServiceGalaxySimplified workspaceService) {
		this.analysisSubmissionService = analysisSubmissionService;
		this.analysisService = analysisService;
		this.galaxyWorkflowService = galaxyWorkflowService;
		this.galaxyHistoriesService = galaxyHistoriesService;
		this.workspaceService = workspaceService;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public AnalysisSubmission prepareSubmission(AnalysisSubmission analysisSubmission) throws ExecutionManagerException {
		checkNotNull(analysisSubmission, "analysisSubmission is null");
		checkNotNull(analysisSubmission.getId(), "analysisSubmission id is null");
		checkArgument(null == analysisSubmission.getRemoteAnalysisId(), "remote analyis id should be null");
		checkArgument(AnalysisState.PREPARING.equals(analysisSubmission.getAnalysisState()),
				"analysis state should be " + AnalysisState.PREPARING);

		logger.debug("Preparing submission for " + analysisSubmission);

		logger.trace("Validating " + analysisSubmission);
		// validateWorkflow(analysisSubmission.getRemoteWorkflow());

		String analysisId = workspaceService.prepareAnalysisWorkspace(analysisSubmission);
		logger.trace("Created Galaxy history for analysis " + " id=" + analysisId + ", " + analysisSubmission);

		return analysisSubmissionService.update(analysisSubmission.getId(),
				ImmutableMap.of("remoteAnalysisId", analysisId));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public AnalysisSubmission executeAnalysis(AnalysisSubmission analysisSubmission) throws ExecutionManagerException,
			IridaWorkflowNotFoundException {
		checkNotNull(analysisSubmission, "analysisSubmission is null");
		checkNotNull(analysisSubmission.getRemoteAnalysisId(), "remote analyis id is null");
		checkArgument(AnalysisState.SUBMITTING.equals(analysisSubmission.getAnalysisState()), " analysis should be "
				+ AnalysisState.SUBMITTING);

		logger.debug("Running submission for " + analysisSubmission);

		logger.trace("Preparing files for " + analysisSubmission);
		PreparedWorkflowGalaxy preparedWorkflow = workspaceService.prepareAnalysisFiles(analysisSubmission);
		WorkflowInputsGalaxy input = preparedWorkflow.getWorkflowInputs();

		logger.trace("Executing " + analysisSubmission);
		galaxyWorkflowService.runWorkflow(input);

		return analysisSubmissionService.read(analysisSubmission.getId());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public Analysis transferAnalysisResults(AnalysisSubmission submittedAnalysis) throws ExecutionManagerException,
			IOException, IridaWorkflowNotFoundException {
		checkNotNull(submittedAnalysis, "submittedAnalysis is null");
		checkNotNull(submittedAnalysis.getRemoteAnalysisId(), "remoteAnalysisId is null");
		checkArgument(AnalysisState.FINISHED_RUNNING.equals(submittedAnalysis.getAnalysisState()),
				" analysis should be " + AnalysisState.FINISHED_RUNNING);
		verifyAnalysisSubmissionExists(submittedAnalysis);

		Analysis savedAnalysis;
		Path tempOutputDirectory = null;
		try {
			tempOutputDirectory = Files.createTempDirectory("analysis-output");
			logger.trace("Created temporary directory " + tempOutputDirectory + " for analysis output files");

			logger.debug("Getting results for " + submittedAnalysis);
			Analysis analysisResults = workspaceService.getAnalysisResults(submittedAnalysis, tempOutputDirectory);

			logger.trace("Saving results for " + submittedAnalysis);
			savedAnalysis = analysisService.create(analysisResults);
		} finally {
			// At this stage any analysis output files should be transfered to
			// the output files repository
			// So it is safe to delete the temporary output files directory
			if (tempOutputDirectory == null) {
				logger.error("Temporary directory for saving analysis output files is null.");
			} else if (!isEmptyDirectory(tempOutputDirectory)) {
				logger.error("Temporary directory " + tempOutputDirectory + " is not empty. Not deleting.");
			} else {
				Files.delete(tempOutputDirectory);
				logger.trace("Deleted temporary directory " + tempOutputDirectory + " for analysis output files");
			}
		}

		analysisSubmissionService.update(submittedAnalysis.getId(), ImmutableMap.of("analysis", savedAnalysis));

		return savedAnalysis;
	}

	/**
	 * Determines if the given Path is an empty directory.
	 * 
	 * @param directory
	 *            The directory to check.
	 * @return True if the given path is an empty directory, false otherwise.
	 */
	private boolean isEmptyDirectory(Path directory) {
		return Files.isDirectory(directory) && directory.toFile().list().length == 0;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public WorkflowStatus getWorkflowStatus(AnalysisSubmission submittedAnalysis) throws ExecutionManagerException {
		checkNotNull(submittedAnalysis, "submittedAnalysis is null");
		checkNotNull(submittedAnalysis.getRemoteAnalysisId(), "remote analysis id is null");

		String analysisId = submittedAnalysis.getRemoteAnalysisId();
		return galaxyHistoriesService.getStatusForHistory(analysisId);
	}

	/**
	 * Validates the given workflow.
	 * 
	 * @param remoteWorkflow
	 *            The Galaxy workflow to validate.
	 * @throws WorkflowException
	 *             If there was an issue validating the workflow.
	 */
	public void validateWorkflow(RemoteWorkflowGalaxy remoteWorkflow) throws WorkflowException {
		checkNotNull(remoteWorkflow, "remoteWorkflow is null");

		if (!galaxyWorkflowService.validateWorkflowByChecksum(remoteWorkflow.getWorkflowChecksum(),
				remoteWorkflow.getWorkflowId())) {
			throw new WorkflowChecksumInvalidException("passed workflow with id=" + remoteWorkflow.getWorkflowId()
					+ " does not have correct checksum " + remoteWorkflow.getWorkflowChecksum());
		}
	}

	/**
	 * Verifies if the analysis submission exists.
	 * 
	 * @param submission
	 *            The submission to check.
	 * @throws EntityNotFoundException
	 *             If the given analysis submission does not exist in the
	 *             database.
	 */
	private void verifyAnalysisSubmissionExists(AnalysisSubmission submission) throws EntityNotFoundException {
		if (!analysisSubmissionService.exists(submission.getId())) {
			throw new EntityNotFoundException("Could not find analysis submission for " + submission);
		}
	}
}
