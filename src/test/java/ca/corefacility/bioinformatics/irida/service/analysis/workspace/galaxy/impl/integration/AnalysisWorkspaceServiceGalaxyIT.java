package ca.corefacility.bioinformatics.irida.service.analysis.workspace.galaxy.impl.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithSecurityContextTestExecutionListener;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import com.github.jmchilton.blend4j.galaxy.GalaxyInstance;
import com.github.jmchilton.blend4j.galaxy.HistoriesClient;
import com.github.jmchilton.blend4j.galaxy.LibrariesClient;
import com.github.jmchilton.blend4j.galaxy.ToolsClient;
import com.github.jmchilton.blend4j.galaxy.WorkflowsClient;
import com.github.jmchilton.blend4j.galaxy.beans.History;
import com.github.jmchilton.blend4j.galaxy.beans.HistoryContents;
import com.github.jmchilton.blend4j.galaxy.beans.LibraryContent;
import com.github.jmchilton.blend4j.galaxy.beans.Workflow;
import com.github.jmchilton.blend4j.galaxy.beans.WorkflowInvocationInputs;
import com.github.jmchilton.blend4j.galaxy.beans.WorkflowInvocationInputs.WorkflowInvocationInput;
import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;

import ca.corefacility.bioinformatics.irida.annotation.GalaxyIntegrationTest;
import ca.corefacility.bioinformatics.irida.config.conditions.WindowsPlatformCondition;
import ca.corefacility.bioinformatics.irida.exceptions.DuplicateSampleException;
import ca.corefacility.bioinformatics.irida.exceptions.ExecutionManagerException;
import ca.corefacility.bioinformatics.irida.exceptions.IridaWorkflowAnalysisTypeException;
import ca.corefacility.bioinformatics.irida.exceptions.IridaWorkflowException;
import ca.corefacility.bioinformatics.irida.exceptions.IridaWorkflowLoadException;
import ca.corefacility.bioinformatics.irida.exceptions.IridaWorkflowNotFoundException;
import ca.corefacility.bioinformatics.irida.exceptions.IridaWorkflowParameterException;
import ca.corefacility.bioinformatics.irida.exceptions.SampleAnalysisDuplicateException;
import ca.corefacility.bioinformatics.irida.exceptions.WorkflowException;
import ca.corefacility.bioinformatics.irida.exceptions.galaxy.GalaxyDatasetNotFoundException;
import ca.corefacility.bioinformatics.irida.model.enums.AnalysisState;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFilePair;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequencingObject;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SingleEndSequenceFile;
import ca.corefacility.bioinformatics.irida.model.workflow.IridaWorkflow;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.Analysis;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.type.BuiltInAnalysisTypes;
import ca.corefacility.bioinformatics.irida.model.workflow.description.IridaWorkflowParameter;
import ca.corefacility.bioinformatics.irida.model.workflow.execution.galaxy.PreparedWorkflowGalaxy;
import ca.corefacility.bioinformatics.irida.model.workflow.execution.galaxy.WorkflowInputsGalaxy;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyHistoriesService;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyLibrariesService;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.integration.LocalGalaxy;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.integration.Util;
import ca.corefacility.bioinformatics.irida.repositories.analysis.submission.AnalysisSubmissionRepository;
import ca.corefacility.bioinformatics.irida.repositories.sample.SampleRepository;
import ca.corefacility.bioinformatics.irida.service.DatabaseSetupGalaxyITService;
import ca.corefacility.bioinformatics.irida.service.SequencingObjectService;
import ca.corefacility.bioinformatics.irida.service.analysis.workspace.galaxy.AnalysisWorkspaceServiceGalaxy;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;
import ca.corefacility.bioinformatics.irida.service.workflow.IridaWorkflowsService;
import org.springframework.transaction.annotation.Transactional;

/**
 * Tests out preparing a workspace for execution of workflows in Galaxy.
 *
 *
 */
@GalaxyIntegrationTest
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DbUnitTestExecutionListener.class,
		WithSecurityContextTestExecutionListener.class })
@DatabaseSetup("/ca/corefacility/bioinformatics/irida/repositories/analysis/AnalysisRepositoryIT.xml")
@DatabaseTearDown("/ca/corefacility/bioinformatics/irida/test/integration/TableReset.xml")
public class AnalysisWorkspaceServiceGalaxyIT {

	@Autowired
	private DatabaseSetupGalaxyITService analysisExecutionGalaxyITService;

	@Autowired
	private LocalGalaxy localGalaxy;

	@Autowired
	private AnalysisWorkspaceServiceGalaxy analysisWorkspaceService;

	@Autowired
	private IridaWorkflowsService iridaWorkflowsService;

	@Autowired
	private AnalysisSubmissionRepository analysisSubmissionRepository;

	@Autowired
	private SampleService sampleService;

	@Autowired
	private SampleRepository sampleRepository;

	@Autowired
	private SequencingObjectService sequencingObjectService;

	@Autowired
	@Qualifier("rootTempDirectory")
	private Path rootTempDirectory;

	private GalaxyHistoriesService galaxyHistoriesService;

	/**
	 * Timeout in seconds to stop polling a Galaxy library.
	 */
	private static final int LIBRARY_TIMEOUT = 5 * 60;

	/**
	 * Polling time in seconds to poll a Galaxy library to check if datasets
	 * have been properly uploaded.
	 */
	private static final int LIBRARY_POLLING_TIME = 5;

	private Path sequenceFilePathA;
	private Path sequenceFilePath2A;
	private Path sequenceFilePathB;
	private Path sequenceFilePath2B;
	private Path sequenceFilePath3;
	private Path referenceFilePath;

	private List<Path> pairSequenceFiles1A;
	private List<Path> pairSequenceFiles2A;

	private List<Path> pairSequenceFiles1AB;
	private List<Path> pairSequenceFiles2AB;

	private Set<SequencingObject> singleFileSet;

	private static final UUID validWorkflowIdSingle = UUID.fromString("739f29ea-ae82-48b9-8914-3d2931405db6");
	private static final UUID validWorkflowIdSingleSingleSample = UUID
			.fromString("a9692a52-5bc6-4da2-a89d-d880bb35bfe4");
	private static final UUID validWorkflowIdPaired = UUID.fromString("ec93b50d-c9dd-4000-98fc-4a70d46ddd36");
	private static final UUID validWorkflowIdPairedSingleSample = UUID
			.fromString("fc93b50d-c9dd-4000-98fc-4a70d46ddd36");
	private static final UUID validWorkflowIdPairedWithParameters = UUID
			.fromString("23434bf8-e551-4efd-9957-e61c6f649f8b");
	private static final UUID validWorkflowIdSinglePaired = UUID.fromString("d92e9918-1e3d-4dea-b2b9-089f1256ac1b");
	private static final UUID phylogenomicsWorkflowId = UUID.fromString("1f9ea289-5053-4e4a-bc76-1f0c60b179f8");

	private static final String OUTPUT1_KEY = "output1";
	private static final String OUTPUT2_KEY = "output2";
	private static final String OUTPUT1_NAME = "output1.txt";
	private static final String OUTPUT2_NAME = "output2.txt";

	private static final String MATRIX_NAME = "snpMatrix.tsv";
	private static final String MATRIX_KEY = "matrix";
	private static final String TREE_NAME = "phylogeneticTree.txt";
	private static final String TREE_KEY = "tree";
	private static final String TABLE_NAME = "snpTable.tsv";
	private static final String TABLE_KEY = "table";

	private static final String INPUTS_SINGLE_NAME = "irida_sequence_files_single";
	private static final String INPUTS_PAIRED_NAME = "irida_sequence_files_paired";

	private static final String SAMPLE1_NAME = "sample1";

	/**
	 * Sets up variables for testing.
	 *
	 * @throws URISyntaxException
	 * @throws IOException
	 * @throws IridaWorkflowLoadException
	 */
	@BeforeEach
	public void setup() throws URISyntaxException, IOException, IridaWorkflowLoadException {
		assumeFalse(WindowsPlatformCondition.isWindows());

		Path sequenceFilePathReal = Paths
				.get(DatabaseSetupGalaxyITService.class.getResource("testData1.fastq").toURI());
		Path referenceFilePathReal = Paths
				.get(DatabaseSetupGalaxyITService.class.getResource("testReference.fasta").toURI());

		Path tempDir = Files.createTempDirectory(rootTempDirectory, "workspaceServiceGalaxyTest");

		sequenceFilePathA = tempDir.resolve("testDataA_R1_001.fastq");
		Files.copy(sequenceFilePathReal, sequenceFilePathA, StandardCopyOption.REPLACE_EXISTING);

		sequenceFilePath2A = tempDir.resolve("testDataA_R2_001.fastq");
		Files.copy(sequenceFilePathReal, sequenceFilePath2A, StandardCopyOption.REPLACE_EXISTING);

		sequenceFilePathB = tempDir.resolve("testDataB_R1_001.fastq");
		Files.copy(sequenceFilePathReal, sequenceFilePathB, StandardCopyOption.REPLACE_EXISTING);

		sequenceFilePath2B = tempDir.resolve("testDataB_R2_001.fastq");
		Files.copy(sequenceFilePathReal, sequenceFilePath2B, StandardCopyOption.REPLACE_EXISTING);

		sequenceFilePath3 = tempDir.resolve("testData3_R1_001.fastq");
		Files.copy(sequenceFilePathReal, sequenceFilePath3, StandardCopyOption.REPLACE_EXISTING);

		referenceFilePath = Files.createTempFile("testReference", ".fasta");
		Files.delete(referenceFilePath);
		Files.copy(referenceFilePathReal, referenceFilePath);

		singleFileSet = Sets.newHashSet(new SingleEndSequenceFile(new SequenceFile(sequenceFilePathA)));

		GalaxyInstance galaxyInstanceAdmin = localGalaxy.getGalaxyInstanceAdmin();
		HistoriesClient historiesClient = galaxyInstanceAdmin.getHistoriesClient();
		ToolsClient toolsClient = galaxyInstanceAdmin.getToolsClient();
		LibrariesClient librariesClient = galaxyInstanceAdmin.getLibrariesClient();
		GalaxyLibrariesService galaxyLibrariesService = new GalaxyLibrariesService(librariesClient,
				LIBRARY_POLLING_TIME, LIBRARY_TIMEOUT, 1);

		galaxyHistoriesService = new GalaxyHistoriesService(historiesClient, toolsClient, galaxyLibrariesService);

		pairSequenceFiles1A = new ArrayList<>();
		pairSequenceFiles1A.add(sequenceFilePathA);
		pairSequenceFiles2A = new ArrayList<>();
		pairSequenceFiles2A.add(sequenceFilePath2A);

		pairSequenceFiles1AB = new ArrayList<>();
		pairSequenceFiles1AB.add(sequenceFilePathA);
		pairSequenceFiles1AB.add(sequenceFilePathB);
		pairSequenceFiles2AB = new ArrayList<>();
		pairSequenceFiles2AB.add(sequenceFilePath2A);
		pairSequenceFiles2AB.add(sequenceFilePath2B);
	}

	/**
	 * Tests successfully preparing a workspace for analysis.
	 *
	 * @throws IridaWorkflowNotFoundException
	 * @throws ExecutionManagerException
	 */
	@Test
	public void testPrepareAnalysisWorkspaceSuccess() throws IridaWorkflowNotFoundException, ExecutionManagerException {
		AnalysisSubmission submission = AnalysisSubmission.builder(validWorkflowIdSingle).name("Name")
				.inputFiles(singleFileSet).build();
		assertNotNull(analysisWorkspaceService.prepareAnalysisWorkspace(submission),
				"preparing an analysis workspace should not return null");
	}

	/**
	 * Tests failure to prepare a workspace for analysis.
	 *
	 * @throws IridaWorkflowNotFoundException
	 * @throws ExecutionManagerException
	 */
	@Test
	public void testPrepareAnalysisWorkspaceFail() throws IridaWorkflowNotFoundException, ExecutionManagerException {
		AnalysisSubmission submission = AnalysisSubmission.builder(validWorkflowIdSingle).name("Name")
				.inputFiles(singleFileSet).build();
		submission.setRemoteAnalysisId("1");
		assertThrows(IllegalArgumentException.class, () -> {
			analysisWorkspaceService.prepareAnalysisWorkspace(submission);
		});
	}

	/**
	 * Tests out successfully preparing single workflow input files for
	 * execution.
	 *
	 * @throws InterruptedException
	 * @throws ExecutionManagerException
	 * @throws IOException
	 * @throws IridaWorkflowException
	 */
	@Test
	@WithMockUser(username = "aaron", roles = "ADMIN")
	public void testPrepareAnalysisFilesSingleSuccess()
			throws InterruptedException, ExecutionManagerException, IOException, IridaWorkflowException {

		History history = new History();
		history.setName("testPrepareAnalysisFilesSingleSuccess");
		HistoriesClient historiesClient = localGalaxy.getGalaxyInstanceAdmin().getHistoriesClient();
		WorkflowsClient workflowsClient = localGalaxy.getGalaxyInstanceAdmin().getWorkflowsClient();
		LibrariesClient librariesClient = localGalaxy.getGalaxyInstanceAdmin().getLibrariesClient();
		History createdHistory = historiesClient.create(history);

		IridaWorkflow iridaWorkflow = iridaWorkflowsService.getIridaWorkflow(validWorkflowIdSingle);
		Path workflowPath = iridaWorkflow.getWorkflowStructure().getWorkflowFile();
		String workflowString = new String(Files.readAllBytes(workflowPath), StandardCharsets.UTF_8);
		Workflow galaxyWorkflow = workflowsClient.importWorkflow(workflowString, false);

		AnalysisSubmission analysisSubmission = analysisExecutionGalaxyITService.setupSubmissionInDatabase(1L,
				sequenceFilePathA, referenceFilePath, validWorkflowIdSingle, false);
		analysisSubmission.setRemoteAnalysisId(createdHistory.getId());
		analysisSubmission.setRemoteWorkflowId(galaxyWorkflow.getId());

		PreparedWorkflowGalaxy preparedWorkflow = analysisWorkspaceService.prepareAnalysisFiles(analysisSubmission);
		assertEquals(createdHistory.getId(), preparedWorkflow.getRemoteAnalysisId(),
				"the response history id should match the input history id");
		assertNotNull(preparedWorkflow.getWorkflowInputs(), "the returned workflow inputs should not be null");
		assertNotNull(preparedWorkflow.getRemoteDataId(), "the returned library id should not be null");

		// verify correct library is created
		List<LibraryContent> libraryContents = librariesClient.getLibraryContents(preparedWorkflow.getRemoteDataId());
		Map<String, List<LibraryContent>> libraryContentsMap = libraryContents.stream()
				.collect(Collectors.groupingBy(LibraryContent::getName));

		assertFalse(libraryContentsMap.isEmpty(), "the returned library should exist in Galaxy");
		String sequenceFileALibraryName = "/" + sequenceFilePathA.getFileName().toString();
		assertEquals(2, libraryContentsMap.size(),
				"the returned library does not contain the correct number of elements");
		assertTrue(libraryContentsMap.containsKey("/"), "the returned library does not contain a root folder");
		assertTrue(libraryContentsMap.containsKey(sequenceFileALibraryName),
				"the returned library does not contain the correct sequence file");
		assertEquals(1, libraryContentsMap.get(sequenceFileALibraryName).size(),
				"the returned library does not contain the correct sequence file");

		// verify correct files have been uploaded
		List<HistoryContents> historyContents = historiesClient.showHistoryContents(createdHistory.getId());
		assertEquals(3, historyContents.size(), "the created history should contain 3 entries");
		Map<String, HistoryContents> contentsMap = historyContentsAsMap(historyContents);
		assertTrue(contentsMap.containsKey(sequenceFilePathA.toFile().getName()),
				"the created history should contain the file " + sequenceFilePathA.toFile().getName());
		assertTrue(contentsMap.containsKey(referenceFilePath.toFile().getName()),
				"the created history should contain the file " + referenceFilePath.toFile().getName());
		assertTrue(contentsMap.containsKey(INPUTS_SINGLE_NAME),
				"the created history should contain the collection with name " + INPUTS_SINGLE_NAME);

		// make sure workflow inputs contains correct information
		Map<String, WorkflowInvocationInput> workflowInputsMap = preparedWorkflow.getWorkflowInputs().getInputsObject()
				.getInputs();
		assertEquals(2, workflowInputsMap.size(), "the created workflow inputs has an invalid number of elements");
	}

	private Map<String, HistoryContents> historyContentsAsMap(List<HistoryContents> historyContents) {
		return historyContents.stream()
				.collect(Collectors.toMap(HistoryContents::getName, historyContent -> historyContent));
	}

	/**
	 * Tests out failing to prepare single workflow input files for execution
	 * (duplicate samples).
	 *
	 * @throws InterruptedException
	 * @throws ExecutionManagerException
	 * @throws IOException
	 * @throws IridaWorkflowException
	 */
	@Test
	@WithMockUser(username = "aaron", roles = "ADMIN")
	public void testPrepareAnalysisFilesSingleFail()
			throws InterruptedException, ExecutionManagerException, IOException, IridaWorkflowException {

		History history = new History();
		history.setName("testPrepareAnalysisFilesSingleFail");
		HistoriesClient historiesClient = localGalaxy.getGalaxyInstanceAdmin().getHistoriesClient();
		WorkflowsClient workflowsClient = localGalaxy.getGalaxyInstanceAdmin().getWorkflowsClient();
		History createdHistory = historiesClient.create(history);

		IridaWorkflow iridaWorkflow = iridaWorkflowsService.getIridaWorkflow(validWorkflowIdSingle);
		Path workflowPath = iridaWorkflow.getWorkflowStructure().getWorkflowFile();
		String workflowString = new String(Files.readAllBytes(workflowPath), StandardCharsets.UTF_8);
		Workflow galaxyWorkflow = workflowsClient.importWorkflow(workflowString, false);

		List<SingleEndSequenceFile> sequenceFiles = analysisExecutionGalaxyITService.setupSequencingObjectInDatabase(1L,
				sequenceFilePathA, sequenceFilePath2A);

		AnalysisSubmission analysisSubmission = analysisExecutionGalaxyITService.setupSubmissionInDatabase(1L,
				Sets.newHashSet(sequenceFiles), referenceFilePath, validWorkflowIdSingle);
		analysisSubmission.setRemoteAnalysisId(createdHistory.getId());
		analysisSubmission.setRemoteWorkflowId(galaxyWorkflow.getId());

		assertThrows(DuplicateSampleException.class, () -> {
			analysisWorkspaceService.prepareAnalysisFiles(analysisSubmission);
		});
	}

	/**
	 * Tests out successfully preparing paired workflow input files for
	 * execution.
	 *
	 * @throws InterruptedException
	 * @throws ExecutionManagerException
	 * @throws IOException
	 * @throws IridaWorkflowException
	 */
	@Test
	@WithMockUser(username = "aaron", roles = "ADMIN")
	public void testPrepareAnalysisFilesPairSuccess()
			throws InterruptedException, ExecutionManagerException, IOException, IridaWorkflowException {

		History history = new History();
		history.setName("testPrepareAnalysisFilesPairSuccess");
		HistoriesClient historiesClient = localGalaxy.getGalaxyInstanceAdmin().getHistoriesClient();
		WorkflowsClient workflowsClient = localGalaxy.getGalaxyInstanceAdmin().getWorkflowsClient();
		LibrariesClient librariesClient = localGalaxy.getGalaxyInstanceAdmin().getLibrariesClient();
		History createdHistory = historiesClient.create(history);

		IridaWorkflow iridaWorkflow = iridaWorkflowsService.getIridaWorkflow(validWorkflowIdPaired);
		Path workflowPath = iridaWorkflow.getWorkflowStructure().getWorkflowFile();
		String workflowString = new String(Files.readAllBytes(workflowPath), StandardCharsets.UTF_8);
		Workflow galaxyWorkflow = workflowsClient.importWorkflow(workflowString, false);

		AnalysisSubmission analysisSubmission = analysisExecutionGalaxyITService.setupPairSubmissionInDatabase(1L,
				pairSequenceFiles1A, pairSequenceFiles2A, referenceFilePath, validWorkflowIdPaired, false);
		analysisSubmission.setRemoteAnalysisId(createdHistory.getId());
		analysisSubmission.setRemoteWorkflowId(galaxyWorkflow.getId());

		PreparedWorkflowGalaxy preparedWorkflow = analysisWorkspaceService.prepareAnalysisFiles(analysisSubmission);
		assertEquals(createdHistory.getId(), preparedWorkflow.getRemoteAnalysisId(),
				"the response history id should match the input history id");
		WorkflowInputsGalaxy workflowInputsGalaxy = preparedWorkflow.getWorkflowInputs();
		assertNotNull(workflowInputsGalaxy, "the returned workflow inputs should not be null");
		assertNotNull(preparedWorkflow.getRemoteDataId(), "the returned library id should not be null");

		// verify correct library is created
		List<LibraryContent> libraryContents = librariesClient.getLibraryContents(preparedWorkflow.getRemoteDataId());
		Map<String, List<LibraryContent>> libraryContentsMap = libraryContents.stream()
				.collect(Collectors.groupingBy(LibraryContent::getName));

		assertFalse(libraryContentsMap.isEmpty(), "the returned library should exist in Galaxy");
		String sequenceFile1ALibraryName = "/" + sequenceFilePathA.getFileName().toString();
		String sequenceFile2ALibraryName = "/" + sequenceFilePath2A.getFileName().toString();
		assertEquals(3, libraryContentsMap.size(),
				"the returned library does not contain the correct number of elements");
		assertTrue(libraryContentsMap.containsKey("/"), "the returned library does not contain a root folder");
		assertTrue(libraryContentsMap.containsKey(sequenceFile1ALibraryName),
				"the returned library does not contain the correct sequence file");
		assertEquals(1, libraryContentsMap.get(sequenceFile1ALibraryName).size(),
				"the returned library does not contain the correct sequence file");
		assertTrue(libraryContentsMap.containsKey(sequenceFile2ALibraryName),
				"the returned library does not contain the correct sequence file");
		assertEquals(1, libraryContentsMap.get(sequenceFile2ALibraryName).size(),
				"the returned library does not contain the correct sequence file");

		// verify correct files have been uploaded
		List<HistoryContents> historyContents = historiesClient.showHistoryContents(createdHistory.getId());
		assertEquals(4, historyContents.size(), "the created history has an invalid number of elements");
		Map<String, HistoryContents> contentsMap = historyContentsAsMap(historyContents);
		assertTrue(contentsMap.containsKey(sequenceFilePathA.toFile().getName()),
				"the created history should contain the file " + sequenceFilePathA.toFile().getName());
		assertTrue(contentsMap.containsKey(sequenceFilePath2A.toFile().getName()),
				"the created history should contain the file " + sequenceFilePath2A.toFile().getName());
		assertTrue(contentsMap.containsKey(referenceFilePath.toFile().getName()),
				"the created history should contain the file " + referenceFilePath.toFile().getName());
		assertTrue(contentsMap.containsKey(INPUTS_PAIRED_NAME),
				"the created history should contain the collection with name " + INPUTS_PAIRED_NAME);

		// make sure workflow inputs contains correct information
		Map<String, WorkflowInvocationInput> workflowInputsMap = preparedWorkflow.getWorkflowInputs().getInputsObject()
				.getInputs();
		assertEquals(2, workflowInputsMap.size(), "the created workflow inputs has an invalid number of elements");
	}

	/**
	 * Tests out successfully preparing paired workflow input files for
	 * execution with parameters.
	 *
	 * @throws InterruptedException
	 * @throws ExecutionManagerException
	 * @throws IOException
	 * @throws IridaWorkflowException
	 */
	@Test
	@WithMockUser(username = "aaron", roles = "ADMIN")
	public void testPrepareAnalysisFilesParametersSuccess()
			throws InterruptedException, ExecutionManagerException, IOException, IridaWorkflowException {

		History history = new History();
		history.setName("testPrepareAnalysisFilesParametersSuccess");
		HistoriesClient historiesClient = localGalaxy.getGalaxyInstanceAdmin().getHistoriesClient();
		WorkflowsClient workflowsClient = localGalaxy.getGalaxyInstanceAdmin().getWorkflowsClient();
		History createdHistory = historiesClient.create(history);

		IridaWorkflow iridaWorkflow = iridaWorkflowsService.getIridaWorkflow(validWorkflowIdPairedWithParameters);
		Path workflowPath = iridaWorkflow.getWorkflowStructure().getWorkflowFile();
		String workflowString = new String(Files.readAllBytes(workflowPath), StandardCharsets.UTF_8);
		Workflow galaxyWorkflow = workflowsClient.importWorkflow(workflowString, false);

		Map<String, String> parameters = ImmutableMap.of("coverage", "20");

		AnalysisSubmission analysisSubmission = analysisExecutionGalaxyITService.setupPairSubmissionInDatabase(1L,
				pairSequenceFiles1A, pairSequenceFiles2A, referenceFilePath, parameters,
				validWorkflowIdPairedWithParameters);
		analysisSubmission.setRemoteAnalysisId(createdHistory.getId());
		analysisSubmission.setRemoteWorkflowId(galaxyWorkflow.getId());

		PreparedWorkflowGalaxy preparedWorkflow = analysisWorkspaceService.prepareAnalysisFiles(analysisSubmission);
		assertEquals(createdHistory.getId(), preparedWorkflow.getRemoteAnalysisId(),
				"the response history id should match the input history id");
		WorkflowInputsGalaxy workflowInputsGalaxy = preparedWorkflow.getWorkflowInputs();
		assertNotNull(workflowInputsGalaxy, "the returned workflow inputs should not be null");
		assertNotNull(preparedWorkflow.getRemoteDataId(), "the returned library id should not be null");

		// verify correct files have been uploaded
		List<HistoryContents> historyContents = historiesClient.showHistoryContents(createdHistory.getId());
		assertEquals(4, historyContents.size(), "the created history has an invalid number of elements");

		WorkflowInvocationInputs workflowInvocationInputs = preparedWorkflow.getWorkflowInputs().getInputsObject();
		assertNotNull(workflowInvocationInputs, "created workflowInvocationInputs is null");

		Map<String, Object> toolParameters = workflowInvocationInputs.getParameters()
				.get("core_pipeline_outputs_paired_with_parameters");
		assertNotNull(toolParameters, "toolParameters is null");

		String coverageMinValue = (String) toolParameters.get("coverageMin");
		assertEquals("20", coverageMinValue, "coverageMinValue should have been changed");
		assertEquals(ImmutableMap.of("coverageMid", "20"), toolParameters.get("conditional"),
				"coverageMidValue should have been changed");
		String coverageMaxValue = (String) toolParameters.get("coverageMin");
		assertEquals("20", coverageMaxValue, "coverageMaxValue should have been changed");
	}

	/**
	 * Tests out successfully preparing paired workflow input files for
	 * execution, no parameters set.
	 *
	 * @throws InterruptedException
	 * @throws ExecutionManagerException
	 * @throws IOException
	 * @throws IridaWorkflowException
	 */
	@Test
	@WithMockUser(username = "aaron", roles = "ADMIN")
	public void testPrepareAnalysisFilesParametersSuccessWithNoParameters()
			throws InterruptedException, ExecutionManagerException, IOException, IridaWorkflowException {

		History history = new History();
		history.setName("testPrepareAnalysisFilesParametersSuccessWithNoParameters");
		HistoriesClient historiesClient = localGalaxy.getGalaxyInstanceAdmin().getHistoriesClient();
		WorkflowsClient workflowsClient = localGalaxy.getGalaxyInstanceAdmin().getWorkflowsClient();
		History createdHistory = historiesClient.create(history);

		IridaWorkflow iridaWorkflow = iridaWorkflowsService.getIridaWorkflow(validWorkflowIdPairedWithParameters);
		Path workflowPath = iridaWorkflow.getWorkflowStructure().getWorkflowFile();
		String workflowString = new String(Files.readAllBytes(workflowPath), StandardCharsets.UTF_8);
		Workflow galaxyWorkflow = workflowsClient.importWorkflow(workflowString, false);

		AnalysisSubmission analysisSubmission = analysisExecutionGalaxyITService.setupPairSubmissionInDatabase(1L,
				pairSequenceFiles1A, pairSequenceFiles2A, referenceFilePath, validWorkflowIdPairedWithParameters,
				false);
		analysisSubmission.setRemoteAnalysisId(createdHistory.getId());
		analysisSubmission.setRemoteWorkflowId(galaxyWorkflow.getId());

		PreparedWorkflowGalaxy preparedWorkflow = analysisWorkspaceService.prepareAnalysisFiles(analysisSubmission);
		assertEquals(createdHistory.getId(), preparedWorkflow.getRemoteAnalysisId(),
				"the response history id should match the input history id");
		WorkflowInputsGalaxy workflowInputsGalaxy = preparedWorkflow.getWorkflowInputs();
		assertNotNull(workflowInputsGalaxy, "the returned workflow inputs should not be null");
		assertNotNull(preparedWorkflow.getRemoteDataId(), "the returned library id should not be null");

		// verify correct files have been uploaded
		List<HistoryContents> historyContents = historiesClient.showHistoryContents(createdHistory.getId());
		assertEquals(4, historyContents.size(), "the created history has an invalid number of elements");

		WorkflowInvocationInputs workflowInvocationInputs = preparedWorkflow.getWorkflowInputs().getInputsObject();
		assertNotNull(workflowInvocationInputs, "created workflowInvocationInputs is null");

		Map<String, Object> toolParameters = workflowInvocationInputs.getParameters()
				.get("core_pipeline_outputs_paired_with_parameters");
		assertNotNull(toolParameters, "toolParameters is null");

		String coverageMinValue = (String) toolParameters.get("coverageMin");
		assertEquals(coverageMinValue, "10", "coverageMinValue should have been changed to default");
		assertEquals(ImmutableMap.of("coverageMid", "10"), toolParameters.get("conditional"),
				"coverageMidValue should have been changed to default");
		String coverageMaxValue = (String) toolParameters.get("coverageMin");
		assertEquals("10", coverageMaxValue, "coverageMaxValue should have been changed to default");
	}

	/**
	 * Tests out successfully preparing paired workflow input files for
	 * execution and ignoring default parameters.
	 *
	 * @throws InterruptedException
	 * @throws ExecutionManagerException
	 * @throws IOException
	 * @throws IridaWorkflowException
	 */
	@Test
	@WithMockUser(username = "aaron", roles = "ADMIN")
	public void testPrepareAnalysisFilesParametersSuccessIgnoreDefaultParameters()
			throws InterruptedException, ExecutionManagerException, IOException, IridaWorkflowException {

		History history = new History();
		history.setName("testPrepareAnalysisFilesParametersSuccessIgnoreDefaultParameters");
		HistoriesClient historiesClient = localGalaxy.getGalaxyInstanceAdmin().getHistoriesClient();
		WorkflowsClient workflowsClient = localGalaxy.getGalaxyInstanceAdmin().getWorkflowsClient();
		History createdHistory = historiesClient.create(history);

		IridaWorkflow iridaWorkflow = iridaWorkflowsService.getIridaWorkflow(validWorkflowIdPairedWithParameters);
		Path workflowPath = iridaWorkflow.getWorkflowStructure().getWorkflowFile();
		String workflowString = new String(Files.readAllBytes(workflowPath), StandardCharsets.UTF_8);
		Workflow galaxyWorkflow = workflowsClient.importWorkflow(workflowString, false);

		Map<String, String> parameters = ImmutableMap.of("coverage", IridaWorkflowParameter.IGNORE_DEFAULT_VALUE);

		AnalysisSubmission analysisSubmission = analysisExecutionGalaxyITService.setupPairSubmissionInDatabase(1L,
				pairSequenceFiles1A, pairSequenceFiles2A, referenceFilePath, parameters,
				validWorkflowIdPairedWithParameters);
		analysisSubmission.setRemoteAnalysisId(createdHistory.getId());
		analysisSubmission.setRemoteWorkflowId(galaxyWorkflow.getId());

		PreparedWorkflowGalaxy preparedWorkflow = analysisWorkspaceService.prepareAnalysisFiles(analysisSubmission);
		assertEquals(createdHistory.getId(), preparedWorkflow.getRemoteAnalysisId(),
				"the response history id should match the input history id");
		WorkflowInputsGalaxy workflowInputsGalaxy = preparedWorkflow.getWorkflowInputs();
		assertNotNull(workflowInputsGalaxy, "the returned workflow inputs should not be null");
		assertNotNull(preparedWorkflow.getRemoteDataId(), "the returned library id should not be null");

		// verify correct files have been uploaded
		List<HistoryContents> historyContents = historiesClient.showHistoryContents(createdHistory.getId());
		assertEquals(4, historyContents.size(), "the created history has an invalid number of elements");

		WorkflowInvocationInputs workflowInvocationInputs = preparedWorkflow.getWorkflowInputs().getInputsObject();
		assertNotNull(workflowInvocationInputs, "created workflowInvocationInputs is null");

		Map<String, Object> toolParameters = workflowInvocationInputs.getParameters()
				.get("core_pipeline_outputs_paired_with_parameters");
		assertNull(toolParameters, "toolParameters is not null");
	}

	/**
	 * Tests out failing to prepare paired workflow input files for execution
	 * with parameters due to an invalid parameter passed.
	 *
	 * @throws InterruptedException
	 * @throws ExecutionManagerException
	 * @throws IOException
	 * @throws IridaWorkflowException
	 */
	@Test
	@WithMockUser(username = "aaron", roles = "ADMIN")
	public void testPrepareAnalysisFilesParametersFailInvalidParameter()
			throws InterruptedException, ExecutionManagerException, IOException, IridaWorkflowException {

		History history = new History();
		history.setName("testPrepareAnalysisFilesParametersFailInvalidParameter");
		HistoriesClient historiesClient = localGalaxy.getGalaxyInstanceAdmin().getHistoriesClient();
		WorkflowsClient workflowsClient = localGalaxy.getGalaxyInstanceAdmin().getWorkflowsClient();
		History createdHistory = historiesClient.create(history);

		IridaWorkflow iridaWorkflow = iridaWorkflowsService.getIridaWorkflow(validWorkflowIdPairedWithParameters);
		Path workflowPath = iridaWorkflow.getWorkflowStructure().getWorkflowFile();
		String workflowString = new String(Files.readAllBytes(workflowPath), StandardCharsets.UTF_8);
		Workflow galaxyWorkflow = workflowsClient.importWorkflow(workflowString, false);

		Map<String, String> parameters = ImmutableMap.of("invalid", "20");

		AnalysisSubmission analysisSubmission = analysisExecutionGalaxyITService.setupPairSubmissionInDatabase(1L,
				pairSequenceFiles1A, pairSequenceFiles2A, referenceFilePath, parameters,
				validWorkflowIdPairedWithParameters);
		analysisSubmission.setRemoteAnalysisId(createdHistory.getId());
		analysisSubmission.setRemoteWorkflowId(galaxyWorkflow.getId());

		assertThrows(IridaWorkflowParameterException.class, () -> {
			analysisWorkspaceService.prepareAnalysisFiles(analysisSubmission);
		});
	}

	/**
	 * Tests out failing to prepare paired workflow input files for execution
	 * (duplicate sample).
	 *
	 * @throws InterruptedException
	 * @throws ExecutionManagerException
	 * @throws IOException
	 * @throws IridaWorkflowException
	 */
	@Test
	@WithMockUser(username = "aaron", roles = "ADMIN")
	public void testPrepareAnalysisFilesPairFail()
			throws InterruptedException, ExecutionManagerException, IOException, IridaWorkflowException {

		History history = new History();
		history.setName("testPrepareAnalysisFilesPairFail");
		HistoriesClient historiesClient = localGalaxy.getGalaxyInstanceAdmin().getHistoriesClient();
		WorkflowsClient workflowsClient = localGalaxy.getGalaxyInstanceAdmin().getWorkflowsClient();
		History createdHistory = historiesClient.create(history);

		IridaWorkflow iridaWorkflow = iridaWorkflowsService.getIridaWorkflow(validWorkflowIdPaired);
		Path workflowPath = iridaWorkflow.getWorkflowStructure().getWorkflowFile();
		String workflowString = new String(Files.readAllBytes(workflowPath), StandardCharsets.UTF_8);
		Workflow galaxyWorkflow = workflowsClient.importWorkflow(workflowString, false);

		// construct two pairs of sequence files with same sample (1L)
		AnalysisSubmission analysisSubmission = analysisExecutionGalaxyITService.setupPairSubmissionInDatabase(1L,
				pairSequenceFiles1AB, pairSequenceFiles2AB, referenceFilePath, validWorkflowIdPaired, false);
		analysisSubmission.setRemoteAnalysisId(createdHistory.getId());
		analysisSubmission.setRemoteWorkflowId(galaxyWorkflow.getId());

		assertThrows(DuplicateSampleException.class, () -> {
			analysisWorkspaceService.prepareAnalysisFiles(analysisSubmission);
		});
	}

	/**
	 * Tests out successfully preparing paired and single workflow input files
	 * for execution.
	 *
	 * @throws InterruptedException
	 * @throws ExecutionManagerException
	 * @throws IOException
	 * @throws IridaWorkflowException
	 */
	@Test
	@WithMockUser(username = "aaron", roles = "ADMIN")
	public void testPrepareAnalysisFilesSinglePairSuccess()
			throws InterruptedException, ExecutionManagerException, IOException, IridaWorkflowException {

		History history = new History();
		history.setName("testPrepareAnalysisFilesPairSuccess");
		HistoriesClient historiesClient = localGalaxy.getGalaxyInstanceAdmin().getHistoriesClient();
		WorkflowsClient workflowsClient = localGalaxy.getGalaxyInstanceAdmin().getWorkflowsClient();
		History createdHistory = historiesClient.create(history);

		IridaWorkflow iridaWorkflow = iridaWorkflowsService.getIridaWorkflow(validWorkflowIdSinglePaired);
		Path workflowPath = iridaWorkflow.getWorkflowStructure().getWorkflowFile();
		String workflowString = new String(Files.readAllBytes(workflowPath), StandardCharsets.UTF_8);
		Workflow galaxyWorkflow = workflowsClient.importWorkflow(workflowString, false);

		AnalysisSubmission analysisSubmission = analysisExecutionGalaxyITService
				.setupSinglePairSubmissionInDatabaseDifferentSample(1L, 2L, pairSequenceFiles1A, pairSequenceFiles2A,
						sequenceFilePath3, referenceFilePath, validWorkflowIdSinglePaired);
		analysisSubmission.setRemoteAnalysisId(createdHistory.getId());
		analysisSubmission.setRemoteWorkflowId(galaxyWorkflow.getId());

		PreparedWorkflowGalaxy preparedWorkflow = analysisWorkspaceService.prepareAnalysisFiles(analysisSubmission);
		assertEquals(createdHistory.getId(), preparedWorkflow.getRemoteAnalysisId(),
				"the response history id should match the input history id");
		WorkflowInputsGalaxy workflowInputsGalaxy = preparedWorkflow.getWorkflowInputs();
		assertNotNull(workflowInputsGalaxy, "the returned workflow inputs should not be null");

		// verify correct files have been uploaded
		List<HistoryContents> historyContents = historiesClient.showHistoryContents(createdHistory.getId());
		assertEquals(6, historyContents.size(), "the created history has an invalid number of elements");
		Map<String, HistoryContents> contentsMap = historyContentsAsMap(historyContents);
		assertTrue(contentsMap.containsKey(sequenceFilePathA.toFile().getName()),
				"the created history should contain the file " + sequenceFilePathA.toFile().getName());
		assertTrue(contentsMap.containsKey(sequenceFilePath2A.toFile().getName()),
				"the created history should contain the file " + sequenceFilePath2A.toFile().getName());
		assertTrue(contentsMap.containsKey(sequenceFilePath3.toFile().getName()),
				"the created history should contain the file " + sequenceFilePath3.toFile().getName());
		assertTrue(contentsMap.containsKey(referenceFilePath.toFile().getName()),
				"the created history should contain the file " + referenceFilePath.toFile().getName());
		assertTrue(contentsMap.containsKey(INPUTS_SINGLE_NAME),
				"the created history should contain a dataset collection with the name " + INPUTS_SINGLE_NAME);
		assertTrue(contentsMap.containsKey(INPUTS_PAIRED_NAME),
				"the created history should contain a dataset collection with the name " + INPUTS_PAIRED_NAME);

		// make sure workflow inputs contains correct information
		Map<String, WorkflowInvocationInput> workflowInputsMap = preparedWorkflow.getWorkflowInputs().getInputsObject()
				.getInputs();
		assertEquals(3, workflowInputsMap.size(), "the created workflow inputs has an invalid number of elements");
	}

	/**
	 * Tests out failing to prepare paired and single workflow input files for
	 * execution (duplicate samples among single and paired input files).
	 *
	 * @throws InterruptedException
	 * @throws ExecutionManagerException
	 * @throws IOException
	 * @throws IridaWorkflowException
	 */
	@Test
	@WithMockUser(username = "aaron", roles = "ADMIN")
	public void testPrepareAnalysisFilesSinglePairFail()
			throws InterruptedException, ExecutionManagerException, IOException, IridaWorkflowException {

		History history = new History();
		history.setName("testPrepareAnalysisFilesSinglePairFail");
		HistoriesClient historiesClient = localGalaxy.getGalaxyInstanceAdmin().getHistoriesClient();
		WorkflowsClient workflowsClient = localGalaxy.getGalaxyInstanceAdmin().getWorkflowsClient();
		History createdHistory = historiesClient.create(history);

		IridaWorkflow iridaWorkflow = iridaWorkflowsService.getIridaWorkflow(validWorkflowIdSinglePaired);
		Path workflowPath = iridaWorkflow.getWorkflowStructure().getWorkflowFile();
		String workflowString = new String(Files.readAllBytes(workflowPath), StandardCharsets.UTF_8);
		Workflow galaxyWorkflow = workflowsClient.importWorkflow(workflowString, false);

		AnalysisSubmission analysisSubmission = analysisExecutionGalaxyITService
				.setupSinglePairSubmissionInDatabaseSameSample(1L, pairSequenceFiles1A, pairSequenceFiles2A,
						sequenceFilePath3, referenceFilePath, validWorkflowIdSinglePaired);
		analysisSubmission.setRemoteAnalysisId(createdHistory.getId());
		analysisSubmission.setRemoteWorkflowId(galaxyWorkflow.getId());

		assertThrows(SampleAnalysisDuplicateException.class, () -> {
			analysisWorkspaceService.prepareAnalysisFiles(analysisSubmission);
		});
	}

	/**
	 * Tests out failure to prepare workflow input files for execution.
	 *
	 * @throws InterruptedException
	 * @throws ExecutionManagerException
	 * @throws IOException
	 * @throws IridaWorkflowException
	 */
	@Test
	@WithMockUser(username = "aaron", roles = "ADMIN")
	public void testPrepareAnalysisFilesFail()
			throws InterruptedException, ExecutionManagerException, IOException, IridaWorkflowException {

		History history = new History();
		history.setName("testPrepareAnalysisFilesFail");
		HistoriesClient historiesClient = localGalaxy.getGalaxyInstanceAdmin().getHistoriesClient();
		History createdHistory = historiesClient.create(history);

		AnalysisSubmission analysisSubmission = analysisExecutionGalaxyITService.setupSubmissionInDatabase(1L,
				sequenceFilePathA, referenceFilePath, validWorkflowIdSingle, false);
		analysisSubmission.setRemoteAnalysisId(createdHistory.getId());
		analysisSubmission.setRemoteWorkflowId("invalid");

		assertThrows(WorkflowException.class, () -> {
			analysisWorkspaceService.prepareAnalysisFiles(analysisSubmission);
		});
	}

	private void uploadFileToHistory(Path filePath, String fileName, String historyId, ToolsClient toolsClient) {
		ToolsClient.FileUploadRequest uploadRequest = new ToolsClient.FileUploadRequest(historyId, filePath.toFile());
		uploadRequest.setDatasetName(fileName);
		toolsClient.upload(uploadRequest);
	}

	/**
	 * Tests out successfully getting results for an analysis (TestAnalysis)
	 * consisting only of single end sequence reads.
	 *
	 * @throws InterruptedException
	 * @throws ExecutionManagerException
	 * @throws IridaWorkflowNotFoundException
	 * @throws IOException
	 * @throws IridaWorkflowAnalysisTypeException
	 * @throws TimeoutException
	 * @throws IridaWorkflowAnalysisLabelException
	 */
	@Test
	@WithMockUser(username = "aaron", roles = "ADMIN")
	public void testGetAnalysisResultsTestAnalysisSingleSuccess()
			throws InterruptedException, ExecutionManagerException, IridaWorkflowNotFoundException, IOException,
			IridaWorkflowAnalysisTypeException, TimeoutException {

		History history = new History();
		history.setName("testGetAnalysisResultsTestAnalysisSingleSuccess");
		HistoriesClient historiesClient = localGalaxy.getGalaxyInstanceAdmin().getHistoriesClient();
		WorkflowsClient workflowsClient = localGalaxy.getGalaxyInstanceAdmin().getWorkflowsClient();
		ToolsClient toolsClient = localGalaxy.getGalaxyInstanceAdmin().getToolsClient();

		History createdHistory = historiesClient.create(history);

		// upload test outputs
		uploadFileToHistory(sequenceFilePathA, OUTPUT1_NAME, createdHistory.getId(), toolsClient);
		uploadFileToHistory(sequenceFilePathA, OUTPUT2_NAME, createdHistory.getId(), toolsClient);

		// wait for history
		Util.waitUntilHistoryComplete(createdHistory.getId(), galaxyHistoriesService, 60);

		IridaWorkflow iridaWorkflow = iridaWorkflowsService.getIridaWorkflow(validWorkflowIdSingle);
		Path workflowPath = iridaWorkflow.getWorkflowStructure().getWorkflowFile();
		String workflowString = new String(Files.readAllBytes(workflowPath), StandardCharsets.UTF_8);
		Workflow galaxyWorkflow = workflowsClient.importWorkflow(workflowString, false);

		AnalysisSubmission analysisSubmission = analysisExecutionGalaxyITService.setupSubmissionInDatabase(1L,
				sequenceFilePathA, referenceFilePath, validWorkflowIdSingle, false);

		Set<SingleEndSequenceFile> submittedSf = sequencingObjectService
				.getSequencingObjectsOfTypeForAnalysisSubmission(analysisSubmission, SingleEndSequenceFile.class);
		Set<SequenceFilePair> pairedFiles = sequencingObjectService
				.getSequencingObjectsOfTypeForAnalysisSubmission(analysisSubmission, SequenceFilePair.class);
		assertEquals(0, pairedFiles.size(), "the created submission should have no paired input files");
		assertEquals(1, submittedSf.size(), "the created submission should have 1 single input file");

		analysisSubmission.setRemoteAnalysisId(createdHistory.getId());
		analysisSubmission.setRemoteWorkflowId(galaxyWorkflow.getId());
		analysisSubmission.setAnalysisState(AnalysisState.COMPLETING);
		analysisSubmissionRepository.save(analysisSubmission);

		Analysis analysis = analysisWorkspaceService.getAnalysisResults(analysisSubmission);
		assertNotNull(analysis, "the analysis results were not properly created");
		assertEquals(Analysis.class, analysis.getClass(), "the Analysis results class is invalid");
		assertEquals(2, analysis.getAnalysisOutputFiles().size(),
				"the analysis results has an invalid number of output files");
		assertEquals(Paths.get(OUTPUT1_NAME), analysis.getAnalysisOutputFile(OUTPUT1_KEY).getFile().getFileName(),
				"the analysis results output file has an invalid name");
		assertEquals(OUTPUT1_NAME, analysis.getAnalysisOutputFile(OUTPUT1_KEY).getLabel(),
				"the analysis results output file has an invalid label");
		assertEquals(Paths.get(OUTPUT2_NAME), analysis.getAnalysisOutputFile(OUTPUT2_KEY).getFile().getFileName(),
				"the analysis results output file has an invalid name");
		assertEquals(OUTPUT2_NAME, analysis.getAnalysisOutputFile(OUTPUT2_KEY).getLabel(),
				"the analysis results output file has an invalid label");
	}

	/**
	 * Tests out successfully getting results for an analysis (TestAnalysis)
	 * consisting only of single end sequence reads (for workflow accepting
	 * single sample).
	 *
	 * @throws InterruptedException
	 * @throws ExecutionManagerException
	 * @throws IridaWorkflowNotFoundException
	 * @throws IOException
	 * @throws IridaWorkflowAnalysisTypeException
	 * @throws TimeoutException
	 */
	@Test
	@WithMockUser(username = "aaron", roles = "ADMIN")
	public void testGetAnalysisResultsTestAnalysisSingleSingleSampleSuccess()
			throws InterruptedException, ExecutionManagerException, IridaWorkflowNotFoundException, IOException,
			IridaWorkflowAnalysisTypeException, TimeoutException {

		History history = new History();
		history.setName("testGetAnalysisResultsTestAnalysisSingleSuccess");
		HistoriesClient historiesClient = localGalaxy.getGalaxyInstanceAdmin().getHistoriesClient();
		WorkflowsClient workflowsClient = localGalaxy.getGalaxyInstanceAdmin().getWorkflowsClient();
		ToolsClient toolsClient = localGalaxy.getGalaxyInstanceAdmin().getToolsClient();

		History createdHistory = historiesClient.create(history);

		// upload test outputs
		uploadFileToHistory(sequenceFilePathA, OUTPUT1_NAME, createdHistory.getId(), toolsClient);
		uploadFileToHistory(sequenceFilePathA, OUTPUT2_NAME, createdHistory.getId(), toolsClient);

		// wait for history
		Util.waitUntilHistoryComplete(createdHistory.getId(), galaxyHistoriesService, 60);

		IridaWorkflow iridaWorkflow = iridaWorkflowsService.getIridaWorkflow(validWorkflowIdSingleSingleSample);
		Path workflowPath = iridaWorkflow.getWorkflowStructure().getWorkflowFile();
		String workflowString = new String(Files.readAllBytes(workflowPath), StandardCharsets.UTF_8);
		Workflow galaxyWorkflow = workflowsClient.importWorkflow(workflowString, false);

		AnalysisSubmission analysisSubmission = analysisExecutionGalaxyITService.setupSubmissionInDatabase(1L,
				sequenceFilePathA, referenceFilePath, validWorkflowIdSingleSingleSample, false);

		Set<SingleEndSequenceFile> submittedSf = sequencingObjectService
				.getSequencingObjectsOfTypeForAnalysisSubmission(analysisSubmission, SingleEndSequenceFile.class);
		Set<SequenceFilePair> pairedFiles = sequencingObjectService
				.getSequencingObjectsOfTypeForAnalysisSubmission(analysisSubmission, SequenceFilePair.class);
		assertEquals(0, pairedFiles.size(), "the created submission should have no paired input files");
		assertEquals(1, submittedSf.size(), "the created submission should have 1 single input file");

		analysisSubmission.setRemoteAnalysisId(createdHistory.getId());
		analysisSubmission.setRemoteWorkflowId(galaxyWorkflow.getId());
		analysisSubmission.setAnalysisState(AnalysisState.COMPLETING);
		analysisSubmissionRepository.save(analysisSubmission);

		Analysis analysis = analysisWorkspaceService.getAnalysisResults(analysisSubmission);
		assertNotNull(analysis, "the analysis results were not properly created");
		assertEquals(Analysis.class, analysis.getClass(), "the Analysis results class is invalid");
		assertEquals(2, analysis.getAnalysisOutputFiles().size(),
				"the analysis results has an invalid number of output files");
		assertEquals(Paths.get(OUTPUT1_NAME), analysis.getAnalysisOutputFile(OUTPUT1_KEY).getFile().getFileName(),
				"the analysis results output file has an invalid name");
		assertEquals(SAMPLE1_NAME + '-' + OUTPUT1_NAME, analysis.getAnalysisOutputFile(OUTPUT1_KEY).getLabel(),
				"the analysis results output file has an invalid label");
		assertEquals(Paths.get(OUTPUT2_NAME), analysis.getAnalysisOutputFile(OUTPUT2_KEY).getFile().getFileName(),
				"the analysis results output file has an invalid name");
		assertEquals(SAMPLE1_NAME + '-' + OUTPUT2_NAME, analysis.getAnalysisOutputFile(OUTPUT2_KEY).getLabel(),
				"the analysis results output file has an invalid label");
	}

	/**
	 * Tests out successfully getting results for an analysis (TestAnalysis)
	 * consisting only of paired sequence reads.
	 *
	 * @throws InterruptedException
	 * @throws ExecutionManagerException
	 * @throws IridaWorkflowNotFoundException
	 * @throws IOException
	 * @throws IridaWorkflowAnalysisTypeException
	 * @throws TimeoutException
	 */
	@Test
	@WithMockUser(username = "aaron", roles = "ADMIN")
	public void testGetAnalysisResultsTestAnalysisPairedSuccess()
			throws InterruptedException, ExecutionManagerException, IridaWorkflowNotFoundException, IOException,
			IridaWorkflowAnalysisTypeException, TimeoutException {

		History history = new History();
		history.setName("testGetAnalysisResultsTestAnalysisPairedSuccess");
		HistoriesClient historiesClient = localGalaxy.getGalaxyInstanceAdmin().getHistoriesClient();
		WorkflowsClient workflowsClient = localGalaxy.getGalaxyInstanceAdmin().getWorkflowsClient();
		ToolsClient toolsClient = localGalaxy.getGalaxyInstanceAdmin().getToolsClient();

		History createdHistory = historiesClient.create(history);

		// upload test outputs
		uploadFileToHistory(sequenceFilePathA, OUTPUT1_NAME, createdHistory.getId(), toolsClient);
		uploadFileToHistory(sequenceFilePathA, OUTPUT2_NAME, createdHistory.getId(), toolsClient);

		// wait for history
		Util.waitUntilHistoryComplete(createdHistory.getId(), galaxyHistoriesService, 60);

		IridaWorkflow iridaWorkflow = iridaWorkflowsService.getIridaWorkflow(validWorkflowIdPaired);
		Path workflowPath = iridaWorkflow.getWorkflowStructure().getWorkflowFile();
		String workflowString = new String(Files.readAllBytes(workflowPath), StandardCharsets.UTF_8);
		Workflow galaxyWorkflow = workflowsClient.importWorkflow(workflowString, false);
		List<Path> paths1 = new ArrayList<>();
		paths1.add(sequenceFilePathA);
		List<Path> paths2 = new ArrayList<>();
		paths2.add(sequenceFilePath2A);

		AnalysisSubmission analysisSubmission = analysisExecutionGalaxyITService.setupPairSubmissionInDatabase(1L,
				paths1, paths2, referenceFilePath, validWorkflowIdPaired, false);

		Set<SingleEndSequenceFile> submittedSingleFiles = sequencingObjectService
				.getSequencingObjectsOfTypeForAnalysisSubmission(analysisSubmission, SingleEndSequenceFile.class);
		Set<SequenceFilePair> pairedFiles = sequencingObjectService
				.getSequencingObjectsOfTypeForAnalysisSubmission(analysisSubmission, SequenceFilePair.class);

		assertEquals(0, submittedSingleFiles.size(), "the created submission should have no single input files");
		assertEquals(1, pairedFiles.size(), "the created submission has an invalid number of paired input files");
		SequenceFilePair submittedSp = pairedFiles.iterator().next();
		Set<SequenceFile> submittedSf = submittedSp.getFiles();
		assertEquals(2, submittedSf.size(), "the paired input should have 2 files");

		analysisSubmission.setRemoteAnalysisId(createdHistory.getId());
		analysisSubmission.setRemoteWorkflowId(galaxyWorkflow.getId());
		analysisSubmission.setAnalysisState(AnalysisState.COMPLETING);
		analysisSubmissionRepository.save(analysisSubmission);

		Analysis analysis = analysisWorkspaceService.getAnalysisResults(analysisSubmission);
		assertNotNull(analysis, "the analysis results were not properly created");
		assertEquals(Analysis.class, analysis.getClass(), "the Analysis results class is invalid");
		assertEquals(2, analysis.getAnalysisOutputFiles().size(),
				"the analysis results has an invalid number of output files");
		assertEquals(Paths.get(OUTPUT1_NAME), analysis.getAnalysisOutputFile(OUTPUT1_KEY).getFile().getFileName(),
				"the analysis results output file has an invalid name");
		assertEquals(OUTPUT1_NAME, analysis.getAnalysisOutputFile(OUTPUT1_KEY).getLabel(),
				"the analysis results output file has an invalid label");
		assertEquals(Paths.get(OUTPUT2_NAME), analysis.getAnalysisOutputFile(OUTPUT2_KEY).getFile().getFileName(),
				"the analysis results output file has an invalid name");
		assertEquals(OUTPUT2_NAME, analysis.getAnalysisOutputFile(OUTPUT2_KEY).getLabel(),
				"the analysis results output file has an invalid label");
	}

	/**
	 * Tests out successfully getting results for an analysis (TestAnalysis)
	 * consisting only of paired sequence reads.
	 *
	 * @throws InterruptedException
	 * @throws ExecutionManagerException
	 * @throws IridaWorkflowNotFoundException
	 * @throws IOException
	 * @throws IridaWorkflowAnalysisTypeException
	 * @throws TimeoutException
	 */
	@Test
//	@Transactional
//	@DirtiesContext
	@WithMockUser(username = "aaron", roles = "ADMIN")
	public void testGetAnalysisResultsTestAnalysisPairedSingleSampleSuccess()
			throws InterruptedException, ExecutionManagerException, IridaWorkflowNotFoundException, IOException,
			IridaWorkflowAnalysisTypeException, TimeoutException {

		History history = new History();
		history.setName("testGetAnalysisResultsTestAnalysisPairedSingleSampleSuccess");
		HistoriesClient historiesClient = localGalaxy.getGalaxyInstanceAdmin().getHistoriesClient();
		WorkflowsClient workflowsClient = localGalaxy.getGalaxyInstanceAdmin().getWorkflowsClient();
		ToolsClient toolsClient = localGalaxy.getGalaxyInstanceAdmin().getToolsClient();

		History createdHistory = historiesClient.create(history);

		// upload test outputs
		uploadFileToHistory(sequenceFilePathA, OUTPUT1_NAME, createdHistory.getId(), toolsClient);
		uploadFileToHistory(sequenceFilePathA, OUTPUT2_NAME, createdHistory.getId(), toolsClient);

		// wait for history
		Util.waitUntilHistoryComplete(createdHistory.getId(), galaxyHistoriesService, 60);

		IridaWorkflow iridaWorkflow = iridaWorkflowsService.getIridaWorkflow(validWorkflowIdPairedSingleSample);
		Path workflowPath = iridaWorkflow.getWorkflowStructure().getWorkflowFile();
		String workflowString = new String(Files.readAllBytes(workflowPath), StandardCharsets.UTF_8);
		Workflow galaxyWorkflow = workflowsClient.importWorkflow(workflowString, false);
		List<Path> paths1 = new ArrayList<>();
		paths1.add(sequenceFilePathA);
		List<Path> paths2 = new ArrayList<>();
		paths2.add(sequenceFilePath2A);

		AnalysisSubmission analysisSubmission = analysisExecutionGalaxyITService.setupPairSubmissionInDatabase(1L,
				paths1, paths2, referenceFilePath, validWorkflowIdPairedSingleSample, false);

		Set<SingleEndSequenceFile> submittedSingleFiles = sequencingObjectService
				.getSequencingObjectsOfTypeForAnalysisSubmission(analysisSubmission, SingleEndSequenceFile.class);
		Set<SequenceFilePair> pairedFiles = sequencingObjectService
				.getSequencingObjectsOfTypeForAnalysisSubmission(analysisSubmission, SequenceFilePair.class);

		assertEquals(0, submittedSingleFiles.size(), "the created submission should have no single input files");
		assertEquals(1, pairedFiles.size(), "the created submission has an invalid number of paired input files");
		SequenceFilePair submittedSp = pairedFiles.iterator().next();
		Set<SequenceFile> submittedSf = submittedSp.getFiles();
		assertEquals(2, submittedSf.size(), "the paired input should have 2 files");

		analysisSubmission.setRemoteAnalysisId(createdHistory.getId());
		analysisSubmission.setRemoteWorkflowId(galaxyWorkflow.getId());
		analysisSubmission.setAnalysisState(AnalysisState.COMPLETING);
		analysisSubmissionRepository.save(analysisSubmission);

		Analysis analysis = analysisWorkspaceService.getAnalysisResults(analysisSubmission);
		assertNotNull(analysis, "the analysis results were not properly created");
		assertEquals(Analysis.class, analysis.getClass(), "the Analysis results class is invalid");
		assertEquals(2, analysis.getAnalysisOutputFiles().size(),
				"the analysis results has an invalid number of output files");
		assertEquals(Paths.get(OUTPUT1_NAME), analysis.getAnalysisOutputFile(OUTPUT1_KEY).getFile().getFileName(),
				"the analysis results output file has an invalid name");
		assertEquals(SAMPLE1_NAME + "-" + OUTPUT1_NAME, analysis.getAnalysisOutputFile(OUTPUT1_KEY).getLabel(),
				"the analysis results output file has an invalid label");
		assertEquals(Paths.get(OUTPUT2_NAME), analysis.getAnalysisOutputFile(OUTPUT2_KEY).getFile().getFileName(),
				"the analysis results output file has an invalid name");
		assertEquals(SAMPLE1_NAME + "-" + OUTPUT2_NAME, analysis.getAnalysisOutputFile(OUTPUT2_KEY).getLabel(),
				"the analysis results output file has an invalid label");
	}

	/**
	 * Tests out successfully getting results for an analysis (TestAnalysis)
	 * when sequencing objects are present, but the sample was deleted while
	 * pipeline was running.
	 *
	 * @throws InterruptedException
	 * @throws ExecutionManagerException
	 * @throws IridaWorkflowNotFoundException
	 * @throws IOException
	 * @throws IridaWorkflowAnalysisTypeException
	 * @throws TimeoutException
	 */
	@Test
	@WithMockUser(username = "aaron", roles = "ADMIN")
	public void testGetAnalysisResultsTestAnalysisDeleteSampleRunningSuccess()
			throws InterruptedException, ExecutionManagerException, IridaWorkflowNotFoundException, IOException,
			IridaWorkflowAnalysisTypeException, TimeoutException {

		History history = new History();
		history.setName("testGetAnalysisResultsTestAnalysisDeleteSampleRunningSuccess");
		HistoriesClient historiesClient = localGalaxy.getGalaxyInstanceAdmin().getHistoriesClient();
		WorkflowsClient workflowsClient = localGalaxy.getGalaxyInstanceAdmin().getWorkflowsClient();
		ToolsClient toolsClient = localGalaxy.getGalaxyInstanceAdmin().getToolsClient();

		History createdHistory = historiesClient.create(history);

		// upload test outputs
		uploadFileToHistory(sequenceFilePathA, OUTPUT1_NAME, createdHistory.getId(), toolsClient);
		uploadFileToHistory(sequenceFilePathA, OUTPUT2_NAME, createdHistory.getId(), toolsClient);

		// wait for history
		Util.waitUntilHistoryComplete(createdHistory.getId(), galaxyHistoriesService, 60);

		IridaWorkflow iridaWorkflow = iridaWorkflowsService.getIridaWorkflow(validWorkflowIdPairedSingleSample);
		Path workflowPath = iridaWorkflow.getWorkflowStructure().getWorkflowFile();
		String workflowString = new String(Files.readAllBytes(workflowPath), StandardCharsets.UTF_8);
		Workflow galaxyWorkflow = workflowsClient.importWorkflow(workflowString, false);
		List<Path> paths1 = new ArrayList<>();
		paths1.add(sequenceFilePathA);
		List<Path> paths2 = new ArrayList<>();
		paths2.add(sequenceFilePath2A);

		AnalysisSubmission analysisSubmission = analysisExecutionGalaxyITService.setupPairSubmissionInDatabase(1L,
				paths1, paths2, referenceFilePath, validWorkflowIdPairedSingleSample, false);

		sampleRepository.deleteById(1L);
		assertTrue(!sampleService.exists(1L));

		analysisSubmission.setRemoteAnalysisId(createdHistory.getId());
		analysisSubmission.setRemoteWorkflowId(galaxyWorkflow.getId());
		analysisSubmission.setAnalysisState(AnalysisState.COMPLETING);
		analysisSubmissionRepository.save(analysisSubmission);

		Analysis analysis = analysisWorkspaceService.getAnalysisResults(analysisSubmission);
		assertNotNull(analysis, "the analysis results were not properly created");
		assertEquals(Analysis.class, analysis.getClass(), "the Analysis results class is invalid");
		assertEquals(2, analysis.getAnalysisOutputFiles().size(),
				"the analysis results has an invalid number of output files");
		assertEquals(Paths.get(OUTPUT1_NAME), analysis.getAnalysisOutputFile(OUTPUT1_KEY).getFile().getFileName(),
				"the analysis results output file has an invalid name");
		assertEquals(OUTPUT1_NAME, analysis.getAnalysisOutputFile(OUTPUT1_KEY).getLabel(),
				"the analysis results output file has an invalid label");
		assertEquals(Paths.get(OUTPUT2_NAME), analysis.getAnalysisOutputFile(OUTPUT2_KEY).getFile().getFileName(),
				"the analysis results output file has an invalid name");
		assertEquals(OUTPUT2_NAME, analysis.getAnalysisOutputFile(OUTPUT2_KEY).getLabel(),
				"the analysis results output file has an invalid label");
	}

	/**
	 * Tests out successfully getting results for an analysis (TestAnalysis)
	 * consisting of both single and paired sequence reads.
	 *
	 * @throws InterruptedException
	 * @throws ExecutionManagerException
	 * @throws IridaWorkflowNotFoundException
	 * @throws IOException
	 * @throws IridaWorkflowAnalysisTypeException
	 * @throws TimeoutException
	 */
	@Test
	@WithMockUser(username = "aaron", roles = "ADMIN")
	public void testGetAnalysisResultsTestAnalysisSinglePairedSuccess()
			throws InterruptedException, ExecutionManagerException, IridaWorkflowNotFoundException, IOException,
			IridaWorkflowAnalysisTypeException, TimeoutException {

		History history = new History();
		history.setName("testGetAnalysisResultsTestAnalysisSinglePairedSuccess");
		HistoriesClient historiesClient = localGalaxy.getGalaxyInstanceAdmin().getHistoriesClient();
		WorkflowsClient workflowsClient = localGalaxy.getGalaxyInstanceAdmin().getWorkflowsClient();
		ToolsClient toolsClient = localGalaxy.getGalaxyInstanceAdmin().getToolsClient();

		History createdHistory = historiesClient.create(history);

		// upload test outputs
		uploadFileToHistory(sequenceFilePathA, OUTPUT1_NAME, createdHistory.getId(), toolsClient);
		uploadFileToHistory(sequenceFilePathA, OUTPUT2_NAME, createdHistory.getId(), toolsClient);

		// wait for history
		Util.waitUntilHistoryComplete(createdHistory.getId(), galaxyHistoriesService, 60);

		IridaWorkflow iridaWorkflow = iridaWorkflowsService.getIridaWorkflow(validWorkflowIdSinglePaired);
		Path workflowPath = iridaWorkflow.getWorkflowStructure().getWorkflowFile();
		String workflowString = new String(Files.readAllBytes(workflowPath), StandardCharsets.UTF_8);
		Workflow galaxyWorkflow = workflowsClient.importWorkflow(workflowString, false);

		List<Path> paths1 = new ArrayList<>();
		paths1.add(sequenceFilePathA);
		List<Path> paths2 = new ArrayList<>();
		paths2.add(sequenceFilePath2A);

		AnalysisSubmission analysisSubmission = analysisExecutionGalaxyITService
				.setupSinglePairSubmissionInDatabaseSameSample(1L, paths1, paths2, sequenceFilePath3, referenceFilePath,
						validWorkflowIdSinglePaired);

		Set<SingleEndSequenceFile> singleFiles = sequencingObjectService
				.getSequencingObjectsOfTypeForAnalysisSubmission(analysisSubmission, SingleEndSequenceFile.class);
		Set<SequenceFilePair> pairedFiles = sequencingObjectService
				.getSequencingObjectsOfTypeForAnalysisSubmission(analysisSubmission, SequenceFilePair.class);

		assertEquals(1, singleFiles.size(), "invalid number of single end input files");
		assertEquals(1, pairedFiles.size(), "invalid number of paired end inputs");
		SequenceFilePair submittedSp = pairedFiles.iterator().next();
		Set<SequenceFile> submittedSf = submittedSp.getFiles();
		assertEquals(2, submittedSf.size(), "invalid number of files for paired input");

		analysisSubmission.setRemoteAnalysisId(createdHistory.getId());
		analysisSubmission.setRemoteWorkflowId(galaxyWorkflow.getId());
		analysisSubmission.setAnalysisState(AnalysisState.COMPLETING);
		analysisSubmissionRepository.save(analysisSubmission);

		Analysis analysis = analysisWorkspaceService.getAnalysisResults(analysisSubmission);
		assertNotNull(analysis, "the analysis results were not properly created");
		assertEquals(Analysis.class, analysis.getClass(), "the Analysis results class is invalid");
		assertEquals(2, analysis.getAnalysisOutputFiles().size(),
				"the analysis results has an invalid number of output files");
		assertEquals(Paths.get(OUTPUT1_NAME), analysis.getAnalysisOutputFile(OUTPUT1_KEY).getFile().getFileName(),
				"the analysis results output file has an invalid name");
		assertEquals(OUTPUT1_NAME, analysis.getAnalysisOutputFile(OUTPUT1_KEY).getLabel(),
				"the analysis results output file has an invalid label");
		assertEquals(Paths.get(OUTPUT2_NAME), analysis.getAnalysisOutputFile(OUTPUT2_KEY).getFile().getFileName(),
				"the analysis results output file has an invalid name");
		assertEquals(OUTPUT2_NAME, analysis.getAnalysisOutputFile(OUTPUT2_KEY).getLabel(),
				"the analysis results output file has an invalid label");
	}

	/**
	 * Tests out successfully getting results for an analysis (phylogenomics).
	 *
	 * @throws InterruptedException
	 * @throws ExecutionManagerException
	 * @throws IridaWorkflowNotFoundException
	 * @throws IOException
	 * @throws IridaWorkflowAnalysisTypeException
	 * @throws TimeoutException
	 */
	@Test
	@WithMockUser(username = "aaron", roles = "ADMIN")
	public void testGetAnalysisResultsPhylogenomicsSuccess() throws InterruptedException, ExecutionManagerException,
			IridaWorkflowNotFoundException, IOException, IridaWorkflowAnalysisTypeException, TimeoutException {

		History history = new History();
		history.setName("testGetAnalysisResultsPhylogenomicsSuccess");
		HistoriesClient historiesClient = localGalaxy.getGalaxyInstanceAdmin().getHistoriesClient();
		WorkflowsClient workflowsClient = localGalaxy.getGalaxyInstanceAdmin().getWorkflowsClient();
		ToolsClient toolsClient = localGalaxy.getGalaxyInstanceAdmin().getToolsClient();

		History createdHistory = historiesClient.create(history);

		// upload test outputs
		uploadFileToHistory(sequenceFilePathA, TABLE_NAME, createdHistory.getId(), toolsClient);
		uploadFileToHistory(sequenceFilePathA, MATRIX_NAME, createdHistory.getId(), toolsClient);
		uploadFileToHistory(sequenceFilePathA, TREE_NAME, createdHistory.getId(), toolsClient);

		// wait for history
		Util.waitUntilHistoryComplete(createdHistory.getId(), galaxyHistoriesService, 60);

		IridaWorkflow iridaWorkflow = iridaWorkflowsService.getIridaWorkflow(phylogenomicsWorkflowId);
		Path workflowPath = iridaWorkflow.getWorkflowStructure().getWorkflowFile();
		String workflowString = new String(Files.readAllBytes(workflowPath), StandardCharsets.UTF_8);
		Workflow galaxyWorkflow = workflowsClient.importWorkflow(workflowString, false);

		AnalysisSubmission analysisSubmission = analysisExecutionGalaxyITService.setupSubmissionInDatabase(1L,
				sequenceFilePathA, referenceFilePath, phylogenomicsWorkflowId, false);
		analysisSubmission.setRemoteAnalysisId(createdHistory.getId());
		analysisSubmission.setRemoteWorkflowId(galaxyWorkflow.getId());
		analysisSubmission.setAnalysisState(AnalysisState.COMPLETING);
		analysisSubmissionRepository.save(analysisSubmission);

		Analysis analysis = analysisWorkspaceService.getAnalysisResults(analysisSubmission);
		assertNotNull(analysis, "the analysis results were not properly created");
		assertEquals(BuiltInAnalysisTypes.PHYLOGENOMICS, analysis.getAnalysisType(),
				"the Analysis results class is invalid");
		assertEquals(3, analysis.getAnalysisOutputFiles().size(),
				"the analysis results has an invalid number of output files");
		assertEquals(Paths.get(TABLE_NAME), analysis.getAnalysisOutputFile(TABLE_KEY).getFile().getFileName(),
				"the analysis results output file has an invalid name");
		assertEquals(TABLE_NAME, analysis.getAnalysisOutputFile(TABLE_KEY).getLabel(),
				"the analysis results output file has an invalid label");
		assertEquals(Paths.get(MATRIX_NAME), analysis.getAnalysisOutputFile(MATRIX_KEY).getFile().getFileName(),
				"the analysis results output file has an invalid name");
		assertEquals(MATRIX_NAME, analysis.getAnalysisOutputFile(MATRIX_KEY).getLabel(),
				"the analysis results output file has an invalid label");
		assertEquals(Paths.get(TREE_NAME), analysis.getAnalysisOutputFile(TREE_KEY).getFile().getFileName(),
				"the analysis results output file has an invalid name");
		assertEquals(TREE_NAME, analysis.getAnalysisOutputFile(TREE_KEY).getLabel(),
				"the analysis results output file has an invalid label");
	}

	/**
	 * Tests out failing to get results for an analysis (missing output file).
	 *
	 * @throws InterruptedException
	 * @throws ExecutionManagerException
	 * @throws IridaWorkflowNotFoundException
	 * @throws IOException
	 * @throws IridaWorkflowAnalysisTypeException
	 * @throws TimeoutException
	 */
	@Test
	@WithMockUser(username = "aaron", roles = "ADMIN")
	public void testGetAnalysisResultsTestAnalysisFail() throws InterruptedException, ExecutionManagerException,
			IridaWorkflowNotFoundException, IOException, IridaWorkflowAnalysisTypeException, TimeoutException {

		History history = new History();
		history.setName("testGetAnalysisResultsTestAnalysisFail");
		HistoriesClient historiesClient = localGalaxy.getGalaxyInstanceAdmin().getHistoriesClient();
		WorkflowsClient workflowsClient = localGalaxy.getGalaxyInstanceAdmin().getWorkflowsClient();
		ToolsClient toolsClient = localGalaxy.getGalaxyInstanceAdmin().getToolsClient();

		History createdHistory = historiesClient.create(history);

		// upload test outputs
		uploadFileToHistory(sequenceFilePathA, OUTPUT1_NAME, createdHistory.getId(), toolsClient);

		// wait for history
		Util.waitUntilHistoryComplete(createdHistory.getId(), galaxyHistoriesService, 60);

		IridaWorkflow iridaWorkflow = iridaWorkflowsService.getIridaWorkflow(validWorkflowIdSingle);
		Path workflowPath = iridaWorkflow.getWorkflowStructure().getWorkflowFile();
		String workflowString = new String(Files.readAllBytes(workflowPath), StandardCharsets.UTF_8);
		Workflow galaxyWorkflow = workflowsClient.importWorkflow(workflowString, false);

		AnalysisSubmission analysisSubmission = analysisExecutionGalaxyITService.setupSubmissionInDatabase(1L,
				sequenceFilePathA, referenceFilePath, validWorkflowIdSingle, false);
		analysisSubmission.setRemoteAnalysisId(createdHistory.getId());
		analysisSubmission.setRemoteWorkflowId(galaxyWorkflow.getId());
		analysisSubmission.setAnalysisState(AnalysisState.COMPLETING);
		analysisSubmissionRepository.save(analysisSubmission);

		assertThrows(GalaxyDatasetNotFoundException.class, () -> {
			analysisWorkspaceService.getAnalysisResults(analysisSubmission);
		});
	}
}
