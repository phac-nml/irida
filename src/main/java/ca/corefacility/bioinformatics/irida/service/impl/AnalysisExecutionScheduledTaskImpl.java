package ca.corefacility.bioinformatics.irida.service.impl;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

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
	private Authentication authentication;

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
	 * @param authentication
	 *            An authentication object for authenticating each of the
	 *            scheduled tasks.
	 */
	@Autowired
	public AnalysisExecutionScheduledTaskImpl(
			AnalysisSubmissionService analysisSubmissionService,
			AnalysisSubmissionRepository analysisSubmissionRepository,
			AnalysisExecutionServicePhylogenomics analysisExecutionServicePhylogenomics,
			Authentication authentication) {
		this.analysisSubmissionService = analysisSubmissionService;
		this.analysisSubmissionRepository = analysisSubmissionRepository;
		this.analysisExecutionServicePhylogenomics = analysisExecutionServicePhylogenomics;
		this.authentication = authentication;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Scheduled(initialDelay = 5000, fixedRate = 15000)
	public void executeAnalyses() {
		SecurityContextHolder.getContext().setAuthentication(authentication);

		logger.debug("Looking for analyses with state " + AnalysisState.NEW);

		List<AnalysisSubmission> analysisSubmissions = analysisSubmissionRepository
				.findByAnalysisState(AnalysisState.NEW);

		if (analysisSubmissions.size() > 0) {
			AnalysisSubmission analysisSubmission = analysisSubmissions.get(0);
			
			setStateForSubmission(analysisSubmission, AnalysisState.PREPARING);

			AnalysisSubmissionPhylogenomics analysisSubmissionPhylogenomics = analysisSubmissionRepository
					.getByType(analysisSubmission.getId(),
							AnalysisSubmissionPhylogenomics.class);

			try {
				AnalysisSubmissionPhylogenomics preparedSubmission = analysisExecutionServicePhylogenomics
						.prepareSubmission(analysisSubmissionPhylogenomics);

				setStateForSubmission(preparedSubmission,
						AnalysisState.SUBMITTING);

				analysisExecutionServicePhylogenomics
						.executeAnalysis(preparedSubmission);

				setStateForSubmission(preparedSubmission, AnalysisState.RUNNING);
			} catch (ExecutionManagerException e) {
				logger.error("Could not execute analysis "
						+ analysisSubmissionPhylogenomics, e);
				setStateForSubmission(analysisSubmissionPhylogenomics,
						AnalysisState.ERROR);
			} finally {
				// Should this be cleared?
				// SecurityContextHolder.clearContext();
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Scheduled(initialDelay = 1000, fixedRate = 15000)
	public void transferAnalysesResults() {
		SecurityContextHolder.getContext().setAuthentication(authentication);

		logger.debug("Looking for analyses with state " + AnalysisState.RUNNING);

		List<AnalysisSubmission> analysisSubmissions = analysisSubmissionRepository
				.findByAnalysisState(AnalysisState.RUNNING);

		if (analysisSubmissions.size() > 0) {
			AnalysisSubmission analysisSubmission = analysisSubmissions.get(0);
			
			AnalysisSubmissionPhylogenomics analysisSubmissionPhylogenomics = analysisSubmissionRepository
					.getByType(analysisSubmission.getId(),
							AnalysisSubmissionPhylogenomics.class);
			try {
				WorkflowStatus workflowStatus = analysisExecutionServicePhylogenomics
						.getWorkflowStatus(analysisSubmissionPhylogenomics);

				handleWorkflowStatus(workflowStatus,
						analysisSubmissionPhylogenomics);

			} catch (ExecutionManagerException e) {
				logger.error("Could not get status for analysis "
						+ analysisSubmissionPhylogenomics, e);
				setStateForSubmission(analysisSubmissionPhylogenomics,
						AnalysisState.ERROR);
			} catch (IOException e) {
				logger.error("Could not transfer results for analysis "
						+ analysisSubmissionPhylogenomics, e);
				setStateForSubmission(analysisSubmissionPhylogenomics,
						AnalysisState.ERROR);
			} finally {
				// SecurityContextHolder.clearContext();
			}
		}
	}

	/**
	 * Handles checking the status of a workflow in an execution manager.
	 * 
	 * @param workflowStatus
	 *            The status of the workflow.
	 * @param analysisSubmissionPhylogenomics
	 *            The analysis submission.
	 * @throws ExecutionManagerException
	 *             If there was an issue in the execution manager.
	 * @throws IOException
	 *             If there was an issue saving the results on an analysis.
	 */
	private void handleWorkflowStatus(WorkflowStatus workflowStatus,
			AnalysisSubmissionPhylogenomics analysisSubmissionPhylogenomics)
			throws ExecutionManagerException, IOException {
		WorkflowState workflowState = workflowStatus.getState();
		switch (workflowState) {
			case OK:
				setStateForSubmission(analysisSubmissionPhylogenomics,
						AnalysisState.FINISHED_RUNNING);

				Analysis analysisResults = analysisExecutionServicePhylogenomics
						.transferAnalysisResults(analysisSubmissionPhylogenomics);

				logger.debug("Transfered results for analysis submission "
						+ analysisSubmissionPhylogenomics.getRemoteAnalysisId()
						+ " to analysis " + analysisResults.getId());

				setStateForSubmission(analysisSubmissionPhylogenomics,
						AnalysisState.COMPLETED);
				break;

			case NEW:
			case UPLOAD:
			case WAITING:
			case QUEUED:
			case RUNNING:
				logger.debug("Workflow for analysis "
						+ analysisSubmissionPhylogenomics
						+ " is running: percent "
						+ workflowStatus.getPercentComplete());
				break;

			default:
				logger.error("Workflow for analysis "
						+ analysisSubmissionPhylogenomics + " in error state "
						+ workflowStatus);
				setStateForSubmission(analysisSubmissionPhylogenomics,
						AnalysisState.ERROR);
				break;
		}
	}

	/**
	 * Changes the given submission to the given state.
	 * 
	 * @param submission
	 *            The submission to change.
	 * @param state
	 *            The state to change this submission to.
	 */
	private void setStateForSubmission(AnalysisSubmission submission,
			AnalysisState state) {
		logger.debug("Changing submission to state " + state + ": "
				+ submission);
		analysisSubmissionService.setStateForAnalysisSubmission(
				submission.getId(), state);
		submission.setAnalysisState(state);
	}
}
