package ca.corefacility.bioinformatics.irida.service.workflow.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.xml.bind.JAXBException;

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
import ca.corefacility.bioinformatics.irida.model.workflow.IridaWorkflow;
import ca.corefacility.bioinformatics.irida.model.workflow.IridaWorkflowDescription;
import ca.corefacility.bioinformatics.irida.model.workflow.IridaWorkflowStructure;
import ca.corefacility.bioinformatics.irida.model.workflow.WorkflowInput;
import ca.corefacility.bioinformatics.irida.model.workflow.WorkflowOutput;
import ca.corefacility.bioinformatics.irida.model.workflow.WorkflowTool;
import ca.corefacility.bioinformatics.irida.service.workflow.IridaWorkflowLoaderService;

import com.github.springtestdbunit.DbUnitTestExecutionListener;

/**
 * Tests loading up workflows.
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
public class IridaWorkflowLoaderServiceIT {

	private final static UUID DEFAULT_ID = UUID.fromString("739f29ea-ae82-48b9-8914-3d2931405db6");

	@Autowired
	private IridaWorkflowLoaderService workflowLoaderService;

	private Path workflowXmlPath;
	private Path workflowStructurePath;
	private Path workflowDirectoryPath;
	private Path workflowVersionDirectoryPath;
	private Path workflowDirectoryPathNoDefinition;
	private Path workflowDirectoryPathNoStructure;
	private Path workflowDirectoryPathNoId;

	@Before
	public void setup() throws JAXBException, URISyntaxException, FileNotFoundException {
		workflowXmlPath = Paths.get(IridaWorkflowLoaderServiceIT.class.getResource(
				"workflows/TestAnalysis/1.0/irida_workflow.xml").toURI());
		workflowStructurePath = Paths.get(IridaWorkflowLoaderServiceIT.class.getResource(
				"workflows/TestAnalysis/1.0/irida_workflow_structure.ga").toURI());
		workflowDirectoryPath = Paths.get(IridaWorkflowLoaderServiceIT.class.getResource("workflows/TestAnalysis")
				.toURI());
		workflowVersionDirectoryPath = Paths.get(IridaWorkflowLoaderServiceIT.class.getResource(
				"workflows/TestAnalysis/1.0").toURI());
		workflowDirectoryPathNoDefinition = Paths.get(IridaWorkflowLoaderServiceIT.class.getResource(
				"workflows/TestAnalysisNoDefinition").toURI());
		workflowDirectoryPathNoStructure = Paths.get(IridaWorkflowLoaderServiceIT.class.getResource(
				"workflows/TestAnalysisNoStructure").toURI());
		workflowDirectoryPathNoId = Paths.get(IridaWorkflowLoaderServiceIT.class.getResource(
				"workflows/TestAnalysisNoId").toURI());
	}

	private IridaWorkflow buildTestWorkflow() throws MalformedURLException {
		return new IridaWorkflow(buildTestDescription(), buildTestStructure());
	}

	private IridaWorkflowStructure buildTestStructure() {
		return new IridaWorkflowStructure(workflowStructurePath);
	}

	private IridaWorkflowDescription buildTestDescription() throws MalformedURLException {
		return buildTestDescription(DEFAULT_ID, "TestWorkflow", "1.0");
	}

	private IridaWorkflowDescription buildTestDescription(UUID id, String name, String version)
			throws MalformedURLException {
		List<WorkflowOutput> outputs = new LinkedList<>();
		outputs.add(new WorkflowOutput("output1", "output1.txt"));
		outputs.add(new WorkflowOutput("output2", "output2.txt"));

		List<WorkflowTool> tools = new LinkedList<>();
		WorkflowTool workflowTool = new WorkflowTool("sam_to_bam",
				"toolshed.g2.bx.psu.edu/repos/devteam/sam_to_bam/sam_to_bam/1.1.4", "1.1.4", "devteam", new URL(
						"http://toolshed.g2.bx.psu.edu/"), "8176b2575aa1");
		tools.add(workflowTool);

		IridaWorkflowDescription iridaWorkflow = new IridaWorkflowDescription(id, name, version, "Mr. Developer",
				"developer@example.com", new WorkflowInput("sequence_reads", "reference"), outputs, tools);

		return iridaWorkflow;
	}

	/**
	 * Tests loading up the workflow description file.
	 * 
	 * @throws IOException
	 * @throws IridaWorkflowLoadException
	 */
	@Test
	public void testLoadWorkflowDescription() throws IOException, IridaWorkflowLoadException {
		IridaWorkflowDescription iridaWorkflowDescription = buildTestDescription();
		IridaWorkflowDescription iridaWorkflowFromFile = workflowLoaderService.loadWorkflowDescription(workflowXmlPath);

		assertEquals(iridaWorkflowFromFile, iridaWorkflowDescription);
	}

	/**
	 * Tests loading up a workflow from a file.
	 * 
	 * @throws IOException
	 * @throws IridaWorkflowLoadException
	 */
	@Test
	public void testLoadWorkflow() throws IOException, IridaWorkflowLoadException {
		IridaWorkflow iridaWorkflow = buildTestWorkflow();
		IridaWorkflow iridaWorkflowFromFile = workflowLoaderService.loadIridaWorkflow(workflowXmlPath,
				workflowStructurePath);

		assertEquals(iridaWorkflowFromFile, iridaWorkflow);
	}

	/**
	 * Tests loading up the workflow structure from a file.
	 * 
	 * @throws FileNotFoundException
	 */
	@Test
	public void testLoadWorkflowStructure() throws FileNotFoundException {
		IridaWorkflowStructure iridaWorkflowStructure = buildTestStructure();
		IridaWorkflowStructure iridaWorkflowStructureFromFile = workflowLoaderService
				.loadWorkflowStructure(workflowStructurePath);

		assertEquals(iridaWorkflowStructure, iridaWorkflowStructureFromFile);
	}

	/**
	 * Tests successfully loading up one version of a workflow from a directory.
	 */
	@Test
	public void testLoadWorkflowFromDirectory() throws IOException, IridaWorkflowLoadException {
		IridaWorkflow iridaWorkflowFromFile = workflowLoaderService
				.loadIridaWorkflowFromDirectory(workflowVersionDirectoryPath);

		assertEquals(buildTestWorkflow(), iridaWorkflowFromFile);
	}

	/**
	 * Tests successfully loading up all implementations of a workflow from a
	 * directory.
	 */
	@Test
	public void testLoadAllWorkflowImplementationsSuccess() throws IOException, IridaWorkflowLoadException {
		Set<IridaWorkflow> iridaWorkflowsFromFile = workflowLoaderService
				.loadAllWorkflowImplementations(workflowDirectoryPath);

		assertEquals(2, iridaWorkflowsFromFile.size());
		Iterator<IridaWorkflow> iter = iridaWorkflowsFromFile.iterator();
		IridaWorkflow workflowA = iter.next();
		IridaWorkflow workflowB = iter.next();

		assertEquals("TestWorkflow", workflowA.getWorkflowDescription().getName());
		assertEquals("TestWorkflow", workflowB.getWorkflowDescription().getName());

		assertTrue("workflows have invalid version numbers",
				(workflowA.getWorkflowDescription().getVersion().equals("1.0") && workflowB.getWorkflowDescription()
						.getVersion().equals("2.0"))
						|| (workflowA.getWorkflowDescription().getVersion().equals("2.0") && workflowB
								.getWorkflowDescription().getVersion().equals("1.0")));
	}

	/**
	 * Tests failing to load up a workflow from a directory (no definition
	 * file).
	 * 
	 * @throws IridaWorkflowLoadException
	 */
	@Test(expected = FileNotFoundException.class)
	public void testLoadWorkflowsFromDirectoryFailNoDefinition() throws IOException, IridaWorkflowLoadException {
		workflowLoaderService.loadAllWorkflowImplementations(workflowDirectoryPathNoDefinition);
	}

	/**
	 * Tests failing to load up a workflow from a directory (no structure file).
	 * 
	 * @throws IridaWorkflowLoadException
	 */
	@Test(expected = FileNotFoundException.class)
	public void testLoadWorkflowsFromDirectoryFailNoStructure() throws IOException, IridaWorkflowLoadException {
		workflowLoaderService.loadAllWorkflowImplementations(workflowDirectoryPathNoStructure);
	}

	/**
	 * Tests failing to load up a workflow with no id.
	 * 
	 * @throws IridaWorkflowLoadException
	 */
	@Test(expected = IridaWorkflowLoadException.class)
	public void testLoadWorkflowsFromDirectoryFailNoId() throws IOException, IridaWorkflowLoadException {
		workflowLoaderService.loadAllWorkflowImplementations(workflowDirectoryPathNoId);
	}
}
