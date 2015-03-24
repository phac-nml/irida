package ca.corefacility.bioinformatics.irida.service.impl.analysis.submission;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.time.Duration;
import java.time.Instant;

import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;
import ca.corefacility.bioinformatics.irida.service.CleanupAnalysisSubmissionCondition;

/**
 * A condition used to cleanup an {@link AnalysisSubmission} if it is too old.
 */
public class CleanupAnalysisSubmissionConditionAge implements CleanupAnalysisSubmissionCondition {

	private final Duration durationToCleanup;

	/**
	 * Constructs a new {@link CleanupAnalysisSubmissionConditionAge} with the
	 * given time before cleanup.
	 * 
	 * @param durationToCleanup
	 *            A {@link Duration} representing the duration before an analysis submission should be cleaned up.
	 */
	public CleanupAnalysisSubmissionConditionAge(Duration durationToCleanup) {
		checkNotNull(durationToCleanup, "durationToCleanup is null");
		checkArgument(!durationToCleanup.isZero() && !durationToCleanup.isNegative(), "durationToCleanup must be positive");
		
		this.durationToCleanup = durationToCleanup;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean shouldCleanupSubmission(AnalysisSubmission analysisSubmission) {
		checkNotNull(analysisSubmission, "analysisSubmission is null");
		checkNotNull(analysisSubmission.getCreatedDate(), "createdDate is null");
		
		Instant instantForCleanup = Instant.now().minus(durationToCleanup);
		Instant createdInstant = analysisSubmission.getCreatedDate().toInstant();
		
		return instantForCleanup.isAfter(createdInstant);
	}
}
