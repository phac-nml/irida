package ca.corefacility.bioinformatics.irida.service.analysis.impl.galaxy.unit;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import ca.corefacility.bioinformatics.irida.exceptions.ExecutionManagerException;
import ca.corefacility.bioinformatics.irida.exceptions.WorkflowException;
import ca.corefacility.bioinformatics.irida.model.workflow.WorkflowState;
import ca.corefacility.bioinformatics.irida.model.workflow.WorkflowStatus;
import ca.corefacility.bioinformatics.irida.model.workflow.galaxy.GalaxyAnalysisId;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyHistoriesService;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyWorkflowService;
import ca.corefacility.bioinformatics.irida.service.analysis.impl.galaxy.AnalysisSubmissionTestImpl;
import ca.corefacility.bioinformatics.irida.service.analysis.impl.galaxy.SubmittedAnalysisGalaxy;
import ca.corefacility.bioinformatics.irida.service.analysis.impl.galaxy.WorkflowManagementServiceGalaxy;

public class WorkflowManagementServiceGalaxyTest {
	
	@Mock private GalaxyHistoriesService galaxyHistoriesService;
	@Mock private GalaxyWorkflowService galaxyWorkflowService;
	@Mock private AnalysisSubmissionTestImpl analysisSubmission;

	private SubmittedAnalysisGalaxy submittedAnalysisGalaxy;

	private WorkflowManagementServiceGalaxy workflowManagement;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		
		workflowManagement = new WorkflowManagementServiceGalaxy(galaxyHistoriesService,
				galaxyWorkflowService);
		
		submittedAnalysisGalaxy = new SubmittedAnalysisGalaxy(new GalaxyAnalysisId("1"), null);
	}
	
	@Ignore
	@Test
	public void testExecuteAnalysisSuccess() throws ExecutionManagerException {
		workflowManagement.executeAnalysis(analysisSubmission);
	}
	
	@Ignore
	@Test
	public void testGetAnalysisResults() throws WorkflowException {
		workflowManagement.getAnalysisResults(submittedAnalysisGalaxy);
	}
	
	/**
	 * Tests out successfully getting the status of a workflow.
	 * @throws ExecutionManagerException
	 */
	@Test
	public void testGetWorkflowStatusSuccess() throws ExecutionManagerException {
		WorkflowStatus workflowStatus = new WorkflowStatus(WorkflowState.OK, 1.0f);
		
		when(galaxyHistoriesService.getStatusForHistory(
				submittedAnalysisGalaxy.getRemoteAnalysisId().getValue())).thenReturn(workflowStatus);
		
		assertEquals(workflowStatus, workflowManagement.getWorkflowStatus(submittedAnalysisGalaxy));
	}
	
	/**
	 * Tests failure to get the status of a workflow (no status).
	 * @throws ExecutionManagerException
	 */
	@Test(expected=WorkflowException.class)
	public void testGetWorkflowStatusFailNoStatus() throws ExecutionManagerException {		
		when(galaxyHistoriesService.getStatusForHistory(submittedAnalysisGalaxy.
				getRemoteAnalysisId().getValue())).thenThrow(new WorkflowException());
		
		workflowManagement.getWorkflowStatus(submittedAnalysisGalaxy);
	}
	
	@Ignore
	@Test
	public void testCancelAnalysis() throws WorkflowException {
		workflowManagement.cancelAnalysis(submittedAnalysisGalaxy);
	}
}
