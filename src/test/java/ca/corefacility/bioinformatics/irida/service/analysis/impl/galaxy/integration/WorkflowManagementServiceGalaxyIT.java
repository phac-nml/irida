package ca.corefacility.bioinformatics.irida.service.analysis.impl.galaxy.integration;

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
import ca.corefacility.bioinformatics.irida.config.conditions.WindowsPlatformCondition;
import ca.corefacility.bioinformatics.irida.config.data.IridaApiTestDataSourceConfig;
import ca.corefacility.bioinformatics.irida.config.pipeline.data.galaxy.NonWindowsLocalGalaxyConfig;
import ca.corefacility.bioinformatics.irida.config.pipeline.data.galaxy.WindowsLocalGalaxyConfig;
import ca.corefacility.bioinformatics.irida.config.processing.IridaApiTestMultithreadingConfig;
import ca.corefacility.bioinformatics.irida.exceptions.WorkflowException;
import ca.corefacility.bioinformatics.irida.model.workflow.WorkflowState;
import ca.corefacility.bioinformatics.irida.model.workflow.WorkflowStatus;
import ca.corefacility.bioinformatics.irida.model.workflow.galaxy.GalaxyAnalysisId;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.integration.LocalGalaxy;
import ca.corefacility.bioinformatics.irida.service.analysis.AnalysisSubmission;
import ca.corefacility.bioinformatics.irida.service.analysis.impl.galaxy.WorkflowManagementServiceGalaxy;

import com.github.springtestdbunit.DbUnitTestExecutionListener;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = {
		IridaApiServicesConfig.class, IridaApiTestDataSourceConfig.class,
		IridaApiTestMultithreadingConfig.class, NonWindowsLocalGalaxyConfig.class, WindowsLocalGalaxyConfig.class  })
@ActiveProfiles("test")
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
		DbUnitTestExecutionListener.class })
public class WorkflowManagementServiceGalaxyIT {
	
	@Autowired
	private LocalGalaxy localGalaxy;
	
	private Path dataFile;
	private Path referenceFile;
	private Set<Path> sequenceFiles;
	
	private GalaxyAnalysisId invalidAnalysisId;

	private WorkflowManagementServiceGalaxy workflowManagement;
	
	@Before
	public void setup() throws URISyntaxException {
		Assume.assumeFalse(WindowsPlatformCondition.isWindows());
		
		dataFile = Paths.get(WorkflowManagementServiceGalaxyIT.class.getResource(
				"testData1.fastq").toURI());
		referenceFile = Paths.get(WorkflowManagementServiceGalaxyIT.class.getResource(
				"testReference.fasta").toURI());
		
		sequenceFiles = new HashSet<>();
		sequenceFiles.add(dataFile);
		
		workflowManagement = new WorkflowManagementServiceGalaxy();
		
		invalidAnalysisId = new GalaxyAnalysisId("invalid");
	}
	
	private AnalysisSubmission buildAnalysisSubmission(String workflowId) {
		RemoteWorkflow remoteWorkflow = new RemoteWorkflowGalaxy();
		remoteWorkflow.setWorkflowId(workflowId);
		
		AnalysisSubmission analysisSubmission = new AnalysisSubmissionTestImpl();
		analysisSubmission.setSequenceFiles(sequenceFiles);
		analysisSubmission.setReferenceFile(referenceFile);
		analysisSubmission.setRemoteWorkflow(remoteWorkflow);
		analysisSubmission.setAnalysisType(AnalysisTest.class);
		
		return analysisSubmission;
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
	 * @throws WorkflowException
	 */
	@Test
	public void testExecuteAnalysisSuccess() throws WorkflowException {
		AnalysisSubmission analysisSubmission =
				buildAnalysisSubmission(localGalaxy.getWorklowCollectionListId());
		
		GalaxyAnalysisId analysisId = workflowManagement.executeAnalysis(analysisSubmission);
		assertNotNull(analysisId);
		
		WorkflowStatus status = workflowManagement.getWorkflowStatus(analysisId);
		assertValidStatus(status);
	}

	/**
	 * Tests out attempting to submit an invalid workflow for execution.
	 * @throws WorkflowException
	 */
	@Test(expected=WorkflowException.class)
	public void testExecuteAnalysisFailInvalidWorkflow() throws WorkflowException {
		AnalysisSubmission analysisSubmission =
				buildAnalysisSubmission(localGalaxy.getInvalidWorkflowId());
		
		workflowManagement.executeAnalysis(analysisSubmission);
	}
	
	/**
	 * Tests out getting a workflow status from an invalid workflow id.
	 * @throws WorkflowException 
	 */
	@Test(expected=WorkflowException.class)
	public void testGetWorkflowStatusInvalidId() throws WorkflowException {
		workflowManagement.getWorkflowStatus(invalidAnalysisId);
	}
	
	/**
	 * Tests out canceling an analysis with an invalid id.
	 * @throws WorkflowException 
	 */
	@Test(expected=WorkflowException.class)
	public void testCancelAnalysisInvalidId() throws WorkflowException {
		workflowManagement.cancelAnalysis(invalidAnalysisId);
	}
}
