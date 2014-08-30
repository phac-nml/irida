package ca.corefacility.bioinformatics.irida.service.impl;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

import ca.corefacility.bioinformatics.irida.exceptions.ExecutionManagerException;
import ca.corefacility.bioinformatics.irida.model.enums.AnalysisState;
import ca.corefacility.bioinformatics.irida.model.workflow.WorkflowState;
import ca.corefacility.bioinformatics.irida.model.workflow.WorkflowStatus;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.Analysis;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.galaxy.phylogenomics.AnalysisSubmissionPhylogenomics;
import ca.corefacility.bioinformatics.irida.repositories.analysis.submission.AnalysisSubmissionRepository;
import ca.corefacility.bioinformatics.irida.service.AnalysisExecutionScheduledTask;
import ca.corefacility.bioinformatics.irida.service.AnalysisSubmissionService;
import ca.corefacility.bioinformatics.irida.service.analysis.execution.galaxy.phylogenomics.impl.AnalysisExecutionServicePhylogenomics;

/**
 * Implementation of analysis execution tasks. This will scan for
 * AnalysisSubmissions and execute the analyses defined by the submissions.
 * 
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 */
public class AnalysisExecutionScheduledTaskImpl implements
		AnalysisExecutionScheduledTask {

	private static final Logger logger = LoggerFactory
			.getLogger(AnalysisExecutionScheduledTaskImpl.class);

	private AnalysisSubmissionService analysisSubmissionService;
	private AnalysisSubmissionRepository analysisSubmissionRepository;
	private AnalysisExecutionServicePhylogenomics analysisExecutionServicePhylogenomics;

	/**
	 * Builds a new AnalysisExecutionScheduledTaskImpl with the given service
	 * classes.
	 * 
	 * @param analysisSubmissionService
	 *            A service for accessing AnalysisSubmissions.
	 * @param analysisSubmissionRepository
	 *            A repository for analysis submissions.
	 * @param analysisExecutionServicePhylogenomics
	 *            A service for executing analyses.
	 */
	@Autowired
	public AnalysisExecutionScheduledTaskImpl(
			AnalysisSubmissionService analysisSubmissionService,
			AnalysisSubmissionRepository analysisSubmissionRepository,
			AnalysisExecutionServicePhylogenomics analysisExecutionServicePhylogenomics) {
		this.analysisSubmissionService = analysisSubmissionService;
		this.analysisSubmissionRepository = analysisSubmissionRepository;
		this.analysisExecutionServicePhylogenomics = analysisExecutionServicePhylogenomics;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Scheduled(initialDelay = 5000, fixedRate = 15000)
	public void executeAnalyses() {
		logger.debug("Looking for analyses with state " + AnalysisState.NEW);

		AnalysisSubmission analysisSubmission = analysisSubmissionRepository
				.findOneByAnalysisState(AnalysisState.NEW);

		logger.debug("Changing submission to state " + AnalysisState.PREPARING
				+ ": " + analysisSubmission);
		analysisSubmissionService.setStateForAnalysisSubmission(
				analysisSubmission.getId(), AnalysisState.PREPARING);

		AnalysisSubmissionPhylogenomics analysisSubmissionPhylogenomics = analysisSubmissionRepository
				.getByType(analysisSubmission.getId(),
						AnalysisSubmissionPhylogenomics.class);

		try {
			analysisExecutionServicePhylogenomics
					.prepareSubmission(analysisSubmissionPhylogenomics);

			logger.debug("Changing submission to state "
					+ AnalysisState.SUBMITTING + ": " + analysisSubmission);
			analysisSubmissionService.setStateForAnalysisSubmission(
					analysisSubmission.getId(), AnalysisState.SUBMITTING);

			analysisExecutionServicePhylogenomics
					.executeAnalysis(analysisSubmissionPhylogenomics);

			logger.debug("Changing submission to state "
					+ AnalysisState.RUNNING + ": " + analysisSubmission);
			analysisSubmissionService.setStateForAnalysisSubmission(
					analysisSubmission.getId(), AnalysisState.RUNNING);
		} catch (ExecutionManagerException e) {
			logger.error("Could not execute analysis " + analysisSubmission, e);
			analysisSubmissionService.setStateForAnalysisSubmission(
					analysisSubmission.getId(), AnalysisState.ERROR);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Scheduled(initialDelay = 1000, fixedRate = 15000)
	public void transferAnalysesResults() {
		logger.debug("Looking for analyses with state " + AnalysisState.RUNNING);

		AnalysisSubmission analysisSubmission = analysisSubmissionRepository
				.findOneByAnalysisState(AnalysisState.RUNNING);

		AnalysisSubmissionPhylogenomics analysisSubmissionPhylogenomics = analysisSubmissionRepository
				.getByType(analysisSubmission.getId(),
						AnalysisSubmissionPhylogenomics.class);
		try {
			WorkflowStatus workflowStatus = analysisExecutionServicePhylogenomics
					.getWorkflowStatus(analysisSubmissionPhylogenomics);
			WorkflowState workflowState = workflowStatus.getState();

			switch (workflowState) {
				case OK:
					logger.debug("Changing submission to state "
							+ AnalysisState.FINISHED_RUNNING + ": "
							+ analysisSubmission);
					analysisSubmissionService.setStateForAnalysisSubmission(
							analysisSubmission.getId(),
							AnalysisState.FINISHED_RUNNING);
					analysisSubmissionPhylogenomics
							.setAnalysisState(AnalysisState.FINISHED_RUNNING);

					Analysis analysisResults = analysisExecutionServicePhylogenomics
							.transferAnalysisResults(analysisSubmissionPhylogenomics);
					logger.debug("Transfered results for analysis submission "
							+ analysisSubmissionPhylogenomics
									.getRemoteAnalysisId() + " to analysis "
							+ analysisResults.getId());

					logger.debug("Changing submission to state "
							+ AnalysisState.COMPLETED + ": "
							+ analysisSubmission);
					analysisSubmissionService
							.setStateForAnalysisSubmission(
									analysisSubmission.getId(),
									AnalysisState.COMPLETED);
					break;

				case NEW:
				case UPLOAD:
				case WAITING:
				case QUEUED:
				case RUNNING:
					logger.debug("Workflow for analysis " + analysisSubmission
							+ " is running: percent "
							+ workflowStatus.getPercentComplete());
					break;

				default:
					logger.error("Workflow for analysis " + analysisSubmission
							+ " in error state");
					analysisSubmissionService.setStateForAnalysisSubmission(
							analysisSubmission.getId(), AnalysisState.ERROR);
					break;
			}
		} catch (ExecutionManagerException e) {
			logger.error("Could not get status for analysis "
					+ analysisSubmission, e);
			analysisSubmissionService.setStateForAnalysisSubmission(
					analysisSubmission.getId(), AnalysisState.ERROR);
		} catch (IOException e) {
			logger.error("Could not transfer results for analysis "
					+ analysisSubmission, e);
			analysisSubmissionService.setStateForAnalysisSubmission(
					analysisSubmission.getId(), AnalysisState.ERROR);
		}
	}
}
