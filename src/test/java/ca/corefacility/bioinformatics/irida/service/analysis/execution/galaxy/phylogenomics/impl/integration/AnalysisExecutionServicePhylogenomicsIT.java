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
import ca.corefacility.bioinformatics.irida.exceptions.galaxy.WorkflowChecksumInvalidException;
import ca.corefacility.bioinformatics.irida.model.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.project.ReferenceFile;
import ca.corefacility.bioinformatics.irida.model.workflow.WorkflowState;
import ca.corefacility.bioinformatics.irida.model.workflow.WorkflowStatus;
import ca.corefacility.bioinformatics.irida.model.workflow.galaxy.phylogenomics.RemoteWorkflowPhylogenomics;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.galaxy.phylogenomics.AnalysisSubmissionPhylogenomics;
import ca.corefacility.bioinformatics.irida.service.analysis.execution.galaxy.phylogenomics.impl.AnalysisExecutionServicePhylogenomics;
import ca.corefacility.bioinformatics.irida.service.workflow.galaxy.phylogenomics.impl.RemoteWorkflowServicePhylogenomics;

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
public class AnalysisExecutionServicePhylogenomicsIT {
	
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
	
	private Path dataFile;
	private Path referenceFile;
	private Set<SequenceFile> sequenceFiles;
	
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
		
		WorkflowOutputs output = analysisSubmitted.getOutputs();
		assertNotNull("output of submitted analysis is null", output);
		WorkflowStatus status = 
				analysisExecutionServicePhylogenomics.getWorkflowStatus(analysisSubmitted);
		assertValidStatus(status);
	}

	/**
	 * Tests out attempting to submit a workflow with an invalid id for execution.
	 * @throws ExecutionManagerException 
	 */
	@Test(expected=WorkflowException.class)
	public void tWorkflowExceptionestExecuteAnalysisFailInvalidWorkflow() throws ExecutionManagerException {
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
}
