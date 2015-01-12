package ca.corefacility.bioinformatics.irida.service.analysis.workspace.galaxy.impl.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithSecurityContextTestExcecutionListener;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import ca.corefacility.bioinformatics.irida.config.IridaApiGalaxyTestConfig;
import ca.corefacility.bioinformatics.irida.config.conditions.WindowsPlatformCondition;
import ca.corefacility.bioinformatics.irida.exceptions.ExecutionManagerException;
import ca.corefacility.bioinformatics.irida.exceptions.IridaWorkflowAnalysisTypeException;
import ca.corefacility.bioinformatics.irida.exceptions.IridaWorkflowLoadException;
import ca.corefacility.bioinformatics.irida.exceptions.IridaWorkflowNotFoundException;
import ca.corefacility.bioinformatics.irida.exceptions.WorkflowException;
import ca.corefacility.bioinformatics.irida.exceptions.galaxy.GalaxyDatasetNotFoundException;
import ca.corefacility.bioinformatics.irida.model.enums.AnalysisState;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.workflow.IridaWorkflow;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.Analysis;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.AnalysisPhylogenomicsPipeline;
import ca.corefacility.bioinformatics.irida.model.workflow.execution.galaxy.PreparedWorkflowGalaxy;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyHistoriesService;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyLibrariesService;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.integration.LocalGalaxy;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.integration.Util;
import ca.corefacility.bioinformatics.irida.repositories.analysis.submission.AnalysisSubmissionRepository;
import ca.corefacility.bioinformatics.irida.service.DatabaseSetupGalaxyITService;
import ca.corefacility.bioinformatics.irida.service.analysis.workspace.galaxy.AnalysisWorkspaceServiceGalaxy;
import ca.corefacility.bioinformatics.irida.service.workflow.IridaWorkflowsService;

import com.github.jmchilton.blend4j.galaxy.GalaxyInstance;
import com.github.jmchilton.blend4j.galaxy.HistoriesClient;
import com.github.jmchilton.blend4j.galaxy.LibrariesClient;
import com.github.jmchilton.blend4j.galaxy.ToolsClient;
import com.github.jmchilton.blend4j.galaxy.WorkflowsClient;
import com.github.jmchilton.blend4j.galaxy.beans.History;
import com.github.jmchilton.blend4j.galaxy.beans.Workflow;
import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;
import com.google.common.collect.Sets;

/**
 * Tests out preparing a workspace for execution of workflows in Galaxy.
 * 
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = { IridaApiGalaxyTestConfig.class })
@ActiveProfiles("test")
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DbUnitTestExecutionListener.class,
		WithSecurityContextTestExcecutionListener.class })
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

	private GalaxyHistoriesService galaxyHistoriesService;

	private Path sequenceFilePath;
	private Path referenceFilePath;

	private Set<SequenceFile> sequenceFilesSet;

	private static final UUID validWorkflowId = UUID.fromString("739f29ea-ae82-48b9-8914-3d2931405db6");
	private static final UUID phylogenomicsWorkflowId = UUID.fromString("1f9ea289-5053-4e4a-bc76-1f0c60b179f8");

	private static final String OUTPUT1_LABEL = "output1";
	private static final String OUTPUT2_LABEL = "output2";
	private static final String OUTPUT1_NAME = "output1.txt";
	private static final String OUTPUT2_NAME = "output2.txt";

	private static final String MATRIX_NAME = "snpMatrix.tsv";
	private static final String MATRIX_LABEL = "matrix";
	private static final String TREE_NAME = "phylogeneticTree.txt";
	private static final String TREE_LABEL = "tree";
	private static final String TABLE_NAME = "snpTable.tsv";
	private static final String TABLE_LABEL = "table";

	/**
	 * Sets up variables for testing.
	 * 
	 * @throws URISyntaxException
	 * @throws IOException
	 * @throws IridaWorkflowLoadException
	 */
	@Before
	public void setup() throws URISyntaxException, IOException, IridaWorkflowLoadException {
		Assume.assumeFalse(WindowsPlatformCondition.isWindows());

		Path sequenceFilePathReal = Paths
				.get(DatabaseSetupGalaxyITService.class.getResource("testData1.fastq").toURI());
		Path referenceFilePathReal = Paths.get(DatabaseSetupGalaxyITService.class.getResource("testReference.fasta")
				.toURI());

		sequenceFilePath = Files.createTempFile("testData1", ".fastq");
		Files.delete(sequenceFilePath);
		Files.copy(sequenceFilePathReal, sequenceFilePath);

		referenceFilePath = Files.createTempFile("testReference", ".fasta");
		Files.delete(referenceFilePath);
		Files.copy(referenceFilePathReal, referenceFilePath);

		sequenceFilesSet = Sets.newHashSet(new SequenceFile(sequenceFilePath));

		GalaxyInstance galaxyInstanceAdmin = localGalaxy.getGalaxyInstanceWorkflowUser();
		HistoriesClient historiesClient = galaxyInstanceAdmin.getHistoriesClient();
		ToolsClient toolsClient = galaxyInstanceAdmin.getToolsClient();
		LibrariesClient librariesClient = galaxyInstanceAdmin.getLibrariesClient();
		GalaxyLibrariesService galaxyLibrariesService = new GalaxyLibrariesService(librariesClient);

		galaxyHistoriesService = new GalaxyHistoriesService(historiesClient, toolsClient, galaxyLibrariesService);
	}

	/**
	 * Tests successfully preparing a workspace for analysis.
	 * 
	 * @throws IridaWorkflowNotFoundException
	 * @throws ExecutionManagerException
	 */
	@Test
	public void testPrepareAnalysisWorkspaceSuccess() throws IridaWorkflowNotFoundException, ExecutionManagerException {
		AnalysisSubmission submission = AnalysisSubmission.createSubmissionSingle("Name", sequenceFilesSet, validWorkflowId);
		assertNotNull(analysisWorkspaceService.prepareAnalysisWorkspace(submission));
	}

	/**
	 * Tests failure to prepare a workspace for analysis.
	 * 
	 * @throws IridaWorkflowNotFoundException
	 * @throws ExecutionManagerException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testPrepareAnalysisWorkspaceFail() throws IridaWorkflowNotFoundException, ExecutionManagerException {
		AnalysisSubmission submission = AnalysisSubmission.createSubmissionSingle("Name", sequenceFilesSet, validWorkflowId);
		submission.setRemoteAnalysisId("1");
		analysisWorkspaceService.prepareAnalysisWorkspace(submission);
	}

	/**
	 * Tests out successfully preparing workflow input files for execution.
	 * 
	 * @throws InterruptedException
	 * @throws ExecutionManagerException
	 * @throws IridaWorkflowNotFoundException
	 * @throws IOException
	 */
	@Test
	@WithMockUser(username = "aaron", roles = "ADMIN")
	public void testPrepareAnalysisFilesSuccess() throws InterruptedException, ExecutionManagerException,
			IridaWorkflowNotFoundException, IOException {

		History history = new History();
		history.setName("testPrepareAnalysisFilesSuccess");
		HistoriesClient historiesClient = localGalaxy.getGalaxyInstanceWorkflowUser().getHistoriesClient();
		WorkflowsClient workflowsClient = localGalaxy.getGalaxyInstanceWorkflowUser().getWorkflowsClient();
		History createdHistory = historiesClient.create(history);

		IridaWorkflow iridaWorkflow = iridaWorkflowsService.getIridaWorkflow(validWorkflowId);
		Path workflowPath = iridaWorkflow.getWorkflowStructure().getWorkflowFile();
		String workflowString = new String(Files.readAllBytes(workflowPath), StandardCharsets.UTF_8);
		Workflow galaxyWorkflow = workflowsClient.importWorkflow(workflowString);

		AnalysisSubmission analysisSubmission = analysisExecutionGalaxyITService.setupSubmissionInDatabase(1L,
				sequenceFilePath, referenceFilePath, validWorkflowId);
		analysisSubmission.setRemoteAnalysisId(createdHistory.getId());
		analysisSubmission.setRemoteWorkflowId(galaxyWorkflow.getId());

		PreparedWorkflowGalaxy preparedWorkflow = analysisWorkspaceService.prepareAnalysisFiles(analysisSubmission);
		assertEquals(createdHistory.getId(), preparedWorkflow.getRemoteAnalysisId());
		assertNotNull(preparedWorkflow.getWorkflowInputs());
	}

	/**
	 * Tests out failure to prepare workflow input files for execution.
	 * 
	 * @throws InterruptedException
	 * @throws ExecutionManagerException
	 * @throws IridaWorkflowNotFoundException
	 * @throws IOException
	 */
	@Test(expected = WorkflowException.class)
	@WithMockUser(username = "aaron", roles = "ADMIN")
	public void testPrepareAnalysisFilesFail() throws InterruptedException, ExecutionManagerException,
			IridaWorkflowNotFoundException, IOException {

		History history = new History();
		history.setName("testPrepareAnalysisFilesFail");
		HistoriesClient historiesClient = localGalaxy.getGalaxyInstanceWorkflowUser().getHistoriesClient();
		History createdHistory = historiesClient.create(history);

		AnalysisSubmission analysisSubmission = analysisExecutionGalaxyITService.setupSubmissionInDatabase(1L,
				sequenceFilePath, referenceFilePath, validWorkflowId);
		analysisSubmission.setRemoteAnalysisId(createdHistory.getId());
		analysisSubmission.setRemoteWorkflowId("invalid");

		PreparedWorkflowGalaxy preparedWorkflow = analysisWorkspaceService.prepareAnalysisFiles(analysisSubmission);
		assertEquals(createdHistory.getId(), preparedWorkflow.getRemoteAnalysisId());
		assertNotNull(preparedWorkflow.getWorkflowInputs());
	}

	private void uploadFileToHistory(Path filePath, String fileName, String historyId, ToolsClient toolsClient) {
		ToolsClient.FileUploadRequest uploadRequest = new ToolsClient.FileUploadRequest(historyId, filePath.toFile());
		uploadRequest.setDatasetName(fileName);
		toolsClient.upload(uploadRequest);
	}

	/**
	 * Tests out successfully getting results for an analysis (TestAnalysis).
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
	public void testGetAnalysisResultsTestAnalysisSuccess() throws InterruptedException, ExecutionManagerException,
			IridaWorkflowNotFoundException, IOException, IridaWorkflowAnalysisTypeException, TimeoutException {

		History history = new History();
		history.setName("testGetAnalysisResultsTestAnalysisSuccess");
		HistoriesClient historiesClient = localGalaxy.getGalaxyInstanceWorkflowUser().getHistoriesClient();
		WorkflowsClient workflowsClient = localGalaxy.getGalaxyInstanceWorkflowUser().getWorkflowsClient();
		ToolsClient toolsClient = localGalaxy.getGalaxyInstanceWorkflowUser().getToolsClient();

		History createdHistory = historiesClient.create(history);

		// upload test outputs
		uploadFileToHistory(sequenceFilePath, OUTPUT1_NAME, createdHistory.getId(), toolsClient);
		uploadFileToHistory(sequenceFilePath, OUTPUT2_NAME, createdHistory.getId(), toolsClient);

		// wait for history
		Util.waitUntilHistoryComplete(createdHistory.getId(), galaxyHistoriesService, 60);

		IridaWorkflow iridaWorkflow = iridaWorkflowsService.getIridaWorkflow(validWorkflowId);
		Path workflowPath = iridaWorkflow.getWorkflowStructure().getWorkflowFile();
		String workflowString = new String(Files.readAllBytes(workflowPath), StandardCharsets.UTF_8);
		Workflow galaxyWorkflow = workflowsClient.importWorkflow(workflowString);

		AnalysisSubmission analysisSubmission = analysisExecutionGalaxyITService.setupSubmissionInDatabase(1L,
				sequenceFilePath, referenceFilePath, validWorkflowId);
		analysisSubmission.setRemoteAnalysisId(createdHistory.getId());
		analysisSubmission.setRemoteWorkflowId(galaxyWorkflow.getId());
		analysisSubmission.setAnalysisState(AnalysisState.COMPLETING);
		analysisSubmissionRepository.save(analysisSubmission);

		Analysis analysis = analysisWorkspaceService.getAnalysisResults(analysisSubmission);
		assertNotNull(analysis);
		assertEquals(Analysis.class, analysis.getClass());
		assertEquals(2, analysis.getAnalysisOutputFiles().size());
		assertEquals(Paths.get(OUTPUT1_NAME), analysis.getAnalysisOutputFile(OUTPUT1_LABEL).getFile().getFileName());
		assertEquals(Paths.get(OUTPUT2_NAME), analysis.getAnalysisOutputFile(OUTPUT2_LABEL).getFile().getFileName());
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
		HistoriesClient historiesClient = localGalaxy.getGalaxyInstanceWorkflowUser().getHistoriesClient();
		WorkflowsClient workflowsClient = localGalaxy.getGalaxyInstanceWorkflowUser().getWorkflowsClient();
		ToolsClient toolsClient = localGalaxy.getGalaxyInstanceWorkflowUser().getToolsClient();

		History createdHistory = historiesClient.create(history);

		// upload test outputs
		uploadFileToHistory(sequenceFilePath, TABLE_NAME, createdHistory.getId(), toolsClient);
		uploadFileToHistory(sequenceFilePath, MATRIX_NAME, createdHistory.getId(), toolsClient);
		uploadFileToHistory(sequenceFilePath, TREE_NAME, createdHistory.getId(), toolsClient);

		// wait for history
		Util.waitUntilHistoryComplete(createdHistory.getId(), galaxyHistoriesService, 60);

		IridaWorkflow iridaWorkflow = iridaWorkflowsService.getIridaWorkflow(phylogenomicsWorkflowId);
		Path workflowPath = iridaWorkflow.getWorkflowStructure().getWorkflowFile();
		String workflowString = new String(Files.readAllBytes(workflowPath), StandardCharsets.UTF_8);
		Workflow galaxyWorkflow = workflowsClient.importWorkflow(workflowString);

		AnalysisSubmission analysisSubmission = analysisExecutionGalaxyITService.setupSubmissionInDatabase(1L,
				sequenceFilePath, referenceFilePath, phylogenomicsWorkflowId);
		analysisSubmission.setRemoteAnalysisId(createdHistory.getId());
		analysisSubmission.setRemoteWorkflowId(galaxyWorkflow.getId());
		analysisSubmission.setAnalysisState(AnalysisState.COMPLETING);
		analysisSubmissionRepository.save(analysisSubmission);

		Analysis analysis = analysisWorkspaceService.getAnalysisResults(analysisSubmission);
		assertNotNull(analysis);
		assertEquals(AnalysisPhylogenomicsPipeline.class, analysis.getClass());
		assertEquals(3, analysis.getAnalysisOutputFiles().size());
		assertEquals(Paths.get(TABLE_NAME), analysis.getAnalysisOutputFile(TABLE_LABEL).getFile().getFileName());
		assertEquals(Paths.get(MATRIX_NAME), analysis.getAnalysisOutputFile(MATRIX_LABEL).getFile().getFileName());
		assertEquals(Paths.get(TREE_NAME), analysis.getAnalysisOutputFile(TREE_LABEL).getFile().getFileName());
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
	@Test(expected = GalaxyDatasetNotFoundException.class)
	@WithMockUser(username = "aaron", roles = "ADMIN")
	public void testGetAnalysisResultsTestAnalysisFail() throws InterruptedException, ExecutionManagerException,
			IridaWorkflowNotFoundException, IOException, IridaWorkflowAnalysisTypeException, TimeoutException {

		History history = new History();
		history.setName("testGetAnalysisResultsTestAnalysisFail");
		HistoriesClient historiesClient = localGalaxy.getGalaxyInstanceWorkflowUser().getHistoriesClient();
		WorkflowsClient workflowsClient = localGalaxy.getGalaxyInstanceWorkflowUser().getWorkflowsClient();
		ToolsClient toolsClient = localGalaxy.getGalaxyInstanceWorkflowUser().getToolsClient();

		History createdHistory = historiesClient.create(history);

		// upload test outputs
		uploadFileToHistory(sequenceFilePath, OUTPUT1_NAME, createdHistory.getId(), toolsClient);
		// uploadFileToHistory(sequenceFilePath, OUTPUT2_NAME,
		// createdHistory.getId(), toolsClient);

		// wait for history
		Util.waitUntilHistoryComplete(createdHistory.getId(), galaxyHistoriesService, 60);

		IridaWorkflow iridaWorkflow = iridaWorkflowsService.getIridaWorkflow(validWorkflowId);
		Path workflowPath = iridaWorkflow.getWorkflowStructure().getWorkflowFile();
		String workflowString = new String(Files.readAllBytes(workflowPath), StandardCharsets.UTF_8);
		Workflow galaxyWorkflow = workflowsClient.importWorkflow(workflowString);

		AnalysisSubmission analysisSubmission = analysisExecutionGalaxyITService.setupSubmissionInDatabase(1L,
				sequenceFilePath, referenceFilePath, validWorkflowId);
		analysisSubmission.setRemoteAnalysisId(createdHistory.getId());
		analysisSubmission.setRemoteWorkflowId(galaxyWorkflow.getId());
		analysisSubmission.setAnalysisState(AnalysisState.COMPLETING);
		analysisSubmissionRepository.save(analysisSubmission);

		analysisWorkspaceService.getAnalysisResults(analysisSubmission);
	}
}
