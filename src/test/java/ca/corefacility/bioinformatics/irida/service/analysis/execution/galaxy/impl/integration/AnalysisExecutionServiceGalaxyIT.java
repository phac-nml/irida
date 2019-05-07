package ca.corefacility.bioinformatics.irida.service.analysis.execution.galaxy.impl.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithSecurityContextTestExecutionListener;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import com.github.jmchilton.blend4j.galaxy.GalaxyResponseException;
import com.github.jmchilton.blend4j.galaxy.WorkflowsClient;
import com.github.jmchilton.blend4j.galaxy.beans.WorkflowDetails;
import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import ca.corefacility.bioinformatics.irida.config.IridaApiGalaxyTestConfig;
import ca.corefacility.bioinformatics.irida.config.conditions.WindowsPlatformCondition;
import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.exceptions.ExecutionManagerException;
import ca.corefacility.bioinformatics.irida.exceptions.IridaWorkflowException;
import ca.corefacility.bioinformatics.irida.exceptions.IridaWorkflowNotFoundException;
import ca.corefacility.bioinformatics.irida.exceptions.NoSuchValueException;
import ca.corefacility.bioinformatics.irida.exceptions.WorkflowException;
import ca.corefacility.bioinformatics.irida.exceptions.galaxy.GalaxyDatasetNotFoundException;
import ca.corefacility.bioinformatics.irida.exceptions.galaxy.NoGalaxyHistoryException;
import ca.corefacility.bioinformatics.irida.exceptions.galaxy.WorkflowUploadException;
import ca.corefacility.bioinformatics.irida.model.enums.AnalysisCleanedState;
import ca.corefacility.bioinformatics.irida.model.enums.AnalysisState;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.Analysis;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.AnalysisOutputFile;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.ToolExecution;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.type.BuiltInAnalysisTypes;
import ca.corefacility.bioinformatics.irida.model.workflow.description.IridaWorkflowParameter;
import ca.corefacility.bioinformatics.irida.model.workflow.execution.galaxy.GalaxyWorkflowStatus;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.integration.LocalGalaxy;
import ca.corefacility.bioinformatics.irida.repositories.analysis.submission.AnalysisSubmissionRepository;
import ca.corefacility.bioinformatics.irida.service.AnalysisService;
import ca.corefacility.bioinformatics.irida.service.AnalysisSubmissionService;
import ca.corefacility.bioinformatics.irida.service.DatabaseSetupGalaxyITService;
import ca.corefacility.bioinformatics.irida.service.analysis.execution.AnalysisExecutionService;

/**
 * Tests out the analysis service for the Galaxy analyses.
 * 
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = { IridaApiGalaxyTestConfig.class })
@ActiveProfiles("test")
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DbUnitTestExecutionListener.class,
		WithSecurityContextTestExecutionListener.class })
@DatabaseSetup("/ca/corefacility/bioinformatics/irida/repositories/analysis/AnalysisRepositoryIT.xml")
@DatabaseTearDown("/ca/corefacility/bioinformatics/irida/test/integration/TableReset.xml")
public class AnalysisExecutionServiceGalaxyIT {
	
	private static final float DELTA = 0.000001f;

	private static final String CMD_LINE_PATTERN = "echo -e \"csv,1[^\"]+\" > (/.*?)+; echo \"output_tree\" > (/.*?)+; echo \"positions\" > (/.*?)+";
	
	// SNVPhyl keys
	private static final String MATRIX_KEY = "matrix";
	private static final String TREE_KEY = "tree";
	private static final String TABLE_KEY = "table";

	private static final Logger logger = LoggerFactory.getLogger(AnalysisExecutionServiceGalaxyIT.class);

	@Autowired
	private DatabaseSetupGalaxyITService analysisExecutionGalaxyITService;

	@Autowired
	private LocalGalaxy localGalaxy;

	@Autowired
	private AnalysisSubmissionRepository analysisSubmissionRepository;

	@Autowired
	private AnalysisSubmissionService analysisSubmissionService;

	@Autowired
	private AnalysisService analysisService;
	
	@Autowired
	private AnalysisExecutionService analysisExecutionService;

	@Autowired
	@Qualifier("rootTempDirectory")
	private Path rootTempDirectory;

	private WorkflowsClient workflowsClient;

	private Path sequenceFilePath;
	private Path sequenceFilePath2;
	private Path referenceFilePath;
	
	private List<Path> pairedPaths1;
	private List<Path> pairedPaths2;

	private Path expectedSnpMatrix;
	private Path expectedSnpTable;
	private Path expectedTree;
	private Path expectedOutputFile1;
	private Path expectedOutputFile2;
	
	private Path expectedContigs;
	private Path expectedAnnotations;
	private Path expectedAnnotationsLog;

	private UUID validIridaWorkflowId = UUID.fromString("c5f29cb2-1b68-4d34-9b93-609266af7551");
	private UUID invalidIridaWorkflowId = UUID.fromString("8ec369e8-1b39-4b9a-97a1-70ac1f6cc9e6");
	private UUID iridaPhylogenomicsWorkflowId = UUID.fromString("1f9ea289-5053-4e4a-bc76-1f0c60b179f8");
	private UUID iridaPhylogenomicsPairedWorkflowId = UUID.fromString("b8c3916c-846e-4a78-96a9-9630911257cd");
	private UUID iridaPhylogenomicsPairedParametersWorkflowId = UUID.fromString("23434bf8-e551-4efd-9957-e61c6f649f8b");
	private UUID iridaPhylogenomicsPairedMultiLeveledParametersWorkflowId = UUID.fromString("12734a7d-a0d7-4ede-9cc3-a76b1f8c14e7");
	private UUID iridaAssemblyAnnotationWorkflowId = UUID.fromString("8c438951-484a-48da-be2b-93b7d29aa2a3");
	private UUID iridaTestAnalysisWorkflowId = UUID.fromString("c5f29cb2-1b68-4d34-9b93-609266af7551");
	private UUID iridaWorkflowIdInvalidWorkflowFile = UUID.fromString("d54f1780-e6c9-472a-92dd-63520ec85967");
	private UUID iridaTestAnalysisWorkflowIdMissingOutput = UUID.fromString("63038f49-9f2c-4850-9de3-deb9eaf57512");

	/**
	 * Sets up variables for testing.
	 * 
	 * @throws URISyntaxException
	 * @throws IOException
	 * @throws IridaWorkflowNotFoundException
	 */
	@Before
	public void setup() throws URISyntaxException, IOException, IridaWorkflowNotFoundException {
		Assume.assumeFalse(WindowsPlatformCondition.isWindows());

		Path sequenceFilePathReal = Paths
				.get(DatabaseSetupGalaxyITService.class.getResource("testData1.fastq").toURI());
		Path referenceFilePathReal = Paths.get(DatabaseSetupGalaxyITService.class.getResource("testReference.fasta")
				.toURI());
		
		expectedOutputFile1 = Paths
				.get(DatabaseSetupGalaxyITService.class.getResource("output1.txt").toURI());
		
		expectedOutputFile2 = Paths
				.get(DatabaseSetupGalaxyITService.class.getResource("output2.txt").toURI());

		Path tempDir = Files.createTempDirectory(rootTempDirectory, "analysisExecutionTest");
		
		sequenceFilePath = tempDir.resolve("testData1_R1_001.fastq");
		Files.copy(sequenceFilePathReal, sequenceFilePath, StandardCopyOption.REPLACE_EXISTING);
		
		sequenceFilePath2 = tempDir.resolve("testData1_R2_001.fastq");
		Files.copy(sequenceFilePathReal, sequenceFilePath2, StandardCopyOption.REPLACE_EXISTING);

		referenceFilePath = Files.createTempFile("testReference", ".fasta");
		Files.copy(referenceFilePathReal, referenceFilePath, StandardCopyOption.REPLACE_EXISTING);

		expectedSnpMatrix = localGalaxy.getWorkflowCorePipelineTestMatrix();
		expectedSnpTable = localGalaxy.getWorkflowCorePipelineTestSnpTable();
		expectedTree = localGalaxy.getWorkflowCorePipelineTestTree();
		
		expectedContigs = Paths
				.get(DatabaseSetupGalaxyITService.class.getResource("contigs.fasta").toURI());
		expectedAnnotations = Paths
				.get(DatabaseSetupGalaxyITService.class.getResource("genome.gbk").toURI());
		expectedAnnotationsLog = Paths
				.get(DatabaseSetupGalaxyITService.class.getResource("prokka.log").toURI());
		
		pairedPaths1 = Lists.newArrayList();
		pairedPaths1.add(sequenceFilePath);
		pairedPaths2 = Lists.newArrayList();
		pairedPaths2.add(sequenceFilePath2);
		
		workflowsClient = localGalaxy.getGalaxyInstanceAdmin().getWorkflowsClient();
	}
	
	/**
	 * Tests out failing to get a workflow status.
	 * 
	 * @throws InterruptedException
	 * @throws NoSuchValueException
	 * @throws ExecutionManagerException
	 * @throws IridaWorkflowNotFoundException
	 * @throws IOException
	 * @throws ExecutionException
	 */
	@Test(expected=ExecutionManagerException.class)
	@WithMockUser(username = "aaron", roles = "ADMIN")
	public void testGetWorkflowStatusFail() throws InterruptedException, NoSuchValueException,
			IridaWorkflowNotFoundException, ExecutionManagerException, IOException, ExecutionException {
		AnalysisSubmission analysisSubmission = analysisExecutionGalaxyITService.setupSubmissionInDatabase(1L,
				sequenceFilePath, referenceFilePath, validIridaWorkflowId, AnalysisState.NEW, false);
		
		Future<AnalysisSubmission> analysisSubmittedFuture = analysisExecutionService
				.prepareSubmission(analysisSubmission);
		AnalysisSubmission analysisSubmitted = analysisSubmittedFuture.get();
		analysisSubmitted.setRemoteAnalysisId("invalid");

		analysisExecutionService.getWorkflowStatus(analysisSubmitted);
	}

	/**
	 * Tests out successfully submitting a workflow for execution.
	 * 
	 * @throws InterruptedException
	 * @throws NoSuchValueException
	 * @throws ExecutionManagerException
	 * @throws IOException
	 * @throws ExecutionException
	 * @throws IridaWorkflowException 
	 */
	@Test
	@WithMockUser(username = "aaron", roles = "ADMIN")
	public void testExecuteAnalysisSuccess() throws InterruptedException, NoSuchValueException,
			ExecutionManagerException, IOException, ExecutionException, IridaWorkflowException {
		AnalysisSubmission analysisSubmission = analysisExecutionGalaxyITService.setupSubmissionInDatabase(1L,
				sequenceFilePath, referenceFilePath, validIridaWorkflowId, false);

		Future<AnalysisSubmission> analysisSubmittedFuture = analysisExecutionService
				.prepareSubmission(analysisSubmission);
		AnalysisSubmission analysisSubmitted = analysisSubmittedFuture.get();

		Future<AnalysisSubmission> analysisExecutedFuture = analysisExecutionService
				.executeAnalysis(analysisSubmitted);
		AnalysisSubmission analysisExecuted = analysisExecutedFuture.get();

		assertEquals(AnalysisState.RUNNING, analysisExecuted.getAnalysisState());

		assertNotNull("remoteAnalysisId is null", analysisExecuted.getRemoteAnalysisId());
		assertNotNull("remoteInputDataId is null", analysisExecuted.getRemoteInputDataId());

		GalaxyWorkflowStatus status = analysisExecutionService.getWorkflowStatus(analysisExecuted);
		analysisExecutionGalaxyITService.assertValidStatus(status);
	}

	/**
	 * Tests out attempting to execute an analysis with an invalid remote
	 * workflow id.
	 * 
	 * @throws Throwable
	 * 
	 */
	@Test(expected = WorkflowException.class)
	@WithMockUser(username = "aaron", roles = "ADMIN")
	public void testExecuteAnalysisFailRemoteWorkflowId() throws Throwable {
		AnalysisSubmission analysisSubmission = analysisExecutionGalaxyITService.setupSubmissionInDatabase(1L,
				sequenceFilePath, referenceFilePath, validIridaWorkflowId, false);

		Future<AnalysisSubmission> analysisSubmittedFuture = analysisExecutionService
				.prepareSubmission(analysisSubmission);
		AnalysisSubmission analysisSubmitted = analysisSubmittedFuture.get();

		analysisSubmitted.setRemoteWorkflowId(localGalaxy.getInvalidWorkflowId());
		
		analysisSubmissionService.update(analysisSubmitted);

		Future<AnalysisSubmission> analysisExecutedFuture = analysisExecutionService
				.executeAnalysis(analysisSubmitted);

		try {
			analysisExecutedFuture.get();
		} catch (ExecutionException e) {
			// check to make sure the submission is in the error state
			AnalysisSubmission savedSubmission = analysisSubmissionRepository.findOne(analysisSubmitted.getId());
			assertEquals(AnalysisState.ERROR, savedSubmission.getAnalysisState());

			throw e.getCause();
		}
	}

	/**
	 * Tests out attempting to execute an analysis with an invalid remote
	 * analysis id.
	 * 
	 * @throws Throwable
	 */
	@Test(expected = NoGalaxyHistoryException.class)
	@WithMockUser(username = "aaron", roles = "ADMIN")
	public void testExecuteAnalysisFailRemoteAnalysisId() throws Throwable {
		AnalysisSubmission analysisSubmission = analysisExecutionGalaxyITService.setupSubmissionInDatabase(1L,
				sequenceFilePath, referenceFilePath, validIridaWorkflowId, false);

		Future<AnalysisSubmission> analysisSubmittedFuture = analysisExecutionService
				.prepareSubmission(analysisSubmission);
		
		AnalysisSubmission analysisSubmitted = analysisSubmittedFuture.get();

		analysisSubmitted.setRemoteAnalysisId("invalid");
		analysisSubmissionService.update(analysisSubmitted);

		Future<AnalysisSubmission> analysisExecutedFuture = analysisExecutionService
				.executeAnalysis(analysisSubmitted);

		try {
			analysisExecutedFuture.get();
		} catch (ExecutionException e) {
			// check to make sure the submission is in the error state
			AnalysisSubmission savedSubmission = analysisSubmissionRepository.findOne(analysisSubmitted.getId());
			logger.debug("Submission on exception=" + savedSubmission.getId());
			assertEquals(AnalysisState.ERROR, savedSubmission.getAnalysisState());

			throw e.getCause();
		}
	}
	
	/**
	 * Tests out attempting to execute an analysis with an invalid parameter value.
	 * 
	 * @throws Throwable
	 */
	@Test(expected = WorkflowException.class)
	@WithMockUser(username = "aaron", roles = "ADMIN")
	public void testExecuteAnalysisFailInvalidParameterValue() throws Throwable {
		Map<String, String> parameters = ImmutableMap.of("coverage", "not an integer for coverage");
		
		AnalysisSubmission analysisSubmission = analysisExecutionGalaxyITService
				.setupPairSubmissionInDatabase(1L, pairedPaths1, pairedPaths2, referenceFilePath, parameters,
						iridaPhylogenomicsPairedParametersWorkflowId, AnalysisState.NEW);

		Future<AnalysisSubmission> analysisSubmittedFuture = analysisExecutionService
				.prepareSubmission(analysisSubmission);
		AnalysisSubmission analysisSubmitted = analysisSubmittedFuture.get();

		Future<AnalysisSubmission> analysisExecutionFuture = analysisExecutionService
				.executeAnalysis(analysisSubmitted);

		try {
			analysisExecutionFuture.get();
		} catch (ExecutionException e) {
			// check to make sure the submission is in the error state
			AnalysisSubmission savedSubmission = analysisSubmissionRepository.findOne(analysisSubmitted.getId());
			logger.debug("Submission on exception=" + savedSubmission.getId());
			assertEquals(AnalysisState.ERROR, savedSubmission.getAnalysisState());

			throw e.getCause();
		}
	}

	/**
	 * Tests out attempting to execute an analysis with an invalid initial
	 * state.
	 * 
	 * @throws NoSuchValueException
	 * @throws ExecutionManagerException
	 * @throws IOException
	 * @throws IridaWorkflowException 
	 * @throws ExecutionException 
	 * @throws InterruptedException 
	 */
	@Test(expected = IllegalArgumentException.class)
	@WithMockUser(username = "aaron", roles = "ADMIN")
	public void testExecuteAnalysisFailState() throws NoSuchValueException, ExecutionManagerException, IOException, IridaWorkflowException, InterruptedException, ExecutionException {
		AnalysisSubmission analysisSubmission = analysisExecutionGalaxyITService.setupSubmissionInDatabase(1L,
				sequenceFilePath, referenceFilePath, validIridaWorkflowId, false);

		analysisExecutionService.prepareSubmission(analysisSubmission).get();
		
		AnalysisSubmission analysisSubmitted = analysisSubmissionRepository.findOne(analysisSubmission.getId());

		analysisSubmitted.setAnalysisState(AnalysisState.NEW);
		analysisExecutionService.executeAnalysis(analysisSubmitted);
	}

	/**
	 * Tests out successfully preparing a workflow submission.
	 * 
	 * @throws InterruptedException
	 * @throws NoSuchValueException
	 * @throws ExecutionManagerException
	 * @throws IOException
	 * @throws IridaWorkflowNotFoundException
	 * @throws ExecutionException
	 */
	@Test
	@WithMockUser(username = "aaron", roles = "ADMIN")
	public void testPrepareSubmissionSuccess() throws InterruptedException, NoSuchValueException,
			IridaWorkflowNotFoundException, IOException, ExecutionManagerException, ExecutionException {
		AnalysisSubmission analysisSubmission = analysisExecutionGalaxyITService.setupSubmissionInDatabase(1L,
				sequenceFilePath, referenceFilePath, validIridaWorkflowId, AnalysisState.NEW, false);
		
		Future<AnalysisSubmission> analysisSubmissionFuture = analysisExecutionService
				.prepareSubmission(analysisSubmission);
		AnalysisSubmission analysisSubmitted = analysisSubmissionFuture.get();
		assertEquals(AnalysisState.PREPARED, analysisSubmitted.getAnalysisState());

		AnalysisSubmission analysisSaved = analysisSubmissionRepository.findOne(analysisSubmission.getId());
		assertEquals(analysisSaved.getId(), analysisSubmitted.getId());
		assertNotNull("analysisSubmitted is null", analysisSaved);
		assertNotNull("remoteWorkflowId is null", analysisSaved.getRemoteWorkflowId());
		assertNotNull("remoteAnalysisId is null", analysisSaved.getRemoteAnalysisId());
		assertEquals(AnalysisState.PREPARED, analysisSaved.getAnalysisState());
	}

	/**
	 * Tests out attempting to prepare a workflow with an invalid id for
	 * execution.
	 * 
	 * @throws Throwable
	 */
	@Test(expected = IridaWorkflowNotFoundException.class)
	@WithMockUser(username = "aaron", roles = "ADMIN")
	public void testPrepareSubmissionFailInvalidWorkflow() throws Throwable {
		AnalysisSubmission analysisSubmission = analysisExecutionGalaxyITService.setupSubmissionInDatabase(1L,
				sequenceFilePath, referenceFilePath, invalidIridaWorkflowId, AnalysisState.NEW, false);

		Future<AnalysisSubmission> analysisSubmissionFuture = analysisExecutionService
				.prepareSubmission(analysisSubmission);
		try {
			analysisSubmissionFuture.get();
		} catch (ExecutionException e) {
			logger.debug("Submission on exception=" + analysisSubmissionService.read(analysisSubmission.getId()));
			assertEquals(AnalysisState.ERROR, analysisSubmissionService.read(analysisSubmission.getId())
					.getAnalysisState());

			// pull out real exception
			throw e.getCause();
		}
	}

	/**
	 * Tests out attempting to prepare a workflow with an invalid Galaxy
	 * workflow file.
	 * 
	 * @throws Throwable
	 */
	@Test(expected = WorkflowUploadException.class)
	@WithMockUser(username = "aaron", roles = "ADMIN")
	public void testPrepareSubmissionFailInvalidGalaxyWorkflowFile() throws Throwable {
		AnalysisSubmission analysisSubmission = analysisExecutionGalaxyITService.setupSubmissionInDatabase(1L,
				sequenceFilePath, referenceFilePath, iridaWorkflowIdInvalidWorkflowFile, AnalysisState.NEW, false);

		Future<AnalysisSubmission> analysisSubmissionFuture = analysisExecutionService
				.prepareSubmission(analysisSubmission);
		try {
			analysisSubmissionFuture.get();
		} catch (ExecutionException e) {
			logger.debug("Submission on exception=" + analysisSubmissionService.read(analysisSubmission.getId()));
			assertEquals(AnalysisState.ERROR, analysisSubmissionService.read(analysisSubmission.getId())
					.getAnalysisState());

			// pull out real exception
			throw e.getCause();
		}
	}

	/**
	 * Tests out getting analysis results successfully.
	 * 
	 * @throws Exception
	 */
	@Test
	@WithMockUser(username = "aaron", roles = "ADMIN")
	public void testTransferAnalysisResultsSuccessPhylogenomics() throws Exception {
		AnalysisSubmission analysisSubmission = analysisExecutionGalaxyITService.setupSubmissionInDatabase(1L,
				sequenceFilePath, referenceFilePath, iridaPhylogenomicsWorkflowId, false);

		Future<AnalysisSubmission> analysisSubmittedFuture = analysisExecutionService
				.prepareSubmission(analysisSubmission);
		AnalysisSubmission analysisSubmitted = analysisSubmittedFuture.get();
		float percentComplete = analysisSubmissionService.getPercentCompleteForAnalysisSubmission(analysisSubmitted.getId());
		assertEquals("percent complete is incorrect", 11.0f, percentComplete, DELTA);

		Future<AnalysisSubmission> analysisExecutionFuture = analysisExecutionService
				.executeAnalysis(analysisSubmitted);
		AnalysisSubmission analysisExecuted = analysisExecutionFuture.get();
		assertNotNull("remoteInputDataId is null", analysisExecuted.getRemoteInputDataId());
		String remoteInputDataId = analysisExecuted.getRemoteInputDataId();
		percentComplete = analysisSubmissionService.getPercentCompleteForAnalysisSubmission(analysisSubmitted.getId());
		assertTrue("percent complete is incorrect", 10.0f <= percentComplete && percentComplete <= 90.0f);

		analysisExecutionGalaxyITService.waitUntilSubmissionComplete(analysisExecuted);
		percentComplete = analysisSubmissionService.getPercentCompleteForAnalysisSubmission(analysisSubmitted.getId());
		assertEquals("percent complete is incorrect", 90.0f, percentComplete, DELTA);

		analysisExecuted.setAnalysisState(AnalysisState.FINISHED_RUNNING);
		percentComplete = analysisSubmissionService.getPercentCompleteForAnalysisSubmission(analysisSubmitted.getId());
		assertEquals("percent complete is incorrect", 90.0f, percentComplete, DELTA);
		
		Future<AnalysisSubmission> analysisSubmissionCompletedFuture = analysisExecutionService
				.transferAnalysisResults(analysisExecuted);
		AnalysisSubmission analysisSubmissionCompleted = analysisSubmissionCompletedFuture.get();
		AnalysisSubmission analysisSubmissionCompletedDatabase = analysisSubmissionService.read(analysisSubmission
				.getId());
		assertEquals(AnalysisState.COMPLETED, analysisSubmissionCompletedDatabase.getAnalysisState());
		assertEquals(AnalysisState.COMPLETED, analysisSubmissionCompleted.getAnalysisState());
		assertEquals("remoteInputDataId should be unchanged in the completed analysis", remoteInputDataId,
				analysisSubmissionCompletedDatabase.getRemoteInputDataId());
		percentComplete = analysisSubmissionService.getPercentCompleteForAnalysisSubmission(analysisSubmitted.getId());
		assertEquals("percent complete is incorrect", 100.0f, percentComplete, DELTA);

		Analysis analysisResults = analysisSubmissionCompleted.getAnalysis();
		Analysis analysisResultsDatabase = analysisSubmissionCompletedDatabase.getAnalysis();
		assertEquals("analysis results in returned submission and from database should be the same",
				analysisResults.getId(), analysisResultsDatabase.getId());

		assertEquals(BuiltInAnalysisTypes.PHYLOGENOMICS, analysisResults.getAnalysisType());

		String analysisId = analysisExecuted.getRemoteAnalysisId();
		assertEquals("id should be set properly for analysis", analysisId,
				analysisResultsDatabase.getExecutionManagerAnalysisId());

		assertEquals(3, analysisResultsDatabase.getAnalysisOutputFiles().size());
		AnalysisOutputFile phylogeneticTree = analysisResultsDatabase.getAnalysisOutputFile(TREE_KEY);
		AnalysisOutputFile snpMatrix = analysisResultsDatabase.getAnalysisOutputFile(MATRIX_KEY);
		AnalysisOutputFile snpTable = analysisResultsDatabase.getAnalysisOutputFile(TABLE_KEY);

		assertTrue("phylogenetic trees should be equal",
				com.google.common.io.Files.equal(expectedTree.toFile(), phylogeneticTree.getFile().toFile()));
		assertEquals(expectedTree.getFileName(), phylogeneticTree.getFile().getFileName());

		assertTrue("snp matrices should be correct",
				com.google.common.io.Files.equal(expectedSnpMatrix.toFile(), snpMatrix.getFile().toFile()));
		assertEquals(expectedSnpMatrix.getFileName(), snpMatrix.getFile().getFileName());

		assertTrue("snpTable should be correct",
				com.google.common.io.Files.equal(expectedSnpTable.toFile(), snpTable.getFile().toFile()));
		assertEquals(expectedSnpTable.getFileName(), snpTable.getFile().getFileName());

		AnalysisSubmission finalSubmission = analysisSubmissionRepository.findOne(analysisExecuted.getId());
		Analysis analysis = finalSubmission.getAnalysis();
		assertNotNull(analysis);

		Analysis savedAnalysisFromDatabase = analysisService.read(analysisResultsDatabase.getId());
		assertEquals(BuiltInAnalysisTypes.PHYLOGENOMICS, savedAnalysisFromDatabase.getAnalysisType());

		assertEquals("Analysis from submission and from database should be the same",
				savedAnalysisFromDatabase.getId(), analysis.getId());

		assertEquals(analysisResultsDatabase.getId(), savedAnalysisFromDatabase.getId());
		assertEquals(analysisResultsDatabase.getAnalysisOutputFile(TREE_KEY).getFile(), savedAnalysisFromDatabase
				.getAnalysisOutputFile(TREE_KEY).getFile());
		assertEquals(analysisResultsDatabase.getAnalysisOutputFile(MATRIX_KEY).getFile(), savedAnalysisFromDatabase.getAnalysisOutputFile(MATRIX_KEY).getFile());
		assertEquals(analysisResultsDatabase.getAnalysisOutputFile(TABLE_KEY).getFile(), savedAnalysisFromDatabase.getAnalysisOutputFile(TABLE_KEY).getFile());
	}
	
	/**
	 * Tests out getting analysis results successfully for phylogenomics
	 * pipeline (paired test version).
	 * 
	 * @throws Exception
	 */
	@Test
	@WithMockUser(username = "aaron", roles = "ADMIN")
	public void testTransferAnalysisResultsSuccessPhylogenomicsPaired() throws Exception {
		AnalysisSubmission analysisSubmission = analysisExecutionGalaxyITService.setupPairSubmissionInDatabase(1L,
				pairedPaths1, pairedPaths2, referenceFilePath, iridaPhylogenomicsPairedWorkflowId, false);

		Future<AnalysisSubmission> analysisSubmittedFuture = analysisExecutionService
				.prepareSubmission(analysisSubmission);
		AnalysisSubmission analysisSubmitted = analysisSubmittedFuture.get();

		Future<AnalysisSubmission> analysisExecutionFuture = analysisExecutionService
				.executeAnalysis(analysisSubmitted);
		AnalysisSubmission analysisExecuted = analysisExecutionFuture.get();

		analysisExecutionGalaxyITService.waitUntilSubmissionComplete(analysisExecuted);

		analysisExecuted.setAnalysisState(AnalysisState.FINISHED_RUNNING);
		Future<AnalysisSubmission> analysisSubmissionCompletedFuture = analysisExecutionService
				.transferAnalysisResults(analysisExecuted);
		AnalysisSubmission analysisSubmissionCompleted = analysisSubmissionCompletedFuture.get();
		AnalysisSubmission analysisSubmissionCompletedDatabase = analysisSubmissionService.read(analysisSubmission
				.getId());
		assertEquals("analysis state is not completed", AnalysisState.COMPLETED,
				analysisSubmissionCompletedDatabase.getAnalysisState());
		assertEquals("analysis state is not completed", AnalysisState.COMPLETED,
				analysisSubmissionCompleted.getAnalysisState());

		Analysis analysisResults = analysisSubmissionCompleted.getAnalysis();
		Analysis analysisResultsDatabase = analysisSubmissionCompletedDatabase.getAnalysis();
		assertEquals("analysis results in returned submission and from database should be the same",
				analysisResults.getId(), analysisResultsDatabase.getId());


		assertEquals(BuiltInAnalysisTypes.PHYLOGENOMICS, analysisResults.getAnalysisType());

		String analysisId = analysisExecuted.getRemoteAnalysisId();
		assertEquals("id should be set properly for analysis", analysisId,
				analysisResultsDatabase.getExecutionManagerAnalysisId());

		assertEquals("invalid number of output files", 3, analysisResultsDatabase.getAnalysisOutputFiles().size());
		AnalysisOutputFile phylogeneticTree = analysisResultsDatabase.getAnalysisOutputFile(TREE_KEY);
		AnalysisOutputFile snpMatrix = analysisResultsDatabase.getAnalysisOutputFile(MATRIX_KEY);
		AnalysisOutputFile snpTable = analysisResultsDatabase.getAnalysisOutputFile(TABLE_KEY);

		assertTrue("phylogenetic trees should be equal",
				com.google.common.io.Files.equal(expectedTree.toFile(), phylogeneticTree.getFile().toFile()));
		assertEquals("invalid file name for snp tree", expectedTree.getFileName(), phylogeneticTree.getFile()
				.getFileName());
		assertTrue(
				"command line (" + phylogeneticTree.getCreatedByTool().getCommandLine()
						+ ") should match the defined pattern (" + CMD_LINE_PATTERN + ") (phylogenetic tree).",
				phylogeneticTree.getCreatedByTool().getCommandLine().matches(CMD_LINE_PATTERN));
		final ToolExecution phyTreeCoreInputs = phylogeneticTree.getCreatedByTool();
		assertEquals("The first tool execution should be by core_pipeline_outputs_paired v0.1.0",
				"core_pipeline_outputs_paired", phyTreeCoreInputs.getToolName());
		assertEquals("The first tool execution should be by core_pipeline_outputs_paired v0.1.0", "0.1.0",
				phyTreeCoreInputs.getToolVersion());
		final ToolExecution phyTreeCoreUpload = phyTreeCoreInputs.getPreviousSteps().iterator().next();
		assertTrue("Second step should be input tool.", phyTreeCoreUpload.isInputTool());

		assertTrue("snp matrices should be correct",
				com.google.common.io.Files.equal(expectedSnpMatrix.toFile(), snpMatrix.getFile().toFile()));
		assertEquals("invalid file name for snp matrix", expectedSnpMatrix.getFileName(), snpMatrix.getFile()
				.getFileName());
		assertTrue(
				"command line (" + snpMatrix.getCreatedByTool().getCommandLine()
						+ ") should match the defined pattern (" + CMD_LINE_PATTERN + ") (snp matrix).",
				snpMatrix.getCreatedByTool().getCommandLine().matches(CMD_LINE_PATTERN));
		final ToolExecution snpMatrixCoreInputs = snpMatrix.getCreatedByTool();
		assertEquals("The first tool execution should be by core_pipeline_outputs_paired v0.1.0",
				"core_pipeline_outputs_paired", snpMatrixCoreInputs.getToolName());
		assertEquals("The first tool execution should be by core_pipeline_outputs_paired v0.1.0", "0.1.0",
				snpMatrixCoreInputs.getToolVersion());
		final ToolExecution snpMatrixCoreUpload = snpMatrixCoreInputs.getPreviousSteps().iterator().next();
		assertTrue("Second step should be input tool.", snpMatrixCoreUpload.isInputTool());

		assertTrue("snpTable should be correct",
				com.google.common.io.Files.equal(expectedSnpTable.toFile(), snpTable.getFile().toFile()));
		assertEquals("invalid file name for snp table", expectedSnpTable.getFileName(), snpTable.getFile()
				.getFileName());
		assertTrue(
				"command line (" + snpTable.getCreatedByTool().getCommandLine() + ") should match the defined pattern ("
						+ CMD_LINE_PATTERN + ") (snp table).",
				snpTable.getCreatedByTool().getCommandLine().matches(CMD_LINE_PATTERN));
		final ToolExecution snpTableCoreInputs = snpTable.getCreatedByTool();
		assertEquals("The first tool execution should be by core_pipeline_outputs_paired v0.1.0",
				"core_pipeline_outputs_paired", snpTableCoreInputs.getToolName());
		assertEquals("The first tool execution should be by core_pipeline_outputs_paired v0.1.0", "0.1.0",
				snpTableCoreInputs.getToolVersion());
		final ToolExecution snpTableCoreUpload = snpTableCoreInputs.getPreviousSteps().iterator().next();
		assertTrue("Second step should be input tool.", snpTableCoreUpload.isInputTool());

		AnalysisSubmission finalSubmission = analysisSubmissionRepository.findOne(analysisExecuted.getId());
		Analysis analysis = finalSubmission.getAnalysis();
		assertNotNull("analysis should not be null in submission", analysis);

		Analysis savedAnalysisFromDatabase = analysisService.read(analysisResultsDatabase.getId());
		assertEquals("saved analysis in submission is not correct class", BuiltInAnalysisTypes.PHYLOGENOMICS,
				savedAnalysisFromDatabase.getAnalysisType());

		assertEquals("Analysis from submission and from database should be the same",
				savedAnalysisFromDatabase.getId(), analysis.getId());

		assertEquals("analysis results from database and from submission should have correct id",
				analysisResultsDatabase.getId(), savedAnalysisFromDatabase.getId());
		assertEquals("analysis results from database and from submission should have correct tree output file",
				analysisResultsDatabase.getAnalysisOutputFile(TREE_KEY).getFile(), savedAnalysisFromDatabase.getAnalysisOutputFile(TREE_KEY)
						.getFile());
		assertEquals("analysis results from database and from submission should have correct matrix output file",
				analysisResultsDatabase.getAnalysisOutputFile(MATRIX_KEY).getFile(), savedAnalysisFromDatabase.getAnalysisOutputFile(MATRIX_KEY).getFile());
		assertEquals("analysis results from database and from submission should have correct table output file",
				analysisResultsDatabase.getAnalysisOutputFile(MATRIX_KEY).getFile(), savedAnalysisFromDatabase.getAnalysisOutputFile(MATRIX_KEY).getFile());
	}
	
	/**
	 * Tests out getting analysis results successfully for phylogenomics
	 * pipeline (paired test version with parameters).
	 * 
	 * @throws Exception
	 */
	@Test
	@WithMockUser(username = "aaron", roles = "ADMIN")
	public void testTransferAnalysisResultsSuccessPhylogenomicsPairedParameters() throws Exception {
		String validCoverage = "20";
		String validCoverageFromProvenance = "\"20\""; // coverage from
														// provenance has quotes
		String validMidCoverageFromProvenance = "20"; // this value does not have quotes around it in final results.
		Map<String, String> parameters = ImmutableMap.of("coverage", validCoverage);
		String validTreeFile = "20 20 20"; // I verify parameters were set
										// correctly by checking output file
										// (where parameters were printed).

		AnalysisSubmission analysisSubmission = analysisExecutionGalaxyITService
				.setupPairSubmissionInDatabase(1L, pairedPaths1, pairedPaths2, referenceFilePath, parameters,
						iridaPhylogenomicsPairedParametersWorkflowId);

		Future<AnalysisSubmission> analysisSubmittedFuture = analysisExecutionService
				.prepareSubmission(analysisSubmission);
		AnalysisSubmission analysisSubmitted = analysisSubmittedFuture.get();

		Future<AnalysisSubmission> analysisExecutionFuture = analysisExecutionService
				.executeAnalysis(analysisSubmitted);
		AnalysisSubmission analysisExecuted = analysisExecutionFuture.get();

		analysisExecutionGalaxyITService.waitUntilSubmissionComplete(analysisExecuted);

		analysisExecuted.setAnalysisState(AnalysisState.FINISHED_RUNNING);
		Future<AnalysisSubmission> analysisSubmissionCompletedFuture = analysisExecutionService
				.transferAnalysisResults(analysisExecuted);
		analysisSubmissionCompletedFuture.get();
		AnalysisSubmission analysisSubmissionCompletedDatabase = analysisSubmissionService.read(analysisSubmission
				.getId());
		assertEquals("analysis state is not completed", AnalysisState.COMPLETED,
				analysisSubmissionCompletedDatabase.getAnalysisState());

		Analysis analysisResults = analysisSubmissionCompletedDatabase.getAnalysis();

		assertEquals("analysis results is an invalid class", BuiltInAnalysisTypes.PHYLOGENOMICS,
				analysisResults.getAnalysisType());

		assertEquals("invalid number of output files", 3, analysisResults.getAnalysisOutputFiles().size());
		AnalysisOutputFile phylogeneticTree = analysisResults.getAnalysisOutputFile(TREE_KEY);
		AnalysisOutputFile snpMatrix = analysisResults.getAnalysisOutputFile(MATRIX_KEY);
		AnalysisOutputFile snpTable = analysisResults.getAnalysisOutputFile(TABLE_KEY);

		// verify parameters were set properly by checking contents of file
		@SuppressWarnings("resource")
		String treeContent = new Scanner(phylogeneticTree.getFile().toFile()).useDelimiter("\\Z").next();
		assertEquals("phylogenetic trees containing the parameters should be equal", validTreeFile, treeContent);

		// phy tree
		final ToolExecution phyTreeCoreInputs = phylogeneticTree.getCreatedByTool();
		assertEquals("The first tool execution should be by core_pipeline_outputs_paired_with_parameters v0.1.0",
				"core_pipeline_outputs_paired_with_parameters", phyTreeCoreInputs.getToolName());
		assertEquals("The first tool execution should be by core_pipeline_outputs_paired_with_parameters v0.1.0",
				"0.1.0", phyTreeCoreInputs.getToolVersion());
		Map<String, String> phyTreeCoreParameters = phyTreeCoreInputs.getExecutionTimeParameters();
		assertEquals("incorrect number of non-file parameters", 4, phyTreeCoreParameters.size());
		assertEquals("parameter coverageMin set incorrectly", validCoverageFromProvenance,
				phyTreeCoreParameters.get("coverageMin"));
		assertEquals("parameter coverageMid set incorrectly", validMidCoverageFromProvenance,
				phyTreeCoreParameters.get("conditional.coverageMid"));
		assertEquals("parameter coverageMax set incorrectly", validCoverageFromProvenance,
				phyTreeCoreParameters.get("coverageMax"));
		assertEquals("parameter conditional_select set incorrectly", "all",
				phyTreeCoreParameters.get("conditional.conditional_select"));

		Set<ToolExecution> phyTreeCorePreviousSteps = phyTreeCoreInputs.getPreviousSteps();
		assertEquals("there should exist 2 previous steps", 2, phyTreeCorePreviousSteps.size());
		Set<String> uploadedFileTypesPhy = Sets.newHashSet();
		for (ToolExecution previousStep : phyTreeCorePreviousSteps) {
			assertTrue("previous steps should be input tools.", previousStep.isInputTool());
			uploadedFileTypesPhy.add(previousStep.getExecutionTimeParameters().get("file_type"));
		}
		assertEquals("uploaded files should have correct types", Sets.newHashSet("\"fastqsanger\"", "\"fasta\""),
				uploadedFileTypesPhy);

		// snp matrix
		final ToolExecution matrixCoreInputs = snpMatrix.getCreatedByTool();
		assertEquals("The first tool execution should be by core_pipeline_outputs_paired_with_parameters v0.1.0",
				"core_pipeline_outputs_paired_with_parameters", matrixCoreInputs.getToolName());
		assertEquals("The first tool execution should be by core_pipeline_outputs_paired_with_parameters v0.1.0",
				"0.1.0", matrixCoreInputs.getToolVersion());
		Map<String, String> matrixCoreParameters = matrixCoreInputs.getExecutionTimeParameters();
		assertEquals("incorrect number of non-file parameters", 4, matrixCoreParameters.size());
		assertEquals("parameter coverageMin set incorrectly", validCoverageFromProvenance,
				matrixCoreParameters.get("coverageMin"));
		assertEquals("parameter coverageMid set incorrectly", validMidCoverageFromProvenance,
				phyTreeCoreParameters.get("conditional.coverageMid"));
		assertEquals("parameter coverageMax set incorrectly", validCoverageFromProvenance,
				matrixCoreParameters.get("coverageMax"));
		assertEquals("parameter conditional_select set incorrectly", "all",
				phyTreeCoreParameters.get("conditional.conditional_select"));

		Set<ToolExecution> matrixCorePreviousSteps = matrixCoreInputs.getPreviousSteps();
		assertEquals("there should exist 2 previous steps", 2, matrixCorePreviousSteps.size());
		Set<String> uploadedFileTypesMatrix = Sets.newHashSet();
		for (ToolExecution previousStep : matrixCorePreviousSteps) {
			assertTrue("previous steps should be input tools.", previousStep.isInputTool());
			uploadedFileTypesMatrix.add(previousStep.getExecutionTimeParameters().get("file_type"));
		}
		assertEquals("uploaded files should have correct types", Sets.newHashSet("\"fastqsanger\"", "\"fasta\""),
				uploadedFileTypesMatrix);

		// snp table
		final ToolExecution tableCoreInputs = snpTable.getCreatedByTool();
		assertEquals("The first tool execution should be by core_pipeline_outputs_paired_with_parameters v0.1.0",
				"core_pipeline_outputs_paired_with_parameters", tableCoreInputs.getToolName());
		assertEquals("The first tool execution should be by core_pipeline_outputs_paired_with_parameters v0.1.0",
				"0.1.0", tableCoreInputs.getToolVersion());
		Map<String, String> tableCoreParameters = tableCoreInputs.getExecutionTimeParameters();
		assertEquals("incorrect number of non-file parameters", 4, tableCoreParameters.size());
		assertEquals("parameter coverageMin set incorrectly", validCoverageFromProvenance,
				tableCoreParameters.get("coverageMin"));
		assertEquals("parameter coverageMid set incorrectly", validMidCoverageFromProvenance,
				phyTreeCoreParameters.get("conditional.coverageMid"));
		assertEquals("parameter coverageMax set incorrectly", validCoverageFromProvenance,
				tableCoreParameters.get("coverageMax"));
		assertEquals("parameter conditional_select set incorrectly", "all",
				phyTreeCoreParameters.get("conditional.conditional_select"));

		Set<ToolExecution> tablePreviousSteps = tableCoreInputs.getPreviousSteps();
		assertEquals("there should exist 2 previous steps", 2, tablePreviousSteps.size());
		Set<String> uploadedFileTypesTable = Sets.newHashSet();
		for (ToolExecution previousStep : tablePreviousSteps) {
			assertTrue("previous steps should be input tools.", previousStep.isInputTool());
			uploadedFileTypesTable.add(previousStep.getExecutionTimeParameters().get("file_type"));
		}
		assertEquals("uploaded files should have correct types", Sets.newHashSet("\"fastqsanger\"", "\"fasta\""),
				uploadedFileTypesTable);
	}

	/**
	 * Tests out getting analysis results successfully for phylogenomics
	 * pipeline (paired test version with no parameters, using defaults).
	 * 
	 * @throws Exception
	 */
	@Test
	@WithMockUser(username = "aaron", roles = "ADMIN")
	public void testTransferAnalysisResultsSuccessPhylogenomicsPairedNoParameters() throws Exception {
		String validCoverageFromProvenance = "\"10\"";
		String validMidCoverageFromProvenance = "10";
		String validTreeFile = "10 10 10"; // I verify parameters were set
										// correctly by checking output file
										// (where parameters were printed).

		AnalysisSubmission analysisSubmission = analysisExecutionGalaxyITService.setupPairSubmissionInDatabase(1L,
				pairedPaths1, pairedPaths2, referenceFilePath, iridaPhylogenomicsPairedParametersWorkflowId, false);

		Future<AnalysisSubmission> analysisSubmittedFuture = analysisExecutionService
				.prepareSubmission(analysisSubmission);
		AnalysisSubmission analysisSubmitted = analysisSubmittedFuture.get();

		Future<AnalysisSubmission> analysisExecutionFuture = analysisExecutionService
				.executeAnalysis(analysisSubmitted);
		AnalysisSubmission analysisExecuted = analysisExecutionFuture.get();

		analysisExecutionGalaxyITService.waitUntilSubmissionComplete(analysisExecuted);

		analysisExecuted.setAnalysisState(AnalysisState.FINISHED_RUNNING);
		Future<AnalysisSubmission> analysisSubmissionCompletedFuture = analysisExecutionService
				.transferAnalysisResults(analysisExecuted);
		analysisSubmissionCompletedFuture.get();
		AnalysisSubmission analysisSubmissionCompletedDatabase = analysisSubmissionService.read(analysisSubmission
				.getId());
		assertEquals("analysis state is not completed", AnalysisState.COMPLETED,
				analysisSubmissionCompletedDatabase.getAnalysisState());

		Analysis analysisResults = analysisSubmissionCompletedDatabase.getAnalysis();

		assertEquals("analysis results is an invalid class", BuiltInAnalysisTypes.PHYLOGENOMICS,
				analysisResults.getAnalysisType());

		assertEquals("invalid number of output files", 3, analysisResults.getAnalysisOutputFiles().size());
		AnalysisOutputFile phylogeneticTree = analysisResults.getAnalysisOutputFile(TREE_KEY);
		AnalysisOutputFile snpMatrix = analysisResults.getAnalysisOutputFile(MATRIX_KEY);
		AnalysisOutputFile snpTable = analysisResults.getAnalysisOutputFile(TABLE_KEY);

		// verify parameters were set properly by checking contents of file
		@SuppressWarnings("resource")
		String treeContent = new Scanner(phylogeneticTree.getFile().toFile()).useDelimiter("\\Z").next();
		assertEquals("phylogenetic trees containing the parameters should be equal", validTreeFile, treeContent);

		// phy tree
		final ToolExecution phyTreeCoreInputs = phylogeneticTree.getCreatedByTool();
		assertEquals("The first tool execution should be by core_pipeline_outputs_paired_with_parameters v0.1.0",
				"core_pipeline_outputs_paired_with_parameters", phyTreeCoreInputs.getToolName());
		assertEquals("The first tool execution should be by core_pipeline_outputs_paired_with_parameters v0.1.0",
				"0.1.0", phyTreeCoreInputs.getToolVersion());
		Map<String, String> phyTreeCoreParameters = phyTreeCoreInputs.getExecutionTimeParameters();
		assertEquals("incorrect number of non-file parameters", 4, phyTreeCoreParameters.size());
		assertEquals("parameter coverageMin set incorrectly", validCoverageFromProvenance,
				phyTreeCoreParameters.get("coverageMin"));
		assertEquals("parameter coverageMid set incorrectly", validMidCoverageFromProvenance,
				phyTreeCoreParameters.get("conditional.coverageMid"));
		assertEquals("parameter coverageMax set incorrectly", validCoverageFromProvenance,
				phyTreeCoreParameters.get("coverageMax"));
		assertEquals("parameter conditional_select set incorrectly", "all",
				phyTreeCoreParameters.get("conditional.conditional_select"));

		Set<ToolExecution> phyTreeCorePreviousSteps = phyTreeCoreInputs.getPreviousSteps();
		assertEquals("there should exist 2 previous steps", 2, phyTreeCorePreviousSteps.size());
		Set<String> uploadedFileTypesPhy = Sets.newHashSet();
		for (ToolExecution previousStep : phyTreeCorePreviousSteps) {
			assertTrue("previous steps should be input tools.", previousStep.isInputTool());
			uploadedFileTypesPhy.add(previousStep.getExecutionTimeParameters().get("file_type"));
		}
		assertEquals("uploaded files should have correct types", Sets.newHashSet("\"fastqsanger\"", "\"fasta\""),
				uploadedFileTypesPhy);

		// snp matrix
		final ToolExecution matrixCoreInputs = snpMatrix.getCreatedByTool();
		assertEquals("The first tool execution should be by core_pipeline_outputs_paired_with_parameters v0.1.0",
				"core_pipeline_outputs_paired_with_parameters", matrixCoreInputs.getToolName());
		assertEquals("The first tool execution should be by core_pipeline_outputs_paired_with_parameters v0.1.0",
				"0.1.0", matrixCoreInputs.getToolVersion());
		Map<String, String> matrixCoreParameters = matrixCoreInputs.getExecutionTimeParameters();
		assertEquals("incorrect number of non-file parameters", 4, matrixCoreParameters.size());
		assertEquals("parameter coverageMin set incorrectly", validCoverageFromProvenance,
				matrixCoreParameters.get("coverageMin"));
		assertEquals("parameter coverageMid set incorrectly", validMidCoverageFromProvenance,
				phyTreeCoreParameters.get("conditional.coverageMid"));
		assertEquals("parameter coverageMax set incorrectly", validCoverageFromProvenance,
				matrixCoreParameters.get("coverageMax"));
		assertEquals("parameter conditional_select set incorrectly", "all",
				phyTreeCoreParameters.get("conditional.conditional_select"));
		
		Set<ToolExecution> matrixCorePreviousSteps = matrixCoreInputs.getPreviousSteps();
		assertEquals("there should exist 2 previous steps", 2, matrixCorePreviousSteps.size());
		Set<String> uploadedFileTypesMatrix = Sets.newHashSet();
		for (ToolExecution previousStep : matrixCorePreviousSteps) {
			assertTrue("previous steps should be input tools.", previousStep.isInputTool());
			uploadedFileTypesMatrix.add(previousStep.getExecutionTimeParameters().get("file_type"));
		}
		assertEquals("uploaded files should have correct types", Sets.newHashSet("\"fastqsanger\"", "\"fasta\""),
				uploadedFileTypesMatrix);

		// snp table
		final ToolExecution tableCoreInputs = snpTable.getCreatedByTool();
		assertEquals("The first tool execution should be by core_pipeline_outputs_paired_with_parameters v0.1.0",
				"core_pipeline_outputs_paired_with_parameters", tableCoreInputs.getToolName());
		assertEquals("The first tool execution should be by core_pipeline_outputs_paired_with_parameters v0.1.0",
				"0.1.0", tableCoreInputs.getToolVersion());
		Map<String, String> tableCoreParameters = tableCoreInputs.getExecutionTimeParameters();
		assertEquals("incorrect number of non-file parameters", 4, tableCoreParameters.size());
		assertEquals("parameter coverageMin set incorrectly", validCoverageFromProvenance,
				tableCoreParameters.get("coverageMin"));
		assertEquals("parameter coverageMid set incorrectly", validMidCoverageFromProvenance,
				phyTreeCoreParameters.get("conditional.coverageMid"));
		assertEquals("parameter coverageMax set incorrectly", validCoverageFromProvenance,
				tableCoreParameters.get("coverageMax"));
		assertEquals("parameter conditional_select set incorrectly", "all",
				phyTreeCoreParameters.get("conditional.conditional_select"));
		
		Set<ToolExecution> tablePreviousSteps = tableCoreInputs.getPreviousSteps();
		assertEquals("there should exist 2 previous steps", 2, tablePreviousSteps.size());
		Set<String> uploadedFileTypesTable = Sets.newHashSet();
		for (ToolExecution previousStep : tablePreviousSteps) {
			assertTrue("previous steps should be input tools.", previousStep.isInputTool());
			uploadedFileTypesTable.add(previousStep.getExecutionTimeParameters().get("file_type"));
		}
		assertEquals("uploaded files should have correct types", Sets.newHashSet("\"fastqsanger\"", "\"fasta\""),
				uploadedFileTypesTable);
	}
	
	/**
	 * Tests out getting analysis results successfully for phylogenomics
	 * pipeline (paired test version and ignoring default parameters).
	 * 
	 * @throws Exception
	 */
	@Test
	@WithMockUser(username = "aaron", roles = "ADMIN")
	public void testTransferAnalysisResultsSuccessPhylogenomicsPairedParametersIgnoreDefaultValues() throws Exception {
		String validMinCoverageFromProvenance = "\"5\"";
		String validMidCoverageFromProvenance = "15";
		String validMaxCoverageFromProvenance = "\"20\"";
		Map<String, String> parameters = ImmutableMap.of("coverage", IridaWorkflowParameter.IGNORE_DEFAULT_VALUE);
		String validTreeFile = "5 15 20"; // I verify parameters were set
										// correctly by checking output file
										// (where parameters were printed).

		AnalysisSubmission analysisSubmission = analysisExecutionGalaxyITService
				.setupPairSubmissionInDatabase(1L, pairedPaths1, pairedPaths2, referenceFilePath, parameters,
						iridaPhylogenomicsPairedParametersWorkflowId);

		Future<AnalysisSubmission> analysisSubmittedFuture = analysisExecutionService
				.prepareSubmission(analysisSubmission);
		AnalysisSubmission analysisSubmitted = analysisSubmittedFuture.get();

		Future<AnalysisSubmission> analysisExecutionFuture = analysisExecutionService
				.executeAnalysis(analysisSubmitted);
		AnalysisSubmission analysisExecuted = analysisExecutionFuture.get();

		analysisExecutionGalaxyITService.waitUntilSubmissionComplete(analysisExecuted);

		analysisExecuted.setAnalysisState(AnalysisState.FINISHED_RUNNING);
		Future<AnalysisSubmission> analysisSubmissionCompletedFuture = analysisExecutionService
				.transferAnalysisResults(analysisExecuted);
		analysisSubmissionCompletedFuture.get();
		AnalysisSubmission analysisSubmissionCompletedDatabase = analysisSubmissionService.read(analysisSubmission
				.getId());
		assertEquals("analysis state is not completed", AnalysisState.COMPLETED,
				analysisSubmissionCompletedDatabase.getAnalysisState());

		Analysis analysisResults = analysisSubmissionCompletedDatabase.getAnalysis();

		assertEquals("analysis results is an invalid class", BuiltInAnalysisTypes.PHYLOGENOMICS,
				analysisResults.getAnalysisType());

		assertEquals("invalid number of output files", 3, analysisResults.getAnalysisOutputFiles().size());
		AnalysisOutputFile phylogeneticTree = analysisResults.getAnalysisOutputFile(TREE_KEY);
		AnalysisOutputFile snpMatrix = analysisResults.getAnalysisOutputFile(MATRIX_KEY);
		AnalysisOutputFile snpTable = analysisResults.getAnalysisOutputFile(TABLE_KEY);

		// verify parameters were set properly by checking contents of file
		@SuppressWarnings("resource")
		String treeContent = new Scanner(phylogeneticTree.getFile().toFile()).useDelimiter("\\Z").next();
		assertEquals("phylogenetic trees containing the parameters should be equal", validTreeFile, treeContent);

		// phy tree
		final ToolExecution phyTreeCoreInputs = phylogeneticTree.getCreatedByTool();
		assertEquals("The first tool execution should be by core_pipeline_outputs_paired_with_parameters v0.1.0",
				"core_pipeline_outputs_paired_with_parameters", phyTreeCoreInputs.getToolName());
		assertEquals("The first tool execution should be by core_pipeline_outputs_paired_with_parameters v0.1.0",
				"0.1.0", phyTreeCoreInputs.getToolVersion());
		Map<String, String> phyTreeCoreParameters = phyTreeCoreInputs.getExecutionTimeParameters();
		assertEquals("incorrect number of non-file parameters", 4, phyTreeCoreParameters.size());
		assertEquals("parameter coverageMin set incorrectly", validMinCoverageFromProvenance,
				phyTreeCoreParameters.get("coverageMin"));
		assertEquals("parameter coverageMid set incorrectly", validMidCoverageFromProvenance,
				phyTreeCoreParameters.get("conditional.coverageMid"));
		assertEquals("parameter coverageMax set incorrectly", validMaxCoverageFromProvenance,
				phyTreeCoreParameters.get("coverageMax"));
		assertEquals("parameter conditional_select set incorrectly", "all",
				phyTreeCoreParameters.get("conditional.conditional_select"));

		Set<ToolExecution> phyTreeCorePreviousSteps = phyTreeCoreInputs.getPreviousSteps();
		assertEquals("there should exist 2 previous steps", 2, phyTreeCorePreviousSteps.size());
		Set<String> uploadedFileTypesPhy = Sets.newHashSet();
		for (ToolExecution previousStep : phyTreeCorePreviousSteps) {
			assertTrue("previous steps should be input tools.", previousStep.isInputTool());
			uploadedFileTypesPhy.add(previousStep.getExecutionTimeParameters().get("file_type"));
		}
		assertEquals("uploaded files should have correct types", Sets.newHashSet("\"fastqsanger\"", "\"fasta\""),
				uploadedFileTypesPhy);

		// snp matrix
		final ToolExecution matrixCoreInputs = snpMatrix.getCreatedByTool();
		assertEquals("The first tool execution should be by core_pipeline_outputs_paired_with_parameters v0.1.0",
				"core_pipeline_outputs_paired_with_parameters", matrixCoreInputs.getToolName());
		assertEquals("The first tool execution should be by core_pipeline_outputs_paired_with_parameters v0.1.0",
				"0.1.0", matrixCoreInputs.getToolVersion());
		Map<String, String> matrixCoreParameters = matrixCoreInputs.getExecutionTimeParameters();
		assertEquals("incorrect number of non-file parameters", 4, matrixCoreParameters.size());
		assertEquals("parameter coverageMin set incorrectly", validMinCoverageFromProvenance,
				matrixCoreParameters.get("coverageMin"));
		assertEquals("parameter coverageMid set incorrectly", validMidCoverageFromProvenance,
				phyTreeCoreParameters.get("conditional.coverageMid"));
		assertEquals("parameter coverageMax set incorrectly", validMaxCoverageFromProvenance,
				matrixCoreParameters.get("coverageMax"));
		assertEquals("parameter conditional_select set incorrectly", "all",
				phyTreeCoreParameters.get("conditional.conditional_select"));

		Set<ToolExecution> matrixCorePreviousSteps = matrixCoreInputs.getPreviousSteps();
		assertEquals("there should exist 2 previous steps", 2, matrixCorePreviousSteps.size());
		Set<String> uploadedFileTypesMatrix = Sets.newHashSet();
		for (ToolExecution previousStep : matrixCorePreviousSteps) {
			assertTrue("previous steps should be input tools.", previousStep.isInputTool());
			uploadedFileTypesMatrix.add(previousStep.getExecutionTimeParameters().get("file_type"));
		}
		assertEquals("uploaded files should have correct types", Sets.newHashSet("\"fastqsanger\"", "\"fasta\""),
				uploadedFileTypesMatrix);

		// snp table
		final ToolExecution tableCoreInputs = snpTable.getCreatedByTool();
		assertEquals("The first tool execution should be by core_pipeline_outputs_paired_with_parameters v0.1.0",
				"core_pipeline_outputs_paired_with_parameters", tableCoreInputs.getToolName());
		assertEquals("The first tool execution should be by core_pipeline_outputs_paired_with_parameters v0.1.0",
				"0.1.0", tableCoreInputs.getToolVersion());
		Map<String, String> tableCoreParameters = tableCoreInputs.getExecutionTimeParameters();
		assertEquals("incorrect number of non-file parameters", 4, tableCoreParameters.size());
		assertEquals("parameter coverageMin set incorrectly", validMinCoverageFromProvenance,
				tableCoreParameters.get("coverageMin"));
		assertEquals("parameter coverageMid set incorrectly", validMidCoverageFromProvenance,
				phyTreeCoreParameters.get("conditional.coverageMid"));
		assertEquals("parameter coverageMax set incorrectly", validMaxCoverageFromProvenance,
				tableCoreParameters.get("coverageMax"));
		assertEquals("parameter conditional_select set incorrectly", "all",
				phyTreeCoreParameters.get("conditional.conditional_select"));

		Set<ToolExecution> tablePreviousSteps = tableCoreInputs.getPreviousSteps();
		assertEquals("there should exist 2 previous steps", 2, tablePreviousSteps.size());
		Set<String> uploadedFileTypesTable = Sets.newHashSet();
		for (ToolExecution previousStep : tablePreviousSteps) {
			assertTrue("previous steps should be input tools.", previousStep.isInputTool());
			uploadedFileTypesTable.add(previousStep.getExecutionTimeParameters().get("file_type"));
		}
		assertEquals("uploaded files should have correct types", Sets.newHashSet("\"fastqsanger\"", "\"fasta\""),
				uploadedFileTypesTable);
	}
	
	/**
	 * Tests out getting analysis results successfully for phylogenomics
	 * pipeline (paired test version with multiple levels of parameters).
	 * 
	 * @throws Exception
	 */
	@Test
	@WithMockUser(username = "aaron", roles = "ADMIN")
	public void testTransferAnalysisResultsSuccessPhylogenomicsPairedMultiLeveledParameters() throws Exception {
		String validCoverage = "20";
		String validCoverageFromProvenance = "\"20\""; // coverage from
														// provenance has quotes
		String validMidCoverageFromProvenance = "20"; // this value does not have quotes around it in final results.
		String validParameterValueFromProvenance = "20";
		Map<String, String> parameters = ImmutableMap.of("coverage", validCoverage);
		String validTreeFile = "20 20 20 20"; // I verify parameters were set
										// correctly by checking output file
										// (where parameters were printed).

		AnalysisSubmission analysisSubmission = analysisExecutionGalaxyITService
				.setupPairSubmissionInDatabase(1L, pairedPaths1, pairedPaths2, referenceFilePath, parameters,
						iridaPhylogenomicsPairedMultiLeveledParametersWorkflowId);
		
		Future<AnalysisSubmission> analysisSubmittedFuture = analysisExecutionService
				.prepareSubmission(analysisSubmission);
		AnalysisSubmission analysisSubmitted = analysisSubmittedFuture.get();

		Future<AnalysisSubmission> analysisExecutionFuture = analysisExecutionService
				.executeAnalysis(analysisSubmitted);
		AnalysisSubmission analysisExecuted = analysisExecutionFuture.get();

		analysisExecutionGalaxyITService.waitUntilSubmissionComplete(analysisExecuted);

		analysisExecuted.setAnalysisState(AnalysisState.FINISHED_RUNNING);
		Future<AnalysisSubmission> analysisSubmissionCompletedFuture = analysisExecutionService
				.transferAnalysisResults(analysisExecuted);
		analysisSubmissionCompletedFuture.get();
		AnalysisSubmission analysisSubmissionCompletedDatabase = analysisSubmissionService.read(analysisSubmission
				.getId());
		assertEquals("analysis state is not completed", AnalysisState.COMPLETED,
				analysisSubmissionCompletedDatabase.getAnalysisState());

		Analysis analysisResults = analysisSubmissionCompletedDatabase.getAnalysis();

		assertEquals("analysis results is an invalid class", BuiltInAnalysisTypes.PHYLOGENOMICS,
				analysisResults.getAnalysisType());

		assertEquals("invalid number of output files", 3, analysisResults.getAnalysisOutputFiles().size());
		AnalysisOutputFile phylogeneticTree = analysisResults.getAnalysisOutputFile(TREE_KEY);

		// verify parameters were set properly by checking contents of file
		@SuppressWarnings("resource")
		String treeContent = new Scanner(phylogeneticTree.getFile().toFile()).useDelimiter("\\Z").next();
		assertEquals("phylogenetic trees containing the parameters should be equal", validTreeFile, treeContent);

		// phy tree
		final ToolExecution phyTreeCoreInputs = phylogeneticTree.getCreatedByTool();
		assertEquals("The first tool execution should be by core_pipeline_outputs_paired_with_multi_level_parameters v0.1.0",
				"core_pipeline_outputs_paired_with_multi_level_parameters", phyTreeCoreInputs.getToolName());
		assertEquals("The first tool execution should be by core_pipeline_outputs_paired_with_multi_level_parameters v0.1.0",
				"0.1.0", phyTreeCoreInputs.getToolVersion());
		Map<String, String> phyTreeCoreParameters = phyTreeCoreInputs.getExecutionTimeParameters();
		assertEquals("incorrect number of non-file parameters", 6, phyTreeCoreParameters.size());
		assertEquals("parameter coverageMin set incorrectly", validCoverageFromProvenance,
				phyTreeCoreParameters.get("coverageMin"));
		assertEquals("parameter coverageMid set incorrectly", validMidCoverageFromProvenance,
				phyTreeCoreParameters.get("conditional.coverageMid"));
		assertEquals("parameter 'parameter' set incorrectly", validParameterValueFromProvenance,
				phyTreeCoreParameters.get("conditional.level2.parameter"));
		assertEquals("parameter coverageMax set incorrectly", validCoverageFromProvenance,
				phyTreeCoreParameters.get("coverageMax"));
		assertEquals("parameter conditional_select set incorrectly", "all",
				phyTreeCoreParameters.get("conditional.conditional_select"));
		assertEquals("parameter conditional_select set incorrectly", "all2",
				phyTreeCoreParameters.get("conditional.level2.level2_select"));
	}
	
	/**
	 * Tests out getting analysis results successfully for assembly and annotation pipeline (test version).
	 * 
	 * @throws Exception
	 */
	@Test
	@WithMockUser(username = "aaron", roles = "ADMIN")
	public void testTransferAnalysisResultsSuccessAssemblyAnnotation() throws Exception {
		AnalysisSubmission analysisSubmission = analysisExecutionGalaxyITService.setupPairSubmissionInDatabase(1L,
				pairedPaths1, pairedPaths2, iridaAssemblyAnnotationWorkflowId);

		Future<AnalysisSubmission> analysisSubmittedFuture = analysisExecutionService
				.prepareSubmission(analysisSubmission);
		AnalysisSubmission analysisSubmitted = analysisSubmittedFuture.get();

		Future<AnalysisSubmission> analysisExecutionFuture = analysisExecutionService
				.executeAnalysis(analysisSubmitted);
		AnalysisSubmission analysisExecuted = analysisExecutionFuture.get();

		analysisExecutionGalaxyITService.waitUntilSubmissionComplete(analysisExecuted);

		analysisExecuted.setAnalysisState(AnalysisState.FINISHED_RUNNING);
		Future<AnalysisSubmission> analysisSubmissionCompletedFuture = analysisExecutionService
				.transferAnalysisResults(analysisExecuted);
		AnalysisSubmission analysisSubmissionCompleted = analysisSubmissionCompletedFuture.get();
		AnalysisSubmission analysisSubmissionCompletedDatabase = analysisSubmissionService.read(analysisSubmission
				.getId());
		assertEquals("analysis state is not completed", AnalysisState.COMPLETED,
				analysisSubmissionCompletedDatabase.getAnalysisState());
		assertEquals("analysis state is not completed", AnalysisState.COMPLETED,
				analysisSubmissionCompleted.getAnalysisState());

		Analysis analysisResults = analysisSubmissionCompleted.getAnalysis();
		Analysis analysisResultsDatabase = analysisSubmissionCompletedDatabase.getAnalysis();
		assertEquals("analysis results in returned submission and from database should be the same",
				analysisResults.getId(), analysisResultsDatabase.getId());

		String analysisId = analysisExecuted.getRemoteAnalysisId();
		assertEquals("id should be set properly for analysis", analysisId,
				analysisResultsDatabase.getExecutionManagerAnalysisId());

		assertEquals("invalid number of output files", 3, analysisResultsDatabase.getAnalysisOutputFiles().size());
		AnalysisOutputFile contigs = analysisResultsDatabase.getAnalysisOutputFile("contigs");
		AnalysisOutputFile annotations = analysisResultsDatabase.getAnalysisOutputFile("annotations-genbank");
		AnalysisOutputFile prokkaLog = analysisResultsDatabase.getAnalysisOutputFile("annotations-log");

		assertTrue("contigs should be equal",
				com.google.common.io.Files.equal(expectedContigs.toFile(), contigs.getFile().toFile()));
		assertEquals("invalid file name for contigs", expectedContigs.getFileName(), contigs.getFile()
				.getFileName());

		assertTrue("annotations should be correct",
				com.google.common.io.Files.equal(expectedAnnotations.toFile(), annotations.getFile().toFile()));
		assertEquals("invalid file name for annotations", expectedAnnotations.getFileName(), annotations.getFile()
				.getFileName());

		assertTrue("annotations log should be correct",
				com.google.common.io.Files.equal(expectedAnnotationsLog.toFile(), prokkaLog.getFile().toFile()));
		assertEquals("invalid file name for annotations log", expectedAnnotationsLog.getFileName(), prokkaLog.getFile()
				.getFileName());
	}
	
	/**
	 * Tests out getting analysis results successfully.
	 * 
	 * @throws Exception
	 */
	@Test
	@WithMockUser(username = "aaron", roles = "ADMIN")
	public void testTransferAnalysisResultsSuccessTestAnalysis() throws Exception {
		AnalysisSubmission analysisSubmission = analysisExecutionGalaxyITService.setupSubmissionInDatabase(1L,
				sequenceFilePath, referenceFilePath, iridaTestAnalysisWorkflowId, false);
		
		Future<AnalysisSubmission> analysisSubmittedFuture = analysisExecutionService
				.prepareSubmission(analysisSubmission);
		AnalysisSubmission analysisSubmitted = analysisSubmittedFuture.get();

		Future<AnalysisSubmission> analysisExecutionFuture = analysisExecutionService
				.executeAnalysis(analysisSubmitted);
		AnalysisSubmission analysisExecuted = analysisExecutionFuture.get();

		analysisExecutionGalaxyITService.waitUntilSubmissionComplete(analysisExecuted);

		analysisExecuted.setAnalysisState(AnalysisState.FINISHED_RUNNING);
		Future<AnalysisSubmission> analysisSubmissionCompletedFuture = analysisExecutionService
				.transferAnalysisResults(analysisExecuted);
		AnalysisSubmission analysisSubmissionCompleted = analysisSubmissionCompletedFuture.get();
		AnalysisSubmission analysisSubmissionCompletedDatabase = analysisSubmissionService.read(analysisSubmission
				.getId());
		assertEquals(AnalysisState.COMPLETED, analysisSubmissionCompletedDatabase.getAnalysisState());
		assertEquals(AnalysisState.COMPLETED, analysisSubmissionCompleted.getAnalysisState());

		Analysis analysisResults = analysisSubmissionCompleted.getAnalysis();
		Analysis analysisResultsDatabase = analysisSubmissionCompletedDatabase.getAnalysis();
		assertEquals("analysis results in returned submission and from database should be the same",
				analysisResults.getId(), analysisResultsDatabase.getId());

		assertEquals(Analysis.class, analysisResults.getClass());

		String analysisId = analysisExecuted.getRemoteAnalysisId();
		assertEquals("id should be set properly for analysis", analysisId,
				analysisResults.getExecutionManagerAnalysisId());

		assertEquals(2, analysisResults.getAnalysisOutputFiles().size());
		AnalysisOutputFile output1 = analysisResultsDatabase.getAnalysisOutputFile("output1");
		AnalysisOutputFile output2 = analysisResultsDatabase.getAnalysisOutputFile("output2");

		assertTrue("output files 1 should be equal",
				com.google.common.io.Files.equal(expectedOutputFile1.toFile(), output1.getFile().toFile()));
		assertEquals(expectedOutputFile1.getFileName(), output1.getFile().getFileName());

		assertTrue("output files 2 should be equal",
				com.google.common.io.Files.equal(expectedOutputFile2.toFile(), output2.getFile().toFile()));
		assertEquals(expectedOutputFile2.getFileName(), output2.getFile().getFileName());

		AnalysisSubmission finalSubmission = analysisSubmissionRepository.findOne(analysisExecuted.getId());
		Analysis analysis = finalSubmission.getAnalysis();
		assertNotNull(analysis);

		Analysis savedAnalysisFromDatabase = analysisService.read(analysisResults.getId());
		assertTrue(savedAnalysisFromDatabase instanceof Analysis);
		Analysis savedTest = (Analysis) savedAnalysisFromDatabase;

		assertEquals("Analysis from submission and from database should be the same",
				savedAnalysisFromDatabase.getId(), analysis.getId());

		assertEquals(analysisResults.getId(), savedTest.getId());
	}
	
	/**
	 * Tests failure to get analysis results due to a missing output file.
	 * @throws Throwable 
	 */
	@Test(expected=GalaxyDatasetNotFoundException.class)
	@WithMockUser(username = "aaron", roles = "ADMIN")
	public void testTransferAnalysisResultsFailTestAnalysisMissingOutput() throws Throwable {
		AnalysisSubmission analysisSubmission = analysisExecutionGalaxyITService.setupSubmissionInDatabase(1L,
				sequenceFilePath, referenceFilePath, iridaTestAnalysisWorkflowIdMissingOutput, false);

		Future<AnalysisSubmission> analysisSubmittedFuture = analysisExecutionService
				.prepareSubmission(analysisSubmission);
		AnalysisSubmission analysisSubmitted = analysisSubmittedFuture.get();

		Future<AnalysisSubmission> analysisExecutionFuture = analysisExecutionService
				.executeAnalysis(analysisSubmitted);
		AnalysisSubmission analysisExecuted = analysisExecutionFuture.get();

		analysisExecutionGalaxyITService.waitUntilSubmissionComplete(analysisExecuted);

		analysisExecuted.setAnalysisState(AnalysisState.FINISHED_RUNNING);
		Future<AnalysisSubmission> analysisSubmissionCompletedFuture = analysisExecutionService
				.transferAnalysisResults(analysisExecuted);
		try {
			analysisSubmissionCompletedFuture.get();
		} catch (ExecutionException e) {
			logger.debug("Submission on exception=" + analysisSubmissionService.read(analysisSubmission.getId()));
			assertEquals(AnalysisState.ERROR, analysisSubmissionService.read(analysisSubmission.getId())
					.getAnalysisState());

			// pull out real exception
			throw e.getCause();
		}
	}

	/**
	 * Tests out failing to get analysis results due to analysis submission
	 * having an invalid id (not submitted).
	 * 
	 * @throws Throwable
	 */
	@Test(expected = EntityNotFoundException.class)
	@WithMockUser(username = "aaron", roles = "ADMIN")
	public void testTransferAnalysisResultsFailInvalidId() throws Throwable {
		AnalysisSubmission analysisSubmission = analysisExecutionGalaxyITService.setupSubmissionInDatabase(1L,
				sequenceFilePath, referenceFilePath, validIridaWorkflowId, false);

		Future<AnalysisSubmission> analysisSubmittedFuture = analysisExecutionService
				.prepareSubmission(analysisSubmission);
		AnalysisSubmission analysisSubmitted = analysisSubmittedFuture.get();

		Future<AnalysisSubmission> analysisExecutionFuture = analysisExecutionService
				.executeAnalysis(analysisSubmitted);
		AnalysisSubmission analysisExecuted = analysisExecutionFuture.get();

		analysisExecutionGalaxyITService.waitUntilSubmissionComplete(analysisExecuted);

		analysisExecuted.setId(555L);
		analysisExecuted.setAnalysisState(AnalysisState.FINISHED_RUNNING);
		Future<AnalysisSubmission> analysisSubmissionCompletedFuture = analysisExecutionService
				.transferAnalysisResults(analysisExecuted);
		try {
			analysisSubmissionCompletedFuture.get();
		} catch (ExecutionException e) {
			logger.debug("Submission on exception=" + analysisSubmissionService.read(analysisSubmission.getId()));
			assertEquals(AnalysisState.ERROR, analysisSubmissionService.read(analysisSubmission.getId())
					.getAnalysisState());

			// pull out real exception
			throw e.getCause();
		}
	}

	/**
	 * Tests out failing to get analysis results due to analysis submission
	 * having an invalid remote analysis id (submission not existing in Galaxy).
	 * 
	 * @throws Throwable
	 */
	@Test(expected = GalaxyResponseException.class)
	@WithMockUser(username = "aaron", roles = "ADMIN")
	public void testTransferAnalysisResultsFailInvalidRemoteAnalysisId() throws Throwable {
		AnalysisSubmission analysisSubmission = analysisExecutionGalaxyITService.setupSubmissionInDatabase(1L,
				sequenceFilePath, referenceFilePath, validIridaWorkflowId, false);

		Future<AnalysisSubmission> analysisSubmittedFuture = analysisExecutionService
				.prepareSubmission(analysisSubmission);
		AnalysisSubmission analysisSubmitted = analysisSubmittedFuture.get();

		Future<AnalysisSubmission> analysisExecutionFuture = analysisExecutionService
				.executeAnalysis(analysisSubmitted);
		AnalysisSubmission analysisExecuted = analysisExecutionFuture.get();

		analysisExecutionGalaxyITService.waitUntilSubmissionComplete(analysisExecuted);

		analysisExecuted.setRemoteAnalysisId("invalid");
		analysisExecuted = analysisSubmissionService.update(analysisExecuted);
		analysisExecuted.setAnalysisState(AnalysisState.FINISHED_RUNNING);
		Future<AnalysisSubmission> analysisSubmissionCompletedFuture = analysisExecutionService
				.transferAnalysisResults(analysisExecuted);
		try {
			analysisSubmissionCompletedFuture.get();
		} catch (ExecutionException e) {
			logger.debug("Submission on exception=" + analysisSubmissionService.read(analysisSubmission.getId()));
			assertEquals(AnalysisState.ERROR, analysisSubmissionService.read(analysisSubmission.getId())
					.getAnalysisState());

			// pull out real exception
			throw e.getCause();
		}
	}

	/**
	 * Tests out cleaning up a completed analysis successfully.
	 * 
	 * @throws Exception
	 */
	@Test
	@WithMockUser(username = "aaron", roles = "ADMIN")
	public void testCleanupCompletedAnalysisSuccess() throws Exception {
		AnalysisSubmission analysisSubmission = analysisExecutionGalaxyITService.setupSubmissionInDatabase(1L,
				sequenceFilePath, referenceFilePath, iridaTestAnalysisWorkflowId, false);

		Future<AnalysisSubmission> analysisSubmittedFuture = analysisExecutionService
				.prepareSubmission(analysisSubmission);
		AnalysisSubmission analysisSubmitted = analysisSubmittedFuture.get();

		Future<AnalysisSubmission> analysisExecutionFuture = analysisExecutionService
				.executeAnalysis(analysisSubmitted);
		AnalysisSubmission analysisExecuted = analysisExecutionFuture.get();

		analysisExecutionGalaxyITService.waitUntilSubmissionComplete(analysisExecuted);

		analysisExecuted.setAnalysisState(AnalysisState.FINISHED_RUNNING);
		Future<AnalysisSubmission> analysisSubmissionCompletedFuture = analysisExecutionService
				.transferAnalysisResults(analysisExecuted);
		AnalysisSubmission analysisSubmissionCompleted = analysisSubmissionCompletedFuture.get();
		assertEquals(AnalysisState.COMPLETED, analysisSubmissionCompleted.getAnalysisState());
		assertEquals(AnalysisCleanedState.NOT_CLEANED, analysisSubmissionCompleted.getAnalysisCleanedState());

		WorkflowDetails workflowDetails = workflowsClient.showWorkflow(analysisSubmissionCompleted
				.getRemoteWorkflowId());
		assertFalse("Workflow is already deleted", workflowDetails.isDeleted());

		// Once analysis is complete, attempt to clean up
		Future<AnalysisSubmission> analysisSubmissionCleanedFuture = analysisExecutionService
				.cleanupSubmission(analysisSubmissionCompleted);
		AnalysisSubmission analysisSubmissionCleaned = analysisSubmissionCleanedFuture.get();
		assertEquals("Analysis submission not properly cleaned", AnalysisCleanedState.CLEANED,
				analysisSubmissionCleaned.getAnalysisCleanedState());

		workflowDetails = workflowsClient.showWorkflow(analysisSubmissionCompleted.getRemoteWorkflowId());
		assertTrue("Workflow is not deleted", workflowDetails.isDeleted());
	}

	/**
	 * Tests out cleaning up an analysis in error successfully.
	 * 
	 * @throws Exception
	 */
	@Test
	@WithMockUser(username = "aaron", roles = "ADMIN")
	public void testCleanupErrorAnalysisSuccess() throws Exception {
		AnalysisSubmission analysisSubmission = analysisExecutionGalaxyITService.setupSubmissionInDatabase(1L,
				sequenceFilePath, referenceFilePath, iridaTestAnalysisWorkflowId, false);
		
		Future<AnalysisSubmission> analysisSubmittedFuture = analysisExecutionService
				.prepareSubmission(analysisSubmission);
		AnalysisSubmission analysisSubmitted = analysisSubmittedFuture.get();

		analysisSubmitted.setAnalysisState(AnalysisState.ERROR);
		analysisSubmissionRepository.save(analysisSubmitted);

		WorkflowDetails workflowDetails = workflowsClient.showWorkflow(analysisSubmitted.getRemoteWorkflowId());
		assertFalse("Workflow is already deleted", workflowDetails.isDeleted());

		// Once analysis is complete, attempt to clean up
		Future<AnalysisSubmission> analysisSubmissionCleanedFuture = analysisExecutionService
				.cleanupSubmission(analysisSubmitted);
		AnalysisSubmission analysisSubmissionCleaned = analysisSubmissionCleanedFuture.get();
		assertEquals("Analysis submission not properly cleaned", AnalysisCleanedState.CLEANED,
				analysisSubmissionCleaned.getAnalysisCleanedState());

		workflowDetails = workflowsClient.showWorkflow(analysisSubmitted.getRemoteWorkflowId());
		assertTrue("Workflow is not deleted", workflowDetails.isDeleted());
	}

	/**
	 * Tests out cleaning up an analysis that's already been cleaned and
	 * failing.
	 * 
	 * @throws Exception
	 */
	@Test(expected = IllegalArgumentException.class)
	@WithMockUser(username = "aaron", roles = "ADMIN")
	public void testCleanupCleanedAnalysisError() throws Exception {
		AnalysisSubmission analysisSubmission = analysisExecutionGalaxyITService.setupSubmissionInDatabase(1L,
				sequenceFilePath, referenceFilePath, iridaTestAnalysisWorkflowId, false);

		Future<AnalysisSubmission> analysisSubmittedFuture = analysisExecutionService
				.prepareSubmission(analysisSubmission);
		AnalysisSubmission analysisSubmitted = analysisSubmittedFuture.get();

		analysisSubmitted.setAnalysisState(AnalysisState.ERROR);
		analysisSubmissionRepository.save(analysisSubmitted);

		Future<AnalysisSubmission> analysisSubmissionCleanedFuture = analysisExecutionService
				.cleanupSubmission(analysisSubmitted);
		AnalysisSubmission cleanedSubmission = analysisSubmissionCleanedFuture.get();

		analysisExecutionService.cleanupSubmission(cleanedSubmission);
	}

	/**
	 * Tests out cleaning up a completed analysis and failing due to a Galaxy
	 * exception.
	 * 
	 * @throws Throwable
	 */
	@Test(expected = ExecutionManagerException.class)
	@WithMockUser(username = "aaron", roles = "ADMIN")
	public void testCleanupCompletedAnalysisFailGalaxy() throws Throwable {
		AnalysisSubmission analysisSubmission = analysisExecutionGalaxyITService.setupSubmissionInDatabase(1L,
				sequenceFilePath, referenceFilePath, iridaTestAnalysisWorkflowId, false);

		Future<AnalysisSubmission> analysisSubmittedFuture = analysisExecutionService
				.prepareSubmission(analysisSubmission);
		AnalysisSubmission analysisSubmitted = analysisSubmittedFuture.get();

		Future<AnalysisSubmission> analysisExecutionFuture = analysisExecutionService
				.executeAnalysis(analysisSubmitted);
		AnalysisSubmission analysisExecuted = analysisExecutionFuture.get();

		analysisExecutionGalaxyITService.waitUntilSubmissionComplete(analysisExecuted);

		analysisExecuted.setAnalysisState(AnalysisState.FINISHED_RUNNING);
		Future<AnalysisSubmission> analysisSubmissionCompletedFuture = analysisExecutionService
				.transferAnalysisResults(analysisExecuted);
		AnalysisSubmission analysisSubmissionCompleted = analysisSubmissionCompletedFuture.get();
		assertEquals(AnalysisState.COMPLETED, analysisSubmissionCompleted.getAnalysisState());
		assertEquals(AnalysisCleanedState.NOT_CLEANED, analysisSubmissionCompleted.getAnalysisCleanedState());

		analysisSubmissionCompleted.setRemoteAnalysisId("invalid");
		analysisSubmissionRepository.save(analysisSubmissionCompleted);

		// Once analysis is complete, attempt to clean up
		Future<AnalysisSubmission> analysisSubmissionCleanedFuture = analysisExecutionService
				.cleanupSubmission(analysisSubmissionCompleted);

		try {
			analysisSubmissionCleanedFuture.get();
			fail("No exception thrown");
		} catch (ExecutionException e) {
			assertEquals("The AnalysisState was changed from COMPLETED", AnalysisState.COMPLETED,
					analysisSubmissionService.read(analysisSubmission.getId()).getAnalysisState());
			assertEquals("The AnalysisCleanedState was not changed to error", AnalysisCleanedState.CLEANING_ERROR,
					analysisSubmissionService.read(analysisSubmission.getId()).getAnalysisCleanedState());

			// pull out real exception
			throw e.getCause();
		}
	}

	/**
	 * Tests out cleaning up an analysis in error and failing due to an error in
	 * Galaxy.
	 * 
	 * @throws Throwable
	 */
	@Test(expected = ExecutionManagerException.class)
	@WithMockUser(username = "aaron", roles = "ADMIN")
	public void testCleanupErrorAnalysisFailGalaxy() throws Throwable {
		AnalysisSubmission analysisSubmission = analysisExecutionGalaxyITService.setupSubmissionInDatabase(1L,
				sequenceFilePath, referenceFilePath, iridaTestAnalysisWorkflowId, AnalysisState.NEW, false);
		
		Future<AnalysisSubmission> analysisSubmittedFuture = analysisExecutionService
				.prepareSubmission(analysisSubmission);
		AnalysisSubmission analysisSubmitted = analysisSubmittedFuture.get();

		analysisSubmitted.setAnalysisState(AnalysisState.ERROR);
		analysisSubmitted.setRemoteWorkflowId("invalid");
		analysisSubmissionRepository.save(analysisSubmitted);

		// Once analysis is complete, attempt to clean up
		Future<AnalysisSubmission> analysisSubmissionCleanedFuture = analysisExecutionService
				.cleanupSubmission(analysisSubmitted);
		try {
			analysisSubmissionCleanedFuture.get();
			fail("No exception thrown");
		} catch (ExecutionException e) {
			assertEquals("The AnalysisState was changed from ERROR", AnalysisState.ERROR, analysisSubmissionService
					.read(analysisSubmission.getId()).getAnalysisState());
			assertEquals("The AnalysisCleanedState was not changed to error", AnalysisCleanedState.CLEANING_ERROR,
					analysisSubmissionService.read(analysisSubmission.getId()).getAnalysisCleanedState());

			// pull out real exception
			throw e.getCause();
		}
	}
}
