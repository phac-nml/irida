package ca.corefacility.bioinformatics.irida.model.workflow.unit;

import static org.junit.Assert.assertEquals;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.JAXBException;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLResult;
import org.junit.Before;
import org.junit.Test;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

import ca.corefacility.bioinformatics.irida.model.workflow.IridaWorkflow;
import ca.corefacility.bioinformatics.irida.model.workflow.WorkflowInput;
import ca.corefacility.bioinformatics.irida.model.workflow.WorkflowOutput;
import ca.corefacility.bioinformatics.irida.model.workflow.WorkflowTool;

/**
 * Tests out saving/loading workflow files.
 * 
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 */
public class IridaWorkflowTest {

	private Jaxb2Marshaller jaxb2marshaller;
	private Path workflowXmlPath;

	@Before
	public void setup() throws JAXBException, URISyntaxException, FileNotFoundException {
		workflowXmlPath = Paths
				.get(IridaWorkflowTest.class.getResource(
						"irida_workflow.xml").toURI());
		
		jaxb2marshaller = new Jaxb2Marshaller();
		jaxb2marshaller.setPackagesToScan(new String[] { "ca.corefacility.bioinformatics.irida.model.workflow" });
	}
	
	/**
	 * Tests loading XML test file.
	 * @throws FileNotFoundException
	 * @throws JAXBException
	 * @throws UnsupportedEncodingException
	 * @throws MalformedURLException 
	 */
	@Test
	public void testLoad() throws FileNotFoundException, JAXBException, UnsupportedEncodingException, MalformedURLException {
		IridaWorkflow iridaWorkflow = new IridaWorkflow();
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
		
		Source source = new StreamSource(new FileInputStream(workflowXmlPath.toFile()));
		IridaWorkflow iridaWorkflowFromFile = (IridaWorkflow)jaxb2marshaller.unmarshal(source);
		
		assertEquals(iridaWorkflowFromFile, iridaWorkflow);
	}

	/**
	 * Tests saving and loading from XML.
	 * @throws JAXBException
	 * @throws IOException 
	 */
	@Test
	public void testSaveAndLoad() throws JAXBException, IOException {
		IridaWorkflow workflow = new IridaWorkflow();
		workflow.setName("TestWorkflow");
		workflow.setVersion("1.0");
		workflow.setAuthor("Mr. Developer");
		workflow.setEmail("developer@example.com");
		workflow.setInputs(new WorkflowInput("sequence_reads", "reference"));

		List<WorkflowOutput> outputs = new LinkedList<>();
		outputs.add(new WorkflowOutput("output1", "output1.txt"));
		outputs.add(new WorkflowOutput("output2", "output2.txt"));
		workflow.setOutputs(outputs);
		
		List<WorkflowTool> tools = new LinkedList<>();
		WorkflowTool workflowTool = new WorkflowTool();
		workflowTool.setUrl(new URL("http://toolshed.g2.bx.psu.edu/"));
		workflowTool.setName("sam_to_bam");
		workflowTool.setOwner("devteam");
		workflowTool.setRevision("8176b2575aa1");
		workflowTool.setVersion("1.1.4");
		workflowTool.setId("toolshed.g2.bx.psu.edu/repos/devteam/sam_to_bam/sam_to_bam/1.1.4");
		tools.add(workflowTool);
		workflow.setTools(tools);

		Path outputFilePath = Files.createTempFile("irida_workflow", "xml");
		outputFilePath.toFile().deleteOnExit();
		OutputStream os = new FileOutputStream(outputFilePath.toFile());
		XMLResult result = new XMLResult(os, OutputFormat.createPrettyPrint());
		jaxb2marshaller.marshal(workflow, result);
		
		Source source = new StreamSource(new FileInputStream(outputFilePath.toFile()));
		IridaWorkflow iridaWorkflowFromFile = (IridaWorkflow)jaxb2marshaller.unmarshal(source);
		
		assertEquals(workflow, iridaWorkflowFromFile);
	}
}
