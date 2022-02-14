package ca.corefacility.bioinformatics.irida.service.workflow.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

import javax.xml.transform.Source;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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

	@BeforeEach
	public void setup() throws IridaWorkflowException, IOException {
		MockitoAnnotations.openMocks(this);

		AnalysisTypesServiceImpl analysisTypesService = new AnalysisTypesServiceImpl();
		analysisTypesService.registerDefaultTypes();

		iridaWorkflowLoaderService = new IridaWorkflowLoaderService(workflowDescriptionUnmarshellar, analysisTypesService);
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

		assertNotNull(iridaWorkflowLoaderService.loadWorkflowDescription(workflowDescriptionPath), "invalid workflow description");
	}

	/**
	 * Tests failing to loading a workflow description.
	 * 
	 * @throws IOException
	 * @throws XmlMappingException
	 * @throws IridaWorkflowLoadException
	 */
	@Test
	public void testLoadWorkflowDescriptionFail() throws XmlMappingException, IOException, IridaWorkflowLoadException {
		IridaWorkflowDescription description = IridaWorkflowTestBuilder.buildTestDescription(iridaWorkflowId, "name",
				"version", null, IridaWorkflowTestBuilder.Input.SINGLE, "reference", true);
		when(workflowDescriptionUnmarshellar.unmarshal(any(Source.class))).thenReturn(description);

		assertThrows(IridaWorkflowLoadException.class, () -> {
			iridaWorkflowLoaderService.loadWorkflowDescription(workflowDescriptionPath);
		});
	}

	/**
	 * Tests successfully loading a workflow structure.
	 */
	@Test
	public void testLoadWorkflowStructureSuccess() throws XmlMappingException, IOException, IridaWorkflowLoadException {
		assertNotNull(iridaWorkflowLoaderService.loadWorkflowStructure(workflowStructurePath), "invalid workflow structure");
	}

	/**
	 * Tests successfully loading a workflow structure.
	 */
	@Test
	public void testLoadWorkflowStructureFail() throws XmlMappingException, IOException, IridaWorkflowLoadException {
		Path workflowStructurePath = Files.createTempFile("workflowLoaderTest", "tmp");
		Files.delete(workflowStructurePath);

		assertThrows(FileNotFoundException.class, () -> {
			iridaWorkflowLoaderService.loadWorkflowStructure(workflowStructurePath);
		});
	}

	/**
	 * Tests successfully loading a workflow.
	 */
	@Test
	public void testLoadIridaWorkflowSuccess() throws XmlMappingException, IOException, IridaWorkflowLoadException {
		when(workflowDescriptionUnmarshellar.unmarshal(any(Source.class))).thenReturn(
				iridaWorkflow.getWorkflowDescription());

		assertNotNull(iridaWorkflowLoaderService.loadIridaWorkflow(workflowDescriptionPath, workflowStructurePath), "invalid workflow");
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

		assertNotNull(iridaWorkflowLoaderService.loadIridaWorkflowFromDirectory(workflowDirectory), "invalid workflow");
	}

	/**
	 * Tests failing to load a workflow from a directory.
	 */
	@Test
	public void testLoadIridaWorkflowFromDirectoryFail() throws XmlMappingException, IOException,
			IridaWorkflowLoadException {
		when(workflowDescriptionUnmarshellar.unmarshal(any(Source.class))).thenReturn(
				iridaWorkflow.getWorkflowDescription());

		Path workflowDirectory = Files.createTempDirectory("workflowLoaderTest");
		Files.createFile(workflowDirectory.resolve("irida_workflow.xml"));

		assertThrows(FileNotFoundException.class, () -> {
			iridaWorkflowLoaderService.loadIridaWorkflowFromDirectory(workflowDirectory);
		});
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

		assertEquals(2, iridaWorkflowLoaderService.loadAllWorkflowImplementations(workflowsDirectory).size(),
				"invalid number of workflows loaded");
	}

	/**
	 * Tests failing to load a set of workflows from a directory.
	 */
	@Test
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

		assertThrows(FileNotFoundException.class, () -> {
			assertEquals(2, iridaWorkflowLoaderService.loadAllWorkflowImplementations(workflowsDirectory).size(),
					"invalid number of workflows loaded");
		});
	}
}
