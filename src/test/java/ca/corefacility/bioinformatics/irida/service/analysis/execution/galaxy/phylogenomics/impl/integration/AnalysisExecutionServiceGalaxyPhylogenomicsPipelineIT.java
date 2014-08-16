package ca.corefacility.bioinformatics.irida.service.analysis.execution.galaxy.phylogenomics.impl.integration;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

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
import ca.corefacility.bioinformatics.irida.exceptions.galaxy.WorkflowInvalidException;
import ca.corefacility.bioinformatics.irida.model.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.project.ReferenceFile;
import ca.corefacility.bioinformatics.irida.model.workflow.WorkflowState;
import ca.corefacility.bioinformatics.irida.model.workflow.WorkflowStatus;
import ca.corefacility.bioinformatics.irida.model.workflow.galaxy.phylogenomics.RemoteWorkflowGalaxyPhylogenomics;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.galaxy.phylogenomics.AnalysisSubmissionGalaxyPhylogenomicsPipeline;
import ca.corefacility.bioinformatics.irida.service.analysis.execution.galaxy.phylogenomics.impl.AnalysisExecutionServiceGalaxyPhylogenomicsPipeline;
import ca.corefacility.bioinformatics.irida.service.workflow.galaxy.phylogenomics.impl.RemoteWorkflowServiceGalaxyPhylogenomics;

import com.github.jmchilton.blend4j.galaxy.beans.WorkflowOutputs;
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
public class AnalysisExecutionServiceGalaxyPhylogenomicsPipelineIT {
	
	@Autowired
	private AnalysisExecutionServiceGalaxyPhylogenomicsPipeline 
		analysisExecutionServiceGalaxyPhylogenomicsPipeline;
	
	@Autowired
	private RemoteWorkflowServiceGalaxyPhylogenomics
		remoteWorkflowServiceGalaxyPhylogenomics;
	
	@Autowired
	private RemoteWorkflowServiceGalaxyPhylogenomics
		remoteWorkflowServiceGalaxyPhylogenomicsInvalidId;
	
	@Autowired
	private RemoteWorkflowServiceGalaxyPhylogenomics
		remoteWorkflowServiceGalaxyPhylogenomicsInvalidChecksum;
	
	private Path dataFile;
	private Path referenceFile;
	private Set<SequenceFile> sequenceFiles;
	
	private AnalysisSubmissionGalaxyPhylogenomicsPipeline
		analysisSubmission;
	
	/**
	 * Sets up variables for testing.
	 * @throws URISyntaxException
	 */
	@Before
	public void setup() throws URISyntaxException {
		Assume.assumeFalse(WindowsPlatformCondition.isWindows());
		
		dataFile = Paths.get(AnalysisExecutionServiceGalaxyPhylogenomicsPipelineIT.class.getResource(
				"testData1.fastq").toURI());
		referenceFile = Paths.get(AnalysisExecutionServiceGalaxyPhylogenomicsPipelineIT.class.getResource(
				"testReference.fasta").toURI());
				
		sequenceFiles = new HashSet<>();
		sequenceFiles.add(new SequenceFile(dataFile));
		
		RemoteWorkflowGalaxyPhylogenomics remoteWorkflow =
				remoteWorkflowServiceGalaxyPhylogenomics.getCurrentWorkflow();
		
		analysisSubmission = new AnalysisSubmissionGalaxyPhylogenomicsPipeline(sequenceFiles,
				new ReferenceFile(referenceFile),remoteWorkflow);
	}
	
	/**
	 * Asserts that the given status is in a valid state for a workflow.
	 * @param status
	 */
	private void assertValidStatus(WorkflowStatus status) {
		assertNotNull(status);
		assertFalse(WorkflowState.UNKNOWN.equals(status.getState()));
		float percentComplete = status.getPercentComplete();
		assertTrue(0.0f <= percentComplete && percentComplete <= 100.0f);
	}
	
	/**
	 * Tests out successfully submitting a workflow for execution.
	 * @throws InterruptedException 
	 * @throws ExecutionManagerException 
	 */
	@Test
	public void testExecuteAnalysisSuccess() throws InterruptedException, ExecutionManagerException {		
		AnalysisSubmissionGalaxyPhylogenomicsPipeline analysisSubmitted = 
				analysisExecutionServiceGalaxyPhylogenomicsPipeline.executeAnalysis(analysisSubmission);
		assertNotNull(analysisSubmitted);
		assertNotNull(analysisSubmitted.getRemoteAnalysisId());
		
		WorkflowOutputs output = analysisSubmitted.getOutputs();
		assertNotNull(output);
		WorkflowStatus status = 
				analysisExecutionServiceGalaxyPhylogenomicsPipeline.getWorkflowStatus(analysisSubmitted);
		assertValidStatus(status);
	}

	/**
	 * Tests out attempting to submit a workflow with an invalid id for execution.
	 * @throws ExecutionManagerException 
	 */
	@Test(expected=WorkflowException.class)
	public void tWorkflowExceptionestExecuteAnalysisFailInvalidWorkflow() throws ExecutionManagerException {
		analysisSubmission.setRemoteWorkflow(
				remoteWorkflowServiceGalaxyPhylogenomicsInvalidId.getCurrentWorkflow());
		
		analysisExecutionServiceGalaxyPhylogenomicsPipeline.executeAnalysis(analysisSubmission);
	}
	
	/**
	 * Tests out attempting to submit a workflow with an invalid checksum for execution.
	 * @throws ExecutionManagerException 
	 */
	@Test(expected=WorkflowInvalidException.class)
	public void testExecuteAnalysisFailInvalidChecksum() throws ExecutionManagerException {
		analysisSubmission.setRemoteWorkflow(
				remoteWorkflowServiceGalaxyPhylogenomicsInvalidChecksum.getCurrentWorkflow());
		
		analysisExecutionServiceGalaxyPhylogenomicsPipeline.executeAnalysis(analysisSubmission);
	}
}
