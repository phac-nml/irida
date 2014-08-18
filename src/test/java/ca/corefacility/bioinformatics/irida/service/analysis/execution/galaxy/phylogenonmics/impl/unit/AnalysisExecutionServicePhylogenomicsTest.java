package ca.corefacility.bioinformatics.irida.service.analysis.execution.galaxy.phylogenonmics.impl.unit;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import ca.corefacility.bioinformatics.irida.exceptions.ExecutionManagerException;
import ca.corefacility.bioinformatics.irida.exceptions.WorkflowException;
import ca.corefacility.bioinformatics.irida.exceptions.galaxy.WorkflowChecksumInvalidException;
import ca.corefacility.bioinformatics.irida.model.workflow.WorkflowState;
import ca.corefacility.bioinformatics.irida.model.workflow.WorkflowStatus;
import ca.corefacility.bioinformatics.irida.model.workflow.galaxy.GalaxyAnalysisId;
import ca.corefacility.bioinformatics.irida.model.workflow.galaxy.PreparedWorkflowGalaxy;
import ca.corefacility.bioinformatics.irida.model.workflow.galaxy.WorkflowInputsGalaxy;
import ca.corefacility.bioinformatics.irida.model.workflow.galaxy.phylogenomics.RemoteWorkflowPhylogenomics;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.galaxy.phylogenomics.AnalysisSubmissionPhylogenomics;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyHistoriesService;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyWorkflowService;
import ca.corefacility.bioinformatics.irida.service.analysis.execution.galaxy.phylogenomics.impl.AnalysisExecutionServicePhylogenomics;
import ca.corefacility.bioinformatics.irida.service.analysis.workspace.galaxy.phylogenomics.impl.WorkspaceServicePhylogenomics;

import com.github.jmchilton.blend4j.galaxy.beans.WorkflowInputs;
import com.github.jmchilton.blend4j.galaxy.beans.WorkflowOutputs;

/**
 * Tests out an execution service for phylogenomics analyses.
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 */
public class AnalysisExecutionServicePhylogenomicsTest {
	
	@Mock private GalaxyHistoriesService galaxyHistoriesService;
	@Mock private GalaxyWorkflowService galaxyWorkflowService;
	@Mock private AnalysisSubmissionPhylogenomics analysisSubmission;
	@Mock private WorkspaceServicePhylogenomics workspaceServicePhylogenomics;
	@Mock private WorkflowInputs workflowInputs;
	@Mock private WorkflowOutputs workflowOutputs;

	private static final String WORKFLOW_ID = "1";
	private static final String WORKFLOW_CHECKSUM = "1";
	private AnalysisExecutionServicePhylogenomics workflowManagement;
	private PreparedWorkflowGalaxy preparedWorkflow;
	private GalaxyAnalysisId analysisId;
	private WorkflowInputsGalaxy workflowInputsGalaxy;

	/**
	 * Setup variables for tests.
	 * @throws WorkflowException 
	 */
	@Before
	public void setup() throws WorkflowException {
		MockitoAnnotations.initMocks(this);
		
		workflowManagement = new AnalysisExecutionServicePhylogenomics(galaxyWorkflowService,
				galaxyHistoriesService, workspaceServicePhylogenomics);
		
		RemoteWorkflowPhylogenomics remoteWorkflow = 
				new RemoteWorkflowPhylogenomics(WORKFLOW_ID, WORKFLOW_CHECKSUM, "1", "1");
		
		when(analysisSubmission.getRemoteWorkflow()).thenReturn(remoteWorkflow);
		when(analysisSubmission.getRemoteAnalysisId()).thenReturn(new GalaxyAnalysisId("1"));
		
		analysisId = new GalaxyAnalysisId("1");
		workflowInputsGalaxy = new WorkflowInputsGalaxy(workflowInputs);
		preparedWorkflow = new PreparedWorkflowGalaxy(analysisId, workflowInputsGalaxy);		
	}
	
	/**
	 * Tests successfully executing an analysis.
	 * @throws ExecutionManagerException
	 */
	@Test
	public void testExecuteAnalysisSuccess() throws ExecutionManagerException {
		when(galaxyWorkflowService.validateWorkflowByChecksum(WORKFLOW_CHECKSUM, WORKFLOW_ID)).
			thenReturn(true);
		when(workspaceServicePhylogenomics.prepareAnalysisWorkspace(analysisSubmission)).
			thenReturn(preparedWorkflow);
		when(galaxyWorkflowService.runWorkflow(workflowInputsGalaxy)).thenReturn(workflowOutputs);
		
		AnalysisSubmissionPhylogenomics returnedSubmission = 
				workflowManagement.executeAnalysis(analysisSubmission);
		
		assertEquals("analysisSubmission not equal to returned submission", analysisSubmission, returnedSubmission);
		
		verify(galaxyWorkflowService).validateWorkflowByChecksum(WORKFLOW_CHECKSUM, WORKFLOW_ID);
		verify(workspaceServicePhylogenomics).prepareAnalysisWorkspace(analysisSubmission);
		verify(galaxyWorkflowService).runWorkflow(workflowInputsGalaxy);
	}
	
	/**
	 * Tests failing to executing an analysis due to invalid workflow.
	 * @throws ExecutionManagerException
	 */
	@Test(expected=WorkflowChecksumInvalidException.class)
	public void testExecuteAnalysisFailInvalidWorkflow() throws ExecutionManagerException {
		when(galaxyWorkflowService.validateWorkflowByChecksum(WORKFLOW_CHECKSUM, WORKFLOW_ID)).
			thenThrow(new WorkflowChecksumInvalidException());
		
		workflowManagement.executeAnalysis(analysisSubmission);
	}
	
	/**
	 * Tests failing to prepare a workflow.
	 * @throws ExecutionManagerException
	 */
	@Test(expected=ExecutionManagerException.class)
	public void testExecuteAnalysisFailPrepareWorkflow() throws ExecutionManagerException {
		when(galaxyWorkflowService.validateWorkflowByChecksum(WORKFLOW_CHECKSUM, WORKFLOW_ID)).
			thenReturn(true);
		when(workspaceServicePhylogenomics.prepareAnalysisWorkspace(analysisSubmission)).
			thenThrow(new ExecutionManagerException());
		
		workflowManagement.executeAnalysis(analysisSubmission);
	}
	
	/**
	 * Tests failing to execute a workflow.
	 * @throws ExecutionManagerException
	 */
	@Test(expected=WorkflowException.class)
	public void testExecuteAnalysisFail() throws ExecutionManagerException {
		when(galaxyWorkflowService.validateWorkflowByChecksum(WORKFLOW_CHECKSUM, WORKFLOW_ID)).
			thenReturn(true);
		when(workspaceServicePhylogenomics.prepareAnalysisWorkspace(analysisSubmission)).
			thenReturn(preparedWorkflow);
		when(galaxyWorkflowService.runWorkflow(workflowInputsGalaxy)).
			thenThrow(new WorkflowException());
		
		workflowManagement.executeAnalysis(analysisSubmission);
	}
	
	/**
	 * Tests out successfully getting the status of a workflow.
	 * @throws ExecutionManagerException
	 */
	@Test
	public void testGetWorkflowStatusSuccess() throws ExecutionManagerException {
		WorkflowStatus workflowStatus = new WorkflowStatus(WorkflowState.OK, 1.0f);
		
		when(galaxyHistoriesService.getStatusForHistory(
				analysisSubmission.getRemoteAnalysisId().getRemoteAnalysisId())).thenReturn(workflowStatus);
		
		assertEquals(workflowStatus, workflowManagement.getWorkflowStatus(analysisSubmission));
	}
	
	/**
	 * Tests failure to get the status of a workflow (no status).
	 * @throws ExecutionManagerException
	 */
	@Test(expected=WorkflowException.class)
	public void testGetWorkflowStatusFailNoStatus() throws ExecutionManagerException {		
		when(galaxyHistoriesService.getStatusForHistory(analysisSubmission.
				getRemoteAnalysisId().getRemoteAnalysisId())).thenThrow(new WorkflowException());
		
		workflowManagement.getWorkflowStatus(analysisSubmission);
	}
}
