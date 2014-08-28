package ca.corefacility.bioinformatics.irida.model.enums;

/**
 * Defins a set of states that an analysis submission can be in.
 * 
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 */
public enum AnalysisState {

	/**
	 * Occurs when an analysis is first submitted.
	 */
	SUBMITTED,

	/**
	 * An analysis that is executing in the execution manager.
	 */
	RUNNING,

	/**
	 * An analysis that has finished running in the execution manager.
	 */
	FINISHED_RUNNING,

	/**
	 * An analysis that has completed and been loaded into IRIDA.
	 */
	COMPLETED,

	/**
	 * An analysis that was not successfully able to run.
	 */
	ERROR;
}
