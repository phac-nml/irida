package ca.corefacility.bioinformatics.irida.service.analysis.execution.galaxy;

import ca.corefacility.bioinformatics.irida.exceptions.ExecutionManagerException;
import ca.corefacility.bioinformatics.irida.exceptions.IridaWorkflowAnalysisTypeException;
import ca.corefacility.bioinformatics.irida.exceptions.IridaWorkflowException;
import ca.corefacility.bioinformatics.irida.exceptions.IridaWorkflowNotFoundException;
import ca.corefacility.bioinformatics.irida.model.enums.AnalysisCleanedState;
import ca.corefacility.bioinformatics.irida.model.enums.AnalysisState;
import ca.corefacility.bioinformatics.irida.model.workflow.execution.galaxy.GalaxyWorkflowStatus;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyHistoriesService;
import ca.corefacility.bioinformatics.irida.service.AnalysisSubmissionService;
import ca.corefacility.bioinformatics.irida.service.analysis.execution.AnalysisExecutionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.util.Collection;
import java.util.concurrent.Future;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Service for performing analyses within a Galaxy execution manager.
 * 
 */
public class AnalysisExecutionServiceGalaxy implements AnalysisExecutionService {
	
	private static final Logger logger = LoggerFactory.getLogger(AnalysisExecutionServiceGalaxy.class);

	private final AnalysisSubmissionService analysisSubmissionService;
	private final GalaxyHistoriesService galaxyHistoriesService;
	private final AnalysisExecutionServiceGalaxyAsync analysisExecutionServiceGalaxyAsync;
	private final AnalysisExecutionServiceGalaxyCleanupAsync analysisExecutionServiceGalaxyCleanupAsync;
	
	@Value("${irida.workflow.max-running}")
	private int maxJobs;

	/**
	 * Builds a new {@link AnalysisExecutionServiceGalaxy} with the given
	 * information.
	 * 
	 * @param analysisSubmissionService
	 *            A service for analysis submissions.
	 * @param galaxyHistoriesService
	 *            A service for Galaxy histories.
	 * @param analysisExecutionServiceGalaxyAsync
	 *            An {@link AnalysisExecutionServiceGalaxyAsync} for executing
	 *            the tasks asynchronously.
	 * @param analysisExecutionServiceGalaxyCleanupAsync
	 *            A service for cleaning up files in Galaxy.
	 */
	@Autowired
	public AnalysisExecutionServiceGalaxy(AnalysisSubmissionService analysisSubmissionService,
			GalaxyHistoriesService galaxyHistoriesService,
			AnalysisExecutionServiceGalaxyAsync analysisExecutionServiceGalaxyAsync,
			AnalysisExecutionServiceGalaxyCleanupAsync analysisExecutionServiceGalaxyCleanupAsync) {
		this.analysisSubmissionService = analysisSubmissionService;
		this.galaxyHistoriesService = galaxyHistoriesService;
		this.analysisExecutionServiceGalaxyAsync = analysisExecutionServiceGalaxyAsync;
		this.analysisExecutionServiceGalaxyCleanupAsync = analysisExecutionServiceGalaxyCleanupAsync;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Future<AnalysisSubmission> prepareSubmission(final AnalysisSubmission analysisSubmission)
			throws IridaWorkflowNotFoundException, IOException, ExecutionManagerException {
		checkArgument(AnalysisState.NEW.equals(analysisSubmission.getAnalysisState()), "analysis state should be "
				+ AnalysisState.NEW);

		analysisSubmission.setAnalysisState(AnalysisState.PREPARING);
		AnalysisSubmission preparingAnalysis = analysisSubmissionService.update(analysisSubmission);

		return analysisExecutionServiceGalaxyAsync.prepareSubmission(preparingAnalysis);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Future<AnalysisSubmission> executeAnalysis(AnalysisSubmission analysisSubmission)
			throws ExecutionManagerException, IridaWorkflowException, IOException {
		checkArgument(AnalysisState.PREPARED.equals(analysisSubmission.getAnalysisState()), " analysis should be "
				+ AnalysisState.PREPARED);

		analysisSubmission.setAnalysisState(AnalysisState.SUBMITTING);
		AnalysisSubmission submittingAnalysis = analysisSubmissionService.update(analysisSubmission);

		return analysisExecutionServiceGalaxyAsync.executeAnalysis(submittingAnalysis);
	}

	/**
	 * {@inheritDoc} 
	 */
	@Override
	public Future<AnalysisSubmission> transferAnalysisResults(AnalysisSubmission submittedAnalysis)
			throws ExecutionManagerException, IOException, IridaWorkflowNotFoundException, IridaWorkflowAnalysisTypeException {
		checkArgument(AnalysisState.FINISHED_RUNNING.equals(submittedAnalysis.getAnalysisState()),
				" analysis should be " + AnalysisState.FINISHED_RUNNING);

		submittedAnalysis.setAnalysisState(AnalysisState.COMPLETING);
		AnalysisSubmission submittingAnalysis = analysisSubmissionService.update(submittedAnalysis);

		return analysisExecutionServiceGalaxyAsync.transferAnalysisResults(submittingAnalysis);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Future<AnalysisSubmission> postProcessResults(AnalysisSubmission analysisSubmission) {
		checkArgument(AnalysisState.TRANSFERRED.equals(analysisSubmission.getAnalysisState()),
				" analysis should be " + AnalysisState.TRANSFERRED);

		analysisSubmission.setAnalysisState(AnalysisState.POST_PROCESSING);
		analysisSubmission = analysisSubmissionService.update(analysisSubmission);

		//re-reading submission to ensure paths get correctly translated
		analysisSubmission = analysisSubmissionService.read(analysisSubmission.getId());

		return analysisExecutionServiceGalaxyAsync.postProcessResults(analysisSubmission);
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

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Future<AnalysisSubmission> cleanupSubmission(AnalysisSubmission analysisSubmission)
			throws ExecutionManagerException {
		checkNotNull(analysisSubmission, "analysisSubmission is null");
		checkArgument(AnalysisCleanedState.NOT_CLEANED.equals(analysisSubmission.getAnalysisCleanedState()),
				"cleaned state is not " + AnalysisCleanedState.NOT_CLEANED);

		analysisSubmission.setAnalysisCleanedState(AnalysisCleanedState.CLEANING);
		AnalysisSubmission cleaningAnalysis = analysisSubmissionService.update(analysisSubmission);

		return analysisExecutionServiceGalaxyCleanupAsync.cleanupSubmission(cleaningAnalysis);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getCapacity() {
		Collection<AnalysisSubmission> runningAnalyses = analysisSubmissionService
				.findAnalysesByState(AnalysisState.getRunningStates());

		int available = maxJobs - runningAnalyses.size();

		logger.trace("Available analysis slots: " + available);
		return available;
	}
}
