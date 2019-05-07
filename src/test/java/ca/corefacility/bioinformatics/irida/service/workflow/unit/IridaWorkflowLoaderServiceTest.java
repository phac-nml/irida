package ca.corefacility.bioinformatics.irida.service.workflow.unit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

import javax.xml.transform.Source;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.oxm.Unmarshaller;
import org.springframework.oxm.XmlMappingException;

import ca.corefacility.bioinformatics.irida.exceptions.IridaWorkflowException;
import ca.corefacility.bioinformatics.irida.exceptions.IridaWorkflowLoadException;
import ca.corefacility.bioinformatics.irida.model.workflow.IridaWorkflow;
import ca.corefacility.bioinformatics.irida.model.workflow.IridaWorkflowTestBuilder;
import ca.corefacility.bioinformatics.irida.model.workflow.description.IridaWorkflowDescription;
import ca.corefacility.bioinformatics.irida.service.impl.AnalysisTypesServiceImpl;
import ca.corefacility.bioinformatics.irida.service.workflow.IridaWorkflowLoaderService;

/**
 * Tests for the IRIDA workflow service.
 * 
 *
 */
public class IridaWorkflowLoaderServiceTest {

	@Mock
	private Unmarshaller workflowDescriptionUnmarshellar;

	private UUID iridaWorkflowId = UUID.fromString("807d1c7a-da99-4559-b8b2-b87ef647319c");
	private IridaWorkflow iridaWorkflow;

	private Path workflowDescriptionPath;
	private Path workflowStructurePath;

	private IridaWorkflowLoaderService iridaWorkflowLoaderService;

	@Before
	public void setup() throws IridaWorkflowException, IOException {
		MockitoAnnotations.initMocks(this);

		iridaWorkflowLoaderService = new IridaWorkflowLoaderService(workflowDescriptionUnmarshellar, new AnalysisTypesServiceImpl());
		iridaWorkflow = IridaWorkflowTestBuilder.buildTestWorkflow(iridaWorkflowId, IridaWorkflowTestBuilder.Input.SINGLE, "reference");

		workflowDescriptionPath = Files.createTempFile("workflowLoaderTest", "tmp");
		workflowStructurePath = Files.createTempFile("workflowLoaderTest", "tmp");
	}

	/**
	 * Tests successfully loading a workflow description.
	 * 
	 * @throws IOException
	 * @throws XmlMappingException
	 * @throws IridaWorkflowLoadException
	 */
	@Test
	public void testLoadWorkflowDescriptionSuccess() throws XmlMappingException, IOException,
			IridaWorkflowLoadException {
		when(workflowDescriptionUnmarshellar.unmarshal(any(Source.class))).thenReturn(
				iridaWorkflow.getWorkflowDescription());

		assertNotNull("invalid workflow description", iridaWorkflowLoaderService.loadWorkflowDescription(workflowDescriptionPath));
	}

	/**
	 * Tests failing to loading a workflow description.
	 * 
	 * @throws IOException
	 * @throws XmlMappingException
	 * @throws IridaWorkflowLoadException
	 */
	@Test(expected = IridaWorkflowLoadException.class)
	public void testLoadWorkflowDescriptionFail() throws XmlMappingException, IOException, IridaWorkflowLoadException {
		IridaWorkflowDescription description = IridaWorkflowTestBuilder.buildTestDescription(iridaWorkflowId, "name",
				"version", null, IridaWorkflowTestBuilder.Input.SINGLE, "reference", true);
		when(workflowDescriptionUnmarshellar.unmarshal(any(Source.class))).thenReturn(description);

		iridaWorkflowLoaderService.loadWorkflowDescription(workflowDescriptionPath);
	}

	/**
	 * Tests successfully loading a workflow structure.
	 */
	@Test
	public void testLoadWorkflowStructureSuccess() throws XmlMappingException, IOException, IridaWorkflowLoadException {
		assertNotNull("invalid workflow structure", iridaWorkflowLoaderService.loadWorkflowStructure(workflowStructurePath));
	}

	/**
	 * Tests successfully loading a workflow structure.
	 */
	@Test(expected = FileNotFoundException.class)
	public void testLoadWorkflowStructureFail() throws XmlMappingException, IOException, IridaWorkflowLoadException {
		Path workflowStructurePath = Files.createTempFile("workflowLoaderTest", "tmp");
		Files.delete(workflowStructurePath);

		iridaWorkflowLoaderService.loadWorkflowStructure(workflowStructurePath);
	}

	/**
	 * Tests successfully loading a workflow.
	 */
	@Test
	public void testLoadIridaWorkflowSuccess() throws XmlMappingException, IOException, IridaWorkflowLoadException {
		when(workflowDescriptionUnmarshellar.unmarshal(any(Source.class))).thenReturn(
				iridaWorkflow.getWorkflowDescription());

		assertNotNull("invalid workflow", iridaWorkflowLoaderService.loadIridaWorkflow(workflowDescriptionPath, workflowStructurePath));
	}

	/**
	 * Tests successfully loading a workflow from a directory.
	 */
	@Test
	public void testLoadIridaWorkflowFromDirectorySuccess() throws XmlMappingException, IOException,
			IridaWorkflowLoadException {
		when(workflowDescriptionUnmarshellar.unmarshal(any(Source.class))).thenReturn(
				iridaWorkflow.getWorkflowDescription());

		Path workflowDirectory = Files.createTempDirectory("workflowLoaderTest");
		Files.createFile(workflowDirectory.resolve("irida_workflow.xml"));
		Files.createFile(workflowDirectory.resolve("irida_workflow_structure.ga"));

		assertNotNull("invalid workflow", iridaWorkflowLoaderService.loadIridaWorkflowFromDirectory(workflowDirectory));
	}

	/**
	 * Tests failing to load a workflow from a directory.
	 */
	@Test(expected = FileNotFoundException.class)
	public void testLoadIridaWorkflowFromDirectoryFail() throws XmlMappingException, IOException,
			IridaWorkflowLoadException {
		when(workflowDescriptionUnmarshellar.unmarshal(any(Source.class))).thenReturn(
				iridaWorkflow.getWorkflowDescription());

		Path workflowDirectory = Files.createTempDirectory("workflowLoaderTest");
		Files.createFile(workflowDirectory.resolve("irida_workflow.xml"));

		iridaWorkflowLoaderService.loadIridaWorkflowFromDirectory(workflowDirectory);
	}

	/**
	 * Tests successfully loading a set of workflows from a directory.
	 */
	@Test
	public void testLoadAllWorkflowImplementationsSuccess() throws XmlMappingException, IOException,
			IridaWorkflowLoadException {
		when(workflowDescriptionUnmarshellar.unmarshal(any(Source.class))).thenReturn(
				iridaWorkflow.getWorkflowDescription());

		Path workflowsDirectory = Files.createTempDirectory("workflowsDirectoryTest");

		Path workflowDirectory = Files.createDirectory(workflowsDirectory.resolve("test"));
		Files.createFile(workflowDirectory.resolve("irida_workflow.xml"));
		Files.createFile(workflowDirectory.resolve("irida_workflow_structure.ga"));

		Path workflowDirectory2 = Files.createDirectory(workflowsDirectory.resolve("test2"));
		Files.createFile(workflowDirectory2.resolve("irida_workflow.xml"));
		Files.createFile(workflowDirectory2.resolve("irida_workflow_structure.ga"));

		assertEquals("invalid number of workflows loaded", 2, iridaWorkflowLoaderService
				.loadAllWorkflowImplementations(workflowsDirectory).size());
	}

	/**
	 * Tests failing to load a set of workflows from a directory.
	 */
	@Test(expected = FileNotFoundException.class)
	public void testLoadAllWorkflowImplementationsFail() throws XmlMappingException, IOException,
			IridaWorkflowLoadException {
		when(workflowDescriptionUnmarshellar.unmarshal(any(Source.class))).thenReturn(
				iridaWorkflow.getWorkflowDescription());

		Path workflowsDirectory = Files.createTempDirectory("workflowsDirectoryTest");

		Path workflowDirectory = Files.createDirectory(workflowsDirectory.resolve("test"));
		Files.createFile(workflowDirectory.resolve("irida_workflow.xml"));

		Path workflowDirectory2 = Files.createDirectory(workflowsDirectory.resolve("test2"));
		Files.createFile(workflowDirectory2.resolve("irida_workflow.xml"));
		Files.createFile(workflowDirectory2.resolve("irida_workflow_structure.ga"));

		assertEquals("invalid number of workflows loaded", 2, iridaWorkflowLoaderService
				.loadAllWorkflowImplementations(workflowsDirectory).size());
	}
}
