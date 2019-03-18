package ca.corefacility.bioinformatics.irida.service.impl.integration.analysis.submission;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.lang.reflect.Field;

import ca.corefacility.bioinformatics.irida.config.data.IridaApiJdbcDataSourceConfig;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithSecurityContextTestExecutionListener;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import ca.corefacility.bioinformatics.irida.config.services.IridaApiServicesConfig;
import ca.corefacility.bioinformatics.irida.model.enums.AnalysisState;
import ca.corefacility.bioinformatics.irida.repositories.analysis.submission.AnalysisSubmissionRepository;
import ca.corefacility.bioinformatics.irida.service.AnalysisSubmissionCleanupService;
import ca.corefacility.bioinformatics.irida.service.impl.analysis.submission.AnalysisSubmissionCleanupServiceImpl;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;

/**
 * Tests for an analysis submission cleanup service.
 * 
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = { IridaApiServicesConfig.class,
		IridaApiJdbcDataSourceConfig.class })
@ActiveProfiles("it")
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DbUnitTestExecutionListener.class,
		WithSecurityContextTestExecutionListener.class })
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
	@Before
	public void setup() throws NoSuchFieldException, SecurityException, IllegalArgumentException,
			IllegalAccessException {
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
	@Test(expected=AccessDeniedException.class)
	@WithMockUser(username = "aaron", roles = "")
	public void testSwitchInconsistentSubmissionsToErrorInvalidUser() {
		analysisSubmissionCleanupService.switchInconsistentSubmissionsToError();
	}
	
	/**
	 * Tests successfully switching submissions to error from autowired service.
	 */
	@Test
	@WithMockUser(username = "aaron", roles = "ADMIN")
	public void testSwitchInconsistentSubmissionsToErrorAutwiredSuccess() {
		int analysisSubmissionsChanged = analysisSubmissionCleanupService.switchInconsistentSubmissionsToError();

		assertEquals("Switched invalid number of submissions", 3, analysisSubmissionsChanged);
		assertEquals("Did not switch SUBMITTING to ERROR", AnalysisState.ERROR, analysisSubmissionRepository.findOne(1L)
				.getAnalysisState());
		assertEquals("Did not switch PREPARING to ERROR", AnalysisState.ERROR, analysisSubmissionRepository.findOne(2L)
				.getAnalysisState());
		assertEquals("Did not switch COMPLETING to ERROR", AnalysisState.ERROR, analysisSubmissionRepository.findOne(3L)
				.getAnalysisState());

		// make sure no other submissions have changed
		assertEquals("Analysis submission state has changed", AnalysisState.NEW,
				analysisSubmissionRepository.findOne(4L).getAnalysisState());
		assertEquals("Analysis submission state has changed", AnalysisState.PREPARED, analysisSubmissionRepository.findOne(5L).getAnalysisState());
		assertEquals("Analysis submission state has changed", AnalysisState.RUNNING,
				analysisSubmissionRepository.findOne(6L).getAnalysisState());
		assertEquals("Analysis submission state has changed", AnalysisState.FINISHED_RUNNING, analysisSubmissionRepository.findOne(7L)
				.getAnalysisState());
		assertEquals("Analysis submission state has changed", AnalysisState.COMPLETED, analysisSubmissionRepository.findOne(8L)
				.getAnalysisState());
		assertEquals("Analysis submission state has changed", AnalysisState.ERROR, analysisSubmissionRepository.findOne(9L)
				.getAnalysisState());

		try {
			analysisSubmissionCleanupService.switchInconsistentSubmissionsToError();
			fail("Did not throw RuntimeException on second run");
		} catch (RuntimeException e) {
		}
	}
	
	/**
	 * Tests successfully switching submissions to error from service we build ourselves.
	 */
	@Test
	@WithMockUser(username = "aaron", roles = "ADMIN")
	public void testSwitchInconsistentSubmissionsToErrorLocalSuccess() {
		int analysisSubmissionsChanged = analysisSubmissionCleanupServiceLocal.switchInconsistentSubmissionsToError();

		assertEquals("Switched invalid number of submissions", 3, analysisSubmissionsChanged);
		assertEquals("Did not switch SUBMITTING to ERROR", AnalysisState.ERROR, analysisSubmissionRepository.findOne(1L)
				.getAnalysisState());
		assertEquals("Did not switch PREPARING to ERROR", AnalysisState.ERROR, analysisSubmissionRepository.findOne(2L)
				.getAnalysisState());
		assertEquals("Did not switch COMPLETING to ERROR", AnalysisState.ERROR, analysisSubmissionRepository.findOne(3L)
				.getAnalysisState());

		// make sure no other submissions have changed
		assertEquals("Analysis submission state has changed", AnalysisState.NEW,
				analysisSubmissionRepository.findOne(4L).getAnalysisState());
		assertEquals("Analysis submission state has changed", AnalysisState.PREPARED, analysisSubmissionRepository.findOne(5L).getAnalysisState());
		assertEquals("Analysis submission state has changed", AnalysisState.RUNNING,
				analysisSubmissionRepository.findOne(6L).getAnalysisState());
		assertEquals("Analysis submission state has changed", AnalysisState.FINISHED_RUNNING, analysisSubmissionRepository.findOne(7L)
				.getAnalysisState());
		assertEquals("Analysis submission state has changed", AnalysisState.COMPLETED, analysisSubmissionRepository.findOne(8L)
				.getAnalysisState());
		assertEquals("Analysis submission state has changed", AnalysisState.ERROR, analysisSubmissionRepository.findOne(9L)
				.getAnalysisState());
	}
	
	/**
	 * Tests successfully failing on attempt to switch submissions to error twice.
	 */
	@Test(expected=RuntimeException.class)
	@WithMockUser(username = "aaron", roles = "ADMIN")
	public void testSwitchInconsistentSubmissionsToErrorTwiceFail() {
		analysisSubmissionCleanupServiceLocal.switchInconsistentSubmissionsToError();
		analysisSubmissionCleanupServiceLocal.switchInconsistentSubmissionsToError();
	}
}
