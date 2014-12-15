package ca.corefacility.bioinformatics.irida.service.analysis.execution.galaxy.impl.integration;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

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
import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.exceptions.ExecutionManagerException;
import ca.corefacility.bioinformatics.irida.exceptions.ExecutionManagerObjectNotFoundException;
import ca.corefacility.bioinformatics.irida.exceptions.IridaWorkflowNotFoundException;
import ca.corefacility.bioinformatics.irida.exceptions.NoSuchValueException;
import ca.corefacility.bioinformatics.irida.exceptions.WorkflowException;
import ca.corefacility.bioinformatics.irida.model.enums.AnalysisState;
import ca.corefacility.bioinformatics.irida.model.workflow.IridaWorkflow;
import ca.corefacility.bioinformatics.irida.model.workflow.WorkflowStatus;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.Analysis;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.AnalysisOutputFile;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.AnalysisPhylogenomicsPipeline;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.TestAnalysis;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisExecutionWorker;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.integration.LocalGalaxy;
import ca.corefacility.bioinformatics.irida.repositories.analysis.submission.AnalysisSubmissionRepository;
import ca.corefacility.bioinformatics.irida.service.AnalysisService;
import ca.corefacility.bioinformatics.irida.service.AnalysisSubmissionService;
import ca.corefacility.bioinformatics.irida.service.DatabaseSetupGalaxyITService;
import ca.corefacility.bioinformatics.irida.service.analysis.execution.AnalysisExecutionServiceSimplified;
import ca.corefacility.bioinformatics.irida.service.workflow.IridaWorkflowsService;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;

/**
 * Tests out the analysis service for the Galaxy analyses.
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
public class AnalysisExecutionServiceGalaxySimplifiedIT {

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
	private AnalysisExecutionServiceSimplified analysisExecutionServiceSimplified;

	@Autowired
	private IridaWorkflowsService iridaWorkflowsService;

	private Path sequenceFilePath;
	private Path referenceFilePath;

	private Path expectedSnpMatrix;
	private Path expectedSnpTable;
	private Path expectedTree;

	private UUID validIridaWorkflowId;
	private UUID invalidIridaWorkflowId = UUID.fromString("8ec369e8-1b39-4b9a-97a1-70ac1f6cc9e6");
	private UUID iridaPhylogenomicsWorkflowId;

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

		sequenceFilePath = Files.createTempFile("testData1", ".fastq");
		Files.delete(sequenceFilePath);
		Files.copy(sequenceFilePathReal, sequenceFilePath);

		referenceFilePath = Files.createTempFile("testReference", ".fasta");
		Files.delete(referenceFilePath);
		Files.copy(referenceFilePathReal, referenceFilePath);

		expectedSnpMatrix = localGalaxy.getWorkflowCorePipelineTestMatrix();
		expectedSnpTable = localGalaxy.getWorkflowCorePipelineTestSnpTable();
		expectedTree = localGalaxy.getWorkflowCorePipelineTestTree();

		IridaWorkflow iridaWorkflow = iridaWorkflowsService.getDefaultWorkflow(TestAnalysis.class);
		validIridaWorkflowId = iridaWorkflow.getWorkflowIdentifier();

		IridaWorkflow iridaPhylogenomicsWorkflow = iridaWorkflowsService
				.getDefaultWorkflow(AnalysisPhylogenomicsPipeline.class);
		iridaPhylogenomicsWorkflowId = iridaPhylogenomicsWorkflow.getWorkflowIdentifier();
	}

	/**
	 * Tests out successfully submitting a workflow for execution.
	 * 
	 * @throws InterruptedException
	 * @throws NoSuchValueException 
	 * @throws ExecutionManagerException 
	 * @throws IridaWorkflowNotFoundException 
	 */
	@Test
	@WithMockUser(username = "aaron", roles = "ADMIN")
	public void testExecuteAnalysisSuccess() throws InterruptedException, NoSuchValueException, IridaWorkflowNotFoundException, ExecutionManagerException {
		AnalysisSubmission analysisSubmission = analysisExecutionGalaxyITService.setupSubmissionInDatabase(1L,
				sequenceFilePath, referenceFilePath, validIridaWorkflowId);

		analysisSubmission.setAnalysisState(AnalysisState.PREPARING);
		AnalysisExecutionWorker preparationWorker = analysisExecutionServiceSimplified
				.prepareSubmission(analysisSubmission);
		AnalysisSubmission analysisSubmitted = preparationWorker.getResult();

		analysisSubmitted.setAnalysisState(AnalysisState.SUBMITTING);
		AnalysisSubmission analysisExecuted = analysisExecutionServiceSimplified
				.executeAnalysis(analysisSubmitted);
		assertNotNull("analysisExecuted is null", analysisExecuted);
		assertNotNull("remoteAnalysisId is null", analysisExecuted.getRemoteAnalysisId());

		WorkflowStatus status = analysisExecutionServiceSimplified.getWorkflowStatus(analysisExecuted);
		analysisExecutionGalaxyITService.assertValidStatus(status);

		AnalysisSubmission savedSubmission = analysisSubmissionRepository.findOne(analysisExecuted.getId());

		assertEquals(analysisExecuted.getRemoteAnalysisId(), savedSubmission.getRemoteAnalysisId());
		assertEquals(analysisExecuted.getRemoteWorkflowId(), savedSubmission.getRemoteWorkflowId());
		assertEquals(analysisExecuted.getWorkflowId(), savedSubmission.getWorkflowId());
		assertEquals(analysisExecuted.getInputFiles(), savedSubmission.getInputFiles());
		assertEquals(analysisExecuted.getReferenceFile(), savedSubmission.getReferenceFile());
	}

	/**
	 * Tests out attempting to execute an analysis with an invalid remote
	 * workflow id.
	 * 
	 * @throws IOException
	 * @throws IridaWorkflowNotFoundException
	 * @throws InterruptedException 
	 * @throws NoSuchValueException 
	 * @throws IllegalArgumentException
	 */
	@Test(expected = WorkflowException.class)
	@WithMockUser(username = "aaron", roles = "ADMIN")
	public void testExecuteAnalysisFailRemoteWorkflowId() throws ExecutionManagerException,
			IridaWorkflowNotFoundException, IOException, InterruptedException, NoSuchValueException {
		AnalysisSubmission analysisSubmission = analysisExecutionGalaxyITService.setupSubmissionInDatabase(1L,
				sequenceFilePath, referenceFilePath, validIridaWorkflowId);

		analysisSubmission.setAnalysisState(AnalysisState.PREPARING);
		AnalysisExecutionWorker preparationWorker = analysisExecutionServiceSimplified
				.prepareSubmission(analysisSubmission);
		AnalysisSubmission analysisSubmitted = preparationWorker.getResult();

		analysisSubmitted.setAnalysisState(AnalysisState.SUBMITTING);
		analysisSubmitted.setRemoteWorkflowId(localGalaxy.getInvalidWorkflowId());
		analysisExecutionServiceSimplified.executeAnalysis(analysisSubmitted);
	}

	/**
	 * Tests out attempting to execute an analysis with an invalid remote
	 * analysis id.
	 * 
	 * @throws IOException
	 * @throws IridaWorkflowNotFoundException
	 * @throws InterruptedException 
	 * @throws NoSuchValueException 
	 * @throws IllegalArgumentException
	 */
	@Test(expected = ExecutionManagerObjectNotFoundException.class)
	@WithMockUser(username = "aaron", roles = "ADMIN")
	public void testExecuteAnalysisFailRemoteAnalysisId() throws ExecutionManagerException,
			IridaWorkflowNotFoundException, IOException, InterruptedException, NoSuchValueException {
		AnalysisSubmission analysisSubmission = analysisExecutionGalaxyITService.setupSubmissionInDatabase(1L,
				sequenceFilePath, referenceFilePath, validIridaWorkflowId);

		analysisSubmission.setAnalysisState(AnalysisState.PREPARING);
		AnalysisExecutionWorker preparationWorker = analysisExecutionServiceSimplified
				.prepareSubmission(analysisSubmission);
		AnalysisSubmission analysisSubmitted = preparationWorker.getResult();

		analysisSubmitted.setAnalysisState(AnalysisState.SUBMITTING);
		analysisSubmitted.setRemoteAnalysisId("invalid");
		analysisExecutionServiceSimplified.executeAnalysis(analysisSubmitted);
	}

	/**
	 * Tests out attempting to execute an analysis with an invalid initial
	 * state.
	 * 
	 * @throws IOException
	 * @throws IridaWorkflowNotFoundException
	 * @throws InterruptedException 
	 * @throws NoSuchValueException 
	 * @throws IllegalArgumentException
	 */
	@Test(expected = IllegalArgumentException.class)
	@WithMockUser(username = "aaron", roles = "ADMIN")
	public void testExecuteAnalysisFailState() throws ExecutionManagerException, IridaWorkflowNotFoundException,
			IOException, InterruptedException, NoSuchValueException {
		AnalysisSubmission analysisSubmission = analysisExecutionGalaxyITService.setupSubmissionInDatabase(1L,
				sequenceFilePath, referenceFilePath, validIridaWorkflowId);

		analysisSubmission.setAnalysisState(AnalysisState.PREPARING);
		AnalysisExecutionWorker preparationWorker = analysisExecutionServiceSimplified
				.prepareSubmission(analysisSubmission);
		AnalysisSubmission analysisSubmitted = preparationWorker.getResult();

		analysisSubmitted.setAnalysisState(AnalysisState.NEW);
		analysisExecutionServiceSimplified.executeAnalysis(analysisSubmitted);
	}

	/**
	 * Tests out successfully preparing a workflow submission.
	 * @throws InterruptedException 
	 * @throws NoSuchValueException 
	 */
	@Test
	@WithMockUser(username = "aaron", roles = "ADMIN")
	public void testPrepareSubmissionSuccess() throws InterruptedException, NoSuchValueException {
		AnalysisSubmission analysisSubmission = analysisExecutionGalaxyITService.setupSubmissionInDatabase(1L,
				sequenceFilePath, referenceFilePath, validIridaWorkflowId);

		analysisSubmission.setAnalysisState(AnalysisState.PREPARING);
		AnalysisExecutionWorker preparationWorker = analysisExecutionServiceSimplified
				.prepareSubmission(analysisSubmission);
		AnalysisSubmission analysisSubmitted = preparationWorker.getResult();
		assertNotNull("analysisSubmitted is null", analysisSubmitted);
		assertNotNull("remoteWorkflowId is null", analysisSubmitted.getRemoteWorkflowId());
		assertNotNull("remoteAnalysisId is null", analysisSubmitted.getRemoteAnalysisId());
	}

	/**
	 * Tests out attempting to prepare a workflow with an invalid id for
	 * execution.
	 * @throws InterruptedException 
	 */
	@Test
	@WithMockUser(username = "aaron", roles = "ADMIN")
	public void testPrepareSubmissionFailInvalidWorkflow() throws InterruptedException {
		AnalysisSubmission analysisSubmission = analysisExecutionGalaxyITService.setupSubmissionInDatabase(1L,
				sequenceFilePath, referenceFilePath, invalidIridaWorkflowId);

		analysisSubmission.setAnalysisState(AnalysisState.PREPARING);
		AnalysisExecutionWorker preparationWorker = analysisExecutionServiceSimplified
				.prepareSubmission(analysisSubmission);
		assertTrue(preparationWorker.exceptionOccured());
		assertEquals(IridaWorkflowNotFoundException.class, preparationWorker.getException().getClass());
	}

	/**
	 * Tests out getting analysis results successfully.
	 * 
	 * @throws Exception
	 */
	@Test
	@WithMockUser(username = "aaron", roles = "ADMIN")
	public void testGetAnalysisResultsSuccessPhylogenomics() throws Exception {
		AnalysisSubmission analysisSubmission = analysisExecutionGalaxyITService.setupSubmissionInDatabase(1L,
				sequenceFilePath, referenceFilePath, iridaPhylogenomicsWorkflowId);

		analysisSubmission.setAnalysisState(AnalysisState.PREPARING);
		AnalysisExecutionWorker preparationWorker = analysisExecutionServiceSimplified
				.prepareSubmission(analysisSubmission);
		assertFalse("An exception occured " + preparationWorker.getException(), preparationWorker.exceptionOccured());
		AnalysisSubmission analysisSubmitted = preparationWorker.getResult();

		analysisSubmitted.setAnalysisState(AnalysisState.SUBMITTING);
		AnalysisSubmission analysisExecuted = analysisExecutionServiceSimplified
				.executeAnalysis(analysisSubmitted);

		analysisExecutionGalaxyITService.waitUntilSubmissionCompleteSimplified(analysisExecuted);

		analysisExecuted.setAnalysisState(AnalysisState.FINISHED_RUNNING);
		Analysis analysisResults = analysisExecutionServiceSimplified.transferAnalysisResults(analysisExecuted);
		assertEquals(AnalysisPhylogenomicsPipeline.class, analysisResults.getClass());
		AnalysisPhylogenomicsPipeline analysisResultsPhylogenomics = (AnalysisPhylogenomicsPipeline) analysisResults;

		String analysisId = analysisExecuted.getRemoteAnalysisId();
		assertEquals("id should be set properly for analysis", analysisId,
				analysisResultsPhylogenomics.getExecutionManagerAnalysisId());

		assertEquals("inputFiles should be the same for submission and results", analysisExecuted.getInputFiles(),
				analysisResultsPhylogenomics.getInputSequenceFiles());

		assertEquals(3, analysisResultsPhylogenomics.getAnalysisOutputFiles().size());
		AnalysisOutputFile phylogeneticTree = analysisResultsPhylogenomics.getPhylogeneticTree();
		AnalysisOutputFile snpMatrix = analysisResultsPhylogenomics.getSnpMatrix();
		AnalysisOutputFile snpTable = analysisResultsPhylogenomics.getSnpTable();

		assertTrue("phylogenetic trees should be equal",
				com.google.common.io.Files.equal(expectedTree.toFile(), phylogeneticTree.getFile().toFile()));
		assertEquals(expectedTree.getFileName(), phylogeneticTree.getFile().getFileName());

		assertTrue("snp matrices should be correct",
				com.google.common.io.Files.equal(expectedSnpMatrix.toFile(), snpMatrix.getFile().toFile()));
		assertEquals(expectedSnpMatrix.getFileName(), snpMatrix.getFile().getFileName());

		assertTrue("snpTable should be correct",
				com.google.common.io.Files.equal(expectedSnpTable.toFile(), snpTable.getFile().toFile()));
		assertEquals(expectedSnpTable.getFileName(), snpTable.getFile().getFileName());

		AnalysisSubmission finalSubmission = analysisSubmissionRepository.getByType(analysisExecuted.getId(),
				AnalysisSubmission.class);
		Analysis analysis = finalSubmission.getAnalysis();
		assertNotNull(analysis);

		Analysis savedAnalysisFromDatabase = analysisService.read(analysisResultsPhylogenomics.getId());
		assertTrue(savedAnalysisFromDatabase instanceof AnalysisPhylogenomicsPipeline);
		AnalysisPhylogenomicsPipeline savedPhylogenomics = (AnalysisPhylogenomicsPipeline) savedAnalysisFromDatabase;

		assertEquals("Analysis from submission and from database should be the same",
				savedAnalysisFromDatabase.getId(), analysis.getId());

		assertEquals(analysisResultsPhylogenomics.getId(), savedPhylogenomics.getId());
		assertEquals(analysisResultsPhylogenomics.getPhylogeneticTree().getFile(), savedPhylogenomics
				.getPhylogeneticTree().getFile());
		assertEquals(analysisResultsPhylogenomics.getSnpMatrix().getFile(), savedPhylogenomics.getSnpMatrix().getFile());
		assertEquals(analysisResultsPhylogenomics.getSnpTable().getFile(), savedPhylogenomics.getSnpTable().getFile());
		assertEquals(analysisResultsPhylogenomics.getInputSequenceFiles(), savedPhylogenomics.getInputSequenceFiles());
	}

	/**
	 * Tests out failing to get analysis results due to analysis not being
	 * submitted.
	 * 
	 * @throws Exception
	 */
	@Test(expected = EntityNotFoundException.class)
	@WithMockUser(username = "aaron", roles = "ADMIN")
	public void testGetAnalysisResultsFail() throws Exception {
		AnalysisSubmission analysisSubmission = analysisExecutionGalaxyITService.setupSubmissionInDatabase(1L,
				sequenceFilePath, referenceFilePath, validIridaWorkflowId);

		analysisSubmission.setAnalysisState(AnalysisState.PREPARING);
		AnalysisExecutionWorker preparationWorker = analysisExecutionServiceSimplified
				.prepareSubmission(analysisSubmission);
		AnalysisSubmission analysisSubmitted = preparationWorker.getResult();

		analysisSubmitted.setAnalysisState(AnalysisState.SUBMITTING);
		AnalysisSubmission analysisExecuted = analysisExecutionServiceSimplified
				.executeAnalysis(analysisSubmitted);

		analysisExecutionGalaxyITService.waitUntilSubmissionCompleteSimplified(analysisExecuted);

		analysisExecuted.setId(555l);
		analysisExecuted.setAnalysisState(AnalysisState.FINISHED_RUNNING);
		analysisExecutionServiceSimplified.transferAnalysisResults(analysisExecuted);
	}
}
