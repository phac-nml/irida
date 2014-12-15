package ca.corefacility.bioinformatics.irida.model.workflow.submission;

import ca.corefacility.bioinformatics.irida.exceptions.NoSuchValueException;
import ca.corefacility.bioinformatics.irida.model.RunnableTaskWorker;

/**
 * Defines methods for a worker thread to implement for handling tasks for
 * submitting analyses.
 * 
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 */
public interface AnalysisExecutionWorker extends RunnableTaskWorker {

	/**
	 * Gets the resulting {@link AnalysisSubmission} from this worker.
	 * 
	 * @return A {@link AnalysisSubmission} if complete.
	 * @throws NoSuchValueException
	 *             If the {@link AnalysisSubmission} does not exist.
	 */
	public AnalysisSubmission getResult() throws NoSuchValueException;

	/**
	 * Gets an exception if any was thrown while running this task.
	 * 
	 * @return An exception if any was thrown while running this task.
	 */
	public Exception getException();
}
