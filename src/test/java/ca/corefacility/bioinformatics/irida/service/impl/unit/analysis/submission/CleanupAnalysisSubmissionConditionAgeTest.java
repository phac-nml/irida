package ca.corefacility.bioinformatics.irida.service.impl.unit.analysis.submission;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
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
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);

		cleanupAnalysisSubmissionConditionAge = new CleanupAnalysisSubmissionConditionAge(1);
	}

	/**
	 * Tests successfully cleaning up a submission when it is greater then one
	 * day old
	 */
	@Test
	public void cleanupSubmissionOneDayOldSuccessClean() {
		when(analysisSubmission.getCreatedDate()).thenReturn(DateTime.now().minusDays(2).toDate());

		assertTrue("Should have been marked to clean",
				cleanupAnalysisSubmissionConditionAge.shouldCleanupSubmission(analysisSubmission));
	}

	/**
	 * Tests successfully not cleaning up a submission when it is less then one
	 * day old.
	 */
	@Test
	public void cleanupSubmissionOneDayOldSuccessNoClean() {
		when(analysisSubmission.getCreatedDate()).thenReturn(DateTime.now().toDate());

		assertFalse("Should have not been marked to clean",
				cleanupAnalysisSubmissionConditionAge.shouldCleanupSubmission(analysisSubmission));
	}

	/**
	 * Tests building a CleanupAnalysisSubmissionConditionAge with a
	 * non-positive days to clean.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void cleanupAnalysisSubmissionConditionAgeFailDaysNotPositive() {
		new CleanupAnalysisSubmissionConditionAge(0);
	}
}
