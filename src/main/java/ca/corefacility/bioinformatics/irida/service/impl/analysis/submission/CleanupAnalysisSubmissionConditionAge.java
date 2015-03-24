package ca.corefacility.bioinformatics.irida.service.impl.analysis.submission;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import org.joda.time.DateTime;

import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;
import ca.corefacility.bioinformatics.irida.service.CleanupAnalysisSubmissionCondition;

/**
 * A condition used to cleanup an {@link AnalysisSubmission} if it is too old.
 */
public class CleanupAnalysisSubmissionConditionAge implements CleanupAnalysisSubmissionCondition {

	private final int daysToCleanup;

	/**
	 * Constructs a new {@link CleanupAnalysisSubmissionConditionAge} with the
	 * given time before cleanup.
	 * 
	 * @param daysToCleanup
	 *            The number of days a submission should exist for before
	 *            cleaning up.
	 */
	public CleanupAnalysisSubmissionConditionAge(int daysToCleanup) {
		checkArgument(daysToCleanup > 0, "daysToCleanup must be positive");
		
		this.daysToCleanup = daysToCleanup;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean shouldCleanupSubmission(AnalysisSubmission analysisSubmission) {
		checkNotNull(analysisSubmission, "analysisSubmission is null");
		checkNotNull(analysisSubmission.getCreatedDate(), "createdDate is null");
		
		DateTime createdDate = new DateTime(analysisSubmission.getCreatedDate());
		DateTime dateForDelete = DateTime.now().minusDays(daysToCleanup);
		
		return dateForDelete.isAfter(createdDate);
	}
}
