package ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.integration;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import javax.validation.ConstraintViolationException;

import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import ca.corefacility.bioinformatics.irida.config.IridaApiServicesConfig;
import ca.corefacility.bioinformatics.irida.config.analysis.AnalysisExecutionServiceTestConfig;
import ca.corefacility.bioinformatics.irida.config.conditions.WindowsPlatformCondition;
import ca.corefacility.bioinformatics.irida.config.data.IridaApiTestDataSourceConfig;
import ca.corefacility.bioinformatics.irida.config.pipeline.data.galaxy.NonWindowsLocalGalaxyConfig;
import ca.corefacility.bioinformatics.irida.config.pipeline.data.galaxy.WindowsLocalGalaxyConfig;
import ca.corefacility.bioinformatics.irida.config.processing.IridaApiTestMultithreadingConfig;
import ca.corefacility.bioinformatics.irida.config.workflow.RemoteWorkflowServiceTestConfig;
import ca.corefacility.bioinformatics.irida.exceptions.galaxy.GalaxyConnectException;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyConnector;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
/**
 * Tests GalaxyConnector to make sure it can properly generate connections to Galaxy.
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = {
	IridaApiServicesConfig.class, IridaApiTestDataSourceConfig.class,
	IridaApiTestMultithreadingConfig.class, NonWindowsLocalGalaxyConfig.class,
	WindowsLocalGalaxyConfig.class, AnalysisExecutionServiceTestConfig.class,
	RemoteWorkflowServiceTestConfig.class})
@ActiveProfiles("test")
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
		DbUnitTestExecutionListener.class })
public class GalaxyConnectorIT {
	
	@Autowired
	private LocalGalaxy localGalaxy;

	/**
	 * Sets up information for Galaxy connector tests.
	 */
	@Before
	public void setup() {
		Assume.assumeFalse(WindowsPlatformCondition.isWindows());
	}

	/**
	 * Test the case of setting up the Galaxy API with an email that does not exist in Galaxy.
	 * @throws ConstraintViolationException
	 * @throws GalaxyConnectException
	 */
	@Test(expected = GalaxyConnectException.class)
	public void testSetupNonExistentEmail()
			throws ConstraintViolationException, GalaxyConnectException {
		GalaxyConnector connector = new GalaxyConnector(localGalaxy.getGalaxyURL(),
				localGalaxy.getNonExistentGalaxyAdminName(),
				localGalaxy.getAdminAPIKey());
		
		assertFalse(connector.isConnected());
		connector.createGalaxyConnection();
	}
	
	/**
	 * Test the case of setting up the Galaxy API with an invalid URL.
	 * @throws ConstraintViolationException
	 * @throws GalaxyConnectException
	 */
	@Test(expected = GalaxyConnectException.class)
	public void testSetupInvalidURL()
			throws ConstraintViolationException, GalaxyConnectException {
		GalaxyConnector connector = new GalaxyConnector(localGalaxy.getInvalidGalaxyURL(),
				localGalaxy.getAdminName(),
				localGalaxy.getAdminAPIKey());
		
		assertFalse(connector.isConnected());
		connector.createGalaxyConnection();
	}
	
	/**
	 * Test the case of properly setting up a connection to the Galaxy API.
	 * @throws ConstraintViolationException
	 * @throws GalaxyConnectException
	 */
	@Test
	public void testSetupGalaxy()
			throws ConstraintViolationException, GalaxyConnectException {
		GalaxyConnector connector = new GalaxyConnector(localGalaxy.getGalaxyURL(),
				localGalaxy.getAdminName(),
				localGalaxy.getAdminAPIKey());
		
		assertTrue(connector.isConnected());
		assertNotNull(connector.createGalaxyConnection());
	}
}
