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
import ca.corefacility.bioinformatics.irida.config.data.IridaApiTestDataSourceConfig;
import ca.corefacility.bioinformatics.irida.config.processing.IridaApiTestMultithreadingConfig;
import ca.corefacility.bioinformatics.irida.config.services.IridaApiServicesConfig;
import ca.corefacility.bioinformatics.irida.exceptions.IridaWorkflowDefaultException;
import ca.corefacility.bioinformatics.irida.exceptions.IridaWorkflowException;
import ca.corefacility.bioinformatics.irida.exceptions.IridaWorkflowNotFoundException;
import ca.corefacility.bioinformatics.irida.model.enums.AnalysisType;
import ca.corefacility.bioinformatics.irida.model.workflow.IridaWorkflow;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.TestAnalysis;
import ca.corefacility.bioinformatics.irida.model.workflow.config.IridaWorkflowIdSet;
import ca.corefacility.bioinformatics.irida.model.workflow.config.IridaWorkflowSet;
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

	private IridaWorkflow testWorkflowPhylogenomics;

	private static final UUID workflowId1v1 = UUID.fromString("739f29ea-ae82-48b9-8914-3d2931405db6");
	private static final UUID workflowId1v2 = UUID.fromString("c5f29cb2-1b68-4d34-9b93-609266af7551");
	private static final UUID workflowIdPhylogenomics = UUID.fromString("1f9ea289-5053-4e4a-bc76-1f0c60b179f8");
	private static final UUID invalidWorkflowId = UUID.fromString("dca0bcc1-cc02-4c08-bd13-c6937d56cf70");

	@Before
	public void setup() throws IOException, URISyntaxException, IridaWorkflowException {
		Path workflowVersion1DirectoryPath = Paths.get(TestAnalysis.class.getResource(
				"workflows/TestAnalysis/1.0").toURI());
		Path workflowVersion2DirectoryPath = Paths.get(TestAnalysis.class.getResource(
				"workflows/TestAnalysis/2.0").toURI());
		Path workflowPhylogenomicsDirectoryPath = Paths.get(TestAnalysis.class.getResource(
				"workflows/AnalysisPhylogenomicsPipeline/0.1").toURI());

		iridaWorkflowsService = new IridaWorkflowsService(new IridaWorkflowSet(Sets.newHashSet()), new IridaWorkflowIdSet(Sets.newHashSet()));

		testWorkflow1v1 = iridaWorkflowLoaderService.loadIridaWorkflowFromDirectory(workflowVersion1DirectoryPath);
		testWorkflow1v2 = iridaWorkflowLoaderService.loadIridaWorkflowFromDirectory(workflowVersion2DirectoryPath);
		testWorkflowPhylogenomics = iridaWorkflowLoaderService.loadIridaWorkflowFromDirectory(workflowPhylogenomicsDirectoryPath);
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
		assertEquals(testWorkflow1v1, iridaWorkflowsService.getDefaultWorkflowByType(AnalysisType.DEFAULT));
	}

	/**
	 * Tests to make sure we fail to get a default workflow if it hasn't been
	 * set.
	 * 
	 * @throws IridaWorkflowNotFoundException
	 */
	@Test(expected = IridaWorkflowNotFoundException.class)
	public void testGetDefaultWorkflowFail() throws IridaWorkflowException {
		iridaWorkflowsService.registerWorkflow(testWorkflow1v1);
		iridaWorkflowsService.getDefaultWorkflowByType(AnalysisType.DEFAULT);
	}

	/**
	 * Tests to make sure we fail to get a default workflow if there is no
	 * registered workflows for the type.
	 * 
	 * @throws IridaWorkflowNotFoundException
	 */
	@Test(expected = IridaWorkflowNotFoundException.class)
	public void testGetDefaultWorkflowFailNoType() throws IridaWorkflowException {
		iridaWorkflowsService.getDefaultWorkflowByType(AnalysisType.DEFAULT);
	}

	/**
	 * Tests to make sure we fail to set a default workflow if a default
	 * workflow of that type exists.
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
	 * 
	 * @throws IridaWorkflowException
	 */
	@Test
	public void testsetDefaultWorkflowsSuccess() throws IridaWorkflowException {
		iridaWorkflowsService.registerWorkflows(Sets.newHashSet(testWorkflow1v1, testWorkflowPhylogenomics));
		iridaWorkflowsService.setDefaultWorkflows(Sets.newHashSet(workflowId1v1, workflowIdPhylogenomics));

		assertEquals(testWorkflow1v1, iridaWorkflowsService.getDefaultWorkflowByType(AnalysisType.DEFAULT));
		assertEquals(testWorkflowPhylogenomics, iridaWorkflowsService.getDefaultWorkflowByType(AnalysisType.PHYLOGENOMICS));
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
	 * 
	 * @throws IridaWorkflowException
	 */
	@Test
	public void testRegisterWorkflowsSuccess() throws IridaWorkflowException {
		iridaWorkflowsService.registerWorkflows(Sets.newHashSet(testWorkflow1v1, testWorkflowPhylogenomics));

		Set<String> workflows = iridaWorkflowsService.getAllWorkflowNames();
		assertEquals(Sets.newHashSet("TestWorkflow", "SNVPhylTest"), workflows);
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
	 * 
	 * @throws IridaWorkflowException
	 */
	@Test
	public void testGetAllWorkflowsByName() throws IridaWorkflowException {
		iridaWorkflowsService.registerWorkflow(testWorkflow1v1);
		iridaWorkflowsService.registerWorkflow(testWorkflowPhylogenomics);

		Set<String> workflows = iridaWorkflowsService.getAllWorkflowNames();
		assertEquals(Sets.newHashSet("TestWorkflow", "SNVPhylTest"), workflows);
	}

	/**
	 * Tests getting a default workflow by the name.
	 * 
	 * @throws IridaWorkflowException
	 */
	@Test
	public void testGetDefaultWorkflowByName() throws IridaWorkflowException {
		iridaWorkflowsService.registerWorkflow(testWorkflow1v1);
		iridaWorkflowsService.registerWorkflow(testWorkflow1v2);
		iridaWorkflowsService.registerWorkflow(testWorkflowPhylogenomics);
		iridaWorkflowsService.setDefaultWorkflow(workflowId1v1);
		iridaWorkflowsService.setDefaultWorkflow(workflowIdPhylogenomics);

		IridaWorkflow defaultWorkflow = iridaWorkflowsService.getDefaultWorkflowByName("TestWorkflow");
		assertEquals(testWorkflow1v1, defaultWorkflow);

		IridaWorkflow defaultWorkflow2 = iridaWorkflowsService.getDefaultWorkflowByName("SNVPhylTest");
		assertEquals(testWorkflowPhylogenomics, defaultWorkflow2);
	}

	/**
	 * Tests getting all workflows for a given analysis type.
	 * 
	 * @throws IridaWorkflowException
	 */
	@Test
	public void testGetAllWorkflowsForAnalysisType() throws IridaWorkflowException {
		iridaWorkflowsService.registerWorkflow(testWorkflow1v1);
		iridaWorkflowsService.registerWorkflow(testWorkflow1v2);

		Set<IridaWorkflow> workflows = iridaWorkflowsService.getAllWorkflowsByType(AnalysisType.DEFAULT);
		assertEquals(2, workflows.size());
	}

	/**
	 * Tests getting all workflows for a given workflow name.
	 * 
	 * @throws IridaWorkflowException
	 */
	@Test
	public void testGetAllWorkflowsForWorkflowName() throws IridaWorkflowException {
		iridaWorkflowsService.registerWorkflow(testWorkflow1v1);
		iridaWorkflowsService.registerWorkflow(testWorkflow1v2);

		Set<IridaWorkflow> workflows = iridaWorkflowsService.getAllWorkflowsByName("TestWorkflow");
		assertEquals(2, workflows.size());
	}
	
	/**
	 * Tests getting all workflow types.
	 * 
	 * @throws IridaWorkflowException
	 */
	@Test
	public void testGetAllWorkflowTypes() throws IridaWorkflowException {
		iridaWorkflowsService.registerWorkflow(testWorkflow1v1);
		iridaWorkflowsService.registerWorkflow(testWorkflow1v2);
		iridaWorkflowsService.registerWorkflow(testWorkflowPhylogenomics);

		Set<AnalysisType> workflowTypes = iridaWorkflowsService.getAllWorkflowTypes();
		assertEquals(Sets.newHashSet(AnalysisType.DEFAULT, AnalysisType.PHYLOGENOMICS), workflowTypes);
	}
	
	/**
	 * Tests getting all workflow types when no workflows are installed.
	 * 
	 * @throws IridaWorkflowException
	 */
	@Test
	public void testGetAllWorkflowTypesNoWorkflows() throws IridaWorkflowException {
		Set<AnalysisType> workflowTypes = iridaWorkflowsService.getAllWorkflowTypes();
		assertEquals(Sets.newHashSet(), workflowTypes);
	}
	
	/**
	 * Tests getting all workflow names.
	 * 
	 * @throws IridaWorkflowException
	 */
	@Test
	public void testGetAllWorkflowNames() throws IridaWorkflowException {
		iridaWorkflowsService.registerWorkflow(testWorkflow1v1);
		iridaWorkflowsService.registerWorkflow(testWorkflow1v2);
		iridaWorkflowsService.registerWorkflow(testWorkflowPhylogenomics);

		Set<String> workflowTypes = iridaWorkflowsService.getAllWorkflowNames();
		assertEquals(Sets.newHashSet("TestWorkflow", "SNVPhylTest"), workflowTypes);
	}
	
	/**
	 * Tests getting all workflow names when no workflows are installed.
	 * 
	 * @throws IridaWorkflowException
	 */
	@Test
	public void testGetAllWorkflowNamesNoWorkflows() throws IridaWorkflowException {
		Set<String> workflowTypes = iridaWorkflowsService.getAllWorkflowNames();
		assertEquals(Sets.newHashSet(), workflowTypes);
	}
}
