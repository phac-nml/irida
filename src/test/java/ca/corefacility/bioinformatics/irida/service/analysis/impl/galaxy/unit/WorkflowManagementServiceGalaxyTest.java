package ca.corefacility.bioinformatics.irida.service.analysis.impl.galaxy.unit;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import ca.corefacility.bioinformatics.irida.exceptions.ExecutionManagerException;
import ca.corefacility.bioinformatics.irida.exceptions.WorkflowException;
import ca.corefacility.bioinformatics.irida.model.workflow.WorkflowState;
import ca.corefacility.bioinformatics.irida.model.workflow.WorkflowStatus;
import ca.corefacility.bioinformatics.irida.model.workflow.galaxy.GalaxyAnalysisId;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyHistoriesService;
import ca.corefacility.bioinformatics.irida.service.analysis.AnalysisSubmission;
import ca.corefacility.bioinformatics.irida.service.analysis.impl.galaxy.GalaxyConnectionService;
import ca.corefacility.bioinformatics.irida.service.analysis.impl.galaxy.WorkflowManagementServiceGalaxy;
import ca.corefacility.bioinformatics.irida.service.analysis.impl.galaxy.integration.ExecutionManagerGalaxy;

public class WorkflowManagementServiceGalaxyTest {
	
	@Mock private GalaxyConnectionService galaxyConnectionService;
	@Mock private GalaxyHistoriesService galaxyHistoriesService;
	@Mock private AnalysisSubmission<ExecutionManagerGalaxy> analysisSubmission;
	@Mock private GalaxyAnalysisId id;
	
	private WorkflowManagementServiceGalaxy workflowManagement;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		
		workflowManagement = new WorkflowManagementServiceGalaxy(galaxyConnectionService);
	}
	
	@Test
	public void testExecuteAnalysisSuccess() throws ExecutionManagerException {
		workflowManagement.executeAnalysis(analysisSubmission);
	}
	
	@Test
	public void testGetAnalysisResults() throws WorkflowException {
		workflowManagement.getAnalysisResults(id);
	}
	
	/**
	 * Tests out successfully getting the status of a workflow.
	 * @throws ExecutionManagerException
	 */
	@Test
	public void testGetWorkflowStatusSuccess() throws ExecutionManagerException {
		WorkflowStatus workflowStatus = new WorkflowStatus(WorkflowState.OK, 1.0f);
		
		when(galaxyConnectionService.getGalaxyHistoriesService(id)).thenReturn(galaxyHistoriesService);
		when(galaxyHistoriesService.getStatusForHistory(id.getValue())).thenReturn(workflowStatus);
		
		assertEquals(workflowStatus, workflowManagement.getWorkflowStatus(id));
	}
	
	/**
	 * Tests failure to get the status of a workflow (no status).
	 * @throws ExecutionManagerException
	 */
	@Test(expected=WorkflowException.class)
	public void testGetWorkflowStatusFailNoStatus() throws ExecutionManagerException {		
		when(galaxyConnectionService.getGalaxyHistoriesService(id)).thenReturn(galaxyHistoriesService);
		when(galaxyHistoriesService.getStatusForHistory(id.getValue())).thenThrow(new WorkflowException());
		
		workflowManagement.getWorkflowStatus(id);
	}
	
	/**
	 * Tests failure to get the status of a workflow (no histories service).
	 * @throws ExecutionManagerException
	 */
	@Test(expected=ExecutionManagerException.class)
	public void testGetWorkflowStatusFailNoHistoriesService() throws ExecutionManagerException {
		WorkflowStatus workflowStatus = new WorkflowStatus(WorkflowState.OK, 1.0f);
		
		when(galaxyConnectionService.getGalaxyHistoriesService(id)).thenThrow(new ExecutionManagerException());
		when(galaxyHistoriesService.getStatusForHistory(id.getValue())).thenReturn(workflowStatus);
		
		assertEquals(workflowStatus, workflowManagement.getWorkflowStatus(id));
	}
	
	@Test
	public void testCancelAnalysis() throws WorkflowException {
		workflowManagement.cancelAnalysis(id);
	}
}
