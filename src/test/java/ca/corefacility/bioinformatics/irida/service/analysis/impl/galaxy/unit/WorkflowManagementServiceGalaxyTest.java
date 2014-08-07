package ca.corefacility.bioinformatics.irida.service.analysis.impl.galaxy.unit;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import ca.corefacility.bioinformatics.irida.exceptions.WorkflowException;
import ca.corefacility.bioinformatics.irida.model.workflow.galaxy.GalaxyWorkflowId;
import ca.corefacility.bioinformatics.irida.service.analysis.AnalysisSubmission;
import ca.corefacility.bioinformatics.irida.service.analysis.impl.galaxy.WorkflowManagementServiceGalaxy;

public class WorkflowManagementServiceGalaxyTest {
	
	@Mock private AnalysisSubmission analysisSubmission;
	@Mock private GalaxyWorkflowId id;
	
	private WorkflowManagementServiceGalaxy workflowManagement;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		
		workflowManagement = new WorkflowManagementServiceGalaxy();
	}
	
	@Test
	public void testExecuteAnalysisSuccess() throws WorkflowException {
		workflowManagement.executeAnalysis(analysisSubmission);
	}
	
	@Test
	public void testGetAnalysisResults() throws WorkflowException {
		workflowManagement.getAnalysisResults(id);
	}
	
	@Test
	public void testGetWorkflowStatus() throws WorkflowException {
		workflowManagement.getWorkflowStatus(id);
	}
	
	@Test
	public void testCancelAnalysis() throws WorkflowException {
		workflowManagement.cancelAnalysis(id);
	}
}
