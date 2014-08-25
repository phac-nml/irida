package ca.corefacility.bioinformatics.irida.service.analysis.execution.galaxy.phylogenomics.impl.integration;

import static org.junit.Assert.*;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import ca.corefacility.bioinformatics.irida.config.IridaApiServicesConfig;
import ca.corefacility.bioinformatics.irida.config.analysis.AnalysisExecutionServiceConfig;
import ca.corefacility.bioinformatics.irida.config.conditions.WindowsPlatformCondition;
import ca.corefacility.bioinformatics.irida.config.data.IridaApiTestDataSourceConfig;
import ca.corefacility.bioinformatics.irida.config.pipeline.data.galaxy.NonWindowsLocalGalaxyConfig;
import ca.corefacility.bioinformatics.irida.config.pipeline.data.galaxy.WindowsLocalGalaxyConfig;
import ca.corefacility.bioinformatics.irida.config.processing.IridaApiTestMultithreadingConfig;
import ca.corefacility.bioinformatics.irida.config.workflow.RemoteWorkflowServiceConfig;
import ca.corefacility.bioinformatics.irida.exceptions.ExecutionManagerException;
import ca.corefacility.bioinformatics.irida.exceptions.WorkflowException;
import ca.corefacility.bioinformatics.irida.exceptions.galaxy.WorkflowChecksumInvalidException;
import ca.corefacility.bioinformatics.irida.model.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.project.ReferenceFile;
import ca.corefacility.bioinformatics.irida.model.workflow.WorkflowState;
import ca.corefacility.bioinformatics.irida.model.workflow.WorkflowStatus;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.AnalysisOutputFile;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.AnalysisPhylogenomicsPipeline;
import ca.corefacility.bioinformatics.irida.model.workflow.galaxy.phylogenomics.RemoteWorkflowPhylogenomics;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.galaxy.phylogenomics.AnalysisSubmissionPhylogenomics;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyWorkflowService;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.integration.LocalGalaxy;
import ca.corefacility.bioinformatics.irida.service.analysis.execution.galaxy.phylogenomics.impl.AnalysisExecutionServicePhylogenomics;
import ca.corefacility.bioinformatics.irida.service.workflow.galaxy.phylogenomics.impl.RemoteWorkflowServicePhylogenomics;

import com.github.springtestdbunit.DbUnitTestExecutionListener;

/**
 * Tests out the analysis service for the phylogenomic pipeline.
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = {
		IridaApiServicesConfig.class, IridaApiTestDataSourceConfig.class,
		IridaApiTestMultithreadingConfig.class, NonWindowsLocalGalaxyConfig.class,
		WindowsLocalGalaxyConfig.class, AnalysisExecutionServiceConfig.class,
		RemoteWorkflowServiceConfig.class})
@ActiveProfiles("test")
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
		DbUnitTestExecutionListener.class })
public class AnalysisExecutionServicePhylogenomicsIT {
	
	@Autowired
	private LocalGalaxy localGalaxy;
	
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
	private GalaxyWorkflowService galaxyWorkflowService;
	
	private Path dataFile;
	private Path referenceFile;
	private Set<SequenceFile> sequenceFiles;
	
	private Path expectedSnpMatrix;
	private Path expectedSnpTable;
	private Path expectedTree;
	
	private AnalysisSubmissionPhylogenomics
		analysisSubmission;
	
	/**
	 * Sets up variables for testing.
	 * @throws URISyntaxException
	 */
	@Before
	public void setup() throws URISyntaxException {
		Assume.assumeFalse(WindowsPlatformCondition.isWindows());
		
		dataFile = Paths.get(AnalysisExecutionServicePhylogenomicsIT.class.getResource(
				"testData1.fastq").toURI());
		referenceFile = Paths.get(AnalysisExecutionServicePhylogenomicsIT.class.getResource(
				"testReference.fasta").toURI());
		
		expectedSnpMatrix = localGalaxy.getWorkflowCorePipelineTestMatrix();
		expectedSnpTable = localGalaxy.getWorkflowCorePipelineTestSnpTable();
		expectedTree = localGalaxy.getWorkflowCorePipelineTestTree();
				
		sequenceFiles = new HashSet<>();
		sequenceFiles.add(new SequenceFile(dataFile));
		
		RemoteWorkflowPhylogenomics remoteWorkflow =
				remoteWorkflowServicePhylogenomics.getCurrentWorkflow();
		
		analysisSubmission = new AnalysisSubmissionPhylogenomics(sequenceFiles,
				new ReferenceFile(referenceFile),remoteWorkflow);
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
	public void testExecuteAnalysisSuccess() throws InterruptedException, ExecutionManagerException {		
		AnalysisSubmissionPhylogenomics analysisSubmitted = 
				analysisExecutionServicePhylogenomics.executeAnalysis(analysisSubmission);
		assertNotNull("analysisSubmitted is null", analysisSubmitted);
		assertNotNull("remoteAnalysisId is null", analysisSubmitted.getRemoteAnalysisId());

		WorkflowStatus status = 
				analysisExecutionServicePhylogenomics.getWorkflowStatus(analysisSubmitted);
		assertValidStatus(status);
	}

	/**
	 * Tests out attempting to submit a workflow with an invalid id for execution.
	 * @throws ExecutionManagerException 
	 */
	@Test(expected=WorkflowException.class)
	public void testExecuteAnalysisFailInvalidWorkflow() throws ExecutionManagerException {
		analysisSubmission.setRemoteWorkflow(
				remoteWorkflowServicePhylogenomicsInvalidId.getCurrentWorkflow());
		
		analysisExecutionServicePhylogenomics.executeAnalysis(analysisSubmission);
	}
	
	/**
	 * Tests out attempting to submit a workflow with an invalid checksum for execution.
	 * @throws ExecutionManagerException 
	 */
	@Test(expected=WorkflowChecksumInvalidException.class)
	public void testExecuteAnalysisFailInvalidChecksum() throws ExecutionManagerException {
		analysisSubmission.setRemoteWorkflow(
				remoteWorkflowServicePhylogenomicsInvalidChecksum.getCurrentWorkflow());
		
		analysisExecutionServicePhylogenomics.executeAnalysis(analysisSubmission);
	}
	
	/**
	 * Tests out getting analysis results successfully.
	 * @throws Exception 
	 */
	@Test
	public void testGetAnalysisResultsSuccess() throws Exception {	

		AnalysisSubmissionPhylogenomics analysisSubmitted = analysisExecutionServicePhylogenomics
				.executeAnalysis(analysisSubmission);

		waitUntilSubmissionComplete(analysisSubmitted);

		AnalysisPhylogenomicsPipeline analysisResults = analysisExecutionServicePhylogenomics
				.getAnalysisResults(analysisSubmission);

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
	}
	
	/**
	 * Tests out failing to get analysis results due to an invalid analysis id.
	 * @throws Exception 
	 */
	@Test(expected=RuntimeException.class)
	public void testGetAnalysisResultsFail() throws Exception {	

		AnalysisSubmissionPhylogenomics analysisSubmitted = analysisExecutionServicePhylogenomics
				.executeAnalysis(analysisSubmission);

		waitUntilSubmissionComplete(analysisSubmitted);

		analysisSubmitted.setRemoteAnalysisId("invalid");
		
		analysisExecutionServicePhylogenomics
				.getAnalysisResults(analysisSubmission);
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
