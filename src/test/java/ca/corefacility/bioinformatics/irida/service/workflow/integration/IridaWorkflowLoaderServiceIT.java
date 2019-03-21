package ca.corefacility.bioinformatics.irida.service.workflow.integration;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.xml.bind.JAXBException;

import ca.corefacility.bioinformatics.irida.config.data.IridaApiJdbcDataSourceConfig;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithSecurityContextTestExecutionListener;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import ca.corefacility.bioinformatics.irida.config.services.IridaApiServicesConfig;
import ca.corefacility.bioinformatics.irida.exceptions.IridaWorkflowLoadException;
import ca.corefacility.bioinformatics.irida.model.workflow.IridaWorkflow;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.TestAnalysis;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.type.BuiltInAnalysisTypes;
import ca.corefacility.bioinformatics.irida.model.workflow.description.IridaToolParameter;
import ca.corefacility.bioinformatics.irida.model.workflow.description.IridaWorkflowDescription;
import ca.corefacility.bioinformatics.irida.model.workflow.description.IridaWorkflowInput;
import ca.corefacility.bioinformatics.irida.model.workflow.description.IridaWorkflowOutput;
import ca.corefacility.bioinformatics.irida.model.workflow.description.IridaWorkflowParameter;
import ca.corefacility.bioinformatics.irida.model.workflow.description.IridaWorkflowToolRepository;
import ca.corefacility.bioinformatics.irida.model.workflow.structure.IridaWorkflowStructure;
import ca.corefacility.bioinformatics.irida.service.workflow.IridaWorkflowLoaderService;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * Tests loading up workflows.
 * 
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = { IridaApiServicesConfig.class,
		IridaApiJdbcDataSourceConfig.class })
@ActiveProfiles("it")
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DbUnitTestExecutionListener.class,
		WithSecurityContextTestExecutionListener.class })
public class IridaWorkflowLoaderServiceIT {

	private final static UUID DEFAULT_SINGLE_ID = UUID.fromString("739f29ea-ae82-48b9-8914-3d2931405db6");
	private final static UUID DEFAULT_PAIRED_ID = UUID.fromString("ec93b50d-c9dd-4000-98fc-4a70d46ddd36");
	private final static UUID DEFAULT_SINGLE_PAIRED_ID = UUID.fromString("d92e9918-1e3d-4dea-b2b9-089f1256ac1b");
	private final static UUID DEFAULT_SINGLE_SAMPLE_ID = UUID.fromString("a9692a52-5bc6-4da2-a89d-d880bb35bfe4");
	private final static UUID DEFAULT_SINGLE_SAMPLE_UNSET_ID = UUID.fromString("6371716f-fbef-4b10-8474-f66666e341bc");
	private final static UUID DEFAULT_NOT_SINGLE_SAMPLE_ID = UUID.fromString("76248aa7-8215-4a97-a3d8-f4b4edc165a6");
	private final static UUID DEFAULT_INVALID_SINGLE_SAMPLE_ID = UUID.fromString("79720955-feba-43a5-8209-ca0bf876eff2");

	@Autowired
	private IridaWorkflowLoaderService workflowLoaderService;

	private Path workflowSingleXmlPath;
	private Path workflowPairedXmlPath;
	private Path workflowSinglePairedXmlPath;
	private Path workflowRequiresSingleSampleXmlPath;
	private Path workflowRequiresSingleSampleUnsetXmlPath;
	private Path workflowNotRequiresSingleSampleXmlPath;
	private Path workflowInvalidRequiresSingleSampleXmlPath;
	private Path workflowStructurePath;
	private Path workflowDirectoryPath;
	private Path workflowVersionDirectoryPath;
	private Path workflowDirectoryPathNoDefinition;
	private Path workflowDirectoryPathNoStructure;
	private Path workflowDirectoryPathNoId;
	private Path workflowDirectoryPathInvalidType;
	private Path workflowDirectoryPathNoParameters;
	private Path workflowDirectoryPathWithParameters;
	private Path workflowDirectoryPathWithParametersNoDefaultNotRequired;
	private Path workflowDirectoryPathWithParametersNoDefaultIsRequired;
	private Path workflowDirectoryPathWithParametersWithDefaultIsRequired;
	private Path workflowDirectoryPathWithParametersWithDynamicSourceNotRequired;
	private Path workflowDirectoryPathWithParametersMultipleDynamicSources;

	@Before
	public void setup() throws JAXBException, URISyntaxException, FileNotFoundException {
		workflowSingleXmlPath = Paths.get(TestAnalysis.class.getResource("workflows/TestAnalysis/1.0/irida_workflow.xml")
				.toURI());
		workflowPairedXmlPath = Paths.get(TestAnalysis.class.getResource("workflows/TestAnalysis/1.0-paired/irida_workflow.xml")
				.toURI());
		workflowSinglePairedXmlPath = Paths.get(TestAnalysis.class.getResource("workflows/TestAnalysis/1.0-single-paired/irida_workflow.xml")
				.toURI());
		workflowRequiresSingleSampleXmlPath = Paths.get(TestAnalysis.class.getResource("workflows/TestAnalysis/1.0-requires-single-sample/irida_workflow.xml")
				.toURI());
		workflowRequiresSingleSampleUnsetXmlPath = Paths.get(TestAnalysis.class.getResource("workflows/TestAnalysis/1.0-requires-single-sample-unset/irida_workflow.xml")
				.toURI());
		workflowNotRequiresSingleSampleXmlPath = Paths.get(TestAnalysis.class.getResource("workflows/TestAnalysis/1.0-not-requires-single-sample/irida_workflow.xml")
				.toURI());
		workflowInvalidRequiresSingleSampleXmlPath = Paths.get(TestAnalysis.class.getResource("workflows/TestAnalysis/1.0-invalid-requires-single-sample/irida_workflow.xml")
				.toURI());
		workflowStructurePath = Paths.get(TestAnalysis.class.getResource(
				"workflows/TestAnalysis/1.0/irida_workflow_structure.ga").toURI());
		workflowDirectoryPath = Paths.get(TestAnalysis.class.getResource("workflows/TestAnalysis").toURI());
		workflowVersionDirectoryPath = Paths.get(TestAnalysis.class.getResource("workflows/TestAnalysis/1.0").toURI());
		workflowDirectoryPathNoDefinition = Paths.get(TestAnalysis.class.getResource(
				"workflows/TestAnalysisNoDefinition").toURI());
		workflowDirectoryPathNoStructure = Paths.get(TestAnalysis.class
				.getResource("workflows/TestAnalysisNoStructure").toURI());
		workflowDirectoryPathInvalidType = Paths.get(TestAnalysis.class
				.getResource("workflows/TestAnalysisInvalidType").toURI());
		workflowDirectoryPathNoParameters = Paths.get(TestAnalysis.class
				.getResource("workflows/TestAnalysisNoParameters/1.0").toURI());
		workflowDirectoryPathWithParameters = Paths.get(TestAnalysis.class
				.getResource("workflows/TestAnalysisWithParameters/1.0").toURI());
		workflowDirectoryPathWithParametersNoDefaultNotRequired = Paths.get(TestAnalysis.class
				.getResource("workflows/TestAnalysisWithParametersNoDefaultNotRequired/1.0").toURI());
		workflowDirectoryPathWithParametersNoDefaultIsRequired = Paths.get(TestAnalysis.class
				.getResource("workflows/TestAnalysisWithParametersNoDefaultIsRequired/1.0").toURI());
		workflowDirectoryPathWithParametersWithDefaultIsRequired = Paths.get(TestAnalysis.class
				.getResource("workflows/TestAnalysisWithParametersWithDefaultIsRequired/1.0").toURI());
		workflowDirectoryPathWithParametersWithDynamicSourceNotRequired = Paths.get(TestAnalysis.class
				.getResource("workflows/TestAnalysisWithParametersWithDynamicSourceNotRequired/1.0").toURI());
		workflowDirectoryPathWithParametersMultipleDynamicSources = Paths.get(TestAnalysis.class
				.getResource("workflows/TestAnalysisWithParametersMultipleDynamicSources/1.0").toURI());
		workflowDirectoryPathNoId = Paths.get(TestAnalysis.class.getResource("workflows/TestAnalysisNoId").toURI());
	}

	private IridaWorkflow buildTestWorkflowSingle() throws MalformedURLException {
		return new IridaWorkflow(buildTestDescriptionSingle(), buildTestStructure());
	}

	private IridaWorkflowStructure buildTestStructure() {
		return new IridaWorkflowStructure(workflowStructurePath);
	}

	private IridaWorkflowDescription buildTestDescriptionSingle() throws MalformedURLException {
		return buildTestDescription(DEFAULT_SINGLE_ID, "TestWorkflow", "1.0", "sequence_reads", null, false);
	}
	
	private IridaWorkflowDescription buildTestDescriptionPaired() throws MalformedURLException {
		return buildTestDescription(DEFAULT_PAIRED_ID, "TestWorkflow", "1.0-paired", null, "sequence_reads_paired", false);
	}
	
	private IridaWorkflowDescription buildTestDescriptionSinglePaired() throws MalformedURLException {
		return buildTestDescription(DEFAULT_SINGLE_PAIRED_ID, "TestWorkflow", "1.0-single-paired", "sequence_reads_single", "sequence_reads_paired", false);
	}
	
	private IridaWorkflowDescription buildTestDescriptionRequiresSingleSample() throws MalformedURLException {
		return buildTestDescription(DEFAULT_SINGLE_SAMPLE_ID, "TestWorkflow", "1.0-requires-single-sample", "sequence_reads_single", "sequence_reads_paired", true);
	}
	
	private IridaWorkflowDescription buildTestDescriptionRequiresSingleSampleUnset() throws MalformedURLException {
		return buildTestDescription(DEFAULT_SINGLE_SAMPLE_UNSET_ID, "TestWorkflow", "1.0-requires-single-sample-unset", "sequence_reads_single", "sequence_reads_paired", false);
	}
	
	private IridaWorkflowDescription buildTestDescriptionNotRequiresSingleSample() throws MalformedURLException {
		return buildTestDescription(DEFAULT_NOT_SINGLE_SAMPLE_ID, "TestWorkflow", "1.0-not-requires-single-sample", "sequence_reads_single", "sequence_reads_paired", false);
	}
	
	private IridaWorkflowDescription buildTestDescriptionRequiresSingleSampleInvalid() throws MalformedURLException {
		return buildTestDescription(DEFAULT_INVALID_SINGLE_SAMPLE_ID, "TestWorkflow", "1.0-invalid-requires-single-sample", "sequence_reads_single", "sequence_reads_paired", false);
	}

	private IridaWorkflowDescription buildTestDescription(UUID id, String name, String version, String sequenceReadsSingle, String sequenceReadsPaired,
			boolean requiresSingleSample)
			throws MalformedURLException {
		List<IridaWorkflowOutput> outputs = new LinkedList<>();
		outputs.add(new IridaWorkflowOutput("output1", "output1.txt"));
		outputs.add(new IridaWorkflowOutput("output2", "output2.txt"));

		List<IridaWorkflowToolRepository> tools = new LinkedList<>();
		IridaWorkflowToolRepository workflowTool = new IridaWorkflowToolRepository("sam_to_bam", "devteam", new URL(
				"http://toolshed.g2.bx.psu.edu/"), "8176b2575aa1");
		tools.add(workflowTool);
		
		List<IridaWorkflowParameter> parameters = new LinkedList<>();
		IridaToolParameter tool1 = new IridaToolParameter("irida.corefacility.ca/galaxy-shed/repos/irida/test-tool/0.1", "a");
		IridaToolParameter tool2 = new IridaToolParameter("irida.corefacility.ca/galaxy-shed/repos/irida/test-tool/0.1", "b");
		IridaWorkflowParameter parameter1 = new IridaWorkflowParameter("test-parameter", "1", Lists.newArrayList(tool1, tool2));
		parameters.add(parameter1);

		IridaWorkflowDescription iridaWorkflow = new IridaWorkflowDescription(id, name, version,
				BuiltInAnalysisTypes.DEFAULT, new IridaWorkflowInput(sequenceReadsSingle, sequenceReadsPaired, "reference", requiresSingleSample),
				outputs, tools, parameters);

		return iridaWorkflow;
	}

	/**
	 * Tests loading up the workflow description file (single end data).
	 * 
	 * @throws IOException
	 * @throws IridaWorkflowLoadException
	 */
	@Test
	public void testLoadWorkflowDescriptionSingle() throws IOException, IridaWorkflowLoadException {
		IridaWorkflowDescription iridaWorkflowDescription = buildTestDescriptionSingle();
		IridaWorkflowDescription iridaWorkflowFromFile = workflowLoaderService.loadWorkflowDescription(workflowSingleXmlPath);

		assertEquals("irida workflow description is invalid", iridaWorkflowFromFile, iridaWorkflowDescription);
	}
	
	/**
	 * Tests loading up the workflow description file (paired end data).
	 * 
	 * @throws IOException
	 * @throws IridaWorkflowLoadException
	 */
	@Test
	public void testLoadWorkflowDescriptionPaired() throws IOException, IridaWorkflowLoadException {
		IridaWorkflowDescription iridaWorkflowDescription = buildTestDescriptionPaired();
		IridaWorkflowDescription iridaWorkflowFromFile = workflowLoaderService.loadWorkflowDescription(workflowPairedXmlPath);

		assertEquals("irida workflow description is invalid", iridaWorkflowFromFile, iridaWorkflowDescription);
	}
	
	/**
	 * Tests loading up the workflow description file (single and paired end data).
	 * 
	 * @throws IOException
	 * @throws IridaWorkflowLoadException
	 */
	@Test
	public void testLoadWorkflowDescriptionSinglePaired() throws IOException, IridaWorkflowLoadException {
		IridaWorkflowDescription iridaWorkflowDescription = buildTestDescriptionSinglePaired();
		IridaWorkflowDescription iridaWorkflowFromFile = workflowLoaderService.loadWorkflowDescription(workflowSinglePairedXmlPath);

		assertEquals("irida workflow description is invalid", iridaWorkflowFromFile, iridaWorkflowDescription);
	}
	
	/**
	 * Tests loading up the workflow description file that does not require a single sample.
	 * 
	 * @throws IOException
	 * @throws IridaWorkflowLoadException
	 */
	@Test
	public void testLoadWorkflowDescriptionNotRequiresSingleSample() throws IOException, IridaWorkflowLoadException {
		IridaWorkflowDescription iridaWorkflowDescription = buildTestDescriptionNotRequiresSingleSample();
		IridaWorkflowDescription iridaWorkflowFromFile = workflowLoaderService.loadWorkflowDescription(workflowNotRequiresSingleSampleXmlPath);

		assertEquals("irida workflow description is invalid", iridaWorkflowFromFile, iridaWorkflowDescription);
	}
	
	/**
	 * Tests loading up the workflow description file that requires a single sample.
	 * 
	 * @throws IOException
	 * @throws IridaWorkflowLoadException
	 */
	@Test
	public void testLoadWorkflowDescriptionRequiresSingleSample() throws IOException, IridaWorkflowLoadException {
		IridaWorkflowDescription iridaWorkflowDescription = buildTestDescriptionRequiresSingleSample();
		IridaWorkflowDescription iridaWorkflowFromFile = workflowLoaderService.loadWorkflowDescription(workflowRequiresSingleSampleXmlPath);

		assertEquals("irida workflow description is invalid", iridaWorkflowFromFile, iridaWorkflowDescription);
	}
	
	/**
	 * Tests loading up the workflow description file that has no requires single sample parameter set.
	 * 
	 * @throws IOException
	 * @throws IridaWorkflowLoadException
	 */
	@Test
	public void testLoadWorkflowDescriptionRequiresSingleSampleUnset() throws IOException, IridaWorkflowLoadException {
		IridaWorkflowDescription iridaWorkflowDescription = buildTestDescriptionRequiresSingleSampleUnset();
		IridaWorkflowDescription iridaWorkflowFromFile = workflowLoaderService.loadWorkflowDescription(workflowRequiresSingleSampleUnsetXmlPath);

		assertEquals("irida workflow description is invalid", iridaWorkflowFromFile, iridaWorkflowDescription);
	}
	
	/**
	 * Tests loading up the workflow description file with an invalid string for requires single sample and setting to default.
	 * 
	 * @throws IOException
	 * @throws IridaWorkflowLoadException
	 */
	@Test
	public void testLoadWorkflowDescriptionRequiresSingleSampleInvalid() throws IOException, IridaWorkflowLoadException {
		IridaWorkflowDescription iridaWorkflowDescription = buildTestDescriptionRequiresSingleSampleInvalid();
		IridaWorkflowDescription iridaWorkflowFromFile = workflowLoaderService.loadWorkflowDescription(workflowInvalidRequiresSingleSampleXmlPath);

		assertEquals("irida workflow description is invalid", iridaWorkflowFromFile, iridaWorkflowDescription);
	}

	/**
	 * Tests loading up a workflow from a file.
	 * 
	 * @throws IOException
	 * @throws IridaWorkflowLoadException
	 */
	@Test
	public void testLoadWorkflow() throws IOException, IridaWorkflowLoadException {
		IridaWorkflow iridaWorkflow = buildTestWorkflowSingle();
		IridaWorkflow iridaWorkflowFromFile = workflowLoaderService.loadIridaWorkflow(workflowSingleXmlPath,
				workflowStructurePath);

		assertEquals("irida workflow is invalid", iridaWorkflowFromFile, iridaWorkflow);
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

		assertEquals("irida workflow structure invalid", iridaWorkflowStructure, iridaWorkflowStructureFromFile);
	}

	/**
	 * Tests successfully loading up one version of a workflow from a directory.
	 */
	@Test
	public void testLoadWorkflowFromDirectory() throws IOException, IridaWorkflowLoadException {
		IridaWorkflow iridaWorkflowFromFile = workflowLoaderService
				.loadIridaWorkflowFromDirectory(workflowVersionDirectoryPath);

		assertEquals("irida workflow is invalid", buildTestWorkflowSingle(), iridaWorkflowFromFile);
	}

	/**
	 * Tests successfully loading up all implementations of a workflow from a
	 * directory.
	 */
	@Test
	public void testLoadAllWorkflowImplementationsSuccess() throws IOException, IridaWorkflowLoadException {
		Set<IridaWorkflow> iridaWorkflowsFromFile = workflowLoaderService
				.loadAllWorkflowImplementations(workflowDirectoryPath);
		
		Set<String> workflowNamesSet = new HashSet<>();
		Set<String> workflowVersionsSet = new HashSet<>();

		assertEquals(11, iridaWorkflowsFromFile.size());
		
		for (IridaWorkflow workflow : iridaWorkflowsFromFile) {
			workflowNamesSet.add(workflow.getWorkflowDescription().getName());
			workflowVersionsSet.add(workflow.getWorkflowDescription().getVersion());
		}

		assertEquals("irida workflow names are invalid", Sets.newHashSet("TestWorkflow"), workflowNamesSet);
		Set<String> validVersionNumbers = Sets.newHashSet("1.0", "2.0", "1.0-invalid", "2.0-missing-output",
				"1.0-paired", "1.0-single-paired", "1.0-requires-single-sample", "1.0-requires-single-sample-unset",
				"1.0-not-requires-single-sample", "1.0-invalid-requires-single-sample", "1.0-paired-end-single-sample");
		assertEquals("irida workflow versions are invalid", validVersionNumbers, workflowVersionsSet);
	}

	/**
	 * Test to make sure we can load a workflow with no parameters.
	 * 
	 * @throws IridaWorkflowLoadException
	 * @throws IOException
	 */
	@Test
	public void testLoadWorkflowNoParameters() throws IridaWorkflowLoadException, IOException {
		IridaWorkflow iridaWorkflowFromFile = workflowLoaderService
				.loadIridaWorkflowFromDirectory(workflowDirectoryPathNoParameters);
		assertFalse("workflow loaded with no parameters", iridaWorkflowFromFile.getWorkflowDescription()
				.acceptsParameters());
		assertNull("parameters should be null", iridaWorkflowFromFile.getWorkflowDescription().getParameters());
	}

	/**
	 * Test to make sure we can load a workflow with parameters.
	 * 
	 * @throws IridaWorkflowLoadException
	 * @throws IOException
	 */
	@Test
	public void testLoadWorkflowWithParameters() throws IridaWorkflowLoadException, IOException {
		IridaWorkflow iridaWorkflowFromFile = workflowLoaderService
				.loadIridaWorkflowFromDirectory(workflowDirectoryPathWithParameters);
		assertTrue("workflow loaded with no parameters", iridaWorkflowFromFile.getWorkflowDescription()
				.acceptsParameters());
		List<IridaWorkflowParameter> parameters = iridaWorkflowFromFile.getWorkflowDescription().getParameters();
		assertNotNull("parameters should not be null", parameters);
		assertEquals("parameters does not have the correct size", 1, parameters.size());
		IridaWorkflowParameter parameter = parameters.get(0);
		assertEquals("parameter does not have the correct name", "test-parameter", parameter.getName());
		assertEquals("default value is not correct", "1", parameter.getDefaultValue());
		assertEquals("parameter does not have correct number of tool parameters", 1, parameter.getToolParameters()
				.size());
	}

	/**
	 * Test to make sure we fail to load a workflow with no default value and without a required="true" attribute.
	 * 
	 * @throws IridaWorkflowLoadException
	 * @throws IOException
	 */
	@Test(expected=IridaWorkflowLoadException.class)
	public void testLoadWorkflowWithParametersNoDefaultValueNotRequiredFail() throws IridaWorkflowLoadException, IOException {
		workflowLoaderService
				.loadIridaWorkflowFromDirectory(workflowDirectoryPathWithParametersNoDefaultNotRequired);
	}

	/**
	 * Test to make sure we fail to load a workflow with no default value and without a required="true" attribute.
	 *
	 * @throws IridaWorkflowLoadException
	 * @throws IOException
	 */
	@Test
	public void testLoadWorkflowWithParametersNoDefaultValueIsRequiredSuccess() throws IridaWorkflowLoadException, IOException {
		IridaWorkflow iridaWorkflow = workflowLoaderService
				.loadIridaWorkflowFromDirectory(workflowDirectoryPathWithParametersNoDefaultIsRequired);
		IridaWorkflowParameter parameter = iridaWorkflow.getWorkflowDescription().getParameters().get(0);
		assertNull("defaultValue should be null if none provided", parameter.getDefaultValue());
		assertTrue("parameter should be required", parameter.isRequired());
	}

	/**
	 * Test to make sure we fail to load a workflow with a default value and a required="true" attribute.
	 *
	 * @throws IridaWorkflowLoadException
	 * @throws IOException
	 */
	@Test(expected=IridaWorkflowLoadException.class)
	public void testLoadWorkflowWithParametersWithDefaultValueIsRequiredFail() throws IridaWorkflowLoadException, IOException {
		workflowLoaderService
				.loadIridaWorkflowFromDirectory(workflowDirectoryPathWithParametersWithDefaultIsRequired);
	}

	/**
	 * Test to make sure we fail to load a workflow with a <dynamicSource> child element and without a required="true" attribute.
	 *
	 * @throws IridaWorkflowLoadException
	 * @throws IOException
	 */
	@Test(expected=IridaWorkflowLoadException.class)
	public void testLoadWorkflowWithParametersWithDynamicSourceNotRequiredFail() throws IridaWorkflowLoadException, IOException {
		workflowLoaderService
				.loadIridaWorkflowFromDirectory(workflowDirectoryPathWithParametersWithDynamicSourceNotRequired);
	}

	/**
	 * Test to make sure we fail to load a workflow with multiple <dynamicSource> child elements.
	 *
	 * @throws IridaWorkflowLoadException
	 * @throws IOException
	 */
	@Test(expected=IridaWorkflowLoadException.class)
	public void testLoadWorkflowWithParametersMultipleDynamicSourcesFail() throws IridaWorkflowLoadException, IOException {
		workflowLoaderService
				.loadIridaWorkflowFromDirectory(workflowDirectoryPathWithParametersMultipleDynamicSources);
	}

	/**
	 * Tests failure to load up all implementations of a workflow from a
	 * directory.
	 */
	@Test(expected=FileNotFoundException.class)
	public void testLoadAllWorkflowImplementationsFail() throws IOException, IridaWorkflowLoadException {
		workflowLoaderService.loadAllWorkflowImplementations(workflowDirectoryPathNoDefinition);
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
	
	/**
	 * Tests failing to load up a workflow with an invalid type.
	 * 
	 * @throws IridaWorkflowLoadException
	 */
	@Test(expected = IridaWorkflowLoadException.class)
	public void testLoadIridaWorkflowFromDirectoryFailInvalidType() throws IOException, IridaWorkflowLoadException {
		workflowLoaderService.loadIridaWorkflowFromDirectory(workflowDirectoryPathInvalidType);
	}
}
