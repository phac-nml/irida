package ca.corefacility.bioinformatics.irida.service.analysis.impl.galaxy.integration;

import static org.junit.Assert.*;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

import org.junit.Assume;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.StandardPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import ca.corefacility.bioinformatics.irida.config.IridaApiServicesConfig;
import ca.corefacility.bioinformatics.irida.config.conditions.WindowsPlatformCondition;
import ca.corefacility.bioinformatics.irida.config.data.IridaApiTestDataSourceConfig;
import ca.corefacility.bioinformatics.irida.config.pipeline.data.galaxy.NonWindowsLocalGalaxyConfig;
import ca.corefacility.bioinformatics.irida.config.pipeline.data.galaxy.WindowsLocalGalaxyConfig;
import ca.corefacility.bioinformatics.irida.config.processing.IridaApiTestMultithreadingConfig;
import ca.corefacility.bioinformatics.irida.exceptions.ExecutionManagerException;
import ca.corefacility.bioinformatics.irida.exceptions.WorkflowException;
import ca.corefacility.bioinformatics.irida.model.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.project.ReferenceFile;
import ca.corefacility.bioinformatics.irida.model.workflow.WorkflowState;
import ca.corefacility.bioinformatics.irida.model.workflow.WorkflowStatus;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.Analysis;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.AnalysisPhylogenomicsPipeline;
import ca.corefacility.bioinformatics.irida.model.workflow.galaxy.RemoteWorkflowGalaxy;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.galaxy.AnalysisSubmissionGalaxyPhylogenomicsPipeline;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyHistoriesService;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyWorkflowService;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.integration.LocalGalaxy;
import ca.corefacility.bioinformatics.irida.service.analysis.impl.galaxy.AnalysisExecutionServiceGalaxyPhylogenomicsPipeline;

import com.github.jmchilton.blend4j.galaxy.HistoriesClient;
import com.github.jmchilton.blend4j.galaxy.ToolsClient;
import com.github.jmchilton.blend4j.galaxy.WorkflowsClient;
import com.github.springtestdbunit.DbUnitTestExecutionListener;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = {
		IridaApiServicesConfig.class, IridaApiTestDataSourceConfig.class,
		IridaApiTestMultithreadingConfig.class, NonWindowsLocalGalaxyConfig.class, WindowsLocalGalaxyConfig.class  })
@ActiveProfiles("test")
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
		DbUnitTestExecutionListener.class })
public class AnalysisExecutionServiceGalaxyPhylogenomicsPipelineIT {
	
	@Autowired
	private LocalGalaxy localGalaxy;
	
	private Path dataFile;
	private Path referenceFile;
	private Set<SequenceFile> sequenceFiles;
	
	private AnalysisExecutionServiceGalaxyPhylogenomicsPipeline workflowManagement;
	
	@Before
	public void setup() throws URISyntaxException {
		Assume.assumeFalse(WindowsPlatformCondition.isWindows());
		
		dataFile = Paths.get(AnalysisExecutionServiceGalaxyPhylogenomicsPipelineIT.class.getResource(
				"testData1.fastq").toURI());
		referenceFile = Paths.get(AnalysisExecutionServiceGalaxyPhylogenomicsPipelineIT.class.getResource(
				"testReference.fasta").toURI());
				
		sequenceFiles = new HashSet<>();
		sequenceFiles.add(new SequenceFile(dataFile));
		
		workflowManagement = buildWorkflowManagementGalaxy();
		
//		invalidSubmittedAnalysis = new AnalysisSubmissionGalaxyPhylogenomicsPipeline(new GalaxyAnalysisId("invalid"), null);
	}
	
	private GalaxyHistoriesService buildGalaxyHistoriesService() {
		HistoriesClient historiesClient = localGalaxy.getGalaxyInstanceAdmin().getHistoriesClient();
		ToolsClient toolsClient = localGalaxy.getGalaxyInstanceAdmin().getToolsClient();
		return new GalaxyHistoriesService(historiesClient, toolsClient);
	}
	
	private GalaxyWorkflowService buildGalaxyWorkflowService() {
		HistoriesClient historiesClient = localGalaxy.getGalaxyInstanceAdmin().getHistoriesClient();
		WorkflowsClient workflowsClient = localGalaxy.getGalaxyInstanceAdmin().getWorkflowsClient();
		
		return new GalaxyWorkflowService(historiesClient, workflowsClient,
						new StandardPasswordEncoder());
	}
	
	private AnalysisExecutionServiceGalaxyPhylogenomicsPipeline buildWorkflowManagementGalaxy() {
		GalaxyHistoriesService galaxyHistoriesService = buildGalaxyHistoriesService();
		GalaxyWorkflowService galaxyWorkflowService = buildGalaxyWorkflowService();
		
		return new AnalysisExecutionServiceGalaxyPhylogenomicsPipeline(galaxyHistoriesService, galaxyWorkflowService);
	}	
	
	private AnalysisSubmissionGalaxyPhylogenomicsPipeline buildAnalysisSubmission() {
		
		RemoteWorkflowGalaxy remoteWorkflow = new RemoteWorkflowGalaxy(localGalaxy.getWorkflowCorePipelineTestId(),
				localGalaxy.getWorkflowCorePipelineTestChecksum());
		
		AnalysisSubmissionGalaxyPhylogenomicsPipeline analysisSubmission = 
				new AnalysisSubmissionGalaxyPhylogenomicsPipeline(sequenceFiles,
						new ReferenceFile(referenceFile), remoteWorkflow);
		analysisSubmission.setInputFiles(sequenceFiles);
		analysisSubmission.setReferenceFile(new ReferenceFile(referenceFile));
		analysisSubmission.setRemoteWorkflow(remoteWorkflow);
		
		return analysisSubmission;
	}
	
	private void waitForAnalysisFinished(AnalysisSubmissionGalaxyPhylogenomicsPipeline analysis) throws InterruptedException, ExecutionManagerException {
		final int max = 1000;
		final int time = 5000;
		for (int i = 0; i < max; i++) {
			WorkflowStatus status = workflowManagement.getWorkflowStatus(analysis);
			if (WorkflowState.OK.equals(status.getState())) {
				break;
			}
			Thread.sleep(time);
		}
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
		AnalysisSubmissionGalaxyPhylogenomicsPipeline analysisSubmission = buildAnalysisSubmission();
		
		AnalysisSubmissionGalaxyPhylogenomicsPipeline analysisSubmitted = workflowManagement.executeAnalysis(analysisSubmission);
		assertNotNull(analysisSubmitted);
		
		WorkflowStatus status = workflowManagement.getWorkflowStatus(analysisSubmitted);
		assertValidStatus(status);
	}
	
	/**
	 * Tests getting results from an executed analysis.
	 * @throws ExecutionManagerException
	 * @throws InterruptedException
	 */
	@Ignore
	@Test
	public void testGetAnalysisResultsSuccess() throws ExecutionManagerException, InterruptedException {
		AnalysisSubmissionGalaxyPhylogenomicsPipeline analysisSubmission = buildAnalysisSubmission();
		
		AnalysisSubmissionGalaxyPhylogenomicsPipeline analysisSubmitted = workflowManagement.executeAnalysis(analysisSubmission);
		assertNotNull(analysisSubmitted);
		
		WorkflowStatus status = workflowManagement.getWorkflowStatus(analysisSubmitted);
		assertValidStatus(status);
		
		waitForAnalysisFinished(analysisSubmitted);
		
		Analysis analysis = workflowManagement.getAnalysisResults(analysisSubmitted);
		assertTrue(analysis instanceof AnalysisPhylogenomicsPipeline);
//		AnalysisPhylogenomicsPipeline analysisResults = (AnalysisPhylogenomicsPipeline)analysis;
		
//		Path outputFile = analysisResults.getOutputFile();
//		assertNotNull(outputFile);
//		assertTrue(outputFile.toFile().exists());
	}

	/**
	 * Tests out attempting to submit an invalid workflow for execution.
	 * @throws ExecutionManagerException 
	 */
	@Test(expected=WorkflowException.class)
	public void testExecuteAnalysisFailInvalidWorkflow() throws ExecutionManagerException {
		AnalysisSubmissionGalaxyPhylogenomicsPipeline analysisSubmission = buildAnalysisSubmission();
		analysisSubmission.getRemoteWorkflow().
			setWorkflowId(localGalaxy.getInvalidWorkflowId());
		
		workflowManagement.executeAnalysis(analysisSubmission);
	}
}
