package ca.corefacility.bioinformatics.irida.service.impl.integration.analysis.submission;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

import java.lang.reflect.Field;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithMockUser;

import ca.corefacility.bioinformatics.irida.annotation.ServiceIntegrationTest;
import ca.corefacility.bioinformatics.irida.model.enums.AnalysisState;
import ca.corefacility.bioinformatics.irida.repositories.analysis.submission.AnalysisSubmissionRepository;
import ca.corefacility.bioinformatics.irida.service.AnalysisSubmissionCleanupService;
import ca.corefacility.bioinformatics.irida.service.impl.analysis.submission.AnalysisSubmissionCleanupServiceImpl;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;

/**
 * Tests for an analysis submission cleanup service.
 * 
 *
 */
@ServiceIntegrationTest
@DatabaseSetup("/ca/corefacility/bioinformatics/irida/service/impl/analysis/submission/AnalysisSubmissionCleanupServiceIT.xml")
@DatabaseTearDown("/ca/corefacility/bioinformatics/irida/test/integration/TableReset.xml")
public class AnalysisSubmissionCleanupServiceImplIT {

	@Autowired
	private AnalysisSubmissionRepository analysisSubmissionRepository;

	@Autowired
	private AnalysisSubmissionCleanupService analysisSubmissionCleanupService;

	@Autowired
	private AnalysisSubmissionCleanupService analysisSubmissionCleanupServiceLocal;

	/**
	 * Setup for tests.
	 * 
	 * @throws SecurityException
	 * @throws NoSuchFieldException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 */
	@BeforeEach
	public void setup()
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		analysisSubmissionCleanupServiceLocal = new AnalysisSubmissionCleanupServiceImpl(analysisSubmissionRepository);

		// Unset the 'ranSwitchInconsistentSubmissionsToError' field so we can
		// properly test with multiple test cases.
		Field ranSwitchInconsistentSubmissionsToError = AnalysisSubmissionCleanupServiceImpl.class
				.getDeclaredField("ranSwitchInconsistentSubmissionsToError");
		ranSwitchInconsistentSubmissionsToError.setAccessible(true);
		ranSwitchInconsistentSubmissionsToError.setBoolean(null, false);
	}

	/**
	 * Tests failing to run service due to invalid user.
	 */
	@Test
	@WithMockUser(username = "aaron", roles = "")
	public void testSwitchInconsistentSubmissionsToErrorInvalidUser() {
		assertThrows(AccessDeniedException.class, () -> {
			analysisSubmissionCleanupService.switchInconsistentSubmissionsToError();
		});
	}

	/**
	 * Tests successfully switching submissions to error from autowired service.
	 */
	@Test
	@WithMockUser(username = "aaron", roles = "ADMIN")
	public void testSwitchInconsistentSubmissionsToErrorAutwiredSuccess() {
		int analysisSubmissionsChanged = analysisSubmissionCleanupService.switchInconsistentSubmissionsToError();

		assertEquals(3, analysisSubmissionsChanged, "Switched invalid number of submissions");
		assertEquals(AnalysisState.ERROR, analysisSubmissionRepository.findById(1L).orElse(null).getAnalysisState(),
				"Did not switch SUBMITTING to ERROR");
		assertEquals(AnalysisState.ERROR, analysisSubmissionRepository.findById(2L).orElse(null).getAnalysisState(),
				"Did not switch PREPARING to ERROR");
		assertEquals(AnalysisState.ERROR, analysisSubmissionRepository.findById(3L).orElse(null).getAnalysisState(),
				"Did not switch COMPLETING to ERROR");

		// make sure no other submissions have changed
		assertEquals(AnalysisState.NEW, analysisSubmissionRepository.findById(4L).orElse(null).getAnalysisState(),
				"Analysis submission state has changed");
		assertEquals(AnalysisState.PREPARED, analysisSubmissionRepository.findById(5L).orElse(null).getAnalysisState(),
				"Analysis submission state has changed");
		assertEquals(AnalysisState.RUNNING, analysisSubmissionRepository.findById(6L).orElse(null).getAnalysisState(),
				"Analysis submission state has changed");
		assertEquals(AnalysisState.FINISHED_RUNNING,
				analysisSubmissionRepository.findById(7L).orElse(null).getAnalysisState(),
				"Analysis submission state has changed");
		assertEquals(AnalysisState.COMPLETED, analysisSubmissionRepository.findById(8L).orElse(null).getAnalysisState(),
				"Analysis submission state has changed");
		assertEquals(AnalysisState.ERROR, analysisSubmissionRepository.findById(9L).orElse(null).getAnalysisState(),
				"Analysis submission state has changed");

		try {
			analysisSubmissionCleanupService.switchInconsistentSubmissionsToError();
			fail("Did not throw RuntimeException on second run");
		} catch (RuntimeException e) {
		}
	}

	/**
	 * Tests successfully switching submissions to error from service we build
	 * ourselves.
	 */
	@Test
	@WithMockUser(username = "aaron", roles = "ADMIN")
	public void testSwitchInconsistentSubmissionsToErrorLocalSuccess() {
		int analysisSubmissionsChanged = analysisSubmissionCleanupServiceLocal.switchInconsistentSubmissionsToError();

		assertEquals(3, analysisSubmissionsChanged, "Switched invalid number of submissions");
		assertEquals(AnalysisState.ERROR, analysisSubmissionRepository.findById(1L).orElse(null).getAnalysisState(),
				"Did not switch SUBMITTING to ERROR");
		assertEquals(AnalysisState.ERROR, analysisSubmissionRepository.findById(2L).orElse(null).getAnalysisState(),
				"Did not switch PREPARING to ERROR");
		assertEquals(AnalysisState.ERROR, analysisSubmissionRepository.findById(3L).orElse(null).getAnalysisState(),
				"Did not switch COMPLETING to ERROR");

		// make sure no other submissions have changed
		assertEquals(AnalysisState.NEW, analysisSubmissionRepository.findById(4L).orElse(null).getAnalysisState(),
				"Analysis submission state has changed");
		assertEquals(AnalysisState.PREPARED, analysisSubmissionRepository.findById(5L).orElse(null).getAnalysisState(),
				"Analysis submission state has changed");
		assertEquals(AnalysisState.RUNNING, analysisSubmissionRepository.findById(6L).orElse(null).getAnalysisState(),
				"Analysis submission state has changed");
		assertEquals(AnalysisState.FINISHED_RUNNING,
				analysisSubmissionRepository.findById(7L).orElse(null).getAnalysisState(),
				"Analysis submission state has changed");
		assertEquals(AnalysisState.COMPLETED, analysisSubmissionRepository.findById(8L).orElse(null).getAnalysisState(),
				"Analysis submission state has changed");
		assertEquals(AnalysisState.ERROR, analysisSubmissionRepository.findById(9L).orElse(null).getAnalysisState(),
				"Analysis submission state has changed");
	}

	/**
	 * Tests successfully failing on attempt to switch submissions to error
	 * twice.
	 */
	@Test
	@WithMockUser(username = "aaron", roles = "ADMIN")
	public void testSwitchInconsistentSubmissionsToErrorTwiceFail() {
		assertThrows(RuntimeException.class, () -> {
			analysisSubmissionCleanupServiceLocal.switchInconsistentSubmissionsToError();
			analysisSubmissionCleanupServiceLocal.switchInconsistentSubmissionsToError();
		});
	}
}
