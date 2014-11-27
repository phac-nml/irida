package ca.corefacility.bioinformatics.irida.service.workflow.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.util.Collection;
import java.util.UUID;

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
import ca.corefacility.bioinformatics.irida.exceptions.IridaWorkflowLoadException;
import ca.corefacility.bioinformatics.irida.exceptions.IridaWorkflowNotFoundException;
import ca.corefacility.bioinformatics.irida.model.workflow.IridaWorkflow;
import ca.corefacility.bioinformatics.irida.service.workflow.IridaWorkflowLoaderService;
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
		IridaApiNoGalaxyTestConfig.class, IridaApiTestDataSourceConfig.class, IridaApiTestMultithreadingConfig.class })
@ActiveProfiles("test")
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DbUnitTestExecutionListener.class,
		WithSecurityContextTestExcecutionListener.class })
public class IridaWorkflowsServiceIT {

	@Autowired
	private IridaWorkflowLoaderService iridaWorkflowLoaderService;

	private IridaWorkflowsService iridaWorkflowsService;

	private static final UUID validWorkflow = UUID.fromString("739f29ea-ae82-48b9-8914-3d2931405db6");
	private static final UUID invalidWorkflow = UUID.fromString("dca0bcc1-cc02-4c08-bd13-c6937d56cf70");

	@Before
	public void setup() throws IOException, IridaWorkflowLoadException {
		iridaWorkflowsService = new IridaWorkflowsService(iridaWorkflowLoaderService);
		iridaWorkflowsService.registerAnalysis(TestAnalysis.class);
	}

	/**
	 * Tests to make sure we can successfully load a workflow.
	 * 
	 * @throws IridaWorkflowNotFoundException
	 * @throws IridaWorkflowLoadException
	 * @throws IOException
	 */
	@Test
	public void testGetIridaWorkflowSuccess() throws IridaWorkflowLoadException {
		assertNotNull(iridaWorkflowsService.getIridaWorkflow(validWorkflow));
	}

	/**
	 * Tests to make sure we fail to load an unknown workflow.
	 * 
	 * @throws IridaWorkflowNotFoundException
	 * 
	 */
	@Test(expected = IridaWorkflowNotFoundException.class)
	public void testGetIridaWorkflowFail() throws IridaWorkflowNotFoundException {
		iridaWorkflowsService.getIridaWorkflow(invalidWorkflow);
	}

	/**
	 * Tests getting a collection of all installed workflows.
	 * 
	 * @throws IridaWorkflowLoadException
	 * @throws IOException
	 */
	@Test
	public void testGetInstalledWorkflows() throws IOException, IridaWorkflowLoadException {
		Collection<IridaWorkflow> iridaWorkflows = iridaWorkflowsService.getInstalledWorkflows();
		assertEquals(2, iridaWorkflows.size());
		IridaWorkflow workflowA = iridaWorkflows.iterator().next();
		assertNotNull(workflowA);
		IridaWorkflow workflowB = iridaWorkflows.iterator().next();
		assertNotNull(workflowB);
	}
}
