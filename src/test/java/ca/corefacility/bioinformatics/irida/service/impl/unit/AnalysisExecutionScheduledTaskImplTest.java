package ca.corefacility.bioinformatics.irida.service.impl.unit;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Future;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.github.jmchilton.blend4j.galaxy.HistoriesClient;
import com.google.common.collect.Sets;

import ca.corefacility.bioinformatics.irida.exceptions.ExecutionManagerException;
import ca.corefacility.bioinformatics.irida.exceptions.IridaWorkflowAnalysisTypeException;
import ca.corefacility.bioinformatics.irida.exceptions.IridaWorkflowException;
import ca.corefacility.bioinformatics.irida.exceptions.IridaWorkflowNotFoundException;
import ca.corefacility.bioinformatics.irida.model.enums.AnalysisCleanedState;
import ca.corefacility.bioinformatics.irida.model.enums.AnalysisState;
import ca.corefacility.bioinformatics.irida.model.project.ReferenceFile;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequencingObject;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.Analysis;
import ca.corefacility.bioinformatics.irida.model.workflow.execution.galaxy.GalaxyWorkflowState;
import ca.corefacility.bioinformatics.irida.model.workflow.execution.galaxy.GalaxyWorkflowStatus;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyJobErrorsService;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.integration.Util;
import ca.corefacility.bioinformatics.irida.repositories.analysis.submission.AnalysisSubmissionRepository;
import ca.corefacility.bioinformatics.irida.repositories.analysis.submission.JobErrorRepository;
import ca.corefacility.bioinformatics.irida.service.AnalysisExecutionScheduledTask;
import ca.corefacility.bioinformatics.irida.service.AnalysisSubmissionService;
import ca.corefacility.bioinformatics.irida.service.CleanupAnalysisSubmissionCondition;
import ca.corefacility.bioinformatics.irida.service.analysis.execution.AnalysisExecutionService;
import ca.corefacility.bioinformatics.irida.service.impl.AnalysisExecutionScheduledTaskImpl;
import ca.corefacility.bioinformatics.irida.service.impl.analysis.submission.CleanupAnalysisSubmissionConditionAge;
import ca.corefacility.bioinformatics.irida.service.impl.TestEmailController;

/**
 * Tests out scheduling analysis tasks.
 */
public class AnalysisExecutionScheduledTaskImplTest {

	@Mock
	private AnalysisSubmissionService analysisSubmissionService;
	@Mock
	private AnalysisSubmissionRepository analysisSubmissionRepository;
	@Mock
	private AnalysisExecutionService analysisExecutionService;

	@Mock
	private Set<SequencingObject> sequenceFiles;

	@Mock
	private ReferenceFile referenceFile;

	@Mock
	private Analysis analysis;

	@Mock
	private AnalysisSubmission analysisSubmissionMock;

	@Mock
	private AnalysisSubmission analysisSubmissionMock2;

	@Mock
	private GalaxyJobErrorsService galaxyJobErrorsService;

	@Mock
	private HistoriesClient historiesClient;

	@Mock
	private JobErrorRepository jobErrorRepository;

	@Mock
	private TestEmailController emailController;

	private static final String ANALYSIS_ID = "1";
	private static final Long INTERNAL_ID = 1L;
	private AnalysisSubmission analysisSubmission;

	private AnalysisExecutionScheduledTask analysisExecutionScheduledTask;

	private UUID workflowId = UUID.randomUUID();

	/**
	 * Sets up variables for tests.
	 */
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);

		analysisExecutionScheduledTask = new AnalysisExecutionScheduledTaskImpl(analysisSubmissionRepository,
				analysisExecutionService, CleanupAnalysisSubmissionCondition.ALWAYS_CLEANUP, galaxyJobErrorsService,
				jobErrorRepository, emailController);

		analysisSubmission = AnalysisSubmission.builder(workflowId)
				.name("my analysis")
				.inputFiles(sequenceFiles)
				.referenceFile(referenceFile)
				.emailPipelineResult(false)
				.build();
		analysisSubmission.setId(INTERNAL_ID);
		analysisSubmission.setRemoteAnalysisId(ANALYSIS_ID);
		when(galaxyJobErrorsService.createNewJobErrors(analysisSubmission)).thenReturn(new ArrayList<>());
	}

	/**
	 * Tests successfully preparing submitted analyses.
	 *
	 * @throws ExecutionManagerException
	 * @throws IridaWorkflowNotFoundException
	 * @throws IOException
	 */
	@Test
	public void testPrepareAnalysesSuccess()
			throws ExecutionManagerException, IridaWorkflowNotFoundException, IOException {
		analysisSubmission.setAnalysisState(AnalysisState.NEW);

		when(analysisSubmissionRepository.findByAnalysisState(AnalysisState.NEW)).thenReturn(
				Arrays.asList(analysisSubmission));
		when(analysisExecutionService.getCapacity()).thenReturn(1);

		analysisExecutionScheduledTask.prepareAnalyses();

		verify(analysisExecutionService).prepareSubmission(analysisSubmission);
	}

	@Test
	public void testPrepareAnalysisPriorities()
			throws ExecutionManagerException, IOException, IridaWorkflowNotFoundException {
		AnalysisSubmission low = AnalysisSubmission.builder(workflowId)
				.name("low")
				.inputFiles(sequenceFiles)
				.priority(AnalysisSubmission.Priority.LOW)
				.build();

		AnalysisSubmission medium = AnalysisSubmission.builder(workflowId)
				.name("medium")
				.inputFiles(sequenceFiles)
				.priority(AnalysisSubmission.Priority.MEDIUM)
				.build();

		AnalysisSubmission high = AnalysisSubmission.builder(workflowId)
				.name("high")
				.inputFiles(sequenceFiles)
				.priority(AnalysisSubmission.Priority.HIGH)
				.build();

		when(analysisSubmissionRepository.findByAnalysisState(AnalysisState.NEW)).thenReturn(
				Arrays.asList(medium, high, low));
		when(analysisExecutionService.getCapacity()).thenReturn(2);

		analysisExecutionScheduledTask.prepareAnalyses();

		verify(analysisExecutionService).prepareSubmission(high);
		verify(analysisExecutionService).prepareSubmission(medium);
		verify(analysisExecutionService, times(0)).prepareSubmission(low);
	}

	/**
	 * Tests no analysis to prepare.
	 *
	 * @throws ExecutionManagerException
	 * @throws IridaWorkflowNotFoundException
	 * @throws IOException
	 */
	@Test
	public void testPrepareAnalysesNoAnalysis()
			throws ExecutionManagerException, IridaWorkflowNotFoundException, IOException {
		analysisSubmission.setAnalysisState(AnalysisState.NEW);

		when(analysisSubmissionRepository.findByAnalysisState(AnalysisState.NEW)).thenReturn(Arrays.asList());

		analysisExecutionScheduledTask.prepareAnalyses();

		verify(analysisExecutionService, never()).prepareSubmission(analysisSubmission);
	}

	/**
	 * Tests successfully executing submitted analyses.
	 *
	 * @throws ExecutionManagerException
	 * @throws IridaWorkflowException
	 * @throws IOException 
	 */
	@Test
	public void testExecuteAnalysesSuccess() throws ExecutionManagerException, IridaWorkflowException, IOException {
		analysisSubmission.setAnalysisState(AnalysisState.PREPARED);

		when(analysisSubmissionRepository.findByAnalysisState(AnalysisState.PREPARED)).thenReturn(
				Arrays.asList(analysisSubmission));

		analysisExecutionScheduledTask.executeAnalyses();

		verify(analysisExecutionService).executeAnalysis(analysisSubmission);
	}

	/**
	 * Tests no analyses to submit.
	 *
	 * @throws ExecutionManagerException
	 * @throws IridaWorkflowException
	 * @throws IOException 
	 */
	@Test
	public void testExecuteAnalysesNoAnalyses() throws ExecutionManagerException, IridaWorkflowException, IOException {
		analysisSubmission.setAnalysisState(AnalysisState.PREPARED);

		when(analysisSubmissionRepository.findByAnalysisState(AnalysisState.PREPARED)).thenReturn(
				new ArrayList<AnalysisSubmission>());

		analysisExecutionScheduledTask.executeAnalyses();

		verify(analysisExecutionService, never()).executeAnalysis(analysisSubmission);
	}

	/**
	 * Tests successfully switching analysis state to
	 * {@link AnalysisState.FINISHED_RUNNING} on success in Galaxy.
	 * Also ,tests not sending an email on pipeline completion.
	 *
	 * @throws ExecutionManagerException
	 * @throws IridaWorkflowNotFoundException
	 */
	@Test
	public void testMonitorRunningAnalysesSuccessFinished()
			throws ExecutionManagerException, IridaWorkflowNotFoundException {
		analysisSubmission.setAnalysisState(AnalysisState.RUNNING);
		Map<GalaxyWorkflowState, Set<String>> stateIds = Util.buildStateIdsWithStateFilled(GalaxyWorkflowState.OK,
				Sets.newHashSet("1"));
		GalaxyWorkflowStatus galaxyWorkflowStatus = new GalaxyWorkflowStatus(GalaxyWorkflowState.OK, stateIds);

		when(analysisSubmissionRepository.findByAnalysisState(AnalysisState.RUNNING)).thenReturn(
				Arrays.asList(analysisSubmission));
		when(analysisExecutionService.getWorkflowStatus(analysisSubmission)).thenReturn(galaxyWorkflowStatus);

		analysisExecutionScheduledTask.monitorRunningAnalyses();

		assertEquals(AnalysisState.FINISHED_RUNNING, analysisSubmission.getAnalysisState());
		verify(analysisSubmissionRepository).save(analysisSubmission);
		verify(emailController, never()).sendPipelineStatusEmail(analysisSubmission);
	}

	/**
	 * Tests successfully switching analysis state to
	 * {@link AnalysisState.FINISHED_RUNNING} on success in Galaxy.
	 * Also, tests sending an email to user on pipeline completion.
	 *
	 * @throws ExecutionManagerException
	 * @throws IridaWorkflowNotFoundException
	 */
	@Test
	public void testMonitorRunningAnalysesSuccessFinishedWithEmail()
			throws ExecutionManagerException, IridaWorkflowNotFoundException {
		analysisSubmission.setAnalysisState(AnalysisState.RUNNING);
		analysisSubmission.setEmailPipelineResult(true);
		Map<GalaxyWorkflowState, Set<String>> stateIds = Util.buildStateIdsWithStateFilled(GalaxyWorkflowState.OK,
				Sets.newHashSet("1"));
		GalaxyWorkflowStatus galaxyWorkflowStatus = new GalaxyWorkflowStatus(GalaxyWorkflowState.OK, stateIds);

		when(analysisSubmissionRepository.findByAnalysisState(AnalysisState.RUNNING)).thenReturn(
				Arrays.asList(analysisSubmission));
		when(analysisExecutionService.getWorkflowStatus(analysisSubmission)).thenReturn(galaxyWorkflowStatus);

		analysisExecutionScheduledTask.monitorRunningAnalyses();

		assertEquals(AnalysisState.FINISHED_RUNNING, analysisSubmission.getAnalysisState());
		verify(analysisSubmissionRepository).save(analysisSubmission);
		verify(emailController).sendPipelineStatusEmail(analysisSubmission);
	}

	/**
	 * Tests successfully skipping over switching analysis state for a running
	 * analysis in Galaxy.
	 *
	 * @throws ExecutionManagerException
	 * @throws IridaWorkflowNotFoundException
	 */
	@Test
	public void testMonitorRunningAnalysesSuccessRunning()
			throws ExecutionManagerException, IridaWorkflowNotFoundException {
		analysisSubmission.setAnalysisState(AnalysisState.RUNNING);
		Map<GalaxyWorkflowState, Set<String>> stateIds = Util.buildStateIdsWithStateFilled(GalaxyWorkflowState.RUNNING,
				Sets.newHashSet("1"));

		when(analysisSubmissionRepository.findByAnalysisState(AnalysisState.RUNNING)).thenReturn(
				Arrays.asList(analysisSubmission));
		when(analysisExecutionService.getWorkflowStatus(analysisSubmission)).thenReturn(
				new GalaxyWorkflowStatus(GalaxyWorkflowState.RUNNING, stateIds));

		analysisExecutionScheduledTask.monitorRunningAnalyses();

		assertEquals(AnalysisState.RUNNING, analysisSubmission.getAnalysisState());
		verify(analysisSubmissionRepository, never()).save(analysisSubmission);
	}

	/**
	 * Tests successfully skipping over switching analysis state for a queued
	 * analysis in Galaxy.
	 *
	 * @throws ExecutionManagerException
	 * @throws IridaWorkflowNotFoundException
	 */
	@Test
	public void testMonitorRunningAnalysesSuccessQueued()
			throws ExecutionManagerException, IridaWorkflowNotFoundException {
		analysisSubmission.setAnalysisState(AnalysisState.RUNNING);
		Map<GalaxyWorkflowState, Set<String>> stateIds = Util.buildStateIdsWithStateFilled(GalaxyWorkflowState.QUEUED,
				Sets.newHashSet("1"));

		when(analysisSubmissionRepository.findByAnalysisState(AnalysisState.RUNNING)).thenReturn(
				Arrays.asList(analysisSubmission));
		when(analysisExecutionService.getWorkflowStatus(analysisSubmission)).thenReturn(
				new GalaxyWorkflowStatus(GalaxyWorkflowState.QUEUED, stateIds));

		analysisExecutionScheduledTask.monitorRunningAnalyses();

		assertEquals(AnalysisState.RUNNING, analysisSubmission.getAnalysisState());
		verify(analysisSubmissionRepository, never()).save(analysisSubmission);
	}

	/**
	 * Tests successfully switching an analysis to {@link AnalysisState.ERROR}
	 * if there was an error Galaxy state. Also, tests not sending a pipeline
	 * result email.
	 *
	 * @throws ExecutionManagerException
	 * @throws IridaWorkflowNotFoundException
	 */
	@Test
	public void testMonitorRunningAnalysesSuccessError()
			throws ExecutionManagerException, IridaWorkflowNotFoundException {
		analysisSubmission.setAnalysisState(AnalysisState.RUNNING);

		Map<GalaxyWorkflowState, Set<String>> stateIds = Util.buildStateIdsWithStateFilled(GalaxyWorkflowState.ERROR,
				Sets.newHashSet("1"));
		GalaxyWorkflowStatus galaxyWorkflowStatus = new GalaxyWorkflowStatus(GalaxyWorkflowState.ERROR, stateIds);

		when(analysisSubmissionRepository.findByAnalysisState(AnalysisState.RUNNING)).thenReturn(
				Arrays.asList(analysisSubmission));
		when(analysisExecutionService.getWorkflowStatus(analysisSubmission)).thenReturn(galaxyWorkflowStatus);

		analysisExecutionScheduledTask.monitorRunningAnalyses();

		assertEquals(AnalysisState.ERROR, analysisSubmission.getAnalysisState());
		verify(analysisSubmissionRepository).save(analysisSubmission);
		verify(emailController, never()).sendPipelineStatusEmail(analysisSubmission);
	}

	/**
	 * Tests successfully switching an analysis to {@link AnalysisState.ERROR}
	 * if there was an error Galaxy state. On  error, user is sent an email
	 * with the pipeline result.
	 *
	 * @throws ExecutionManagerException
	 * @throws IridaWorkflowNotFoundException
	 */
	@Test
	public void testMonitorRunningAnalysesSuccessErrorWithEmail()
			throws ExecutionManagerException, IridaWorkflowNotFoundException {
		analysisSubmission.setAnalysisState(AnalysisState.RUNNING);
		analysisSubmission.setEmailPipelineResult(true);
		Map<GalaxyWorkflowState, Set<String>> stateIds = Util.buildStateIdsWithStateFilled(GalaxyWorkflowState.ERROR,
				Sets.newHashSet("1"));
		GalaxyWorkflowStatus galaxyWorkflowStatus = new GalaxyWorkflowStatus(GalaxyWorkflowState.ERROR, stateIds);

		when(analysisSubmissionRepository.findByAnalysisState(AnalysisState.RUNNING)).thenReturn(
				Arrays.asList(analysisSubmission));
		when(analysisExecutionService.getWorkflowStatus(analysisSubmission)).thenReturn(galaxyWorkflowStatus);

		analysisExecutionScheduledTask.monitorRunningAnalyses();

		assertEquals(AnalysisState.ERROR, analysisSubmission.getAnalysisState());
		verify(analysisSubmissionRepository).save(analysisSubmission);
		verify(emailController).sendPipelineStatusEmail(analysisSubmission);
	}

	/**
	 * Tests successfully switching an analysis to {@link AnalysisState.ERROR}
	 * if there was an error building the workflow status. Also, tests not
	 * sending of pipeline status email.
	 *
	 * @throws ExecutionManagerException
	 * @throws IridaWorkflowNotFoundException
	 */
	@Test
	public void testMonitorRunningAnalysesErrorWorkflowStatus()
			throws ExecutionManagerException, IridaWorkflowNotFoundException {
		analysisSubmission.setAnalysisState(AnalysisState.RUNNING);

		when(analysisSubmissionRepository.findByAnalysisState(AnalysisState.RUNNING)).thenReturn(
				Arrays.asList(analysisSubmission));
		when(analysisExecutionService.getWorkflowStatus(analysisSubmission)).thenThrow(new IllegalArgumentException());

		analysisExecutionScheduledTask.monitorRunningAnalyses();

		assertEquals(AnalysisState.ERROR, analysisSubmission.getAnalysisState());
		verify(analysisSubmissionRepository).save(analysisSubmission);
		verify(emailController, never()).sendPipelineStatusEmail(analysisSubmission);
	}

	/**
	 * Tests successfully switching an analysis to {@link AnalysisState.ERROR}
	 * if there was an error building the workflow status. Also, tests
	 * sending of pipeline status email.
	 *
	 * @throws ExecutionManagerException
	 * @throws IridaWorkflowNotFoundException
	 */
	@Test
	public void testMonitorRunningAnalysesErrorWorkflowStatusWithEmail()
			throws ExecutionManagerException, IridaWorkflowNotFoundException {
		analysisSubmission.setAnalysisState(AnalysisState.RUNNING);
		analysisSubmission.setEmailPipelineResult(true);

		when(analysisSubmissionRepository.findByAnalysisState(AnalysisState.RUNNING)).thenReturn(
				Arrays.asList(analysisSubmission));
		when(analysisExecutionService.getWorkflowStatus(analysisSubmission)).thenThrow(new IllegalArgumentException());

		analysisExecutionScheduledTask.monitorRunningAnalyses();

		assertEquals(AnalysisState.ERROR, analysisSubmission.getAnalysisState());
		verify(analysisSubmissionRepository).save(analysisSubmission);
		verify(emailController).sendPipelineStatusEmail(analysisSubmission);
	}

	/**
	 * Tests successfully switching an analysis to {@link AnalysisState.ERROR}
	 * if there was an Galaxy job with an error, but still running.
	 *
	 * @throws ExecutionManagerException
	 * @throws IridaWorkflowNotFoundException
	 */
	@Test
	public void testMonitorRunningAnalysesSuccessErrorStillRunning()
			throws ExecutionManagerException, IridaWorkflowNotFoundException {
		analysisSubmission.setAnalysisState(AnalysisState.RUNNING);

		Map<GalaxyWorkflowState, Set<String>> stateIds = Util.buildStateIdsWithStateFilled(GalaxyWorkflowState.ERROR,
				Sets.newHashSet("1"));
		GalaxyWorkflowStatus galaxyWorkflowStatus = new GalaxyWorkflowStatus(GalaxyWorkflowState.RUNNING, stateIds);

		when(analysisSubmissionRepository.findByAnalysisState(AnalysisState.RUNNING)).thenReturn(
				Arrays.asList(analysisSubmission));
		when(analysisExecutionService.getWorkflowStatus(analysisSubmission)).thenReturn(galaxyWorkflowStatus);

		analysisExecutionScheduledTask.monitorRunningAnalyses();

		assertEquals(AnalysisState.ERROR, analysisSubmission.getAnalysisState());
		verify(analysisSubmissionRepository).save(analysisSubmission);
	}

	/**
	 * Tests successfully transferring results for a submitted analysis.
	 *
	 * @throws ExecutionManagerException
	 * @throws IOException
	 * @throws IridaWorkflowNotFoundException
	 * @throws IridaWorkflowAnalysisTypeException
	 */
	@Test
	public void testTransferAnalysesResultsSuccess()
			throws ExecutionManagerException, IOException, IridaWorkflowNotFoundException,
			IridaWorkflowAnalysisTypeException {
		analysisSubmission.setAnalysisState(AnalysisState.FINISHED_RUNNING);

		when(analysisSubmissionRepository.findByAnalysisState(AnalysisState.FINISHED_RUNNING)).thenReturn(
				Arrays.asList(analysisSubmission));

		analysisExecutionScheduledTask.transferAnalysesResults();

		verify(analysisExecutionService).transferAnalysisResults(analysisSubmission);
	}

	/**
	 * Tests no analysis results to check if they can be transferred.
	 *
	 * @throws ExecutionManagerException
	 * @throws IOException
	 * @throws IridaWorkflowNotFoundException
	 * @throws IridaWorkflowAnalysisTypeException
	 */
	@Test
	public void testTransferAnalysesResultsNoAnalyses()
			throws ExecutionManagerException, IOException, IridaWorkflowNotFoundException,
			IridaWorkflowAnalysisTypeException {
		when(analysisSubmissionRepository.findByAnalysisState(AnalysisState.FINISHED_RUNNING)).thenReturn(
				new ArrayList<AnalysisSubmission>());

		analysisExecutionScheduledTask.transferAnalysesResults();

		verify(analysisExecutionService, never()).transferAnalysisResults(analysisSubmission);
	}

	/**
	 * Tests successfully cleaning up analysis submissions with completed
	 * status.
	 *
	 * @throws ExecutionManagerException
	 */
	@Test
	public void testCleanupAnalysisSubmissionsCompletedSuccess() throws ExecutionManagerException {
		analysisSubmission.setAnalysisState(AnalysisState.COMPLETED);
		analysisSubmission.setAnalysisCleanedState(AnalysisCleanedState.NOT_CLEANED);

		when(analysisSubmissionRepository.findByAnalysisState(AnalysisState.COMPLETED,
				AnalysisCleanedState.NOT_CLEANED)).thenReturn(Arrays.asList(analysisSubmission));

		Set<Future<AnalysisSubmission>> futureSubmissionsSet = analysisExecutionScheduledTask.cleanupAnalysisSubmissions();

		assertEquals("Incorrect size for futureSubmissionsSet", 1, futureSubmissionsSet.size());
		verify(analysisExecutionService).cleanupSubmission(analysisSubmission);
	}

	/**
	 * Tests successfully cleaning up analysis submissions with error status.
	 *
	 * @throws ExecutionManagerException
	 */
	@Test
	public void testCleanupAnalysisSubmissionsErrorSuccess() throws ExecutionManagerException {
		analysisSubmission.setAnalysisState(AnalysisState.ERROR);
		analysisSubmission.setAnalysisCleanedState(AnalysisCleanedState.NOT_CLEANED);

		when(analysisSubmissionRepository.findByAnalysisState(AnalysisState.ERROR,
				AnalysisCleanedState.NOT_CLEANED)).thenReturn(Arrays.asList(analysisSubmission));

		Set<Future<AnalysisSubmission>> futureSubmissionsSet = analysisExecutionScheduledTask.cleanupAnalysisSubmissions();

		assertEquals("Incorrect size for futureSubmissionsSet", 1, futureSubmissionsSet.size());
		verify(analysisExecutionService).cleanupSubmission(analysisSubmission);
	}

	/**
	 * Tests successfully not cleaning up any analysis submissions in the
	 * running state.
	 *
	 * @throws ExecutionManagerException
	 */
	@Test
	public void testCleanupAnalysisSubmissionsRunningNoClean() throws ExecutionManagerException {
		analysisSubmission.setAnalysisState(AnalysisState.RUNNING);
		analysisSubmission.setAnalysisCleanedState(AnalysisCleanedState.NOT_CLEANED);

		when(analysisSubmissionRepository.findByAnalysisState(AnalysisState.RUNNING,
				AnalysisCleanedState.NOT_CLEANED)).thenReturn(Arrays.asList(analysisSubmission));

		Set<Future<AnalysisSubmission>> futureSubmissionsSet = analysisExecutionScheduledTask.cleanupAnalysisSubmissions();

		assertEquals("Incorrect size for futureSubmissionsSet", 0, futureSubmissionsSet.size());
		verify(analysisExecutionService, never()).cleanupSubmission(analysisSubmission);
	}

	/**
	 * Tests successfully not cleaning up an analysis in completed when it's
	 * already cleaned.
	 *
	 * @throws ExecutionManagerException
	 */
	@Test
	public void testCleanupAnalysisSubmissionsCompletedCleanedSuccess() throws ExecutionManagerException {
		analysisSubmission.setAnalysisState(AnalysisState.COMPLETED);
		analysisSubmission.setAnalysisCleanedState(AnalysisCleanedState.CLEANED);

		when(analysisSubmissionRepository.findByAnalysisState(AnalysisState.COMPLETED,
				AnalysisCleanedState.NOT_CLEANED)).thenReturn(Arrays.asList(analysisSubmission));

		Set<Future<AnalysisSubmission>> futureSubmissionsSet = analysisExecutionScheduledTask.cleanupAnalysisSubmissions();

		assertEquals("Incorrect size for futureSubmissionsSet", 0, futureSubmissionsSet.size());
		verify(analysisExecutionService, never()).cleanupSubmission(analysisSubmission);
	}

	/**
	 * Tests successfully not cleaning up an analysis in completed when it's in
	 * a cleaning error.
	 *
	 * @throws ExecutionManagerException
	 */
	@Test
	public void testCleanupAnalysisSubmissionsCompletedCleanedErrorSuccess() throws ExecutionManagerException {
		analysisSubmission.setAnalysisState(AnalysisState.COMPLETED);
		analysisSubmission.setAnalysisCleanedState(AnalysisCleanedState.CLEANING_ERROR);

		when(analysisSubmissionRepository.findByAnalysisState(AnalysisState.COMPLETED,
				AnalysisCleanedState.NOT_CLEANED)).thenReturn(Arrays.asList(analysisSubmission));

		Set<Future<AnalysisSubmission>> futureSubmissionsSet = analysisExecutionScheduledTask.cleanupAnalysisSubmissions();

		assertEquals("Incorrect size for futureSubmissionsSet", 0, futureSubmissionsSet.size());
		verify(analysisExecutionService, never()).cleanupSubmission(analysisSubmission);
	}

	/**
	 * Tests successfully not cleaning up an analysis in completed when it's
	 * already being cleaned.
	 *
	 * @throws ExecutionManagerException
	 */
	@Test
	public void testCleanupAnalysisSubmissionsCompletedCleanedCleaningSuccess() throws ExecutionManagerException {
		analysisSubmission.setAnalysisState(AnalysisState.COMPLETED);
		analysisSubmission.setAnalysisCleanedState(AnalysisCleanedState.CLEANING);

		when(analysisSubmissionRepository.findByAnalysisState(AnalysisState.COMPLETED,
				AnalysisCleanedState.NOT_CLEANED)).thenReturn(Arrays.asList(analysisSubmission));

		Set<Future<AnalysisSubmission>> futureSubmissionsSet = analysisExecutionScheduledTask.cleanupAnalysisSubmissions();

		assertEquals("Incorrect size for futureSubmissionsSet", 0, futureSubmissionsSet.size());
		verify(analysisExecutionService, never()).cleanupSubmission(analysisSubmission);
	}

	/**
	 * Tests successfully cleaning up analysis submissions with completed status
	 * that are over a day old (time limit until they should be cleaned).
	 *
	 * @throws ExecutionManagerException
	 */
	@Test
	public void testCleanupAnalysisSubmissionsCompletedOverOneDaySuccess() throws ExecutionManagerException {
		analysisExecutionScheduledTask = new AnalysisExecutionScheduledTaskImpl(analysisSubmissionRepository,
				analysisExecutionService, new CleanupAnalysisSubmissionConditionAge(Duration.ofDays(1)),
				galaxyJobErrorsService, jobErrorRepository, emailController);

		when(analysisSubmissionMock.getAnalysisState()).thenReturn(AnalysisState.COMPLETED);
		when(analysisSubmissionMock.getAnalysisCleanedState()).thenReturn(AnalysisCleanedState.NOT_CLEANED);
		when(analysisSubmissionMock.getCreatedDate()).thenReturn(DateTime.now()
				.minusDays(2)
				.toDate());

		when(analysisSubmissionRepository.findByAnalysisState(AnalysisState.COMPLETED,
				AnalysisCleanedState.NOT_CLEANED)).thenReturn(Arrays.asList(analysisSubmissionMock));

		Set<Future<AnalysisSubmission>> futureSubmissionsSet = analysisExecutionScheduledTask.cleanupAnalysisSubmissions();

		assertEquals("Incorrect size for futureSubmissionsSet", 1, futureSubmissionsSet.size());
		verify(analysisExecutionService).cleanupSubmission(analysisSubmissionMock);
	}

	/**
	 * Tests successfully cleaning up analysis submissions with completed status
	 * when cleanup time is zero.
	 *
	 * @throws ExecutionManagerException
	 */
	@Test
	public void testCleanupAnalysisSubmissionsCompletedCleanupZeroSuccess() throws ExecutionManagerException {
		analysisExecutionScheduledTask = new AnalysisExecutionScheduledTaskImpl(analysisSubmissionRepository,
				analysisExecutionService, new CleanupAnalysisSubmissionConditionAge(Duration.ZERO),
				galaxyJobErrorsService, jobErrorRepository, emailController);

		when(analysisSubmissionMock.getAnalysisState()).thenReturn(AnalysisState.COMPLETED);
		when(analysisSubmissionMock.getAnalysisCleanedState()).thenReturn(AnalysisCleanedState.NOT_CLEANED);
		when(analysisSubmissionMock.getCreatedDate()).thenReturn(DateTime.now()
				.toDate());

		when(analysisSubmissionRepository.findByAnalysisState(AnalysisState.COMPLETED,
				AnalysisCleanedState.NOT_CLEANED)).thenReturn(Arrays.asList(analysisSubmissionMock));

		Set<Future<AnalysisSubmission>> futureSubmissionsSet = analysisExecutionScheduledTask.cleanupAnalysisSubmissions();

		assertEquals("Incorrect size for futureSubmissionsSet", 1, futureSubmissionsSet.size());
		verify(analysisExecutionService).cleanupSubmission(analysisSubmissionMock);
	}

	/**
	 * Tests successfully not cleaning up analysis submissions with completed
	 * status that are under a day old (time limit until they should be
	 * cleaned).
	 *
	 * @throws ExecutionManagerException
	 */
	@Test
	public void testCleanupAnalysisSubmissionsCompletedUnderOneDaySuccess() throws ExecutionManagerException {
		analysisExecutionScheduledTask = new AnalysisExecutionScheduledTaskImpl(analysisSubmissionRepository,
				analysisExecutionService, new CleanupAnalysisSubmissionConditionAge(Duration.ofDays(1)),
				galaxyJobErrorsService, jobErrorRepository, emailController);

		when(analysisSubmissionMock.getAnalysisState()).thenReturn(AnalysisState.COMPLETED);
		when(analysisSubmissionMock.getAnalysisCleanedState()).thenReturn(AnalysisCleanedState.NOT_CLEANED);
		when(analysisSubmissionMock.getCreatedDate()).thenReturn(DateTime.now()
				.toDate());

		when(analysisSubmissionRepository.findByAnalysisState(AnalysisState.COMPLETED,
				AnalysisCleanedState.NOT_CLEANED)).thenReturn(Arrays.asList(analysisSubmissionMock));

		Set<Future<AnalysisSubmission>> futureSubmissionsSet = analysisExecutionScheduledTask.cleanupAnalysisSubmissions();

		assertEquals("Incorrect size for futureSubmissionsSet", 0, futureSubmissionsSet.size());
		verify(analysisExecutionService, never()).cleanupSubmission(analysisSubmissionMock);
	}

	/**
	 * Tests successfully cleaning up analysis submissions with completed status
	 * where one is over a day old (so gets cleaned up) and one is under a day
	 * old (so does not get cleaned up).
	 *
	 * @throws ExecutionManagerException
	 */
	@Test
	public void testCleanupAnalysisSubmissionsCompletedOverUnderOneDaySuccess() throws ExecutionManagerException {
		analysisExecutionScheduledTask = new AnalysisExecutionScheduledTaskImpl(analysisSubmissionRepository,
				analysisExecutionService, new CleanupAnalysisSubmissionConditionAge(Duration.ofDays(1)),
				galaxyJobErrorsService, jobErrorRepository, emailController);

		when(analysisSubmissionMock.getAnalysisState()).thenReturn(AnalysisState.COMPLETED);
		when(analysisSubmissionMock.getAnalysisCleanedState()).thenReturn(AnalysisCleanedState.NOT_CLEANED);
		when(analysisSubmissionMock.getCreatedDate()).thenReturn(DateTime.now()
				.minusDays(2)
				.toDate());

		when(analysisSubmissionMock2.getAnalysisState()).thenReturn(AnalysisState.COMPLETED);
		when(analysisSubmissionMock2.getAnalysisCleanedState()).thenReturn(AnalysisCleanedState.NOT_CLEANED);
		when(analysisSubmissionMock2.getCreatedDate()).thenReturn(DateTime.now()
				.toDate());

		when(analysisSubmissionRepository.findByAnalysisState(AnalysisState.COMPLETED,
				AnalysisCleanedState.NOT_CLEANED)).thenReturn(
				Arrays.asList(analysisSubmissionMock, analysisSubmissionMock2));

		Set<Future<AnalysisSubmission>> futureSubmissionsSet = analysisExecutionScheduledTask.cleanupAnalysisSubmissions();

		assertEquals("Incorrect size for futureSubmissionsSet", 1, futureSubmissionsSet.size());
		verify(analysisExecutionService).cleanupSubmission(analysisSubmissionMock);
		verify(analysisExecutionService, never()).cleanupSubmission(analysisSubmissionMock2);
	}
}
