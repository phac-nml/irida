package ca.corefacility.bioinformatics.irida.service.impl;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

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
@Service
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
		logger.debug("Looking for analyses with state "
				+ AnalysisState.SUBMITTED);

		List<AnalysisSubmission> analysisSubmissions = analysisSubmissionRepository
				.findByAnalysisState(AnalysisState.SUBMITTED);

		for (AnalysisSubmission analysisSubmission : analysisSubmissions) {
			logger.debug("Checking analysis " + analysisSubmission);
			String analysisSubmissionId = analysisSubmission
					.getRemoteAnalysisId();
			AnalysisSubmissionPhylogenomics analysisSubmissionPhylogenomics = analysisSubmissionRepository
					.getByType(analysisSubmissionId,
							AnalysisSubmissionPhylogenomics.class);
			try {
				analysisExecutionServicePhylogenomics
						.executeAnalysis(analysisSubmissionPhylogenomics);
			} catch (ExecutionManagerException e) {
				logger.error(
						"Could not execute analysis " + analysisSubmission, e);
				analysisSubmissionService.setStateForAnalysisSubmission(
						analysisSubmissionId, AnalysisState.ERROR);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Scheduled(initialDelay = 1000, fixedRate = 15000)
	public void transferAnalysesResults() {
		logger.debug("Looking for analyses with state " + AnalysisState.RUNNING);

		List<AnalysisSubmission> analysisSubmissions = analysisSubmissionRepository
				.findByAnalysisState(AnalysisState.RUNNING);

		for (AnalysisSubmission analysisSubmission : analysisSubmissions) {
			String analysisSubmissionId = analysisSubmission
					.getRemoteAnalysisId();
			AnalysisSubmissionPhylogenomics analysisSubmissionPhylogenomics = analysisSubmissionRepository
					.getByType(analysisSubmissionId,
							AnalysisSubmissionPhylogenomics.class);
			try {
				WorkflowStatus workflowStatus = analysisExecutionServicePhylogenomics
						.getWorkflowStatus(analysisSubmissionPhylogenomics);
				WorkflowState workflowState = workflowStatus.getState();

				switch (workflowState) {
					case OK:
						Analysis analysisResults = analysisExecutionServicePhylogenomics
								.transferAnalysisResults(analysisSubmissionPhylogenomics);
						logger.debug("Transfered results for analysis submission "
								+ analysisSubmissionPhylogenomics
										.getRemoteAnalysisId()
								+ " to analysis " + analysisResults.getId());
						break;

					case NEW:
					case UPLOAD:
					case WAITING:
					case QUEUED:
					case RUNNING:
						logger.debug("Workflow for analysis "
								+ analysisSubmission + " is running: percent "
								+ workflowStatus.getPercentComplete());
						break;

					default:
						logger.error("Workflow for analysis "
								+ analysisSubmission + " in error state");
						analysisSubmissionService
								.setStateForAnalysisSubmission(
										analysisSubmissionId,
										AnalysisState.ERROR);
						break;
				}
			} catch (ExecutionManagerException e) {
				logger.error("Could not get status for analysis "
						+ analysisSubmission, e);
				analysisSubmissionService.setStateForAnalysisSubmission(
						analysisSubmissionId, AnalysisState.ERROR);
			} catch (IOException e) {
				logger.error("Could not transfer results for analysis "
						+ analysisSubmission, e);
				analysisSubmissionService.setStateForAnalysisSubmission(
						analysisSubmissionId, AnalysisState.ERROR);
			}
		}
	}
}
