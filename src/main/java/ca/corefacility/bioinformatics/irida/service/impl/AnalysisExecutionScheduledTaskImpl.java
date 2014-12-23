package ca.corefacility.bioinformatics.irida.service.impl;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.AsyncResult;

import com.google.common.collect.Sets;

import ca.corefacility.bioinformatics.irida.exceptions.ExecutionManagerException;
import ca.corefacility.bioinformatics.irida.exceptions.IridaWorkflowNotFoundException;
import ca.corefacility.bioinformatics.irida.model.enums.AnalysisState;
import ca.corefacility.bioinformatics.irida.model.workflow.execution.galaxy.GalaxyWorkflowState;
import ca.corefacility.bioinformatics.irida.model.workflow.execution.galaxy.GalaxyWorkflowStatus;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;
import ca.corefacility.bioinformatics.irida.repositories.analysis.submission.AnalysisSubmissionRepository;
import ca.corefacility.bioinformatics.irida.service.AnalysisExecutionScheduledTask;
import ca.corefacility.bioinformatics.irida.service.analysis.execution.AnalysisExecutionService;

/**
 * Implementation of analysis execution tasks. This will scan for
 * {@link AnalysisSubmission}s and execute the {@link Analysis} defined by the
 * submissions.
 * 
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 */
public class AnalysisExecutionScheduledTaskImpl implements AnalysisExecutionScheduledTask {
	
	private Object prepareAnalysesLock = new Object();
	private Object executeAnalysesLock = new Object();
	private Object monitorRunningAnalysesLock = new Object();
	private Object transferAnalysesResultsLock = new Object();

	private static final Logger logger = LoggerFactory.getLogger(AnalysisExecutionScheduledTaskImpl.class);

	private AnalysisSubmissionRepository analysisSubmissionRepository;
	private AnalysisExecutionService analysisExecutionService;

	/**
	 * Builds a new AnalysisExecutionScheduledTaskImpl with the given service
	 * classes.
	 * 
	 * @param analysisSubmissionRepository
	 *            A repository for {@link AnalysisSubmission}s.
	 * @param analysisExecutionServiceGalaxy
	 *            A service for executing {@link AnalysisSubmission}s.
	 */
	@Autowired
	public AnalysisExecutionScheduledTaskImpl(AnalysisSubmissionRepository analysisSubmissionRepository,
			AnalysisExecutionService analysisExecutionServiceGalaxy) {
		this.analysisSubmissionRepository = analysisSubmissionRepository;
		this.analysisExecutionService = analysisExecutionServiceGalaxy;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Set<Future<AnalysisSubmission>> prepareAnalyses() {
		synchronized(prepareAnalysesLock) {
			logger.trace("Running prepareAnalyses");
			
			List<AnalysisSubmission> analysisSubmissions = analysisSubmissionRepository
					.findByAnalysisState(AnalysisState.NEW);
	
			Set<Future<AnalysisSubmission>> submissions = Sets.newHashSet();
	
			for (AnalysisSubmission analysisSubmission : analysisSubmissions) {
				logger.debug("Preparing " + analysisSubmission);
	
				try {
					submissions.add(analysisExecutionService.prepareSubmission(analysisSubmission));
				} catch (ExecutionManagerException | IridaWorkflowNotFoundException | IOException e) {
					logger.error("Error preparing submission " + analysisSubmission, e);
				}
			}
	
			return submissions;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Set<Future<AnalysisSubmission>> executeAnalyses() {
		synchronized(executeAnalysesLock) {
			logger.trace("Running executeAnalyses");
			
			List<AnalysisSubmission> analysisSubmissions = analysisSubmissionRepository
					.findByAnalysisState(AnalysisState.PREPARED);
	
			Set<Future<AnalysisSubmission>> submissions = Sets.newHashSet();
	
			for (AnalysisSubmission analysisSubmission : analysisSubmissions) {
				logger.debug("Executing " + analysisSubmission);
	
				try {
					submissions.add(analysisExecutionService.executeAnalysis(analysisSubmission));
				} catch (ExecutionManagerException | IridaWorkflowNotFoundException e) {
					logger.error("Error executing submission " + analysisSubmission, e);
				}
			}
	
			return submissions;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Set<Future<AnalysisSubmission>> monitorRunningAnalyses() {
		synchronized(monitorRunningAnalysesLock) {
			logger.trace("Running monitorRunningAnalyses");

			List<AnalysisSubmission> analysisSubmissions = analysisSubmissionRepository
					.findByAnalysisState(AnalysisState.RUNNING);
	
			Set<Future<AnalysisSubmission>> submissions = Sets.newHashSet();
	
			for (AnalysisSubmission analysisSubmission : analysisSubmissions) {
				logger.debug("Checking state of " + analysisSubmission);
	
				try {
					GalaxyWorkflowStatus workflowStatus = analysisExecutionService
							.getWorkflowStatus(analysisSubmission);
					submissions.add(handleWorkflowStatus(workflowStatus, analysisSubmission));
				} catch (ExecutionManagerException | RuntimeException e) {
					logger.error("Error checking state for " + analysisSubmission, e);
					analysisSubmission.setAnalysisState(AnalysisState.ERROR);
					submissions.add(new AsyncResult<>(analysisSubmissionRepository.save(analysisSubmission)));
				}
			}
	
			return submissions;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Set<Future<AnalysisSubmission>> transferAnalysesResults() {
		synchronized(transferAnalysesResultsLock) {
			logger.trace("Running transferAnalysesResults");
			
			List<AnalysisSubmission> analysisSubmissions = analysisSubmissionRepository
					.findByAnalysisState(AnalysisState.FINISHED_RUNNING);
	
			Set<Future<AnalysisSubmission>> submissions = Sets.newHashSet();
	
			for (AnalysisSubmission analysisSubmission : analysisSubmissions) {
				logger.debug("Transferring results for " + analysisSubmission);
	
				try {
					submissions.add(analysisExecutionService.transferAnalysisResults(analysisSubmission));
				} catch (ExecutionManagerException | IridaWorkflowNotFoundException | IOException e) {
					logger.error("Error transferring submission " + analysisSubmission, e);
				}
			}
	
			return submissions;
		}
	}

	/**
	 * Handles checking the status of a workflow in an execution manager.
	 * 
	 * @param workflowStatus
	 *            The status of the workflow.
	 * @param analysisSubmission
	 *            The {@link AnalysisSubmission}.
	 * @return A {@link Future} with an {@link AnalysisSubmission} for this
	 *         submission.
	 */
	private Future<AnalysisSubmission> handleWorkflowStatus(GalaxyWorkflowStatus workflowStatus,
			AnalysisSubmission analysisSubmission) {
		Future<AnalysisSubmission> returnedSubmission;

		GalaxyWorkflowState workflowState = workflowStatus.getState();
		switch (workflowState) {
		case OK:
			logger.debug("Analysis finished " + analysisSubmission);

			analysisSubmission.setAnalysisState(AnalysisState.FINISHED_RUNNING);
			returnedSubmission = new AsyncResult<>(analysisSubmissionRepository.save(analysisSubmission));
			break;

		case NEW:
		case UPLOAD:
		case WAITING:
		case QUEUED:
		case RUNNING:
			logger.debug("Workflow for analysis " + analysisSubmission + " is running: percent "
					+ workflowStatus.getPercentComplete());
			returnedSubmission = new AsyncResult<>(analysisSubmission);

			break;

		default:
			logger.error("Workflow for analysis " + analysisSubmission + " in error state " + workflowStatus);

			analysisSubmission.setAnalysisState(AnalysisState.ERROR);
			returnedSubmission = new AsyncResult<>(analysisSubmissionRepository.save(analysisSubmission));

			break;
		}

		return returnedSubmission;
	}
}
