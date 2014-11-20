package ca.corefacility.bioinformatics.irida.service.workflow.integration;

import static org.junit.Assert.assertEquals;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.oxm.XmlMappingException;
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

	@Autowired
	private IridaWorkflowLoaderService workflowLoaderService;

	private Path workflowXmlPath;
	private Path workflowStructurePath;

	@Before
	public void setup() throws JAXBException, URISyntaxException, FileNotFoundException {
		workflowXmlPath = Paths.get(IridaWorkflowLoaderServiceIT.class.getResource("irida_workflow.xml").toURI());
		workflowStructurePath = Paths.get(IridaWorkflowLoaderServiceIT.class.getResource("irida_workflow_structure.ga")
				.toURI());
	}

	private IridaWorkflow buildTestWorkflow() throws MalformedURLException {
		return new IridaWorkflow(buildTestDescription(), buildTestStructure());
	}

	private IridaWorkflowStructure buildTestStructure() {
		return new IridaWorkflowStructure(workflowStructurePath);
	}

	private IridaWorkflowDescription buildTestDescription() throws MalformedURLException {
		IridaWorkflowDescription iridaWorkflow = new IridaWorkflowDescription();
		iridaWorkflow.setName("TestWorkflow");
		iridaWorkflow.setVersion("1.0");
		iridaWorkflow.setAuthor("Mr. Developer");
		iridaWorkflow.setEmail("developer@example.com");
		iridaWorkflow.setInputs(new WorkflowInput("sequence_reads", "reference"));

		List<WorkflowOutput> outputs = new LinkedList<>();
		outputs.add(new WorkflowOutput("output1", "output1.txt"));
		outputs.add(new WorkflowOutput("output2", "output2.txt"));
		iridaWorkflow.setOutputs(outputs);

		List<WorkflowTool> tools = new LinkedList<>();
		WorkflowTool workflowTool = new WorkflowTool();
		workflowTool.setUrl(new URL("http://toolshed.g2.bx.psu.edu/"));
		workflowTool.setName("sam_to_bam");
		workflowTool.setOwner("devteam");
		workflowTool.setRevision("8176b2575aa1");
		workflowTool.setVersion("1.1.4");
		workflowTool.setId("toolshed.g2.bx.psu.edu/repos/devteam/sam_to_bam/sam_to_bam/1.1.4");
		tools.add(workflowTool);
		iridaWorkflow.setTools(tools);

		return iridaWorkflow;
	}

	/**
	 * Tests loading up the workflow description file.
	 * 
	 * @throws JAXBException
	 * @throws XmlMappingException
	 * @throws IOException
	 */
	@Test
	public void testLoadWorkflowDescription() throws JAXBException, XmlMappingException, IOException {
		IridaWorkflowDescription iridaWorkflowDescription = buildTestDescription();
		IridaWorkflowDescription iridaWorkflowFromFile = workflowLoaderService.loadWorkflowDescription(workflowXmlPath);

		assertEquals(iridaWorkflowFromFile, iridaWorkflowDescription);
	}

	/**
	 * Tests loading up a workflow from a file.
	 * 
	 * @throws JAXBException
	 * @throws XmlMappingException
	 * @throws IOException
	 */
	@Test
	public void testLoadWorkflow() throws JAXBException, XmlMappingException, IOException {
		IridaWorkflow iridaWorkflow = buildTestWorkflow();
		IridaWorkflow iridaWorkflowFromFile = workflowLoaderService.loadIridaWorkflow(workflowXmlPath,
				workflowStructurePath);

		assertEquals(iridaWorkflowFromFile, iridaWorkflow);
	}

	/**
	 * Tests loading up the workflow structure from a file.
	 */
	@Test
	public void testLoadWorkflowStructure() {
		IridaWorkflowStructure iridaWorkflowStructure = buildTestStructure();
		IridaWorkflowStructure iridaWorkflowStructureFromFile = workflowLoaderService
				.loadWorkflowStructure(workflowStructurePath);

		assertEquals(iridaWorkflowStructure, iridaWorkflowStructureFromFile);
	}
}
