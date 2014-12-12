package ca.corefacility.bioinformatics.irida.service.analysis.execution.galaxy.impl.unit;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.exceptions.ExecutionManagerException;
import ca.corefacility.bioinformatics.irida.exceptions.IridaWorkflowNotFoundException;
import ca.corefacility.bioinformatics.irida.exceptions.WorkflowException;
import ca.corefacility.bioinformatics.irida.exceptions.galaxy.WorkflowUploadException;
import ca.corefacility.bioinformatics.irida.model.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.enums.AnalysisState;
import ca.corefacility.bioinformatics.irida.model.workflow.IridaWorkflow;
import ca.corefacility.bioinformatics.irida.model.workflow.IridaWorkflowStructure;
import ca.corefacility.bioinformatics.irida.model.workflow.WorkflowState;
import ca.corefacility.bioinformatics.irida.model.workflow.WorkflowStatus;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.Analysis;
import ca.corefacility.bioinformatics.irida.model.workflow.galaxy.PreparedWorkflowGalaxy;
import ca.corefacility.bioinformatics.irida.model.workflow.galaxy.WorkflowInputsGalaxy;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyHistoriesService;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyWorkflowService;
import ca.corefacility.bioinformatics.irida.service.AnalysisService;
import ca.corefacility.bioinformatics.irida.service.AnalysisSubmissionService;
import ca.corefacility.bioinformatics.irida.service.analysis.execution.galaxy.AnalysisExecutionServiceGalaxySimplified;
import ca.corefacility.bioinformatics.irida.service.analysis.workspace.galaxy.AnalysisWorkspaceServiceGalaxySimplified;
import ca.corefacility.bioinformatics.irida.service.workflow.IridaWorkflowsService;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;

/**
 * Tests out an execution service for Galaxy analyses.
 * 
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 */
public class AnalysisExecutionServiceGalaxySimplifiedTest {

	@Mock
	private AnalysisSubmissionService analysisSubmissionService;
	@Mock
	private AnalysisService analysisService;
	@Mock
	private GalaxyHistoriesService galaxyHistoriesService;
	@Mock
	private GalaxyWorkflowService galaxyWorkflowService;
	@Mock
	private AnalysisWorkspaceServiceGalaxySimplified analysisWorkspaceServiceSimplified;
	@Mock
	private Analysis analysisResults;
	@Mock
	private IridaWorkflowsService iridaWorkflowsService;

	@Mock
	private IridaWorkflow iridaWorkflow;
	@Mock
	private IridaWorkflowStructure iridaWorkflowStructure;
	@Mock
	private Path workflowFile;

	private AnalysisSubmission analysisSubmission;
	private AnalysisSubmission analysisSubmitted;

	private static final UUID WORKFLOW_ID = UUID.randomUUID();
	private static final String REMOTE_WORKFLOW_ID = "1";
	private static final Long INTERNAL_ANALYSIS_ID = 2l;
	private static final String ANALYSIS_ID = "2";
	private AnalysisExecutionServiceGalaxySimplified workflowManagement;
	private PreparedWorkflowGalaxy preparedWorkflow;
	private WorkflowInputsGalaxy workflowInputsGalaxy;

	private Map<String, Object> analysisIdMap;

	/**
	 * Setup variables for tests.
	 * 
	 * @throws IridaWorkflowNotFoundException
	 * @throws IOException
	 * @throws ExecutionManagerException
	 */
	@Before
	public void setup() throws IridaWorkflowNotFoundException, IOException, ExecutionManagerException {
		MockitoAnnotations.initMocks(this);

		String submissionName = "name";
		Set<SequenceFile> submissionInputFiles = Sets.newHashSet();

		analysisSubmission = new AnalysisSubmission(submissionName, submissionInputFiles, WORKFLOW_ID);
		analysisSubmitted = new AnalysisSubmission(submissionName, submissionInputFiles, WORKFLOW_ID);

		workflowManagement = new AnalysisExecutionServiceGalaxySimplified(analysisSubmissionService, analysisService,
				galaxyWorkflowService, galaxyHistoriesService, analysisWorkspaceServiceSimplified,
				iridaWorkflowsService);

		when(iridaWorkflowsService.getIridaWorkflow(WORKFLOW_ID)).thenReturn(iridaWorkflow);
		when(iridaWorkflow.getWorkflowStructure()).thenReturn(iridaWorkflowStructure);
		when(iridaWorkflowStructure.getWorkflowFile()).thenReturn(workflowFile);

		when(analysisSubmissionService.create(analysisSubmission)).thenReturn(analysisSubmission);
		when(analysisSubmissionService.read(INTERNAL_ANALYSIS_ID)).thenReturn(analysisSubmission);

		analysisSubmission.setId(INTERNAL_ANALYSIS_ID);
		analysisSubmitted.setId(INTERNAL_ANALYSIS_ID);
		analysisSubmission.setAnalysisState(AnalysisState.PREPARING);
		analysisSubmitted.setAnalysisState(AnalysisState.PREPARING);

		analysisSubmitted.setRemoteAnalysisId(ANALYSIS_ID);

		preparedWorkflow = new PreparedWorkflowGalaxy(ANALYSIS_ID, workflowInputsGalaxy);

		analysisIdMap = ImmutableMap.of();

		when(galaxyWorkflowService.uploadGalaxyWorkflow(workflowFile)).thenReturn(REMOTE_WORKFLOW_ID);
		when(analysisWorkspaceServiceSimplified.prepareAnalysisWorkspace(analysisSubmission)).thenReturn(ANALYSIS_ID);
		when(
				analysisSubmissionService.update(INTERNAL_ANALYSIS_ID,
						ImmutableMap.of("remoteAnalysisId", ANALYSIS_ID, "remoteWorkflowId", REMOTE_WORKFLOW_ID)))
				.thenReturn(analysisSubmitted);
	}

	/**
	 * Tests successfully preparing an analysis submission.
	 * 
	 * @throws ExecutionManagerException
	 * @throws IOException
	 * @throws IridaWorkflowNotFoundException
	 */
	@Test
	public void testPrepareSubmissionSuccess() throws ExecutionManagerException, IridaWorkflowNotFoundException,
			IOException {
		AnalysisSubmission returnedSubmission = workflowManagement.prepareSubmission(analysisSubmission);

		assertEquals("analysisSubmission not equal to returned submission", analysisSubmitted, returnedSubmission);

		verify(galaxyWorkflowService).uploadGalaxyWorkflow(workflowFile);
		verify(analysisWorkspaceServiceSimplified).prepareAnalysisWorkspace(analysisSubmission);
		verify(analysisSubmissionService).update(INTERNAL_ANALYSIS_ID,
				ImmutableMap.of("remoteWorkflowId", REMOTE_WORKFLOW_ID, "remoteAnalysisId", ANALYSIS_ID));
	}

	/**
	 * Tests failing to prepare an analysis due to an issue when uploading the
	 * workflow.
	 * 
	 * @throws ExecutionManagerException
	 * @throws IOException
	 * @throws IridaWorkflowNotFoundException
	 */
	@Test(expected = WorkflowUploadException.class)
	public void testPrepareSubmissionFailInvalidWorkflow() throws ExecutionManagerException, IOException,
			IridaWorkflowNotFoundException {
		when(galaxyWorkflowService.uploadGalaxyWorkflow(workflowFile)).thenThrow(
				new WorkflowUploadException(null, null));

		workflowManagement.prepareSubmission(analysisSubmission);
	}

	/**
	 * Tests failing to prepare an analysis workspace.
	 * 
	 * @throws ExecutionManagerException
	 * @throws IOException
	 * @throws IridaWorkflowNotFoundException
	 */
	@Test(expected = ExecutionManagerException.class)
	public void testPrepareSubmissionFailWorkspace() throws ExecutionManagerException, IridaWorkflowNotFoundException,
			IOException {
		when(analysisWorkspaceServiceSimplified.prepareAnalysisWorkspace(analysisSubmission)).thenThrow(
				new ExecutionManagerException());

		workflowManagement.prepareSubmission(analysisSubmission);
	}

	/**
	 * Tests successfully executing an analysis.
	 * 
	 * @throws ExecutionManagerException
	 * @throws IridaWorkflowNotFoundException
	 */
	@Test
	public void testExecuteAnalysisSuccess() throws ExecutionManagerException, IridaWorkflowNotFoundException {
		analysisSubmission.setAnalysisState(AnalysisState.SUBMITTING);
		analysisSubmission.setRemoteAnalysisId(ANALYSIS_ID);
		when(analysisWorkspaceServiceSimplified.prepareAnalysisFiles(analysisSubmission)).thenReturn(preparedWorkflow);

		AnalysisSubmission returnedSubmission = workflowManagement.executeAnalysis(analysisSubmission);

		assertEquals("analysisSubmission not equal to returned submission", analysisSubmission, returnedSubmission);

		verify(analysisWorkspaceServiceSimplified).prepareAnalysisFiles(analysisSubmission);
		verify(galaxyWorkflowService).runWorkflow(workflowInputsGalaxy);
	}

	/**
	 * Tests failing to executing an analysis due to already being submitted.
	 * 
	 * @throws IridaWorkflowNotFoundException
	 * @throws IllegalArgumentException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testExecuteAnalysisFailAlreadySubmitted() throws ExecutionManagerException,
			IridaWorkflowNotFoundException {
		analysisSubmission.setRemoteAnalysisId(ANALYSIS_ID);
		analysisSubmission.setAnalysisState(AnalysisState.RUNNING);

		workflowManagement.executeAnalysis(analysisSubmission);
	}

	/**
	 * Tests failing to prepare a workflow.
	 * 
	 * @throws ExecutionManagerException
	 * @throws IridaWorkflowNotFoundException
	 */
	@Test(expected = ExecutionManagerException.class)
	public void testExecuteAnalysisFailPrepareWorkflow() throws ExecutionManagerException,
			IridaWorkflowNotFoundException {
		analysisSubmission.setAnalysisState(AnalysisState.SUBMITTING);
		analysisSubmission.setRemoteAnalysisId(ANALYSIS_ID);
		when(analysisWorkspaceServiceSimplified.prepareAnalysisFiles(analysisSubmission)).thenThrow(
				new ExecutionManagerException());

		workflowManagement.executeAnalysis(analysisSubmission);
	}

	/**
	 * Tests failing to execute a workflow.
	 * 
	 * @throws ExecutionManagerException
	 * @throws IridaWorkflowNotFoundException
	 */
	@Test(expected = WorkflowException.class)
	public void testExecuteAnalysisFail() throws ExecutionManagerException, IridaWorkflowNotFoundException {
		analysisSubmission.setAnalysisState(AnalysisState.SUBMITTING);
		analysisSubmission.setRemoteAnalysisId(ANALYSIS_ID);
		when(analysisWorkspaceServiceSimplified.prepareAnalysisFiles(analysisSubmission)).thenReturn(preparedWorkflow);
		when(galaxyWorkflowService.runWorkflow(workflowInputsGalaxy)).thenThrow(new WorkflowException());

		workflowManagement.executeAnalysis(analysisSubmission);
	}

	/**
	 * Tests out successfully getting the status of a workflow.
	 * 
	 * @throws ExecutionManagerException
	 */
	@Test
	public void testGetWorkflowStatusSuccess() throws ExecutionManagerException {
		WorkflowStatus workflowStatus = new WorkflowStatus(WorkflowState.OK, 1.0f);
		analysisSubmission.setRemoteAnalysisId(ANALYSIS_ID);

		when(galaxyHistoriesService.getStatusForHistory(analysisSubmission.getRemoteAnalysisId())).thenReturn(
				workflowStatus);

		assertEquals(workflowStatus, workflowManagement.getWorkflowStatus(analysisSubmission));
	}

	/**
	 * Tests failure to get the status of a workflow (no status).
	 * 
	 * @throws ExecutionManagerException
	 */
	@Test(expected = WorkflowException.class)
	public void testGetWorkflowStatusFailNoStatus() throws ExecutionManagerException {
		analysisSubmission.setRemoteAnalysisId(ANALYSIS_ID);

		when(galaxyHistoriesService.getStatusForHistory(analysisSubmission.getRemoteAnalysisId())).thenThrow(
				new WorkflowException());

		workflowManagement.getWorkflowStatus(analysisSubmission);
	}

	/**
	 * Tests successfully getting analysis results.
	 * 
	 * @throws ExecutionManagerException
	 * @throws IOException
	 * @throws IridaWorkflowNotFoundException
	 */
	@Test
	public void testTransferAnalysisResultsSuccess() throws ExecutionManagerException, IOException,
			IridaWorkflowNotFoundException {
		analysisSubmission.setRemoteAnalysisId(ANALYSIS_ID);
		analysisSubmission.setAnalysisState(AnalysisState.FINISHED_RUNNING);
		when(analysisSubmissionService.exists(INTERNAL_ANALYSIS_ID)).thenReturn(true);
		when(analysisWorkspaceServiceSimplified.getAnalysisResults(analysisSubmission)).thenReturn(analysisResults);
		when(analysisService.create(analysisResults)).thenReturn(analysisResults);

		Analysis actualResults = workflowManagement.transferAnalysisResults(analysisSubmission);
		assertEquals("analysisResults should be equal", analysisResults, actualResults);

		verify(analysisService).create(analysisResults);
	}

	/**
	 * Tests failing to get analysis results.
	 * 
	 * @throws ExecutionManagerException
	 * @throws IOException
	 * @throws IridaWorkflowNotFoundException
	 */
	@Test(expected = ExecutionManagerException.class)
	public void testGetAnalysisResultsFail() throws ExecutionManagerException, IOException,
			IridaWorkflowNotFoundException {
		analysisSubmission.setRemoteAnalysisId(ANALYSIS_ID);
		analysisSubmission.setAnalysisState(AnalysisState.FINISHED_RUNNING);
		when(analysisSubmissionService.exists(INTERNAL_ANALYSIS_ID)).thenReturn(true);
		when(analysisWorkspaceServiceSimplified.getAnalysisResults(analysisSubmission)).thenThrow(
				new ExecutionManagerException());

		workflowManagement.transferAnalysisResults(analysisSubmission);
	}

	/**
	 * Tests failing to get analysis results due to not being submitted (null
	 * id).
	 * 
	 * @throws ExecutionManagerException
	 * @throws IOException
	 * @throws IridaWorkflowNotFoundException
	 */
	@Test(expected = NullPointerException.class)
	public void testGetAnalysisResultsFailNotSubmittedNullId() throws ExecutionManagerException, IOException,
			IridaWorkflowNotFoundException {
		analysisSubmission.setRemoteAnalysisId(null);
		analysisSubmission.setAnalysisState(AnalysisState.FINISHED_RUNNING);

		workflowManagement.transferAnalysisResults(analysisSubmission);
	}

	/**
	 * Tests failing to get analysis results due to submission with invalid id.
	 * 
	 * @throws ExecutionManagerException
	 * @throws IOException
	 * @throws IridaWorkflowNotFoundException
	 */
	@Test(expected = EntityNotFoundException.class)
	public void testGetAnalysisResultsFailAnalysisIdInvalid() throws ExecutionManagerException, IOException,
			IridaWorkflowNotFoundException {
		analysisSubmission.setRemoteAnalysisId(ANALYSIS_ID);
		analysisSubmission.setAnalysisState(AnalysisState.FINISHED_RUNNING);
		when(analysisSubmissionService.exists(INTERNAL_ANALYSIS_ID)).thenReturn(false);

		workflowManagement.transferAnalysisResults(analysisSubmission);
	}
}
