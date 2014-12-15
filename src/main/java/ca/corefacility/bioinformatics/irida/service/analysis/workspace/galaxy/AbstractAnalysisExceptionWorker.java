package ca.corefacility.bioinformatics.irida.service.analysis.workspace.galaxy;

import ca.corefacility.bioinformatics.irida.exceptions.NoSuchValueException;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisExecutionWorker;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;

/**
 * Abstract class for defining some common code for a worker task for analysis
 * executions.
 * 
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 */
public abstract class AbstractAnalysisExceptionWorker extends Thread implements AnalysisExecutionWorker {

	private Exception exception = null;
	private boolean isFinished = false;
	private AnalysisSubmission result = null;
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void run() {
		try {
			result = doWork();
		} catch (Exception e) {
			exception = e;
		}
		
		isFinished = true;
	}
	
	/**
	 * Performs the internal work of execution of analyses.
	 * @return The persisted {@link AnalysisSubmission} once the work is complete.
	 * @throws Exception If an exception occurred.
	 */
	protected abstract AnalysisSubmission doWork();

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean exceptionOccured() {
		return exception != null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isFinished() {
		return isFinished;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AnalysisSubmission getResult() throws NoSuchValueException {
		if (result == null) {
			throw new NoSuchValueException("No value for getResult()");
		} else {
			return result;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Exception getException() {
		return exception;
	}
}