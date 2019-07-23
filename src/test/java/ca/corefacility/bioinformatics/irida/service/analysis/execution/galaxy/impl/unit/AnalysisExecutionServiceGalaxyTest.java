package ca.corefacility.bioinformatics.irida.service.analysis.execution.galaxy.impl.unit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doThrow;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import ca.corefacility.bioinformatics.irida.exceptions.*;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.access.AccessDeniedException;

import com.github.jmchilton.blend4j.galaxy.beans.HistoryDeleteResponse;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import ca.corefacility.bioinformatics.irida.exceptions.galaxy.WorkflowUploadException;
import ca.corefacility.bioinformatics.irida.model.enums.AnalysisCleanedState;
import ca.corefacility.bioinformatics.irida.model.enums.AnalysisState;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequencingObject;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SingleEndSequenceFile;
import ca.corefacility.bioinformatics.irida.model.workflow.IridaWorkflow;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.Analysis;
import ca.corefacility.bioinformatics.irida.model.workflow.execution.galaxy.GalaxyWorkflowState;
import ca.corefacility.bioinformatics.irida.model.workflow.execution.galaxy.GalaxyWorkflowStatus;
import ca.corefacility.bioinformatics.irida.model.workflow.execution.galaxy.PreparedWorkflowGalaxy;
import ca.corefacility.bioinformatics.irida.model.workflow.execution.galaxy.WorkflowInputsGalaxy;
import ca.corefacility.bioinformatics.irida.model.workflow.structure.IridaWorkflowStructure;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;
import ca.corefacility.bioinformatics.irida.pipeline.results.AnalysisSubmissionSampleProcessor;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyHistoriesService;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyLibrariesService;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyWorkflowService;
import ca.corefacility.bioinformatics.irida.service.AnalysisService;
import ca.corefacility.bioinformatics.irida.service.AnalysisSubmissionService;
import ca.corefacility.bioinformatics.irida.service.analysis.execution.galaxy.AnalysisExecutionServiceGalaxy;
import ca.corefacility.bioinformatics.irida.service.analysis.execution.galaxy.AnalysisExecutionServiceGalaxyAsync;
import ca.corefacility.bioinformatics.irida.service.analysis.execution.galaxy.AnalysisExecutionServiceGalaxyCleanupAsync;
import ca.corefacility.bioinformatics.irida.service.analysis.workspace.galaxy.AnalysisWorkspaceServiceGalaxy;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;
import ca.corefacility.bioinformatics.irida.service.workflow.IridaWorkflowsService;

/**
 * Tests out an execution service for Galaxy analyses.
 * 
 *
 */
public class AnalysisExecutionServiceGalaxyTest {

	@Mock
	private AnalysisSubmissionService analysisSubmissionService;
	@Mock
	private AnalysisService analysisService;
	@Mock
	private GalaxyHistoriesService galaxyHistoriesService;
	@Mock
	private GalaxyWorkflowService galaxyWorkflowService;
	@Mock
	private GalaxyLibrariesService galaxyLibrariesService;
	@Mock
	private AnalysisWorkspaceServiceGalaxy analysisWorkspaceService;
	@Mock
	private Analysis analysisResults;
	@Mock
	private IridaWorkflowsService iridaWorkflowsService;
	@Mock
	private AnalysisSubmissionSampleProcessor analysisSubmissionSampleProcessor;
	@Mock
	private SampleService sampleService;

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
	private AnalysisSubmission analysisCompletedCleaning;
	private AnalysisSubmission analysisCompletedCleaned;
	private AnalysisSubmission analysisError;
	private AnalysisSubmission analysisErrorCleaning;
	private AnalysisSubmission analysisErrorCleaned;

	private static final UUID WORKFLOW_ID = UUID.randomUUID();
	private static final String REMOTE_WORKFLOW_ID = "1";
	private static final Long INTERNAL_ANALYSIS_ID = 2L;
	private static final String ANALYSIS_ID = "2";
	private static final String LIBRARY_ID = "3";
	private AnalysisExecutionServiceGalaxy workflowManagement;
	private PreparedWorkflowGalaxy preparedWorkflow;
	private WorkflowInputsGalaxy workflowInputsGalaxy;

	/**
	 * Setup variables for tests.
	 * 
	 * @throws IridaWorkflowNotFoundException
	 * @throws IOException
	 * @throws ExecutionManagerException
	 * @throws NoSuchValueException
	 * @throws IridaWorkflowAnalysisTypeException 
	 */
	@Before
	public void setup() throws IridaWorkflowNotFoundException, IOException, ExecutionManagerException,
			NoSuchValueException, IridaWorkflowAnalysisTypeException, AnalysisAlreadySetException {
		MockitoAnnotations.initMocks(this);

		String submissionName = "name";
		Set<SequencingObject> submissionInputFiles = Sets.newHashSet(new SingleEndSequenceFile(new SequenceFile()));

		analysisSubmission = AnalysisSubmission.builder(WORKFLOW_ID).name(submissionName + "intial").inputFiles(submissionInputFiles).build();
		analysisPreparing = AnalysisSubmission.builder(WORKFLOW_ID).name(submissionName + "preparing").inputFiles(submissionInputFiles).build();
		analysisPrepared = AnalysisSubmission.builder(WORKFLOW_ID).name(submissionName + "prepared").inputFiles(submissionInputFiles).build();
		analysisSubmitting = AnalysisSubmission.builder(WORKFLOW_ID).name(submissionName + "submitting").inputFiles(submissionInputFiles).build();
		analysisRunning = AnalysisSubmission.builder(WORKFLOW_ID).name(submissionName + "running").inputFiles(submissionInputFiles).build();
		analysisFinishedRunning = AnalysisSubmission.builder(WORKFLOW_ID).name(submissionName + "finishedrunning").inputFiles(submissionInputFiles).build();
		analysisCompleting = AnalysisSubmission.builder(WORKFLOW_ID).name(submissionName + "completing").inputFiles(submissionInputFiles).build();
		analysisCompleted = AnalysisSubmission.builder(WORKFLOW_ID).name(submissionName + "completed").inputFiles(submissionInputFiles).build();
		analysisCompletedCleaning = AnalysisSubmission.builder(WORKFLOW_ID).name(submissionName + "cleaning").inputFiles(submissionInputFiles).build();
		analysisCompletedCleaned = AnalysisSubmission.builder(WORKFLOW_ID).name(submissionName + "cleaned").inputFiles(submissionInputFiles).build();
		analysisError = AnalysisSubmission.builder(WORKFLOW_ID).name(submissionName + "error").inputFiles(submissionInputFiles).build();
		analysisErrorCleaning = AnalysisSubmission.builder(WORKFLOW_ID).name(submissionName + "errorcleaning").inputFiles(submissionInputFiles).build();
		analysisErrorCleaned = AnalysisSubmission.builder(WORKFLOW_ID).name(submissionName + "errorcleaned").inputFiles(submissionInputFiles).build();

		AnalysisExecutionServiceGalaxyAsync workflowManagementAsync = new AnalysisExecutionServiceGalaxyAsync(
				analysisSubmissionService, analysisService, galaxyWorkflowService, analysisWorkspaceService,
				iridaWorkflowsService, analysisSubmissionSampleProcessor);
		AnalysisExecutionServiceGalaxyCleanupAsync analysisExecutionServiceGalaxyCleanupAsync = new AnalysisExecutionServiceGalaxyCleanupAsync(
				analysisSubmissionService, galaxyWorkflowService, galaxyHistoriesService, galaxyLibrariesService);
		workflowManagement = new AnalysisExecutionServiceGalaxy(analysisSubmissionService, galaxyHistoriesService,
				workflowManagementAsync, analysisExecutionServiceGalaxyCleanupAsync);

		when(iridaWorkflowsService.getIridaWorkflow(WORKFLOW_ID)).thenReturn(iridaWorkflow);
		when(iridaWorkflow.getWorkflowStructure()).thenReturn(iridaWorkflowStructure);
		when(iridaWorkflowStructure.getWorkflowFile()).thenReturn(workflowFile);

		when(analysisSubmissionService.create(analysisSubmission)).thenReturn(analysisSubmission);
		when(analysisSubmissionService.read(INTERNAL_ANALYSIS_ID)).thenReturn(analysisSubmission);

		analysisSubmission.setId(INTERNAL_ANALYSIS_ID);

		analysisPreparing.setId(INTERNAL_ANALYSIS_ID);
		analysisPreparing.setAnalysisState(AnalysisState.PREPARING);

		when(analysisWorkspaceService.prepareAnalysisWorkspace(analysisPreparing)).thenReturn(ANALYSIS_ID);

		analysisPrepared.setId(INTERNAL_ANALYSIS_ID);
		analysisPrepared.setAnalysisState(AnalysisState.PREPARED);
		analysisPrepared.setRemoteWorkflowId(REMOTE_WORKFLOW_ID);
		analysisPrepared.setRemoteAnalysisId(ANALYSIS_ID);

		analysisSubmitting.setId(INTERNAL_ANALYSIS_ID);
		analysisSubmitting.setAnalysisState(AnalysisState.SUBMITTING);
		analysisSubmitting.setRemoteWorkflowId(REMOTE_WORKFLOW_ID);
		analysisSubmitting.setRemoteAnalysisId(ANALYSIS_ID);

		analysisRunning.setId(INTERNAL_ANALYSIS_ID);
		analysisRunning.setAnalysisState(AnalysisState.RUNNING);
		analysisRunning.setRemoteWorkflowId(REMOTE_WORKFLOW_ID);
		analysisRunning.setRemoteAnalysisId(ANALYSIS_ID);
		analysisRunning.setRemoteInputDataId(LIBRARY_ID);

		analysisFinishedRunning.setId(INTERNAL_ANALYSIS_ID);
		analysisFinishedRunning.setAnalysisState(AnalysisState.FINISHED_RUNNING);
		analysisFinishedRunning.setRemoteWorkflowId(REMOTE_WORKFLOW_ID);
		analysisFinishedRunning.setRemoteAnalysisId(ANALYSIS_ID);
		analysisFinishedRunning.setRemoteInputDataId(LIBRARY_ID);

		analysisCompleting.setId(INTERNAL_ANALYSIS_ID);
		analysisCompleting.setAnalysisState(AnalysisState.COMPLETING);
		analysisCompleting.setRemoteWorkflowId(REMOTE_WORKFLOW_ID);
		analysisCompleting.setRemoteAnalysisId(ANALYSIS_ID);
		analysisCompleting.setRemoteInputDataId(LIBRARY_ID);

		when(analysisWorkspaceService.getAnalysisResults(analysisCompleting)).thenReturn(analysisResults);
		when(analysisService.create(analysisResults)).thenReturn(analysisResults);

		analysisCompleted.setId(INTERNAL_ANALYSIS_ID);
		analysisCompleted.setAnalysisState(AnalysisState.COMPLETED);
		analysisCompleted.setRemoteWorkflowId(REMOTE_WORKFLOW_ID);
		analysisCompleted.setRemoteAnalysisId(ANALYSIS_ID);
		analysisCompleted.setAnalysisCleanedState(AnalysisCleanedState.NOT_CLEANED);
		analysisCompleted.setAnalysis(analysisResults);
		analysisCompleted.setRemoteInputDataId(LIBRARY_ID);
		analysisCompleted.setUpdateSamples(true);
		
		analysisCompletedCleaning.setId(INTERNAL_ANALYSIS_ID);
		analysisCompletedCleaning.setAnalysisState(AnalysisState.COMPLETED);
		analysisCompletedCleaning.setRemoteWorkflowId(REMOTE_WORKFLOW_ID);
		analysisCompletedCleaning.setRemoteAnalysisId(ANALYSIS_ID);
		analysisCompletedCleaning.setAnalysisCleanedState(AnalysisCleanedState.CLEANING);
		analysisCompletedCleaning.setAnalysis(analysisResults);
		analysisCompletedCleaning.setRemoteInputDataId(LIBRARY_ID);
		
		analysisCompletedCleaned.setId(INTERNAL_ANALYSIS_ID);
		analysisCompletedCleaned.setAnalysisState(AnalysisState.COMPLETED);
		analysisCompletedCleaned.setRemoteWorkflowId(REMOTE_WORKFLOW_ID);
		analysisCompletedCleaned.setRemoteAnalysisId(ANALYSIS_ID);
		analysisCompletedCleaned.setAnalysisCleanedState(AnalysisCleanedState.CLEANED);
		analysisCompletedCleaned.setAnalysis(analysisResults);
		analysisCompletedCleaned.setRemoteInputDataId(LIBRARY_ID);

		analysisError.setId(INTERNAL_ANALYSIS_ID);
		analysisError.setAnalysisState(AnalysisState.ERROR);
		analysisError.setRemoteWorkflowId(REMOTE_WORKFLOW_ID);
		analysisError.setAnalysisCleanedState(AnalysisCleanedState.NOT_CLEANED);
		
		analysisErrorCleaning.setId(INTERNAL_ANALYSIS_ID);
		analysisErrorCleaning.setAnalysisState(AnalysisState.ERROR);
		analysisErrorCleaning.setRemoteWorkflowId(REMOTE_WORKFLOW_ID);
		analysisErrorCleaning.setAnalysisCleanedState(AnalysisCleanedState.CLEANING);
		
		analysisErrorCleaned.setId(INTERNAL_ANALYSIS_ID);
		analysisErrorCleaned.setAnalysisState(AnalysisState.ERROR);
		analysisErrorCleaned.setRemoteWorkflowId(REMOTE_WORKFLOW_ID);
		analysisErrorCleaned.setAnalysisCleanedState(AnalysisCleanedState.CLEANED);

		analysisPrepared.setRemoteAnalysisId(ANALYSIS_ID);

		preparedWorkflow = new PreparedWorkflowGalaxy(ANALYSIS_ID, LIBRARY_ID, workflowInputsGalaxy);

		when(galaxyWorkflowService.uploadGalaxyWorkflow(workflowFile)).thenReturn(REMOTE_WORKFLOW_ID);
		
		when(galaxyHistoriesService.deleteHistory(ANALYSIS_ID)).thenReturn(new HistoryDeleteResponse());
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
		analysisSubmission.setAnalysisState(AnalysisState.NEW);
		
		when(analysisSubmissionService.update(analysisSubmission)).thenReturn(analysisPreparing);
		when(analysisSubmissionService.update(analysisPreparing)).thenReturn(analysisPrepared);
		
		Future<AnalysisSubmission> preparedAnalysisFuture = workflowManagement.prepareSubmission(analysisSubmission);
		AnalysisSubmission returnedSubmission = preparedAnalysisFuture.get();

		assertEquals("analysisSubmission not equal to returned submission", analysisPrepared, returnedSubmission);

		verify(galaxyWorkflowService).uploadGalaxyWorkflow(workflowFile);
		verify(analysisWorkspaceService).prepareAnalysisWorkspace(analysisPreparing);
		verify(analysisSubmissionService, times(2)).update(any(AnalysisSubmission.class));
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

		when(analysisSubmissionService.update(analysisSubmission)).thenReturn(analysisPreparing);

		analysisSubmission.setAnalysisState(AnalysisState.NEW);
		Future<AnalysisSubmission> preparedAnalysisFuture = workflowManagement.prepareSubmission(analysisSubmission);

		ArgumentCaptor<AnalysisSubmission> captor = ArgumentCaptor.forClass(AnalysisSubmission.class);
		verify(analysisSubmissionService).update(captor.capture());

		AnalysisSubmission value = captor.getValue();
		assertEquals(INTERNAL_ANALYSIS_ID, value.getId());
		assertEquals(AnalysisState.ERROR, value.getAnalysisState());

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
		when(analysisWorkspaceService.prepareAnalysisWorkspace(any(AnalysisSubmission.class))).thenThrow(
				new ExecutionManagerException());

		analysisSubmission.setAnalysisState(AnalysisState.NEW);

		when(analysisSubmissionService.update(analysisSubmission)).thenReturn(analysisPreparing);

		Future<AnalysisSubmission> preparedAnalysisFuture = workflowManagement.prepareSubmission(analysisSubmission);

		assertTrue(preparedAnalysisFuture.isDone());
		preparedAnalysisFuture.get();
	}

	/**
	 * Tests successfully executing an analysis.
	 * 
	 * @throws ExecutionManagerException
	 * @throws NoSuchValueException
	 * @throws ExecutionException
	 * @throws InterruptedException
	 * @throws IridaWorkflowException 
	 * @throws IOException 
	 */
	@Test
	public void testExecuteAnalysisSuccess() throws ExecutionManagerException, NoSuchValueException, InterruptedException, ExecutionException, IridaWorkflowException, IOException {
		when(analysisWorkspaceService.prepareAnalysisFiles(any(AnalysisSubmission.class))).thenReturn(
				preparedWorkflow);

		when(analysisSubmissionService.update(analysisPrepared)).thenReturn(analysisSubmitting);
		when(analysisSubmissionService.update(analysisSubmitting)).thenReturn(analysisRunning);
		
		Future<AnalysisSubmission> preparedAnalysisFuture = workflowManagement.executeAnalysis(analysisPrepared);
		AnalysisSubmission returnedSubmission = preparedAnalysisFuture.get();
		assertEquals("analysisSubmitted not equal to returned submission", analysisRunning, returnedSubmission);
		
		verify(analysisWorkspaceService).prepareAnalysisFiles(analysisSubmitting);
		verify(galaxyWorkflowService).runWorkflow(workflowInputsGalaxy);
		
		ArgumentCaptor<AnalysisSubmission> captor = ArgumentCaptor.forClass(AnalysisSubmission.class);
		
		verify(analysisSubmissionService, times(2)).update(captor.capture());
		
		List<AnalysisSubmission> allValues = captor.getAllValues();
		
		Iterator<AnalysisSubmission> iterator = allValues.iterator();
		assertEquals(analysisPrepared,iterator.next());
		
		AnalysisSubmission updated = iterator.next();
		assertEquals(analysisSubmitting,updated);
		
		assertEquals(LIBRARY_ID, updated.getRemoteInputDataId());
	}

	/**
	 * Tests failing to executing an analysis due to already being submitted.
	 * 
	 * @throws ExecutionManagerException
	 * @throws ExecutionException
	 * @throws InterruptedException
	 * @throws IridaWorkflowException 
	 * @throws IOException 
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testExecuteAnalysisFailAlreadySubmitted() throws ExecutionManagerException, InterruptedException, ExecutionException, IridaWorkflowException, IOException {
		analysisPrepared.setAnalysisState(AnalysisState.RUNNING);
		workflowManagement.executeAnalysis(analysisSubmission);
	}

	/**
	 * Tests failing to prepare a workflow.
	 * 
	 * @throws ExecutionManagerException
	 * @throws ExecutionException
	 * @throws InterruptedException
	 * @throws IridaWorkflowException 
	 * @throws IOException 
	 */
	@Test(expected = ExecutionManagerException.class)
	public void testExecuteAnalysisFailPrepareWorkflow() throws ExecutionManagerException, InterruptedException, ExecutionException, IridaWorkflowException, IOException {
		when(analysisWorkspaceService.prepareAnalysisFiles(any(AnalysisSubmission.class))).thenThrow(
				new ExecutionManagerException());

		when(analysisSubmissionService.update(analysisPrepared)).thenReturn(analysisSubmitting);
		
		Future<AnalysisSubmission> submittedAnalysisFuture = workflowManagement.executeAnalysis(analysisPrepared);

		submittedAnalysisFuture.get();
	}

	/**
	 * Tests failing to execute a workflow.
	 * 
	 * @throws ExecutionManagerException
	 * @throws ExecutionException
	 * @throws InterruptedException
	 * @throws IridaWorkflowException
	 * @throws IOException 
	 */
	@Test(expected = WorkflowException.class)
	public void testExecuteAnalysisFail() throws ExecutionManagerException, InterruptedException, ExecutionException,
			IridaWorkflowException, IOException {
		when(analysisWorkspaceService.prepareAnalysisFiles(any(AnalysisSubmission.class))).thenReturn(preparedWorkflow);
		when(galaxyWorkflowService.runWorkflow(workflowInputsGalaxy)).thenThrow(new WorkflowException());
		when(analysisSubmissionService.update(analysisPrepared)).thenReturn(analysisSubmitting);

		Future<AnalysisSubmission> submittedAnalysisFuture = workflowManagement.executeAnalysis(analysisPrepared);

		assertEquals(AnalysisState.ERROR, submittedAnalysisFuture.get().getAnalysisState());

		verify(analysisSubmissionService, times(2)).update(any(AnalysisSubmission.class));
	}

	/**
	 * Tests out successfully getting the status of a workflow.
	 * 
	 * @throws ExecutionManagerException
	 */
	@Test
	public void testGetWorkflowStatusSuccess() throws ExecutionManagerException {
		GalaxyWorkflowStatus workflowStatus = new GalaxyWorkflowStatus(GalaxyWorkflowState.OK, Maps.newHashMap());
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
	 * @throws IridaWorkflowAnalysisTypeException 
	 */
	@Test
	public void testTransferAnalysisResultsSuccess() throws ExecutionManagerException, IOException,
			IridaWorkflowNotFoundException, InterruptedException, ExecutionException, IridaWorkflowAnalysisTypeException {
		when(analysisSubmissionService.exists(INTERNAL_ANALYSIS_ID)).thenReturn(true);
		when(analysisSubmissionService.update(analysisFinishedRunning)).thenReturn(analysisCompleting);
		when(analysisSubmissionService.update(analysisCompleting)).thenReturn(analysisCompleted);
		
		Future<AnalysisSubmission> actualCompletedSubmissionFuture = workflowManagement
				.transferAnalysisResults(analysisFinishedRunning);
		AnalysisSubmission actualCompletedSubmission = actualCompletedSubmissionFuture.get();
		assertEquals(analysisCompleted, actualCompletedSubmission);
		assertEquals("analysisResults should be equal", analysisResults, actualCompletedSubmission.getAnalysis());

		verify(analysisService).create(analysisResults);
		verify(analysisSubmissionService, times(2)).update(any(AnalysisSubmission.class));
	}
	
	/**
	 * Tests successfully getting analysis results even if updating samples failed.
	 */
	@Test
	public void testTransferAnalysisResultsSuccessUpdateSamplesFail()
			throws ExecutionManagerException, IOException, IridaWorkflowNotFoundException, InterruptedException,
			ExecutionException, IridaWorkflowAnalysisTypeException, PostProcessingException {
		when(analysisSubmissionService.exists(INTERNAL_ANALYSIS_ID)).thenReturn(true);
		when(analysisSubmissionService.update(analysisFinishedRunning)).thenReturn(analysisCompleting);
		when(analysisSubmissionService.update(analysisCompleting)).thenReturn(analysisCompleted);
		doThrow(new AccessDeniedException("")).when(analysisSubmissionSampleProcessor).updateSamples(analysisSubmission);
		
		Future<AnalysisSubmission> actualCompletedSubmissionFuture = workflowManagement
				.transferAnalysisResults(analysisFinishedRunning);
		AnalysisSubmission actualCompletedSubmission = actualCompletedSubmissionFuture.get();
		assertEquals(analysisCompleted, actualCompletedSubmission);
		assertEquals("analysisResults should be equal", analysisResults, actualCompletedSubmission.getAnalysis());

		verify(analysisService).create(analysisResults);
		verify(analysisSubmissionService, times(2)).update(any(AnalysisSubmission.class));
	}

	/**
	 * Tests failing to get analysis results.
	 * 
	 * @throws ExecutionManagerException
	 * @throws IOException
	 * @throws IridaWorkflowNotFoundException
	 * @throws ExecutionException
	 * @throws InterruptedException
	 * @throws IridaWorkflowAnalysisTypeException 
	 */
	@Test(expected = ExecutionManagerException.class)
	public void testTransferAnalysisResultsFail() throws ExecutionManagerException, IOException,
			IridaWorkflowNotFoundException, InterruptedException, ExecutionException, IridaWorkflowAnalysisTypeException {
		when(analysisSubmissionService.exists(INTERNAL_ANALYSIS_ID)).thenReturn(true);
		when(analysisWorkspaceService.getAnalysisResults(analysisCompleting)).thenThrow(
				new ExecutionManagerException());
		
		when(analysisSubmissionService.update(analysisFinishedRunning)).thenReturn(analysisCompleting);

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
	 * @throws IridaWorkflowAnalysisTypeException 
	 */
	@Test(expected = NullPointerException.class)
	public void testTransferAnalysisResultsFailNotSubmittedNullId() throws ExecutionManagerException, IOException,
			IridaWorkflowNotFoundException, InterruptedException, ExecutionException, IridaWorkflowAnalysisTypeException {
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
	 * @throws IridaWorkflowAnalysisTypeException
	 */
	@Test(expected = EntityNotFoundException.class)
	public void testTransferAnalysisResultsFailAnalysisIdInvalid() throws ExecutionManagerException, IOException,
			IridaWorkflowNotFoundException, IridaWorkflowAnalysisTypeException {
		analysisSubmission.setRemoteAnalysisId(ANALYSIS_ID);
		analysisSubmission.setAnalysisState(AnalysisState.FINISHED_RUNNING);
		when(analysisSubmissionService.exists(INTERNAL_ANALYSIS_ID)).thenReturn(false);

		when(analysisSubmissionService.update(analysisSubmission)).thenReturn(analysisCompleting);

		workflowManagement.transferAnalysisResults(analysisSubmission);
	}
	
	/**
	 * Tests failing to get analysis results due the type of analysis object
	 * being invalid.
	 * 
	 * @throws Throwable
	 */
	@Test(expected = IridaWorkflowAnalysisTypeException.class)
	public void testTransferAnalysisResultsFailAnalysisTypeInvalid() throws Throwable {
		when(analysisSubmissionService.exists(INTERNAL_ANALYSIS_ID)).thenReturn(true);
		when(analysisWorkspaceService.getAnalysisResults(analysisCompleting)).thenThrow(
				new IridaWorkflowAnalysisTypeException(null));

		when(analysisSubmissionService.update(analysisFinishedRunning)).thenReturn(analysisCompleting);

		Future<AnalysisSubmission> actualCompletedSubmissionFuture = workflowManagement
				.transferAnalysisResults(analysisFinishedRunning);
		try {
			actualCompletedSubmissionFuture.get();
		} catch (ExecutionException e) {
			throw e.getCause();
		}
	}

	/**
	 * Tests successfully executing the cleanup method on a completed
	 * submission.
	 * 
	 * @throws ExecutionManagerException
	 * @throws ExecutionException
	 * @throws InterruptedException
	 */
	@Test
	public void testCleanupCompletedSubmissionSuccess() throws ExecutionManagerException, InterruptedException,
			ExecutionException {
		when(analysisSubmissionService.update(analysisCompleted)).thenReturn(analysisCompletedCleaning);
		when(analysisSubmissionService.update(analysisCompletedCleaning)).thenReturn(analysisCompletedCleaned);

		Future<AnalysisSubmission> cleaningSubmissionFuture = workflowManagement.cleanupSubmission(analysisCompleted);
		AnalysisSubmission cleaningSubmission = cleaningSubmissionFuture.get();
		assertEquals(AnalysisCleanedState.CLEANED, cleaningSubmission.getAnalysisCleanedState());

		verify(galaxyHistoriesService).deleteHistory(ANALYSIS_ID);
		verify(galaxyLibrariesService).deleteLibrary(LIBRARY_ID);
		verify(galaxyWorkflowService).deleteWorkflow(REMOTE_WORKFLOW_ID);
	}

	/**
	 * Tests successfully executing the cleanup method on a error submission.
	 * 
	 * @throws ExecutionManagerException
	 * @throws ExecutionException
	 * @throws InterruptedException
	 */
	@Test
	public void testCleanupErrorSubmissionSuccess() throws ExecutionManagerException, InterruptedException,
			ExecutionException {
		when(analysisSubmissionService.update(analysisError)).thenReturn(analysisErrorCleaning);
		when(analysisSubmissionService.update(analysisErrorCleaning)).thenReturn(analysisErrorCleaned);

		Future<AnalysisSubmission> cleaningSubmissionFuture = workflowManagement.cleanupSubmission(analysisError);
		AnalysisSubmission cleaningSubmission = cleaningSubmissionFuture.get();
		assertEquals(AnalysisCleanedState.CLEANED, cleaningSubmission.getAnalysisCleanedState());

		verify(galaxyHistoriesService, never()).deleteHistory(ANALYSIS_ID);
		verify(galaxyLibrariesService, never()).deleteLibrary(LIBRARY_ID);
		verify(galaxyWorkflowService).deleteWorkflow(REMOTE_WORKFLOW_ID);
	}
}
