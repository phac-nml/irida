package ca.corefacility.bioinformatics.irida.service.workflow.integration;

import static org.junit.Assert.assertNotNull;

import java.net.URISyntaxException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithSecurityContextTestExcecutionListener;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import ca.corefacility.bioinformatics.irida.config.IridaApiNoGalaxyTestConfig;
import ca.corefacility.bioinformatics.irida.config.IridaApiServicesConfig;
import ca.corefacility.bioinformatics.irida.config.data.IridaApiTestDataSourceConfig;
import ca.corefacility.bioinformatics.irida.config.processing.IridaApiTestMultithreadingConfig;
import ca.corefacility.bioinformatics.irida.config.workflow.IridaWorkflowsServiceConfig;
import ca.corefacility.bioinformatics.irida.exceptions.IridaWorkflowLoadException;
import ca.corefacility.bioinformatics.irida.service.workflow.IridaWorkflowsService;

import com.github.springtestdbunit.DbUnitTestExecutionListener;

/**
 * Tests our the {@link IridaWorkflowsService}.
 * 
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = { IridaApiServicesConfig.class,
		IridaApiNoGalaxyTestConfig.class, IridaApiTestDataSourceConfig.class, IridaApiTestMultithreadingConfig.class,
		IridaWorkflowsServiceConfig.class })
@ActiveProfiles("test")
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DbUnitTestExecutionListener.class,
		WithSecurityContextTestExcecutionListener.class })
public class IridaWorkflowsServiceIT {

	@Autowired
	private IridaWorkflowsService iridaWorkflowsService;

	@Before
	public void setup() throws URISyntaxException {
	}

	/**
	 * Tests to make sure we can successfully load a workflow.
	 * 
	 * @throws IridaWorkflowLoadException
	 */
	@Test
	public void testLoadIridaWorkflowSuccess() throws IridaWorkflowLoadException {
		assertNotNull(iridaWorkflowsService.loadIridaWorkflow("TestWorkflow"));
	}

	/**
	 * Tests to make sure we fail to load an unknown workflow.
	 * 
	 * @throws IridaWorkflowLoadException
	 */
	@Test(expected = IridaWorkflowLoadException.class)
	public void testLoadIridaWorkflowFail() throws IridaWorkflowLoadException {
		iridaWorkflowsService.loadIridaWorkflow("InvalidWorkflow");
	}
}
