package ca.corefacility.bioinformatics.irida.service.impl;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import ca.corefacility.bioinformatics.irida.exceptions.ExecutionManagerException;
import ca.corefacility.bioinformatics.irida.exceptions.IridaWorkflowNotFoundException;
import ca.corefacility.bioinformatics.irida.model.enums.AnalysisState;
import ca.corefacility.bioinformatics.irida.model.workflow.WorkflowState;
import ca.corefacility.bioinformatics.irida.model.workflow.WorkflowStatus;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.Analysis;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;
import ca.corefacility.bioinformatics.irida.repositories.analysis.submission.AnalysisSubmissionRepository;
import ca.corefacility.bioinformatics.irida.service.AnalysisExecutionScheduledTask;
import ca.corefacility.bioinformatics.irida.service.AnalysisSubmissionService;
import ca.corefacility.bioinformatics.irida.service.analysis.execution.AnalysisExecutionServiceSimplified;

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
	private AnalysisExecutionServiceSimplified analysisExecutionService;

	/**
	 * Builds a new AnalysisExecutionScheduledTaskImpl with the given service
	 * classes.
	 * 
	 * @param analysisSubmissionService
	 *            A service for accessing AnalysisSubmissions.
	 * @param analysisSubmissionRepository
	 *            A repository for analysis submissions.
	 * @param analysisExecutionService
	 *            A service for executing analyses.
	 */
	@Autowired
	public AnalysisExecutionScheduledTaskImpl(
			AnalysisSubmissionService analysisSubmissionService,
			AnalysisSubmissionRepository analysisSubmissionRepository,
			AnalysisExecutionServiceSimplified analysisExecutionService) {
		this.analysisSubmissionService = analysisSubmissionService;
		this.analysisSubmissionRepository = analysisSubmissionRepository;
		this.analysisExecutionService = analysisExecutionService;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void executeAnalyses() {
		logger.debug("Looking for analyses with state " + AnalysisState.NEW);

		List<AnalysisSubmission> analysisSubmissions = analysisSubmissionRepository
				.findByAnalysisState(AnalysisState.NEW);

		if (analysisSubmissions.size() > 0) {
			AnalysisSubmission analysisSubmission = analysisSubmissions
					.get(0);

			setStateForSubmission(analysisSubmission,
					AnalysisState.PREPARING);

			try {
				AnalysisSubmission preparedSubmission = analysisExecutionService
						.prepareSubmission(analysisSubmission);

				setStateForSubmission(preparedSubmission,
						AnalysisState.SUBMITTING);

				analysisExecutionService
						.executeAnalysis(preparedSubmission);

				setStateForSubmission(preparedSubmission,
						AnalysisState.RUNNING);
			} catch (ExecutionManagerException e) {
				logger.error("Could not execute analysis "
						+ analysisSubmission, e);
				setStateForSubmission(analysisSubmission,
						AnalysisState.ERROR);
			} catch (Exception e) {
				logger.error("Error for analysis", e);
				setStateForSubmission(analysisSubmission,
						AnalysisState.ERROR);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void transferAnalysesResults() {
		logger.debug("Looking for analyses with state "
				+ AnalysisState.RUNNING);

		List<AnalysisSubmission> analysisSubmissions = analysisSubmissionRepository
				.findByAnalysisState(AnalysisState.RUNNING);

		if (analysisSubmissions.size() > 0) {
			AnalysisSubmission analysisSubmission = analysisSubmissions
					.get(0);

			try {
				WorkflowStatus workflowStatus = analysisExecutionService
						.getWorkflowStatus(analysisSubmission);

				handleWorkflowStatus(workflowStatus,
						analysisSubmission);

			} catch (ExecutionManagerException e) {
				logger.error("Could not get status for analysis "
						+ analysisSubmission, e);
				setStateForSubmission(analysisSubmission,
						AnalysisState.ERROR);
			} catch (IOException e) {
				logger.error("Could not transfer results for analysis "
						+ analysisSubmission, e);
				setStateForSubmission(analysisSubmission,
						AnalysisState.ERROR);
			} catch (Exception e) {
				logger.error("Error for analysis", e);
				setStateForSubmission(analysisSubmission,
						AnalysisState.ERROR);
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
	 * @throws IridaWorkflowNotFoundException  If an IRIDA workflow could not be found for the analysis.
	 */
	private void handleWorkflowStatus(WorkflowStatus workflowStatus,
			AnalysisSubmission analysisSubmission)
			throws ExecutionManagerException, IOException, IridaWorkflowNotFoundException {
		WorkflowState workflowState = workflowStatus.getState();
		switch (workflowState) {
			case OK:
				setStateForSubmission(analysisSubmission,
						AnalysisState.FINISHED_RUNNING);

				Analysis analysisResults = analysisExecutionService
						.transferAnalysisResults(analysisSubmission);

				logger.debug("Transfered results for analysis submission "
						+ analysisSubmission.getRemoteAnalysisId()
						+ " to analysis " + analysisResults.getId());

				setStateForSubmission(analysisSubmission,
						AnalysisState.COMPLETED);
				break;

			case NEW:
			case UPLOAD:
			case WAITING:
			case QUEUED:
			case RUNNING:
				logger.debug("Workflow for analysis "
						+ analysisSubmission
						+ " is running: percent "
						+ workflowStatus.getPercentComplete());
				break;

			default:
				logger.error("Workflow for analysis "
						+ analysisSubmission + " in error state "
						+ workflowStatus);
				setStateForSubmission(analysisSubmission,
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
