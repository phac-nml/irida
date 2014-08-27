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
import java.util.HashSet;
import java.util.Set;

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
import ca.corefacility.bioinformatics.irida.model.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.project.ReferenceFile;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.workflow.WorkflowState;
import ca.corefacility.bioinformatics.irida.model.workflow.WorkflowStatus;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.Analysis;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.AnalysisOutputFile;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.AnalysisPhylogenomicsPipeline;
import ca.corefacility.bioinformatics.irida.model.workflow.galaxy.phylogenomics.RemoteWorkflowPhylogenomics;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.galaxy.phylogenomics.AnalysisSubmissionPhylogenomics;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyWorkflowService;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.integration.LocalGalaxy;
import ca.corefacility.bioinformatics.irida.repositories.analysis.submission.AnalysisSubmissionRepository;
import ca.corefacility.bioinformatics.irida.repositories.referencefile.ReferenceFileRepository;
import ca.corefacility.bioinformatics.irida.repositories.sequencefile.SequenceFileRepository;
import ca.corefacility.bioinformatics.irida.repositories.workflow.RemoteWorkflowRepository;
import ca.corefacility.bioinformatics.irida.service.AnalysisService;
import ca.corefacility.bioinformatics.irida.service.SequenceFileService;
import ca.corefacility.bioinformatics.irida.service.analysis.execution.galaxy.phylogenomics.impl.AnalysisExecutionServicePhylogenomics;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;
import ca.corefacility.bioinformatics.irida.service.workflow.galaxy.phylogenomics.impl.RemoteWorkflowServicePhylogenomics;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;

/**
 * Tests out the analysis service for the phylogenomic pipeline.
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = {
		IridaApiServicesConfig.class, IridaApiTestDataSourceConfig.class,
		IridaApiTestMultithreadingConfig.class, NonWindowsLocalGalaxyConfig.class,
		WindowsLocalGalaxyConfig.class, AnalysisExecutionServiceTestConfig.class,
		RemoteWorkflowServiceTestConfig.class})
@ActiveProfiles("test")
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
		DbUnitTestExecutionListener.class, WithSecurityContextTestExcecutionListener.class })
@DatabaseSetup("/ca/corefacility/bioinformatics/irida/repositories/analysis/AnalysisRepositoryIT.xml")
@DatabaseTearDown("/ca/corefacility/bioinformatics/irida/test/integration/TableReset.xml")
public class AnalysisExecutionServicePhylogenomicsIT {
	
	@Autowired
	private LocalGalaxy localGalaxy;
	
	@Autowired
	private ReferenceFileRepository referenceFileRepository;
	
	@Autowired
	private SequenceFileRepository sequenceFileRepository;
	
	@Autowired
	private SequenceFileService seqeunceFileService;
	
	@Autowired
	private RemoteWorkflowRepository remoteWorkflowRepository;
	
	@Autowired
	private AnalysisSubmissionRepository analysisSubmissionRepository;
	
	@Autowired
	private AnalysisService analysisService;
	
	@Autowired
	private AnalysisExecutionServicePhylogenomics 
		analysisExecutionServicePhylogenomics;
	
	@Autowired
	private RemoteWorkflowServicePhylogenomics
		remoteWorkflowServicePhylogenomics;
	
	@Autowired
	private RemoteWorkflowServicePhylogenomics
		remoteWorkflowServicePhylogenomicsInvalidId;
	
	@Autowired
	private RemoteWorkflowServicePhylogenomics
		remoteWorkflowServicePhylogenomicsInvalidChecksum;
	
	@Autowired
	private SampleService sampleService;
	
	@Autowired
	private GalaxyWorkflowService galaxyWorkflowService;
		
	private Path sequenceFilePath;
	private Path referenceFilePath;
	
	private Path expectedSnpMatrix;
	private Path expectedSnpTable;
	private Path expectedTree;
		
	/**
	 * Sets up variables for testing.
	 * @throws URISyntaxException
	 * @throws IOException 
	 */
	@Before
	public void setup() throws URISyntaxException, IOException {
		Assume.assumeFalse(WindowsPlatformCondition.isWindows());
		
		Path sequenceFilePathReal = Paths.get(AnalysisExecutionServicePhylogenomicsIT.class.getResource(
				"testData1.fastq").toURI());		
		Path referenceFilePathReal = Paths.get(AnalysisExecutionServicePhylogenomicsIT.class.getResource(
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
	 * Sets up an AnalysisSubmission and saves all dependencies in database.
	 * @param sequenceFilePath  The path to an input sequence file for this test.
	 * @param referenceFilePath  The path to an input reference file for this test.
	 * @param remoteWorkflow  A remote workflow to execute for this test.
	 * @return  An AnalysisSubmissionPhylogenomics which has been saved to the database.
	 */
	private AnalysisSubmissionPhylogenomics setupSubmissionInDatabase(Path sequenceFilePath,
		Path referenceFilePath, RemoteWorkflowPhylogenomics remoteWorkflow) {
		
		Sample sample = sampleService.read(1L);
		Join<Sample, SequenceFile> sampleSeqFile = 
				seqeunceFileService.createSequenceFileInSample(new SequenceFile(sequenceFilePath), sample);
		SequenceFile sequenceFile = sampleSeqFile.getObject();
		
		Set<SequenceFile> sequenceFiles = new HashSet<>();
		sequenceFiles.add(sequenceFile);
		
		ReferenceFile referenceFile = referenceFileRepository.save(new ReferenceFile(referenceFilePath));
				
		RemoteWorkflowPhylogenomics remoteWorkflowSaved = remoteWorkflowRepository.save(remoteWorkflow);
		
		return new AnalysisSubmissionPhylogenomics(sequenceFiles,
				referenceFile, remoteWorkflowSaved);
	}
	
	/**
	 * Asserts that the given status is in a valid state for a workflow.
	 * @param status
	 */
	private void assertValidStatus(WorkflowStatus status) {
		assertNotNull("WorkflowStatus is null", status);
		assertFalse("WorkflowState is " + WorkflowState.UNKNOWN, WorkflowState.UNKNOWN.equals(status.getState()));
		float percentComplete = status.getPercentComplete();
		assertTrue("percentComplete not in range of 0 to 100", 0.0f <= percentComplete && percentComplete <= 100.0f);
	}
	
	/**
	 * Tests out successfully submitting a workflow for execution.
	 * @throws InterruptedException 
	 * @throws ExecutionManagerException 
	 */
	@Test
	@WithMockUser(username = "aaron", roles = "ADMIN")
	public void testExecuteAnalysisSuccess() throws InterruptedException, ExecutionManagerException {
		RemoteWorkflowPhylogenomics remoteWorkflowUnsaved = 
				remoteWorkflowServicePhylogenomics.getCurrentWorkflow();
		
		AnalysisSubmissionPhylogenomics analysisSubmission = 
				setupSubmissionInDatabase(sequenceFilePath, referenceFilePath, remoteWorkflowUnsaved);
		
		AnalysisSubmissionPhylogenomics analysisSubmitted = 
				analysisExecutionServicePhylogenomics.executeAnalysis(analysisSubmission);
		assertNotNull("analysisSubmitted is null", analysisSubmitted);
		assertNotNull("remoteAnalysisId is null", analysisSubmitted.getRemoteAnalysisId());

		WorkflowStatus status = 
				analysisExecutionServicePhylogenomics.getWorkflowStatus(analysisSubmitted);
		assertValidStatus(status);
		
		AnalysisSubmissionPhylogenomics savedSubmission = analysisSubmissionRepository.getByType(analysisSubmitted.getRemoteAnalysisId(),
				AnalysisSubmissionPhylogenomics.class);
		
		assertEquals(analysisSubmitted.getRemoteAnalysisId(), savedSubmission.getRemoteAnalysisId());
		assertEquals(analysisSubmitted.getRemoteWorkflow(), savedSubmission.getRemoteWorkflow());
		assertEquals(analysisSubmitted.getInputFiles(), savedSubmission.getInputFiles());
		assertEquals(analysisSubmitted.getReferenceFile(), savedSubmission.getReferenceFile());
	}
	
	/**
	 * Tests out attempting to submit an analysis twice.
	 * @throws IllegalArgumentException 
	 */
	@Test(expected=IllegalArgumentException.class)
	@WithMockUser(username = "aaron", roles = "ADMIN")
	public void testExecuteAnalysisFailTwice() throws ExecutionManagerException {
		RemoteWorkflowPhylogenomics remoteWorkflowUnsaved = 
				remoteWorkflowServicePhylogenomics.getCurrentWorkflow();
		
		AnalysisSubmissionPhylogenomics analysisSubmission 
			= setupSubmissionInDatabase(sequenceFilePath, referenceFilePath, remoteWorkflowUnsaved);
		
		AnalysisSubmissionPhylogenomics submitted 
			= analysisExecutionServicePhylogenomics.executeAnalysis(analysisSubmission);
		analysisExecutionServicePhylogenomics.executeAnalysis(submitted);
	}

	/**
	 * Tests out attempting to submit a workflow with an invalid id for execution.
	 * @throws ExecutionManagerException 
	 */
	@Test(expected=WorkflowException.class)
	@WithMockUser(username = "aaron", roles = "ADMIN")
	public void testExecuteAnalysisFailInvalidWorkflow() throws ExecutionManagerException {
		RemoteWorkflowPhylogenomics remoteWorkflowUnsaved = 
				remoteWorkflowServicePhylogenomicsInvalidId.getCurrentWorkflow();
		
		AnalysisSubmissionPhylogenomics analysisSubmission = 
				setupSubmissionInDatabase(sequenceFilePath, referenceFilePath, remoteWorkflowUnsaved);
		
		analysisExecutionServicePhylogenomics.executeAnalysis(analysisSubmission);
	}
	
	/**
	 * Tests out attempting to submit a workflow with an invalid checksum for execution.
	 * @throws ExecutionManagerException 
	 */
	@Test(expected=WorkflowChecksumInvalidException.class)
	@WithMockUser(username = "aaron", roles = "ADMIN")
	public void testExecuteAnalysisFailInvalidChecksum() throws ExecutionManagerException {
		RemoteWorkflowPhylogenomics remoteWorkflowUnsaved = 
				remoteWorkflowServicePhylogenomicsInvalidChecksum.getCurrentWorkflow();
		
		AnalysisSubmissionPhylogenomics analysisSubmission = 
				setupSubmissionInDatabase(sequenceFilePath, referenceFilePath, remoteWorkflowUnsaved);
		
		analysisExecutionServicePhylogenomics.executeAnalysis(analysisSubmission);
	}
	
	/**
	 * Tests out getting analysis results successfully.
	 * @throws Exception 
	 */
	@Test
	@WithMockUser(username = "aaron", roles = "ADMIN")
	public void testGetAnalysisResultsSuccess() throws Exception {	
		RemoteWorkflowPhylogenomics remoteWorkflowUnsaved = 
				remoteWorkflowServicePhylogenomics.getCurrentWorkflow();
		
		AnalysisSubmissionPhylogenomics analysisSubmission = 
				setupSubmissionInDatabase(sequenceFilePath, referenceFilePath, remoteWorkflowUnsaved);
		
		AnalysisSubmissionPhylogenomics analysisSubmitted = analysisExecutionServicePhylogenomics
				.executeAnalysis(analysisSubmission);

		waitUntilSubmissionComplete(analysisSubmitted);

		AnalysisPhylogenomicsPipeline analysisResults = analysisExecutionServicePhylogenomics
				.transferAnalysisResults(analysisSubmission);

		String analysisId = analysisSubmitted.getRemoteAnalysisId();
		assertEquals("id should be set properly for analysis",
				analysisId,
				analysisResults.getExecutionManagerAnalysisId());

		assertEquals("inputFiles should be the same for submission and results",
				analysisSubmission.getInputFiles(), analysisResults.getInputSequenceFiles());
		
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
		AnalysisPhylogenomicsPipeline savedPhylogenomics = (AnalysisPhylogenomicsPipeline)savedAnalysis;
		
		assertEquals(analysisResults.getId(), savedPhylogenomics.getId());
		assertEquals(analysisResults.getPhylogeneticTree().getFile(), savedPhylogenomics.getPhylogeneticTree().getFile());
		assertEquals(analysisResults.getSnpMatrix().getFile(), savedPhylogenomics.getSnpMatrix().getFile());
		assertEquals(analysisResults.getSnpTable().getFile(), savedPhylogenomics.getSnpTable().getFile());
		assertEquals(analysisResults.getInputSequenceFiles(), savedPhylogenomics.getInputSequenceFiles());
	}
	
	/**
	 * Tests out failing to get analysis results due to analysis not being submitted.
	 * @throws Exception 
	 */
	@Test(expected=EntityNotFoundException.class)
	@WithMockUser(username = "aaron", roles = "ADMIN")
	public void testGetAnalysisResultsFail() throws Exception {	
		RemoteWorkflowPhylogenomics remoteWorkflowUnsaved = 
				remoteWorkflowServicePhylogenomics.getCurrentWorkflow();
		
		AnalysisSubmissionPhylogenomics analysisSubmissionBefore = 
				setupSubmissionInDatabase(sequenceFilePath, referenceFilePath, remoteWorkflowUnsaved);
		
		AnalysisSubmissionPhylogenomics analysisSubmittedAfter = analysisExecutionServicePhylogenomics
				.executeAnalysis(analysisSubmissionBefore);

		waitUntilSubmissionComplete(analysisSubmittedAfter);

		analysisSubmittedAfter.setRemoteAnalysisId("notSubmittedId");
		
		analysisExecutionServicePhylogenomics
				.transferAnalysisResults(analysisSubmittedAfter);
	}
	
	/**
	 * Wait for the given analysis submission to be complete.
	 * @param analysisSubmission  The analysis submission to wait for.
	 * @throws Exception 
	 */
	private void waitUntilSubmissionComplete(AnalysisSubmissionPhylogenomics analysisSubmission) throws Exception {
		final int totalSecondsWait = 1*60; // 1 minute
		
		WorkflowStatus workflowStatus;
		
		long timeBefore = System.currentTimeMillis();
		do {
			workflowStatus = analysisExecutionServicePhylogenomics.getWorkflowStatus(analysisSubmission);
			
			long timeAfter = System.currentTimeMillis();
			double deltaSeconds = (timeAfter - timeBefore)/1000.0;
			if (deltaSeconds <= totalSecondsWait) {
				Thread.sleep(2000);
			} else {
				throw new Exception("Timeout for submission " + analysisSubmission.getRemoteAnalysisId() +
						" " + deltaSeconds + "s > " + totalSecondsWait + "s");
			}
		} while (!WorkflowState.OK.equals(workflowStatus.getState()));
	}
}
