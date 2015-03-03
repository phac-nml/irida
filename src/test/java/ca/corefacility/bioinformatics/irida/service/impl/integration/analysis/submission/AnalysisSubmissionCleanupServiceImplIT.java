package ca.corefacility.bioinformatics.irida.service.impl.integration.analysis.submission;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithSecurityContextTestExcecutionListener;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import ca.corefacility.bioinformatics.irida.config.IridaApiNoGalaxyTestConfig;
import ca.corefacility.bioinformatics.irida.config.data.IridaApiTestDataSourceConfig;
import ca.corefacility.bioinformatics.irida.config.processing.IridaApiTestMultithreadingConfig;
import ca.corefacility.bioinformatics.irida.config.services.IridaApiServicesConfig;
import ca.corefacility.bioinformatics.irida.model.enums.AnalysisState;
import ca.corefacility.bioinformatics.irida.service.AnalysisSubmissionCleanupService;
import ca.corefacility.bioinformatics.irida.service.AnalysisSubmissionService;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;

/**
 * Tests for an analysis submission cleanup service.
 * 
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = { IridaApiNoGalaxyTestConfig.class,
		IridaApiServicesConfig.class, IridaApiTestDataSourceConfig.class, IridaApiTestMultithreadingConfig.class })
@ActiveProfiles("test")
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DbUnitTestExecutionListener.class,
		WithSecurityContextTestExcecutionListener.class })
@DatabaseSetup("/ca/corefacility/bioinformatics/irida/service/impl/analysis/submission/AnalysisSubmissionCleanupServiceIT.xml")
@DatabaseTearDown("/ca/corefacility/bioinformatics/irida/test/integration/TableReset.xml")
public class AnalysisSubmissionCleanupServiceImplIT {

	@Autowired
	private AnalysisSubmissionService analysisSubmissionService;
	
	@Autowired
	private AnalysisSubmissionCleanupService analysisSubmissionCleanupService;
	
	@Test(expected=AccessDeniedException.class)
	@WithMockUser(username = "aaron", roles = "")
	public void testSwitchInconsistentSubmissionsToErrorInvalidUser() {
		analysisSubmissionCleanupService.switchInconsistentSubmissionsToError();
	}
	
	/**
	 * Tests successfully switching submissions to error.
	 */
	@Test
	@WithMockUser(username = "aaron", roles = "ADMIN")
	public void testSwitchInconsistentSubmissionsToErrorSuccess() {
		int analysisSubmissionsChanged = analysisSubmissionCleanupService.switchInconsistentSubmissionsToError();

		assertEquals("Switched invalid number of submissions", 3, analysisSubmissionsChanged);
		assertEquals("Did not switch SUBMITTING to ERROR", AnalysisState.ERROR, analysisSubmissionService.read(1L)
				.getAnalysisState());
		assertEquals("Did not switch PREPARING to ERROR", AnalysisState.ERROR, analysisSubmissionService.read(2L)
				.getAnalysisState());
		assertEquals("Did not switch COMPLETING to ERROR", AnalysisState.ERROR, analysisSubmissionService.read(3L)
				.getAnalysisState());
	}
}
