package ca.corefacility.bioinformatics.irida.service.analysis.execution.galaxy.impl.unit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.exceptions.ExecutionManagerException;
import ca.corefacility.bioinformatics.irida.exceptions.IridaWorkflowNotFoundException;
import ca.corefacility.bioinformatics.irida.exceptions.NoSuchValueException;
import ca.corefacility.bioinformatics.irida.exceptions.WorkflowException;
import ca.corefacility.bioinformatics.irida.exceptions.galaxy.WorkflowUploadException;
import ca.corefacility.bioinformatics.irida.model.enums.AnalysisState;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
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
import ca.corefacility.bioinformatics.irida.service.analysis.execution.galaxy.AnalysisExecutionServiceGalaxyAsyncSimplified;
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
	private AnalysisSubmission analysisPreparing;
	private AnalysisSubmission analysisPrepared;
	private AnalysisSubmission analysisSubmitting;
	private AnalysisSubmission analysisRunning;
	private AnalysisSubmission analysisFinishedRunning;
	private AnalysisSubmission analysisCompleting;
	private AnalysisSubmission analysisCompleted;
	private AnalysisSubmission analysisError;

	private static final UUID WORKFLOW_ID = UUID.randomUUID();
	private static final String REMOTE_WORKFLOW_ID = "1";
	private static final Long INTERNAL_ANALYSIS_ID = 2l;
	private static final String ANALYSIS_ID = "2";
	private AnalysisExecutionServiceGalaxySimplified workflowManagement;
	private PreparedWorkflowGalaxy preparedWorkflow;
	private WorkflowInputsGalaxy workflowInputsGalaxy;

	/**
	 * Setup variables for tests.
	 * 
	 * @throws IridaWorkflowNotFoundException
	 * @throws IOException
	 * @throws ExecutionManagerException
	 * @throws NoSuchValueException
	 */
	@Before
	public void setup() throws IridaWorkflowNotFoundException, IOException, ExecutionManagerException,
			NoSuchValueException {
		MockitoAnnotations.initMocks(this);

		String submissionName = "name";
		Set<SequenceFile> submissionInputFiles = Sets.newHashSet();

		analysisSubmission = new AnalysisSubmission(submissionName, submissionInputFiles, WORKFLOW_ID);
		analysisPreparing = new AnalysisSubmission(submissionName, submissionInputFiles, WORKFLOW_ID);
		analysisPrepared = new AnalysisSubmission(submissionName, submissionInputFiles, WORKFLOW_ID);
		analysisSubmitting = new AnalysisSubmission(submissionName, submissionInputFiles, WORKFLOW_ID);
		analysisRunning = new AnalysisSubmission(submissionName, submissionInputFiles, WORKFLOW_ID);
		analysisFinishedRunning = new AnalysisSubmission(submissionName, submissionInputFiles, WORKFLOW_ID);
		analysisCompleting = new AnalysisSubmission(submissionName, submissionInputFiles, WORKFLOW_ID);
		analysisCompleted = new AnalysisSubmission(submissionName, submissionInputFiles, WORKFLOW_ID);
		analysisError = new AnalysisSubmission(submissionName, submissionInputFiles, WORKFLOW_ID);

		AnalysisExecutionServiceGalaxyAsyncSimplified workflowManagementAsync = new AnalysisExecutionServiceGalaxyAsyncSimplified(
				analysisSubmissionService, analysisService, galaxyWorkflowService, analysisWorkspaceServiceSimplified,
				iridaWorkflowsService);
		workflowManagement = new AnalysisExecutionServiceGalaxySimplified(analysisSubmissionService,
				galaxyHistoriesService, workflowManagementAsync);

		when(iridaWorkflowsService.getIridaWorkflow(WORKFLOW_ID)).thenReturn(iridaWorkflow);
		when(iridaWorkflow.getWorkflowStructure()).thenReturn(iridaWorkflowStructure);
		when(iridaWorkflowStructure.getWorkflowFile()).thenReturn(workflowFile);

		when(analysisSubmissionService.create(analysisSubmission)).thenReturn(analysisSubmission);
		when(analysisSubmissionService.read(INTERNAL_ANALYSIS_ID)).thenReturn(analysisSubmission);

		analysisSubmission.setId(INTERNAL_ANALYSIS_ID);

		analysisPreparing.setId(INTERNAL_ANALYSIS_ID);
		analysisPreparing.setAnalysisState(AnalysisState.PREPARING);
		when(
				analysisSubmissionService.update(INTERNAL_ANALYSIS_ID,
						ImmutableMap.of("analysisState", AnalysisState.PREPARING))).thenReturn(analysisPreparing);
		when(analysisWorkspaceServiceSimplified.prepareAnalysisWorkspace(analysisPreparing)).thenReturn(ANALYSIS_ID);

		analysisPrepared.setId(INTERNAL_ANALYSIS_ID);
		analysisPrepared.setAnalysisState(AnalysisState.PREPARED);
		analysisPrepared.setRemoteAnalysisId(REMOTE_WORKFLOW_ID);
		analysisPrepared.setRemoteAnalysisId(ANALYSIS_ID);
		when(
				analysisSubmissionService.update(INTERNAL_ANALYSIS_ID, ImmutableMap.of("remoteAnalysisId", ANALYSIS_ID,
						"remoteWorkflowId", REMOTE_WORKFLOW_ID, "analysisState", AnalysisState.PREPARED))).thenReturn(
				analysisPrepared);

		analysisSubmitting.setId(INTERNAL_ANALYSIS_ID);
		analysisSubmitting.setAnalysisState(AnalysisState.SUBMITTING);
		analysisSubmitting.setRemoteAnalysisId(REMOTE_WORKFLOW_ID);
		analysisSubmitting.setRemoteAnalysisId(ANALYSIS_ID);
		when(
				analysisSubmissionService.update(INTERNAL_ANALYSIS_ID,
						ImmutableMap.of("analysisState", AnalysisState.SUBMITTING))).thenReturn(analysisSubmitting);

		analysisRunning.setId(INTERNAL_ANALYSIS_ID);
		analysisRunning.setAnalysisState(AnalysisState.RUNNING);
		analysisRunning.setRemoteAnalysisId(REMOTE_WORKFLOW_ID);
		analysisRunning.setRemoteAnalysisId(ANALYSIS_ID);
		when(
				analysisSubmissionService.update(INTERNAL_ANALYSIS_ID,
						ImmutableMap.of("analysisState", AnalysisState.RUNNING))).thenReturn(analysisRunning);

		analysisFinishedRunning.setId(INTERNAL_ANALYSIS_ID);
		analysisFinishedRunning.setAnalysisState(AnalysisState.FINISHED_RUNNING);
		analysisFinishedRunning.setRemoteAnalysisId(REMOTE_WORKFLOW_ID);
		analysisFinishedRunning.setRemoteAnalysisId(ANALYSIS_ID);

		analysisCompleting.setId(INTERNAL_ANALYSIS_ID);
		analysisCompleting.setAnalysisState(AnalysisState.SUBMITTING);
		analysisCompleting.setRemoteAnalysisId(REMOTE_WORKFLOW_ID);
		analysisCompleting.setRemoteAnalysisId(ANALYSIS_ID);
		when(
				analysisSubmissionService.update(INTERNAL_ANALYSIS_ID,
						ImmutableMap.of("analysisState", AnalysisState.COMPLETING))).thenReturn(analysisCompleting);
		when(analysisWorkspaceServiceSimplified.getAnalysisResults(analysisCompleting)).thenReturn(analysisResults);
		when(analysisService.create(analysisResults)).thenReturn(analysisResults);

		analysisCompleted.setId(INTERNAL_ANALYSIS_ID);
		analysisCompleted.setAnalysisState(AnalysisState.SUBMITTING);
		analysisCompleted.setRemoteAnalysisId(REMOTE_WORKFLOW_ID);
		analysisCompleted.setRemoteAnalysisId(ANALYSIS_ID);
		analysisCompleted.setAnalysis(analysisResults);
		when(
				analysisSubmissionService.update(INTERNAL_ANALYSIS_ID,
						ImmutableMap.of("analysis", analysisResults, "analysisState", AnalysisState.COMPLETED)))
				.thenReturn(analysisCompleted);

		analysisError.setId(INTERNAL_ANALYSIS_ID);
		analysisError.setAnalysisState(AnalysisState.ERROR);
		when(
				analysisSubmissionService.update(INTERNAL_ANALYSIS_ID,
						ImmutableMap.of("analysisState", AnalysisState.ERROR))).thenReturn(analysisError);

		analysisPrepared.setRemoteAnalysisId(ANALYSIS_ID);

		preparedWorkflow = new PreparedWorkflowGalaxy(ANALYSIS_ID, workflowInputsGalaxy);

		when(galaxyWorkflowService.uploadGalaxyWorkflow(workflowFile)).thenReturn(REMOTE_WORKFLOW_ID);
	}

	/**
	 * Tests successfully preparing an analysis submission.
	 * 
	 * @throws ExecutionManagerException
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws NoSuchValueException
	 * @throws ExecutionException
	 * @throws IridaWorkflowNotFoundException
	 */
	@Test
	public void testPrepareSubmissionSuccess() throws InterruptedException, ExecutionManagerException, IOException,
			NoSuchValueException, ExecutionException, IridaWorkflowNotFoundException {
		Future<AnalysisSubmission> preparedAnalysisFuture = workflowManagement.prepareSubmission(analysisSubmission);
		AnalysisSubmission returnedSubmission = preparedAnalysisFuture.get();

		assertEquals("analysisSubmission not equal to returned submission", analysisPrepared, returnedSubmission);

		verify(galaxyWorkflowService).uploadGalaxyWorkflow(workflowFile);
		verify(analysisWorkspaceServiceSimplified).prepareAnalysisWorkspace(analysisPreparing);
		verify(analysisSubmissionService).update(
				INTERNAL_ANALYSIS_ID,
				ImmutableMap.of("remoteWorkflowId", REMOTE_WORKFLOW_ID, "remoteAnalysisId", ANALYSIS_ID,
						"analysisState", AnalysisState.PREPARED));
	}

	/**
	 * Tests failing to prepare an analysis due to an issue when uploading the
	 * workflow.
	 * 
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws ExecutionManagerException
	 * @throws IridaWorkflowNotFoundException
	 * @throws ExecutionException
	 */
	@Test(expected = WorkflowUploadException.class)
	public void testPrepareSubmissionFailInvalidWorkflow() throws InterruptedException, IOException,
			IridaWorkflowNotFoundException, ExecutionManagerException, ExecutionException {
		when(galaxyWorkflowService.uploadGalaxyWorkflow(workflowFile)).thenThrow(
				new WorkflowUploadException(null, null));

		Future<AnalysisSubmission> preparedAnalysisFuture = workflowManagement.prepareSubmission(analysisSubmission);

		verify(analysisSubmissionService).update(INTERNAL_ANALYSIS_ID,
				ImmutableMap.of("analysisState", AnalysisState.ERROR));

		assertTrue(preparedAnalysisFuture.isDone());
		preparedAnalysisFuture.get();
	}

	/**
	 * Tests failing to prepare an analysis workspace.
	 * 
	 * @throws ExecutionManagerException
	 * @throws InterruptedException
	 * @throws IOException
	 * @throws IridaWorkflowNotFoundException
	 * @throws ExecutionException
	 */
	@Test(expected = ExecutionManagerException.class)
	public void testPrepareSubmissionFailWorkspace() throws ExecutionManagerException, InterruptedException,
			IridaWorkflowNotFoundException, IOException, ExecutionException {
		when(analysisWorkspaceServiceSimplified.prepareAnalysisWorkspace(any(AnalysisSubmission.class))).thenThrow(
				new ExecutionManagerException());

		Future<AnalysisSubmission> preparedAnalysisFuture = workflowManagement.prepareSubmission(analysisSubmission);

		verify(analysisSubmissionService).update(INTERNAL_ANALYSIS_ID,
				ImmutableMap.of("analysisState", AnalysisState.ERROR));

		assertTrue(preparedAnalysisFuture.isDone());
		preparedAnalysisFuture.get();
	}

	/**
	 * Tests successfully executing an analysis.
	 * 
	 * @throws ExecutionManagerException
	 * @throws IridaWorkflowNotFoundException
	 * @throws NoSuchValueException
	 * @throws ExecutionException
	 * @throws InterruptedException
	 */
	@Test
	public void testExecuteAnalysisSuccess() throws ExecutionManagerException, IridaWorkflowNotFoundException,
			NoSuchValueException, InterruptedException, ExecutionException {
		when(analysisWorkspaceServiceSimplified.prepareAnalysisFiles(any(AnalysisSubmission.class))).thenReturn(
				preparedWorkflow);

		Future<AnalysisSubmission> preparedAnalysisFuture = workflowManagement.executeAnalysis(analysisPrepared);
		AnalysisSubmission returnedSubmission = preparedAnalysisFuture.get();
		assertEquals("analysisSubmitted not equal to returned submission", analysisRunning, returnedSubmission);

		verify(analysisSubmissionService).update(INTERNAL_ANALYSIS_ID,
				ImmutableMap.of("analysisState", AnalysisState.SUBMITTING));
		verify(analysisWorkspaceServiceSimplified).prepareAnalysisFiles(analysisSubmitting);
		verify(galaxyWorkflowService).runWorkflow(workflowInputsGalaxy);
		verify(analysisSubmissionService).update(INTERNAL_ANALYSIS_ID,
				ImmutableMap.of("analysisState", AnalysisState.RUNNING));
	}

	/**
	 * Tests failing to executing an analysis due to already being submitted.
	 * 
	 * @throws ExecutionManagerException
	 * @throws IridaWorkflowNotFoundException
	 * @throws ExecutionException
	 * @throws InterruptedException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testExecuteAnalysisFailAlreadySubmitted() throws IridaWorkflowNotFoundException,
			ExecutionManagerException, InterruptedException, ExecutionException {
		analysisPrepared.setAnalysisState(AnalysisState.RUNNING);
		workflowManagement.executeAnalysis(analysisSubmission);
	}

	/**
	 * Tests failing to prepare a workflow.
	 * 
	 * @throws ExecutionManagerException
	 * @throws IridaWorkflowNotFoundException
	 * @throws ExecutionException
	 * @throws InterruptedException
	 */
	@Test(expected = ExecutionManagerException.class)
	public void testExecuteAnalysisFailPrepareWorkflow() throws IridaWorkflowNotFoundException,
			ExecutionManagerException, InterruptedException, ExecutionException {
		when(analysisWorkspaceServiceSimplified.prepareAnalysisFiles(any(AnalysisSubmission.class))).thenThrow(
				new ExecutionManagerException());

		Future<AnalysisSubmission> submittedAnalysisFuture = workflowManagement.executeAnalysis(analysisPrepared);

		verify(analysisSubmissionService).update(INTERNAL_ANALYSIS_ID,
				ImmutableMap.of("analysisState", AnalysisState.ERROR));

		submittedAnalysisFuture.get();
	}

	/**
	 * Tests failing to execute a workflow.
	 * 
	 * @throws ExecutionManagerException
	 * @throws IridaWorkflowNotFoundException
	 * @throws ExecutionException
	 * @throws InterruptedException
	 */
	@Test(expected = WorkflowException.class)
	public void testExecuteAnalysisFail() throws IridaWorkflowNotFoundException, ExecutionManagerException,
			InterruptedException, ExecutionException {
		when(analysisWorkspaceServiceSimplified.prepareAnalysisFiles(any(AnalysisSubmission.class))).thenReturn(
				preparedWorkflow);
		when(galaxyWorkflowService.runWorkflow(workflowInputsGalaxy)).thenThrow(new WorkflowException());

		Future<AnalysisSubmission> submittedAnalysisFuture = workflowManagement.executeAnalysis(analysisPrepared);

		verify(analysisSubmissionService).update(INTERNAL_ANALYSIS_ID,
				ImmutableMap.of("analysisState", AnalysisState.ERROR));

		submittedAnalysisFuture.get();
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
	 * @throws ExecutionException
	 * @throws InterruptedException
	 */
	@Test
	public void testTransferAnalysisResultsSuccess() throws ExecutionManagerException, IOException,
			IridaWorkflowNotFoundException, InterruptedException, ExecutionException {
		when(analysisSubmissionService.exists(INTERNAL_ANALYSIS_ID)).thenReturn(true);

		Future<AnalysisSubmission> actualCompletedSubmissionFuture = workflowManagement
				.transferAnalysisResults(analysisFinishedRunning);
		AnalysisSubmission actualCompletedSubmission = actualCompletedSubmissionFuture.get();
		assertEquals(analysisCompleted, actualCompletedSubmission);
		assertEquals("analysisResults should be equal", analysisResults, actualCompletedSubmission.getAnalysis());

		verify(analysisService).create(analysisResults);
		verify(analysisSubmissionService).update(INTERNAL_ANALYSIS_ID,
				ImmutableMap.of("analysis", analysisResults, "analysisState", AnalysisState.COMPLETED));
	}

	/**
	 * Tests failing to get analysis results.
	 * 
	 * @throws ExecutionManagerException
	 * @throws IOException
	 * @throws IridaWorkflowNotFoundException
	 * @throws ExecutionException
	 * @throws InterruptedException
	 */
	@Test(expected = ExecutionManagerException.class)
	public void testTransferAnalysisResultsFail() throws ExecutionManagerException, IOException,
			IridaWorkflowNotFoundException, InterruptedException, ExecutionException {
		when(analysisSubmissionService.exists(INTERNAL_ANALYSIS_ID)).thenReturn(true);
		when(analysisWorkspaceServiceSimplified.getAnalysisResults(analysisCompleting)).thenThrow(
				new ExecutionManagerException());

		Future<AnalysisSubmission> actualCompletedSubmissionFuture = workflowManagement
				.transferAnalysisResults(analysisFinishedRunning);
		actualCompletedSubmissionFuture.get();
	}

	/**
	 * Tests failing to get analysis results due to not being submitted (null
	 * id).
	 * 
	 * @throws ExecutionManagerException
	 * @throws IOException
	 * @throws IridaWorkflowNotFoundException
	 * @throws ExecutionException
	 * @throws InterruptedException
	 */
	@Test(expected = NullPointerException.class)
	public void testTransferAnalysisResultsFailNotSubmittedNullId() throws ExecutionManagerException, IOException,
			IridaWorkflowNotFoundException, InterruptedException, ExecutionException {
		when(analysisSubmissionService.exists(INTERNAL_ANALYSIS_ID)).thenReturn(true);
		analysisCompleting.setRemoteAnalysisId(null);

		Future<AnalysisSubmission> actualCompletedSubmissionFuture = workflowManagement
				.transferAnalysisResults(analysisFinishedRunning);
		actualCompletedSubmissionFuture.get();
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
