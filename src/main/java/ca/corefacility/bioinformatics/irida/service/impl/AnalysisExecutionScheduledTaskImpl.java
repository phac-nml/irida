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
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;
import ca.corefacility.bioinformatics.irida.repositories.analysis.submission.AnalysisSubmissionRepository;
import ca.corefacility.bioinformatics.irida.service.AnalysisExecutionScheduledTask;
import ca.corefacility.bioinformatics.irida.service.analysis.execution.AnalysisExecutionServiceSimplified;

/**
 * Implementation of analysis execution tasks. This will scan for
 * AnalysisSubmissions and execute the analyses defined by the submissions.
 * 
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 */
public class AnalysisExecutionScheduledTaskImpl implements AnalysisExecutionScheduledTask {

	private static final Logger logger = LoggerFactory.getLogger(AnalysisExecutionScheduledTaskImpl.class);

	private AnalysisSubmissionRepository analysisSubmissionRepository;
	private AnalysisExecutionServiceSimplified analysisExecutionServiceSimplified;

	/**
	 * Builds a new AnalysisExecutionScheduledTaskImpl with the given service
	 * classes.
	 * 
	 * @param analysisSubmissionRepository
	 *            A repository for {@link AnalysisSubmission}s.
	 * @param analysisExecutionServiceGalaxySimplified
	 *            A service for executing {@link AnalysisSubmission}s.
	 */
	@Autowired
	public AnalysisExecutionScheduledTaskImpl(AnalysisSubmissionRepository analysisSubmissionRepository,
			AnalysisExecutionServiceSimplified analysisExecutionServiceGalaxySimplified) {
		this.analysisSubmissionRepository = analysisSubmissionRepository;
		this.analysisExecutionServiceSimplified = analysisExecutionServiceGalaxySimplified;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void prepareAnalyses() {
		List<AnalysisSubmission> analysisSubmissions = analysisSubmissionRepository
				.findByAnalysisState(AnalysisState.NEW);

		for (AnalysisSubmission analysisSubmission : analysisSubmissions) {
			logger.debug("Preparing " + analysisSubmission);

			try {
				analysisExecutionServiceSimplified.prepareSubmission(analysisSubmission);
			} catch (ExecutionManagerException | IridaWorkflowNotFoundException | IOException e) {
				logger.error("Error preparing submission " + analysisSubmission, e);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void executeAnalyses() {
		List<AnalysisSubmission> analysisSubmissions = analysisSubmissionRepository
				.findByAnalysisState(AnalysisState.PREPARED);

		for (AnalysisSubmission analysisSubmission : analysisSubmissions) {
			logger.debug("Executing " + analysisSubmission);

			try {
				analysisExecutionServiceSimplified.executeAnalysis(analysisSubmission);
			} catch (ExecutionManagerException | IridaWorkflowNotFoundException e) {
				logger.error("Error executing submission " + analysisSubmission, e);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void monitorRunningAnalyses() {
		List<AnalysisSubmission> analysisSubmissions = analysisSubmissionRepository
				.findByAnalysisState(AnalysisState.RUNNING);

		for (AnalysisSubmission analysisSubmission : analysisSubmissions) {
			logger.debug("Checking state of " + analysisSubmission);

			try {
				WorkflowStatus workflowStatus = analysisExecutionServiceSimplified
						.getWorkflowStatus(analysisSubmission);
				handleWorkflowStatus(workflowStatus, analysisSubmission);
			} catch (ExecutionManagerException e) {
				logger.error("Error checking state for " + analysisSubmission, e);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void transferAnalysesResults() {
		List<AnalysisSubmission> analysisSubmissions = analysisSubmissionRepository
				.findByAnalysisState(AnalysisState.FINISHED_RUNNING);

		for (AnalysisSubmission analysisSubmission : analysisSubmissions) {
			logger.debug("Transferring results for " + analysisSubmission);

			try {
				analysisExecutionServiceSimplified.transferAnalysisResults(analysisSubmission);
			} catch (ExecutionManagerException | IridaWorkflowNotFoundException | IOException e) {
				logger.error("Error transferring submission " + analysisSubmission, e);
			}
		}
	}

	/**
	 * Handles checking the status of a workflow in an execution manager.
	 * 
	 * @param workflowStatus
	 *            The status of the workflow.
	 * @param analysisSubmission
	 *            The {@link AnalysisSubmission}.
	 */
	private void handleWorkflowStatus(WorkflowStatus workflowStatus, AnalysisSubmission analysisSubmission) {
		WorkflowState workflowState = workflowStatus.getState();
		switch (workflowState) {
		case OK:
			logger.debug("Analysis finished " + analysisSubmission);

			analysisSubmission.setAnalysisState(AnalysisState.FINISHED_RUNNING);
			analysisSubmissionRepository.save(analysisSubmission);
			break;

		case NEW:
		case UPLOAD:
		case WAITING:
		case QUEUED:
		case RUNNING:
			logger.debug("Workflow for analysis " + analysisSubmission + " is running: percent "
					+ workflowStatus.getPercentComplete());
			break;

		default:
			logger.error("Workflow for analysis " + analysisSubmission + " in error state " + workflowStatus);

			analysisSubmission.setAnalysisState(AnalysisState.ERROR);
			analysisSubmissionRepository.save(analysisSubmission);

			break;
		}
	}
}
