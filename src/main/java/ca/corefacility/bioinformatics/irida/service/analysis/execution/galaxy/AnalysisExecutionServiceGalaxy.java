package ca.corefacility.bioinformatics.irida.service.analysis.execution.galaxy;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.util.concurrent.Future;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;

import ca.corefacility.bioinformatics.irida.exceptions.ExecutionManagerException;
import ca.corefacility.bioinformatics.irida.exceptions.IridaWorkflowAnalysisTypeException;
import ca.corefacility.bioinformatics.irida.exceptions.IridaWorkflowException;
import ca.corefacility.bioinformatics.irida.exceptions.IridaWorkflowNotFoundException;
import ca.corefacility.bioinformatics.irida.model.enums.AnalysisState;
import ca.corefacility.bioinformatics.irida.model.workflow.execution.galaxy.GalaxyWorkflowStatus;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyHistoriesService;
import ca.corefacility.bioinformatics.irida.service.AnalysisSubmissionService;
import ca.corefacility.bioinformatics.irida.service.analysis.execution.AnalysisExecutionService;

import com.google.common.collect.ImmutableMap;

/**
 * Service for performing analyses within a Galaxy execution manager.
 * 
 */
public class AnalysisExecutionServiceGalaxy implements AnalysisExecutionService {

	private final AnalysisSubmissionService analysisSubmissionService;
	private final GalaxyHistoriesService galaxyHistoriesService;
	private final AnalysisExecutionServiceGalaxyAsync analysisExecutionServiceGalaxyAsync;

	/**
	 * Builds a new {@link AnalysisExecutionServiceGalaxy} with the
	 * given information.
	 * 
	 * @param analysisSubmissionService
	 *            A service for analysis submissions.
	 * @param galaxyHistoriesService
	 *            A service for Galaxy histories.
	 * @param analysisExecutionServiceGalaxyAsync
	 *            An {@link AnalysisExecutionServiceGalaxyAsync} for
	 *            executing the tasks asynchronously.
	 */
	@Autowired
	public AnalysisExecutionServiceGalaxy(AnalysisSubmissionService analysisSubmissionService,
			GalaxyHistoriesService galaxyHistoriesService,
			AnalysisExecutionServiceGalaxyAsync analysisExecutionServiceGalaxyAsync) {
		this.analysisSubmissionService = analysisSubmissionService;
		this.galaxyHistoriesService = galaxyHistoriesService;
		this.analysisExecutionServiceGalaxyAsync = analysisExecutionServiceGalaxyAsync;
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

		return analysisExecutionServiceGalaxyAsync.prepareSubmission(preparingAnalysis);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public Future<AnalysisSubmission> executeAnalysis(AnalysisSubmission analysisSubmission)
			throws ExecutionManagerException, IridaWorkflowException {
		checkArgument(AnalysisState.PREPARED.equals(analysisSubmission.getAnalysisState()), " analysis should be "
				+ AnalysisState.PREPARED);

		AnalysisSubmission submittingAnalysis = analysisSubmissionService.update(analysisSubmission.getId(),
				ImmutableMap.of("analysisState", AnalysisState.SUBMITTING));

		return analysisExecutionServiceGalaxyAsync.executeAnalysis(submittingAnalysis);
	}

	/**
	 * {@inheritDoc} 
	 */
	@Override
	@Transactional
	public Future<AnalysisSubmission> transferAnalysisResults(AnalysisSubmission submittedAnalysis)
			throws ExecutionManagerException, IOException, IridaWorkflowNotFoundException, IridaWorkflowAnalysisTypeException {
		checkArgument(AnalysisState.FINISHED_RUNNING.equals(submittedAnalysis.getAnalysisState()),
				" analysis should be " + AnalysisState.FINISHED_RUNNING);

		AnalysisSubmission submittingAnalysis = analysisSubmissionService.update(submittedAnalysis.getId(),
				ImmutableMap.of("analysisState", AnalysisState.COMPLETING));

		return analysisExecutionServiceGalaxyAsync.transferAnalysisResults(submittingAnalysis);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public GalaxyWorkflowStatus getWorkflowStatus(AnalysisSubmission submittedAnalysis) throws ExecutionManagerException {
		checkNotNull(submittedAnalysis, "submittedAnalysis is null");
		checkNotNull(submittedAnalysis.getRemoteAnalysisId(), "remote analysis id is null");

		String analysisId = submittedAnalysis.getRemoteAnalysisId();
		return galaxyHistoriesService.getStatusForHistory(analysisId);
	}
}
