package ca.corefacility.bioinformatics.irida.service.analysis.execution.galaxy.phylogenomics.impl.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

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

import ca.corefacility.bioinformatics.irida.config.IridaApiServicesConfig;
import ca.corefacility.bioinformatics.irida.config.analysis.AnalysisExecutionServiceTestConfig;
import ca.corefacility.bioinformatics.irida.config.conditions.WindowsPlatformCondition;
import ca.corefacility.bioinformatics.irida.config.data.IridaApiTestDataSourceConfig;
import ca.corefacility.bioinformatics.irida.config.pipeline.data.galaxy.NonWindowsLocalGalaxyConfig;
import ca.corefacility.bioinformatics.irida.config.pipeline.data.galaxy.WindowsLocalGalaxyConfig;
import ca.corefacility.bioinformatics.irida.config.processing.IridaApiTestMultithreadingConfig;
import ca.corefacility.bioinformatics.irida.config.workflow.RemoteWorkflowServiceTestConfig;
import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.exceptions.ExecutionManagerException;
import ca.corefacility.bioinformatics.irida.exceptions.WorkflowException;
import ca.corefacility.bioinformatics.irida.exceptions.galaxy.WorkflowChecksumInvalidException;
import ca.corefacility.bioinformatics.irida.model.enums.AnalysisState;
import ca.corefacility.bioinformatics.irida.model.workflow.WorkflowState;
import ca.corefacility.bioinformatics.irida.model.workflow.WorkflowStatus;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.Analysis;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.AnalysisOutputFile;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.AnalysisPhylogenomicsPipeline;
import ca.corefacility.bioinformatics.irida.model.workflow.galaxy.phylogenomics.RemoteWorkflowPhylogenomics;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.galaxy.phylogenomics.AnalysisSubmissionPhylogenomics;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.integration.LocalGalaxy;
import ca.corefacility.bioinformatics.irida.repositories.analysis.submission.AnalysisSubmissionRepository;
import ca.corefacility.bioinformatics.irida.service.AnalysisExecutionGalaxyITService;
import ca.corefacility.bioinformatics.irida.service.AnalysisService;
import ca.corefacility.bioinformatics.irida.service.AnalysisSubmissionService;
import ca.corefacility.bioinformatics.irida.service.analysis.execution.galaxy.phylogenomics.impl.AnalysisExecutionServicePhylogenomics;
import ca.corefacility.bioinformatics.irida.service.workflow.galaxy.phylogenomics.impl.RemoteWorkflowServicePhylogenomics;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;

/**
 * Tests out the analysis service for the phylogenomic pipeline.
 * 
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = {
		IridaApiServicesConfig.class, IridaApiTestDataSourceConfig.class,
		IridaApiTestMultithreadingConfig.class,
		NonWindowsLocalGalaxyConfig.class, WindowsLocalGalaxyConfig.class,
		AnalysisExecutionServiceTestConfig.class,
		RemoteWorkflowServiceTestConfig.class })
@ActiveProfiles("test")
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
		DbUnitTestExecutionListener.class,
		WithSecurityContextTestExcecutionListener.class })
@DatabaseSetup("/ca/corefacility/bioinformatics/irida/repositories/analysis/AnalysisRepositoryIT.xml")
@DatabaseTearDown("/ca/corefacility/bioinformatics/irida/test/integration/TableReset.xml")
public class AnalysisExecutionServicePhylogenomicsIT {

	@Autowired
	private AnalysisExecutionGalaxyITService analysisExecutionGalaxyITService;

	@Autowired
	private LocalGalaxy localGalaxy;

	@Autowired
	private AnalysisSubmissionRepository analysisSubmissionRepository;

	@Autowired
	private AnalysisSubmissionService analysisSubmissionService;

	@Autowired
	private AnalysisService analysisService;

	@Autowired
	private AnalysisExecutionServicePhylogenomics analysisExecutionServicePhylogenomics;

	@Autowired
	private RemoteWorkflowServicePhylogenomics remoteWorkflowServicePhylogenomics;

	@Autowired
	private RemoteWorkflowServicePhylogenomics remoteWorkflowServicePhylogenomicsInvalidId;

	@Autowired
	private RemoteWorkflowServicePhylogenomics remoteWorkflowServicePhylogenomicsInvalidChecksum;

	private Path sequenceFilePath;
	private Path referenceFilePath;

	private Path expectedSnpMatrix;
	private Path expectedSnpTable;
	private Path expectedTree;

	/**
	 * Sets up variables for testing.
	 * 
	 * @throws URISyntaxException
	 * @throws IOException
	 */
	@Before
	public void setup() throws URISyntaxException, IOException {
		Assume.assumeFalse(WindowsPlatformCondition.isWindows());

		Path sequenceFilePathReal = Paths
				.get(AnalysisExecutionGalaxyITService.class.getResource(
						"testData1.fastq").toURI());
		Path referenceFilePathReal = Paths
				.get(AnalysisExecutionGalaxyITService.class.getResource(
						"testReference.fasta").toURI());

		sequenceFilePath = Files.createTempFile("testData1", ".fastq");
		Files.delete(sequenceFilePath);
		Files.copy(sequenceFilePathReal, sequenceFilePath);

		referenceFilePath = Files.createTempFile("testReference", ".fasta");
		Files.delete(referenceFilePath);
		Files.copy(referenceFilePathReal, referenceFilePath);

		expectedSnpMatrix = localGalaxy.getWorkflowCorePipelineTestMatrix();
		expectedSnpTable = localGalaxy.getWorkflowCorePipelineTestSnpTable();
		expectedTree = localGalaxy.getWorkflowCorePipelineTestTree();
	}

	/**
	 * Asserts that the given status is in a valid state for a workflow.
	 * 
	 * @param status
	 */
	private void assertValidStatus(WorkflowStatus status) {
		assertNotNull("WorkflowStatus is null", status);
		assertFalse("WorkflowState is " + WorkflowState.UNKNOWN,
				WorkflowState.UNKNOWN.equals(status.getState()));
		float percentComplete = status.getPercentComplete();
		assertTrue("percentComplete not in range of 0 to 100",
				0.0f <= percentComplete && percentComplete <= 100.0f);
	}

	/**
	 * Tests out successfully submitting a workflow for execution.
	 * 
	 * @throws InterruptedException
	 * @throws ExecutionManagerException
	 */
	@Test
	@WithMockUser(username = "aaron", roles = "ADMIN")
	public void testExecuteAnalysisSuccess() throws InterruptedException,
			ExecutionManagerException {
		RemoteWorkflowPhylogenomics remoteWorkflowUnsaved = remoteWorkflowServicePhylogenomics
				.getCurrentWorkflow();

		AnalysisSubmissionPhylogenomics analysisSubmission = analysisExecutionGalaxyITService
				.setupSubmissionInDatabase(sequenceFilePath, referenceFilePath,
						remoteWorkflowUnsaved);

		AnalysisSubmissionPhylogenomics analysisSubmitted = analysisExecutionServicePhylogenomics
				.prepareSubmission(analysisSubmission);

		AnalysisSubmissionPhylogenomics analysisExecuted = analysisExecutionServicePhylogenomics
				.executeAnalysis(analysisSubmitted);
		assertNotNull("analysisExecuted is null", analysisExecuted);
		assertNotNull("remoteAnalysisId is null",
				analysisExecuted.getRemoteAnalysisId());
		assertEquals(AnalysisState.RUNNING, analysisExecuted.getAnalysisState());

		WorkflowStatus status = analysisExecutionServicePhylogenomics
				.getWorkflowStatus(analysisExecuted);
		assertValidStatus(status);

		AnalysisSubmissionPhylogenomics savedSubmission = analysisSubmissionRepository
				.getByType(analysisExecuted.getRemoteAnalysisId(),
						AnalysisSubmissionPhylogenomics.class);

		assertEquals(analysisExecuted.getRemoteAnalysisId(),
				savedSubmission.getRemoteAnalysisId());
		assertEquals(analysisExecuted.getRemoteWorkflow(),
				savedSubmission.getRemoteWorkflow());
		assertEquals(analysisExecuted.getInputFiles(),
				savedSubmission.getInputFiles());
		assertEquals(analysisExecuted.getReferenceFile(),
				savedSubmission.getReferenceFile());
	}

	/**
	 * Tests out attempting to submit an analysis twice.
	 * 
	 * @throws IllegalArgumentException
	 */
	@Test(expected = IllegalArgumentException.class)
	@WithMockUser(username = "aaron", roles = "ADMIN")
	public void testExecuteAnalysisFailTwice() throws ExecutionManagerException {
		RemoteWorkflowPhylogenomics remoteWorkflowUnsaved = remoteWorkflowServicePhylogenomics
				.getCurrentWorkflow();

		AnalysisSubmissionPhylogenomics analysisSubmission = analysisExecutionGalaxyITService
				.setupSubmissionInDatabase(sequenceFilePath, referenceFilePath,
						remoteWorkflowUnsaved);

		AnalysisSubmissionPhylogenomics analysisSubmitted = analysisExecutionServicePhylogenomics
				.prepareSubmission(analysisSubmission);

		AnalysisSubmissionPhylogenomics executed = analysisExecutionServicePhylogenomics
				.executeAnalysis(analysisSubmitted);

		analysisExecutionServicePhylogenomics.executeAnalysis(executed);
	}

	/**
	 * Tests out successfully preparing a workflow submission.
	 * 
	 * @throws InterruptedException
	 * @throws ExecutionManagerException
	 */
	@Test
	@WithMockUser(username = "aaron", roles = "ADMIN")
	public void testPrepareSubmissionSuccess() throws InterruptedException,
			ExecutionManagerException {
		RemoteWorkflowPhylogenomics remoteWorkflowUnsaved = remoteWorkflowServicePhylogenomics
				.getCurrentWorkflow();

		AnalysisSubmissionPhylogenomics analysisSubmission = analysisExecutionGalaxyITService
				.setupSubmissionInDatabase(sequenceFilePath, referenceFilePath,
						remoteWorkflowUnsaved);

		AnalysisSubmissionPhylogenomics analysisSubmitted = analysisExecutionServicePhylogenomics
				.prepareSubmission(analysisSubmission);
		assertNotNull("analysisSubmitted is null", analysisSubmitted);
		assertNotNull("remoteAnalysisId is null",
				analysisSubmitted.getRemoteAnalysisId());
		assertEquals(AnalysisState.SUBMITTED,
				analysisSubmitted.getAnalysisState());
	}

	/**
	 * Tests out attempting to prepare a workflow with an invalid id for
	 * execution.
	 * 
	 * @throws ExecutionManagerException
	 */
	@Test(expected = WorkflowException.class)
	@WithMockUser(username = "aaron", roles = "ADMIN")
	public void testPrepareSubmissionFailInvalidWorkflow()
			throws ExecutionManagerException {
		RemoteWorkflowPhylogenomics remoteWorkflowUnsaved = remoteWorkflowServicePhylogenomicsInvalidId
				.getCurrentWorkflow();

		AnalysisSubmissionPhylogenomics analysisSubmission = analysisExecutionGalaxyITService
				.setupSubmissionInDatabase(sequenceFilePath, referenceFilePath,
						remoteWorkflowUnsaved);

		analysisExecutionServicePhylogenomics
				.prepareSubmission(analysisSubmission);
	}

	/**
	 * Tests out attempting to submit a workflow with an invalid checksum for
	 * execution.
	 * 
	 * @throws ExecutionManagerException
	 */
	@Test(expected = WorkflowChecksumInvalidException.class)
	@WithMockUser(username = "aaron", roles = "ADMIN")
	public void testPrepareSubmissionFailInvalidChecksum()
			throws ExecutionManagerException {
		RemoteWorkflowPhylogenomics remoteWorkflowUnsaved = remoteWorkflowServicePhylogenomicsInvalidChecksum
				.getCurrentWorkflow();

		AnalysisSubmissionPhylogenomics analysisSubmission = analysisExecutionGalaxyITService
				.setupSubmissionInDatabase(sequenceFilePath, referenceFilePath,
						remoteWorkflowUnsaved);

		analysisExecutionServicePhylogenomics
				.prepareSubmission(analysisSubmission);
	}

	/**
	 * Tests out getting analysis results successfully.
	 * 
	 * @throws Exception
	 */
	@Test
	@WithMockUser(username = "aaron", roles = "ADMIN")
	public void testGetAnalysisResultsSuccess() throws Exception {
		RemoteWorkflowPhylogenomics remoteWorkflowUnsaved = remoteWorkflowServicePhylogenomics
				.getCurrentWorkflow();

		AnalysisSubmissionPhylogenomics analysisSubmission = analysisExecutionGalaxyITService
				.setupSubmissionInDatabase(sequenceFilePath, referenceFilePath,
						remoteWorkflowUnsaved);

		AnalysisSubmissionPhylogenomics analysisSubmitted = analysisExecutionServicePhylogenomics
				.prepareSubmission(analysisSubmission);

		AnalysisSubmissionPhylogenomics analysisExecuted = analysisExecutionServicePhylogenomics
				.executeAnalysis(analysisSubmitted);

		analysisExecutionGalaxyITService
				.waitUntilSubmissionComplete(analysisExecuted);

		AnalysisPhylogenomicsPipeline analysisResults = analysisExecutionServicePhylogenomics
				.transferAnalysisResults(analysisExecuted);
		AnalysisState state = analysisSubmissionService
				.getStateForAnalysisSubmission(analysisExecuted
						.getRemoteAnalysisId());
		assertEquals(AnalysisState.COMPLETED, state);

		String analysisId = analysisExecuted.getRemoteAnalysisId();
		assertEquals("id should be set properly for analysis", analysisId,
				analysisResults.getExecutionManagerAnalysisId());

		assertEquals(
				"inputFiles should be the same for submission and results",
				analysisExecuted.getInputFiles(),
				analysisResults.getInputSequenceFiles());

		assertEquals(3, analysisResults.getAnalysisOutputFiles().size());
		AnalysisOutputFile phylogeneticTree = analysisResults
				.getPhylogeneticTree();
		AnalysisOutputFile snpMatrix = analysisResults.getSnpMatrix();
		AnalysisOutputFile snpTable = analysisResults.getSnpTable();

		assertTrue("phylogenetic trees should be equal",
				com.google.common.io.Files.equal(expectedTree.toFile(),
						phylogeneticTree.getFile().toFile()));
		assertTrue("snp matrices should be correct",
				com.google.common.io.Files.equal(expectedSnpMatrix.toFile(),
						snpMatrix.getFile().toFile()));
		assertTrue("snpTable should be correct",
				com.google.common.io.Files.equal(expectedSnpTable.toFile(),
						snpTable.getFile().toFile()));

		Analysis savedAnalysis = analysisService.read(analysisResults.getId());
		assertTrue(savedAnalysis instanceof AnalysisPhylogenomicsPipeline);
		AnalysisPhylogenomicsPipeline savedPhylogenomics = (AnalysisPhylogenomicsPipeline) savedAnalysis;

		assertEquals(analysisResults.getId(), savedPhylogenomics.getId());
		assertEquals(analysisResults.getPhylogeneticTree().getFile(),
				savedPhylogenomics.getPhylogeneticTree().getFile());
		assertEquals(analysisResults.getSnpMatrix().getFile(),
				savedPhylogenomics.getSnpMatrix().getFile());
		assertEquals(analysisResults.getSnpTable().getFile(),
				savedPhylogenomics.getSnpTable().getFile());
		assertEquals(analysisResults.getInputSequenceFiles(),
				savedPhylogenomics.getInputSequenceFiles());
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
		RemoteWorkflowPhylogenomics remoteWorkflowUnsaved = remoteWorkflowServicePhylogenomics
				.getCurrentWorkflow();

		AnalysisSubmissionPhylogenomics analysisSubmission = analysisExecutionGalaxyITService
				.setupSubmissionInDatabase(sequenceFilePath, referenceFilePath,
						remoteWorkflowUnsaved);

		AnalysisSubmissionPhylogenomics analysisSubmitted = analysisExecutionServicePhylogenomics
				.prepareSubmission(analysisSubmission);

		AnalysisSubmissionPhylogenomics analysisExecuted = analysisExecutionServicePhylogenomics
				.executeAnalysis(analysisSubmitted);

		analysisExecutionGalaxyITService
				.waitUntilSubmissionComplete(analysisExecuted);

		analysisExecuted.setRemoteAnalysisId("notSubmittedId");

		analysisExecutionServicePhylogenomics
				.transferAnalysisResults(analysisExecuted);
	}
}
