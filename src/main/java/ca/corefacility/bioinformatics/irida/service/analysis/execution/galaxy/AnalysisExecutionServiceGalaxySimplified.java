package ca.corefacility.bioinformatics.irida.service.analysis.execution.galaxy;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.util.concurrent.Future;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.exceptions.ExecutionManagerException;
import ca.corefacility.bioinformatics.irida.exceptions.IridaWorkflowNotFoundException;
import ca.corefacility.bioinformatics.irida.model.enums.AnalysisState;
import ca.corefacility.bioinformatics.irida.model.workflow.WorkflowStatus;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.Analysis;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyHistoriesService;
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

	private final AnalysisSubmissionService analysisSubmissionService;
	private final AnalysisService analysisService;
	private final AnalysisWorkspaceServiceGalaxySimplified workspaceService;
	private final GalaxyHistoriesService galaxyHistoriesService;
	private final AnalysisExecutionServiceGalaxyAsyncSimplified analysisExecutionServiceGalaxyAsyncSimplified;

	/**
	 * Builds a new {@link AnalysisExecutionServiceGalaxySimplified} with the
	 * given information.
	 * 
	 * @param analysisSubmissionService
	 *            A service for analysis submissions.
	 * @param analysisService
	 *            A service for analysis results.
	 * @param galaxyHistoriesService
	 *            A service for Galaxy histories.
	 * @param workspaceService
	 *            A service for a workflow workspace.
	 * @param analysisExecutionServiceGalaxyAsyncSimplified
	 *            An {@link AnalysisExecutionServiceGalaxyAsyncSimplified} for
	 *            executing the tasks asynchronously.
	 */
	@Autowired
	public AnalysisExecutionServiceGalaxySimplified(AnalysisSubmissionService analysisSubmissionService,
			AnalysisService analysisService, GalaxyHistoriesService galaxyHistoriesService,
			AnalysisWorkspaceServiceGalaxySimplified workspaceService,
			AnalysisExecutionServiceGalaxyAsyncSimplified analysisExecutionServiceGalaxyAsyncSimplified) {
		this.analysisSubmissionService = analysisSubmissionService;
		this.analysisService = analysisService;
		this.galaxyHistoriesService = galaxyHistoriesService;
		this.workspaceService = workspaceService;
		this.analysisExecutionServiceGalaxyAsyncSimplified = analysisExecutionServiceGalaxyAsyncSimplified;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public Future<AnalysisSubmission> prepareSubmission(final AnalysisSubmission analysisSubmission)
			throws IridaWorkflowNotFoundException, IOException, ExecutionManagerException {
		checkArgument(AnalysisState.NEW.equals(analysisSubmission.getAnalysisState()), "analysis state should be "
				+ AnalysisState.NEW);

		AnalysisSubmission preparingAnalysis = analysisSubmissionService.update(analysisSubmission.getId(),
				ImmutableMap.of("analysisState", AnalysisState.PREPARING));

		return analysisExecutionServiceGalaxyAsyncSimplified.prepareSubmission(preparingAnalysis);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public Future<AnalysisSubmission> executeAnalysis(AnalysisSubmission analysisSubmission)
			throws IridaWorkflowNotFoundException, ExecutionManagerException {
		checkArgument(AnalysisState.PREPARED.equals(analysisSubmission.getAnalysisState()), " analysis should be "
				+ AnalysisState.PREPARED);

		AnalysisSubmission submittingAnalysis = analysisSubmissionService.update(analysisSubmission.getId(),
				ImmutableMap.of("analysisState", AnalysisState.SUBMITTING));

		return analysisExecutionServiceGalaxyAsyncSimplified.executeAnalysis(submittingAnalysis);
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

		logger.debug("Getting results for " + submittedAnalysis);
		Analysis analysisResults = workspaceService.getAnalysisResults(submittedAnalysis);

		logger.trace("Saving results for " + submittedAnalysis);
		Analysis savedAnalysis = analysisService.create(analysisResults);

		analysisSubmissionService.update(submittedAnalysis.getId(), ImmutableMap.of("analysis", savedAnalysis));

		return savedAnalysis;
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
