package ca.corefacility.bioinformatics.irida.service.analysis.execution.galaxy;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.util.concurrent.Future;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;

import ca.corefacility.bioinformatics.irida.exceptions.ExecutionManagerException;
import ca.corefacility.bioinformatics.irida.exceptions.IridaWorkflowNotFoundException;
import ca.corefacility.bioinformatics.irida.model.enums.AnalysisState;
import ca.corefacility.bioinformatics.irida.model.workflow.WorkflowStatus;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyHistoriesService;
import ca.corefacility.bioinformatics.irida.service.AnalysisSubmissionService;
import ca.corefacility.bioinformatics.irida.service.analysis.execution.AnalysisExecutionServiceSimplified;

import com.google.common.collect.ImmutableMap;

/**
 * Service for performing analyses within a Galaxy execution manager.
 * 
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 */
public class AnalysisExecutionServiceGalaxySimplified implements AnalysisExecutionServiceSimplified {

	private final AnalysisSubmissionService analysisSubmissionService;
	private final GalaxyHistoriesService galaxyHistoriesService;
	private final AnalysisExecutionServiceGalaxyAsyncSimplified analysisExecutionServiceGalaxyAsyncSimplified;

	/**
	 * Builds a new {@link AnalysisExecutionServiceGalaxySimplified} with the
	 * given information.
	 * 
	 * @param analysisSubmissionService
	 *            A service for analysis submissions.
	 * @param galaxyHistoriesService
	 *            A service for Galaxy histories.
	 * @param analysisExecutionServiceGalaxyAsyncSimplified
	 *            An {@link AnalysisExecutionServiceGalaxyAsyncSimplified} for
	 *            executing the tasks asynchronously.
	 */
	@Autowired
	public AnalysisExecutionServiceGalaxySimplified(AnalysisSubmissionService analysisSubmissionService,
			GalaxyHistoriesService galaxyHistoriesService,
			AnalysisExecutionServiceGalaxyAsyncSimplified analysisExecutionServiceGalaxyAsyncSimplified) {
		this.analysisSubmissionService = analysisSubmissionService;
		this.galaxyHistoriesService = galaxyHistoriesService;
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
	public Future<AnalysisSubmission> transferAnalysisResults(AnalysisSubmission submittedAnalysis)
			throws ExecutionManagerException, IOException, IridaWorkflowNotFoundException {
		checkArgument(AnalysisState.FINISHED_RUNNING.equals(submittedAnalysis.getAnalysisState()),
				" analysis should be " + AnalysisState.FINISHED_RUNNING);

		AnalysisSubmission submittingAnalysis = analysisSubmissionService.update(submittedAnalysis.getId(),
				ImmutableMap.of("analysisState", AnalysisState.COMPLETING));

		return analysisExecutionServiceGalaxyAsyncSimplified.transferAnalysisResults(submittingAnalysis);
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
}
