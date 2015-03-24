package ca.corefacility.bioinformatics.irida.service;

import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;

/**
 * Defines an interface for a condition on whether or not to clean up an
 * {@link AnalysisSubmission}.
 */
public interface CleanupAnalysisSubmissionCondition {

	/**
	 * Condition which never cleans up analysis submissions.
	 */
	public static final CleanupAnalysisSubmissionCondition NEVER_CLEANUP = new CleanupAnalysisSubmissionCondition() {
		@Override
		public boolean shouldCleanupSubmission(AnalysisSubmission analysisSubmission) {
			return false;
		}
	};
	
	/**
	 * Condition which always cleans up analysis submissions.
	 */
	public static final CleanupAnalysisSubmissionCondition ALWAYS_CLEANUP = new CleanupAnalysisSubmissionCondition() {
		@Override
		public boolean shouldCleanupSubmission(AnalysisSubmission analysisSubmission) {
			return true;
		}
	};

	/**
	 * Whether or not the passed {@link AnalysisSubmission} should be cleaned
	 * up.
	 * 
	 * @param analysisSubmission
	 *            The submission to check.
	 * @return True if the submission should be cleaned up, false otherwise.
	 */
	public boolean shouldCleanupSubmission(AnalysisSubmission analysisSubmission);
}
