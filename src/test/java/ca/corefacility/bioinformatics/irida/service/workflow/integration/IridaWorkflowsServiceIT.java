package ca.corefacility.bioinformatics.irida.service.workflow.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.util.Collection;
import java.util.Set;
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
import com.google.common.collect.Sets;

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

	private static final UUID validWorkflowId = UUID.fromString("739f29ea-ae82-48b9-8914-3d2931405db6");
	private static final UUID invalidWorkflowId = UUID.fromString("dca0bcc1-cc02-4c08-bd13-c6937d56cf70");

	@Before
	public void setup() throws IOException, IridaWorkflowLoadException {
		iridaWorkflowsService = new IridaWorkflowsService(iridaWorkflowLoaderService);
	}

	/**
	 * Tests to make sure we can successfully load a workflow.
	 * 
	 * @throws IridaWorkflowNotFoundException
	 * @throws IridaWorkflowLoadException
	 * @throws IOException
	 */
	@Test
	public void testGetIridaWorkflowSuccess() throws IridaWorkflowLoadException, IOException {
		iridaWorkflowsService.registerAnalysis(TestAnalysis.class, validWorkflowId);
		assertNotNull(iridaWorkflowsService.getIridaWorkflow(validWorkflowId));
	}

	/**
	 * Tests to make sure we fail to load an unknown workflow.
	 * 
	 * @throws IridaWorkflowLoadException
	 * @throws IOException
	 * 
	 */
	@Test(expected = IridaWorkflowNotFoundException.class)
	public void testGetIridaWorkflowFail() throws IOException, IridaWorkflowLoadException {
		iridaWorkflowsService.registerAnalysis(TestAnalysis.class, validWorkflowId);
		iridaWorkflowsService.getIridaWorkflow(invalidWorkflowId);
	}

	/**
	 * Tests to make sure we succeed to load a valid default workflow.
	 * 
	 * @throws IridaWorkflowNotFoundException
	 * @throws IridaWorkflowLoadException
	 * @throws IOException
	 */
	@Test
	public void testGetDefaultWorkflowSuccess() throws IridaWorkflowLoadException, IOException {
		iridaWorkflowsService.registerAnalysis(TestAnalysis.class, validWorkflowId);
		assertNotNull(iridaWorkflowsService.getDefaultWorkflow(TestAnalysis.class));
	}

	/**
	 * Tests to make sure we fail on setting an invalid default workflow.
	 * 
	 * @throws IridaWorkflowNotFoundException
	 * @throws IridaWorkflowLoadException
	 * @throws IOException
	 */
	@Test(expected = IridaWorkflowLoadException.class)
	public void testGetDefaultWorkflowFail() throws IridaWorkflowLoadException, IOException {
		iridaWorkflowsService.registerAnalysis(TestAnalysis.class, invalidWorkflowId);
	}

	/**
	 * Tests to make sure we fail to register duplicate workflows.
	 * 
	 * @throws IridaWorkflowLoadException
	 * @throws IOException
	 * 
	 */
	@Test(expected = IridaWorkflowLoadException.class)
	public void testRegisterAnalysisDuplicateFail() throws IOException, IridaWorkflowLoadException {
		iridaWorkflowsService.registerAnalysis(TestAnalysis.class, validWorkflowId);
		iridaWorkflowsService.registerAnalysis(TestAnalysis.class, validWorkflowId);
	}

	/**
	 * Tests getting a collection of all installed workflows.
	 * 
	 * @throws IridaWorkflowLoadException
	 * @throws IOException
	 */
	@Test
	public void testGetInstalledWorkflows() throws IOException, IridaWorkflowLoadException {
		iridaWorkflowsService.registerAnalysis(TestAnalysis.class, validWorkflowId);
		Collection<IridaWorkflow> iridaWorkflows = iridaWorkflowsService.getInstalledWorkflows();
		assertEquals(2, iridaWorkflows.size());
		IridaWorkflow workflowA = iridaWorkflows.iterator().next();
		assertNotNull(workflowA);
		IridaWorkflow workflowB = iridaWorkflows.iterator().next();
		assertNotNull(workflowB);
	}
	
	/**
	 * Tests getting a list of all the names of all installed workflows.
	 * @throws IOException
	 * @throws IridaWorkflowLoadException
	 */
	@Test
	public void testGetAllWorkflowsByName() throws IOException, IridaWorkflowLoadException {
		iridaWorkflowsService.registerAnalysis(TestAnalysis.class, validWorkflowId);
		Set<String> names = iridaWorkflowsService.getAllWorkflowsByName();
		assertEquals(Sets.newHashSet("TestWorkflow"), names);
	}
	
	/**
	 * Tests getting a default workflow by the analysis type.
	 * @throws IOException
	 * @throws IridaWorkflowLoadException
	 */
	@Test
	public void testGetDefaultWorkflowAnalysisType() throws IOException, IridaWorkflowLoadException {
		iridaWorkflowsService.registerAnalysis(TestAnalysis.class, validWorkflowId);
		IridaWorkflow defaultWorkflow = iridaWorkflowsService.getDefaultWorkflow(TestAnalysis.class);
		assertEquals(validWorkflowId, defaultWorkflow.getWorkflowIdentifier());
	}
	
	/**
	 * Tests getting a default workflow by the name.
	 * @throws IOException
	 * @throws IridaWorkflowLoadException
	 */
	@Test
	public void testGetDefaultWorkflowByName() throws IOException, IridaWorkflowLoadException {
		iridaWorkflowsService.registerAnalysis(TestAnalysis.class, validWorkflowId);
		IridaWorkflow defaultWorkflow = iridaWorkflowsService.getDefaultWorkflow("TestWorkflow");
		assertEquals(validWorkflowId, defaultWorkflow.getWorkflowIdentifier());
	}
	
	/**
	 * Tests getting all workflows for a given analysis type.
	 * @throws IOException
	 * @throws IridaWorkflowLoadException
	 */
	@Test
	public void testGetAllWorkflowsForAnalysisType() throws IOException, IridaWorkflowLoadException {
		iridaWorkflowsService.registerAnalysis(TestAnalysis.class, validWorkflowId);
		Set<IridaWorkflow> workflows = iridaWorkflowsService.getAllWorkflowsFor(TestAnalysis.class);
		assertEquals(2, workflows.size());
	}
	
	/**
	 * Tests getting all workflows for a given workflow name.
	 * @throws IOException
	 * @throws IridaWorkflowLoadException
	 */
	@Test
	public void testGetAllWorkflowsForWorkflowName() throws IOException, IridaWorkflowLoadException {
		iridaWorkflowsService.registerAnalysis(TestAnalysis.class, validWorkflowId);
		Set<IridaWorkflow> workflows = iridaWorkflowsService.getAllWorkflowsFor("TestWorkflow");
		assertEquals(2, workflows.size());
	}
}
