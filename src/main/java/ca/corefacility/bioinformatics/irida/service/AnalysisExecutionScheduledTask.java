package ca.corefacility.bioinformatics.irida.service;

/**
 * A service for executing analysis tasks.
 * 
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 */
public interface AnalysisExecutionScheduledTask {

	/**
	 * Cycle through any outstanding submissions and execute them.
	 */
	public void executeAnalyses();

	/**
	 * Cycle through any completed submissions and transfer the results.
	 */
	public void transferAnalysesResults();
}
