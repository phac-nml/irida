package ca.corefacility.bioinformatics.irida.service;

/**
 * A service for executing {@link AnalysisSubmission} tasks.
 * 
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 */
public interface AnalysisExecutionScheduledTask {
	
	/**
	 * Cycle through new {@AnalysisSubmission}s and prepare them for execution.
	 */
	public void prepareAnalyses();

	/**
	 * Cycle through any outstanding {@link AnalysisSubmission}s and execute them.
	 */
	public void executeAnalyses();
	
	/**
	 * Cycle through any {@link AnalysisSubmission}s currently running and mark as complete any completed analyses.
	 */
	public void monitorRunningAnalyses();

	/**
	 * Cycle through any completed {@link AnalysisSubmission}s and transfer the results to IRIDA.
	 */
	public void transferAnalysesResults();
}
