package ca.corefacility.bioinformatics.irida.service.workflow.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
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
import ca.corefacility.bioinformatics.irida.exceptions.IridaWorkflowDefaultException;
import ca.corefacility.bioinformatics.irida.exceptions.IridaWorkflowException;
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
	
	private IridaWorkflow testWorkflow1v1;
	private IridaWorkflow testWorkflow1v2;
	
	private IridaWorkflow testWorkflow2;
	
	private static final UUID workflowId1v1 = UUID.fromString("739f29ea-ae82-48b9-8914-3d2931405db6");
	private static final UUID workflowId1v2 = UUID.fromString("c5f29cb2-1b68-4d34-9b93-609266af7551");
	private static final UUID workflowId2 = UUID.fromString("ee59af98-16c8-4337-a49b-8ecf7378dc65");
	private static final UUID invalidWorkflowId = UUID.fromString("dca0bcc1-cc02-4c08-bd13-c6937d56cf70");

	@Before
	public void setup() throws IOException, IridaWorkflowLoadException, URISyntaxException {
		Path workflowVersion1DirectoryPath = Paths.get(IridaWorkflowLoaderServiceIT.class.getResource(
				"workflows/TestAnalysis/1.0").toURI());
		Path workflowVersion2DirectoryPath = Paths.get(IridaWorkflowLoaderServiceIT.class.getResource(
				"workflows/TestAnalysis/2.0").toURI());
		Path workflow2DirectoryPath = Paths.get(IridaWorkflowLoaderServiceIT.class.getResource(
				"workflows/TestAnalysis2/1.0").toURI());
		
		iridaWorkflowsService = new IridaWorkflowsService();
		
		testWorkflow1v1 = iridaWorkflowLoaderService.loadIridaWorkflowFromDirectory(workflowVersion1DirectoryPath);
		testWorkflow1v2 = iridaWorkflowLoaderService.loadIridaWorkflowFromDirectory(workflowVersion2DirectoryPath);
		testWorkflow2 = iridaWorkflowLoaderService.loadIridaWorkflowFromDirectory(workflow2DirectoryPath);

	}

	/**
	 * Tests to make sure we can successfully load a workflow.
	 * 
	 * @throws IridaWorkflowNotFoundException
	 * @throws IridaWorkflowException 
	 */
	@Test
	public void testGetIridaWorkflowSuccess() throws IridaWorkflowException {
		iridaWorkflowsService.registerWorkflow(testWorkflow1v1);
		assertNotNull(iridaWorkflowsService.getIridaWorkflow(workflowId1v1));
	}

	/**
	 * Tests to make sure we fail to load an unknown workflow.
	 * 
	 * @throws IridaWorkflowException 
	 * 
	 */
	@Test(expected = IridaWorkflowNotFoundException.class)
	public void testGetIridaWorkflowFail() throws IridaWorkflowException {
		iridaWorkflowsService.registerWorkflow(testWorkflow1v1);
		iridaWorkflowsService.getIridaWorkflow(invalidWorkflowId);
	}

	/**
	 * Tests to make sure we succeed to load a valid default workflow.
	 * 
	 * @throws IridaWorkflowNotFoundException
	 * @throws IridaWorkflowException 
	 */
	@Test
	public void testGetDefaultWorkflowSuccess() throws IridaWorkflowException {
		iridaWorkflowsService.registerWorkflow(testWorkflow1v1);
		iridaWorkflowsService.registerWorkflow(testWorkflow1v2);
		iridaWorkflowsService.setDefaultWorkflow(workflowId1v1);
		assertEquals(testWorkflow1v1, iridaWorkflowsService.getDefaultWorkflow(TestAnalysis.class));
	}

	/**
	 * Tests to make sure we fail to get a default workflow if it hasn't been set.
	 * 
	 * @throws IridaWorkflowNotFoundException
	 */
	@Test(expected = IridaWorkflowNotFoundException.class)
	public void testGetDefaultWorkflowFail() throws IridaWorkflowException {
		iridaWorkflowsService.registerWorkflow(testWorkflow1v1);
		iridaWorkflowsService.getDefaultWorkflow(TestAnalysis.class);
	}
	
	/**
	 * Tests to make sure we fail to get a default workflow if there is no registered workflows for the type.
	 * 
	 * @throws IridaWorkflowNotFoundException
	 */
	@Test(expected = IridaWorkflowNotFoundException.class)
	public void testGetDefaultWorkflowFailNoType() throws IridaWorkflowException {
		iridaWorkflowsService.getDefaultWorkflow(TestAnalysis.class);
	}
	
	/**
	 * Tests to make sure we fail to set a default workflow if a default workflow of that type exists.
	 * 
	 * @throws IridaWorkflowNotFoundException
	 */
	@Test(expected = IridaWorkflowDefaultException.class)
	public void testSetDefaultWorkflowDuplicateFail() throws IridaWorkflowException {
		iridaWorkflowsService.registerWorkflow(testWorkflow1v1);
		iridaWorkflowsService.registerWorkflow(testWorkflow1v2);
		iridaWorkflowsService.setDefaultWorkflow(workflowId1v1);
		iridaWorkflowsService.setDefaultWorkflow(workflowId1v2);
	}
	
	/**
	 * Tests setting a collection of default workflows
	 * @throws IridaWorkflowException 
	 */
	@Test
	public void testsetDefaultWorkflowsSuccess() throws IridaWorkflowException {
		iridaWorkflowsService.registerWorkflows(Sets.newHashSet(testWorkflow1v1, testWorkflow2));
		iridaWorkflowsService.setDefaultWorkflows(Sets.newHashSet(workflowId1v1, workflowId2));
		
		assertEquals(testWorkflow1v1, iridaWorkflowsService.getDefaultWorkflow(TestAnalysis.class));
		assertEquals(testWorkflow2, iridaWorkflowsService.getDefaultWorkflow(TestAnalysis2.class));
	}

	/**
	 * Tests to make sure we fail to register duplicate workflows.
	 * 
	 * @throws IridaWorkflowException 
	 * 
	 */
	@Test(expected = IridaWorkflowException.class)
	public void testRegisterWorkflowDuplicateFail() throws IridaWorkflowException {
		iridaWorkflowsService.registerWorkflow(testWorkflow1v1);
		iridaWorkflowsService.registerWorkflow(testWorkflow1v1);
	}
	
	/**
	 * Tests registering a set of workflows.
	 * @throws IridaWorkflowException 
	 */
	@Test
	public void testRegisterWorkflowsSuccess() throws IridaWorkflowException {
		iridaWorkflowsService.registerWorkflows(Sets.newHashSet(testWorkflow1v1, testWorkflow2));
		
		Set<String> workflows = iridaWorkflowsService.getAllWorkflowNames();
		assertEquals(Sets.newHashSet("TestWorkflow", "TestWorkflow2"), workflows);
	}

	/**
	 * Tests getting a collection of all installed workflows.
	 * 
	 * @throws IridaWorkflowException 
	 */
	@Test
	public void testGetInstalledWorkflows() throws IridaWorkflowException {
		iridaWorkflowsService.registerWorkflow(testWorkflow1v1);
		iridaWorkflowsService.registerWorkflow(testWorkflow1v2);
		Collection<IridaWorkflow> iridaWorkflows = iridaWorkflowsService.getInstalledWorkflows();
		assertEquals(2, iridaWorkflows.size());
		IridaWorkflow workflowA = iridaWorkflows.iterator().next();
		assertNotNull(workflowA);
		IridaWorkflow workflowB = iridaWorkflows.iterator().next();
		assertNotNull(workflowB);
	}
	
	/**
	 * Tests getting a list of all installed workflows by name.
	 * @throws IridaWorkflowException 
	 */
	@Test
	public void testGetAllWorkflowsByName() throws IridaWorkflowException {
		iridaWorkflowsService.registerWorkflow(testWorkflow1v1);
		iridaWorkflowsService.registerWorkflow(testWorkflow2);
		
		Set<String> workflows = iridaWorkflowsService.getAllWorkflowNames();
		assertEquals(Sets.newHashSet("TestWorkflow", "TestWorkflow2"), workflows);
	}
	
	/**
	 * Tests getting a default workflow by the name.
	 * @throws IridaWorkflowException 
	 */
	@Test
	public void testGetDefaultWorkflowByName() throws IridaWorkflowException {
		iridaWorkflowsService.registerWorkflow(testWorkflow1v1);
		iridaWorkflowsService.registerWorkflow(testWorkflow1v2);
		iridaWorkflowsService.registerWorkflow(testWorkflow2);
		iridaWorkflowsService.setDefaultWorkflow(workflowId1v1);
		iridaWorkflowsService.setDefaultWorkflow(workflowId2);
		
		IridaWorkflow defaultWorkflow = iridaWorkflowsService.getDefaultWorkflow("TestWorkflow");
		assertEquals(testWorkflow1v1, defaultWorkflow);
		
		IridaWorkflow defaultWorkflow2 = iridaWorkflowsService.getDefaultWorkflow("TestWorkflow2");
		assertEquals(testWorkflow2, defaultWorkflow2);
	}
	
	/**
	 * Tests getting all workflows for a given analysis type.
	 * @throws IridaWorkflowException 
	 */
	@Test
	public void testGetAllWorkflowsForAnalysisType() throws IridaWorkflowException {
		iridaWorkflowsService.registerWorkflow(testWorkflow1v1);
		iridaWorkflowsService.registerWorkflow(testWorkflow1v2);
		
		Set<IridaWorkflow> workflows = iridaWorkflowsService.getAllWorkflowsByClass(TestAnalysis.class);
		assertEquals(2, workflows.size());
	}
	
	/**
	 * Tests getting all workflows for a given workflow name.
	 * @throws IridaWorkflowException 
	 */
	@Test
	public void testGetAllWorkflowsForWorkflowName() throws IridaWorkflowException {
		iridaWorkflowsService.registerWorkflow(testWorkflow1v1);
		iridaWorkflowsService.registerWorkflow(testWorkflow1v2);
		
		Set<IridaWorkflow> workflows = iridaWorkflowsService.getAllWorkflowsByName("TestWorkflow");
		assertEquals(2, workflows.size());
	}
}
