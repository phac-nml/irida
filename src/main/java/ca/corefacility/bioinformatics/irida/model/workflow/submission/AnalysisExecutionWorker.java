package ca.corefacility.bioinformatics.irida.model.workflow.submission;

import static com.google.common.base.Preconditions.checkNotNull;
import ca.corefacility.bioinformatics.irida.exceptions.NoSuchValueException;
import ca.corefacility.bioinformatics.irida.model.RunnableTaskWorker;
import ca.corefacility.bioinformatics.irida.model.enums.AnalysisState;
import ca.corefacility.bioinformatics.irida.service.AnalysisSubmissionService;

import com.google.common.collect.ImmutableMap;

/**
 * Defines methods for a worker thread to implement for handling tasks for
 * submitting analyses.
 * 
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 */
public abstract class AnalysisExecutionWorker implements RunnableTaskWorker {

	private AnalysisSubmission submission;
	private AnalysisSubmissionService analysisSubmissionService;

	private Exception exception = null;
	private boolean isFinished = false;
	private AnalysisSubmission result = null;

	/**
	 * Builds a new {@link AnalysisExecutionWorker} for the given submission.
	 * 
	 * @param submission
	 *            The submission for this worker.
	 * @param analysisSubmissionService
	 *            The service class for analysis submissions.
	 */
	public AnalysisExecutionWorker(AnalysisSubmission submission, AnalysisSubmissionService analysisSubmissionService) {
		checkNotNull(submission, "submission is null");
		checkNotNull(analysisSubmissionService, "analysisSubmissionService is null");

		this.submission = submission;
		this.analysisSubmissionService = analysisSubmissionService;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void run() {
		try {
			result = doWork();
		} catch (Exception e) {
			exception = e;

			analysisSubmissionService.update(submission.getId(), ImmutableMap.of("analysisState", AnalysisState.ERROR));
		}

		isFinished = true;
	}

	/**
	 * Performs the internal work of execution of analyses.
	 * 
	 * @return The persisted {@link AnalysisSubmission} once the work is
	 *         complete.
	 * @throws Exception
	 *             If an exception occurred.
	 */
	protected abstract AnalysisSubmission doWork() throws Exception;

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
	 * Gets the resulting {@link AnalysisSubmission} from this worker.
	 * 
	 * @return A {@link AnalysisSubmission} if complete.
	 * @throws NoSuchValueException
	 *             If the {@link AnalysisSubmission} does not exist.
	 */
	public AnalysisSubmission getResult() throws NoSuchValueException {
		if (result == null) {
			throw new NoSuchValueException("No value for getResult()");
		} else {
			return result;
		}
	}

	/**
	 * Gets an exception if any was thrown while running this task.
	 * 
	 * @return An exception if any was thrown while running this task.
	 */
	public Exception getException() {
		return exception;
	}
}
