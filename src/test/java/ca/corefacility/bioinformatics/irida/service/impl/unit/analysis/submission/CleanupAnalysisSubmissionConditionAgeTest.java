package ca.corefacility.bioinformatics.irida.service.impl.unit.analysis.submission;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.time.Duration;

import org.joda.time.DateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;
import ca.corefacility.bioinformatics.irida.service.impl.analysis.submission.CleanupAnalysisSubmissionConditionAge;

/**
 * Tests for {@link CleanupAnalysisSubmissionConditionAge}.
 */
public class CleanupAnalysisSubmissionConditionAgeTest {

	@Mock
	private AnalysisSubmission analysisSubmission;

	private CleanupAnalysisSubmissionConditionAge cleanupAnalysisSubmissionConditionAge;

	/**
	 * Setup for tests.
	 */
	@BeforeEach
	public void setup() {
		MockitoAnnotations.openMocks(this);

		cleanupAnalysisSubmissionConditionAge = new CleanupAnalysisSubmissionConditionAge(Duration.ofDays(1));
	}

	/**
	 * Tests successfully cleaning up a submission when it is greater then one
	 * day old
	 */
	@Test
	public void cleanupSubmissionOneDayOldSuccessClean() {
		when(analysisSubmission.getCreatedDate()).thenReturn(DateTime.now().minusDays(2).toDate());

		assertTrue(cleanupAnalysisSubmissionConditionAge.shouldCleanupSubmission(analysisSubmission),
				"Should have been marked to clean");
	}
	
	/**
	 * Tests successfully cleaning up a submission when it is greater then one hour old.
	 */
	@Test
	public void cleanupSubmissionOneHourOldSuccessClean() {
		cleanupAnalysisSubmissionConditionAge = new CleanupAnalysisSubmissionConditionAge(Duration.ofHours(1));
		when(analysisSubmission.getCreatedDate()).thenReturn(DateTime.now().minusHours(2).toDate());

		assertTrue(cleanupAnalysisSubmissionConditionAge.shouldCleanupSubmission(analysisSubmission),
				"Should have been marked to clean");
	}

	/**
	 * Tests successfully not cleaning up a submission when it is less then one
	 * day old.
	 */
	@Test
	public void cleanupSubmissionOneDayOldSuccessNoClean() {
		when(analysisSubmission.getCreatedDate()).thenReturn(DateTime.now().toDate());

		assertFalse(cleanupAnalysisSubmissionConditionAge.shouldCleanupSubmission(analysisSubmission),
				"Should have not been marked to clean");
	}
	
	/**
	 * Tests successfully cleaning up a submission instantly (cleanup time is 0).
	 */
	@Test
	public void cleanupSubmissionZeroTimeSuccessClean() {
		cleanupAnalysisSubmissionConditionAge = new CleanupAnalysisSubmissionConditionAge(Duration.ZERO);
		
		when(analysisSubmission.getCreatedDate()).thenReturn(DateTime.now().toDate());

		assertTrue(cleanupAnalysisSubmissionConditionAge.shouldCleanupSubmission(analysisSubmission),
				"Should have been marked to clean");
	}

	/**
	 * Tests building a CleanupAnalysisSubmissionConditionAge with a
	 * negative duration to clean.
	 */
	@Test
	public void cleanupAnalysisSubmissionConditionAgeFailDaysNotPositive() {
		assertThrows(IllegalArgumentException.class, () -> {
			new CleanupAnalysisSubmissionConditionAge(Duration.ofDays(-1));
		});
	}
}
