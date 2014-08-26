package ca.corefacility.bioinformatics.irida.service.analysis.execution.galaxy.phylogenonmics.impl.unit;

import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.exceptions.ExecutionManagerException;
import ca.corefacility.bioinformatics.irida.exceptions.WorkflowException;
import ca.corefacility.bioinformatics.irida.exceptions.galaxy.WorkflowChecksumInvalidException;
import ca.corefacility.bioinformatics.irida.model.workflow.WorkflowState;
import ca.corefacility.bioinformatics.irida.model.workflow.WorkflowStatus;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.AnalysisPhylogenomicsPipeline;
import ca.corefacility.bioinformatics.irida.model.workflow.galaxy.PreparedWorkflowGalaxy;
import ca.corefacility.bioinformatics.irida.model.workflow.galaxy.WorkflowInputsGalaxy;
import ca.corefacility.bioinformatics.irida.model.workflow.galaxy.phylogenomics.RemoteWorkflowPhylogenomics;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.galaxy.phylogenomics.AnalysisSubmissionPhylogenomics;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyHistoriesService;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyWorkflowService;
import ca.corefacility.bioinformatics.irida.repositories.analysis.submission.AnalysisSubmissionRepository;
import ca.corefacility.bioinformatics.irida.service.AnalysisService;
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
	
	@Mock private AnalysisSubmissionRepository analysisSubmissionRepository;
	@Mock private AnalysisService analysisService;
	@Mock private GalaxyHistoriesService galaxyHistoriesService;
	@Mock private GalaxyWorkflowService galaxyWorkflowService;
	@Mock private AnalysisSubmissionPhylogenomics analysisSubmission;
	@Mock private WorkspaceServicePhylogenomics workspaceServicePhylogenomics;
	@Mock private WorkflowInputs workflowInputs;
	@Mock private WorkflowOutputs workflowOutputs;
	@Mock private AnalysisPhylogenomicsPipeline analysisResults;

	private static final String WORKFLOW_ID = "1";
	private static final String WORKFLOW_CHECKSUM = "1";
	private AnalysisExecutionServicePhylogenomics workflowManagement;
	private PreparedWorkflowGalaxy preparedWorkflow;
	private String analysisId;
	private WorkflowInputsGalaxy workflowInputsGalaxy;
	
	private static final String TREE_LABEL = "tree";
	private static final String MATRIX_LABEL = "snp_matrix";
	private static final String TABLE_LABEL = "snp_table";

	/**
	 * Setup variables for tests.
	 * @throws WorkflowException 
	 */
	@Before
	public void setup() throws WorkflowException {
		MockitoAnnotations.initMocks(this);
		
		workflowManagement = new AnalysisExecutionServicePhylogenomics(analysisSubmissionRepository,
				analysisService, galaxyWorkflowService, galaxyHistoriesService,
				workspaceServicePhylogenomics);
		
		RemoteWorkflowPhylogenomics remoteWorkflow = 
				new RemoteWorkflowPhylogenomics(WORKFLOW_ID, WORKFLOW_CHECKSUM, "1", "1",
						TREE_LABEL, MATRIX_LABEL, TABLE_LABEL);
		
		when(analysisSubmission.getRemoteWorkflow()).thenReturn(remoteWorkflow);
		when(analysisSubmission.getRemoteAnalysisId()).thenReturn("1");
		
		when(analysisSubmissionRepository.save(analysisSubmission)).thenReturn(analysisSubmission);
		
		analysisId = "1";
		workflowInputsGalaxy = new WorkflowInputsGalaxy(workflowInputs);
		preparedWorkflow = new PreparedWorkflowGalaxy(analysisId, workflowInputsGalaxy);		
	}
	
	/**
	 * Tests successfully executing an analysis.
	 * @throws ExecutionManagerException
	 */
	@Test
	public void testExecuteAnalysisSuccess() throws ExecutionManagerException {
		when(analysisSubmission.getRemoteAnalysisId()).thenReturn(null);
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
		verify(analysisSubmissionRepository).save(analysisSubmission);
	}
	
	/**
	 * Tests failing to executing an analysis due to invalid workflow.
	 * @throws ExecutionManagerException
	 */
	@Test(expected=WorkflowChecksumInvalidException.class)
	public void testExecuteAnalysisFailInvalidWorkflow() throws ExecutionManagerException {
		when(analysisSubmission.getRemoteAnalysisId()).thenReturn(null);
		when(galaxyWorkflowService.validateWorkflowByChecksum(WORKFLOW_CHECKSUM, WORKFLOW_ID)).
			thenThrow(new WorkflowChecksumInvalidException());
		
		workflowManagement.executeAnalysis(analysisSubmission);
	}
	
	/**
	 * Tests failing to executing an analysis due to already being submitted.
	 * @throws IllegalArgumentException
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testExecuteAnalysisFailAlreadySubmitted() throws ExecutionManagerException {
		when(analysisSubmission.getRemoteAnalysisId()).thenReturn("1");
		
		workflowManagement.executeAnalysis(analysisSubmission);
	}
	
	/**
	 * Tests failing to prepare a workflow.
	 * @throws ExecutionManagerException
	 */
	@Test(expected=ExecutionManagerException.class)
	public void testExecuteAnalysisFailPrepareWorkflow() throws ExecutionManagerException {
		when(analysisSubmission.getRemoteAnalysisId()).thenReturn(null);
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
		when(analysisSubmission.getRemoteAnalysisId()).thenReturn(null);
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
				analysisSubmission.getRemoteAnalysisId())).thenReturn(workflowStatus);
		
		assertEquals(workflowStatus, workflowManagement.getWorkflowStatus(analysisSubmission));
	}
	
	/**
	 * Tests failure to get the status of a workflow (no status).
	 * @throws ExecutionManagerException
	 */
	@Test(expected=WorkflowException.class)
	public void testGetWorkflowStatusFailNoStatus() throws ExecutionManagerException {		
		when(galaxyHistoriesService.getStatusForHistory(analysisSubmission.
				getRemoteAnalysisId())).thenThrow(new WorkflowException());
		
		workflowManagement.getWorkflowStatus(analysisSubmission);
	}
	
	/**
	 * Tests successfully getting analysis results.
	 * @throws ExecutionManagerException
	 * @throws IOException 
	 */
	@Test
	public void testGetAnalysisResultsSuccess() throws ExecutionManagerException, IOException {
		String id = "invalid";
		
		when(analysisSubmission.getRemoteAnalysisId()).thenReturn(id);
		when(analysisSubmissionRepository.exists(id)).thenReturn(true);
		when(workspaceServicePhylogenomics.getAnalysisResults(analysisSubmission)).thenReturn(analysisResults);
		when(analysisService.create(analysisResults)).thenReturn(analysisResults);
		
		AnalysisPhylogenomicsPipeline actualResults = workflowManagement.transferAnalysisResults(analysisSubmission);
		assertEquals("analysisResults should be equal", analysisResults, actualResults);
		
		verify(analysisService).create(analysisResults);
	}
	
	/**
	 * Tests failing to get analysis results.
	 * @throws ExecutionManagerException
	 * @throws IOException 
	 */
	@Test(expected=ExecutionManagerException.class)
	public void testGetAnalysisResultsFail() throws ExecutionManagerException, IOException {
		String id = "invalid";
		
		when(analysisSubmission.getRemoteAnalysisId()).thenReturn(id);
		when(analysisSubmissionRepository.exists(id)).thenReturn(true);
		
		when(workspaceServicePhylogenomics.getAnalysisResults(analysisSubmission)).
			thenThrow(new ExecutionManagerException());
		
		workflowManagement.transferAnalysisResults(analysisSubmission);
	}
	
	/**
	 * Tests failing to get analysis results due to not being submitted (null id).
	 * @throws ExecutionManagerException
	 * @throws IOException 
	 */
	@Test(expected=NullPointerException.class)
	public void testGetAnalysisResultsFailNotSubmittedNullId() throws ExecutionManagerException, IOException {
		when(analysisSubmission.getRemoteAnalysisId()).thenReturn(null);
		
		workflowManagement.transferAnalysisResults(analysisSubmission);
	}
	
	/**
	 * Tests failing to get analysis results due to submission with invalid id.
	 * @throws ExecutionManagerException
	 * @throws IOException 
	 */
	@Test(expected=EntityNotFoundException.class)
	public void testGetAnalysisResultsFailAnalysisIdInvalid() throws ExecutionManagerException, IOException {
		String id = "invalid";
		
		when(analysisSubmission.getRemoteAnalysisId()).thenReturn(id);
		when(analysisSubmissionRepository.exists(id)).thenReturn(false);
		
		workflowManagement.transferAnalysisResults(analysisSubmission);
	}
}
