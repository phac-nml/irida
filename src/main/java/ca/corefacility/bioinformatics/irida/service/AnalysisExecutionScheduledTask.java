package ca.corefacility.bioinformatics.irida.service;

import java.util.Set;
import java.util.concurrent.Future;

import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;

/**
 * A service for executing {@link AnalysisSubmission} tasks.
 * 
 *
 */
public interface AnalysisExecutionScheduledTask {
	
	/**
	 * Cycle through new {@link AnalysisSubmission}s and prepare
	 * them for execution.
	 * 
	 * @return A {@link Set} of {@link Future} {@link AnalysisSubmission}s
	 *         reflecting the changes in this submission along each of the
	 *         stages.
	 */
	public Set<Future<AnalysisSubmission>> prepareAnalyses();

	/**
	 * Cycle through any outstanding {@link AnalysisSubmission}s and execute
	 * them.
	 * 
	 * @return A {@link Set} of {@link Future} {@link AnalysisSubmission}s
	 *         reflecting the changes in this submission along each of the
	 *         stages.
	 */
	public Set<Future<AnalysisSubmission>> executeAnalyses();

	/**
	 * Cycle through any {@link AnalysisSubmission}s currently running and mark
	 * as complete any completed analyses.
	 * 
	 * @return A {@link Set} of {@link Future} {@link AnalysisSubmission}s
	 *         reflecting the changes in this submission along each of the
	 *         stages.
	 */
	public Set<Future<AnalysisSubmission>> monitorRunningAnalyses();

	/**
	 * Cycle through any completed {@link AnalysisSubmission}s and transfer the
	 * results to IRIDA.
	 * 
	 * @return A {@link Set} of {@link Future} {@link AnalysisSubmission}s
	 *         reflecting the changes in this submission along each of the
	 *         stages.
	 */
	public Set<Future<AnalysisSubmission>> transferAnalysesResults();

	/**
	 * Cycle through any transferred {@link AnalysisSubmission}s and perform post processing as necessary
	 *
	 * @return a Set of Future {@link AnalysisSubmission}s
	 */
	public Set<Future<AnalysisSubmission>> postProcessResults();
	
	/**
	 * Cycle through any completed or errored {@link AnalysisSubmission}s and
	 * delete intermediate files in the execution manager.
	 * 
	 * @return A {@link Set} of {@link Future} {@link AnalysisSubmission}s for
	 *         all the analyses that were cleaned.
	 */
	public Set<Future<AnalysisSubmission>> cleanupAnalysisSubmissions();
}
