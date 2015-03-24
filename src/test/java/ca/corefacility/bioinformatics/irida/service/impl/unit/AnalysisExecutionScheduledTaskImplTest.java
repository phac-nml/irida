package ca.corefacility.bioinformatics.irida.service.impl.unit;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import ca.corefacility.bioinformatics.irida.exceptions.ExecutionManagerException;
import ca.corefacility.bioinformatics.irida.exceptions.IridaWorkflowAnalysisTypeException;
import ca.corefacility.bioinformatics.irida.exceptions.IridaWorkflowException;
import ca.corefacility.bioinformatics.irida.exceptions.IridaWorkflowNotFoundException;
import ca.corefacility.bioinformatics.irida.model.enums.AnalysisState;
import ca.corefacility.bioinformatics.irida.model.project.ReferenceFile;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.AnalysisPhylogenomicsPipeline;
import ca.corefacility.bioinformatics.irida.model.workflow.execution.galaxy.GalaxyWorkflowState;
import ca.corefacility.bioinformatics.irida.model.workflow.execution.galaxy.GalaxyWorkflowStatus;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.integration.Util;
import ca.corefacility.bioinformatics.irida.repositories.analysis.submission.AnalysisSubmissionRepository;
import ca.corefacility.bioinformatics.irida.service.AnalysisExecutionScheduledTask;
import ca.corefacility.bioinformatics.irida.service.AnalysisSubmissionService;
import ca.corefacility.bioinformatics.irida.service.CleanupAnalysisSubmissionCondition;
import ca.corefacility.bioinformatics.irida.service.analysis.execution.AnalysisExecutionService;
import ca.corefacility.bioinformatics.irida.service.impl.AnalysisExecutionScheduledTaskImpl;

import com.google.common.collect.Sets;

/**
 * Tests out scheduling analysis tasks.
 * 
 *
 */
public class AnalysisExecutionScheduledTaskImplTest {

	@Mock
	private AnalysisSubmissionService analysisSubmissionService;
	@Mock
	private AnalysisSubmissionRepository analysisSubmissionRepository;
	@Mock
	private AnalysisExecutionService analysisExecutionService;

	@Mock
	private Set<SequenceFile> sequenceFiles;

	@Mock
	private ReferenceFile referenceFile;

	@Mock
	private AnalysisPhylogenomicsPipeline analysis;

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
				analysisExecutionService, CleanupAnalysisSubmissionCondition.NEVER_CLEANUP);

		analysisSubmission = AnalysisSubmission.builder(workflowId)
				.name("my analysis")
				.inputFilesSingle(sequenceFiles)
				.referenceFile(referenceFile)
				.build();
		analysisSubmission.setId(INTERNAL_ID);
		analysisSubmission.setRemoteAnalysisId(ANALYSIS_ID);
	}

	/**
	 * Tests successfully preparing submitted analyses.
	 * 
	 * @throws ExecutionManagerException
	 * @throws IridaWorkflowNotFoundException
	 * @throws IOException
	 */
	@Test
	public void testPrepareAnalysesSuccess() throws ExecutionManagerException, IridaWorkflowNotFoundException,
			IOException {
		analysisSubmission.setAnalysisState(AnalysisState.NEW);

		when(analysisSubmissionRepository.findByAnalysisState(AnalysisState.NEW)).thenReturn(
				Arrays.asList(analysisSubmission));

		analysisExecutionScheduledTask.prepareAnalyses();

		verify(analysisExecutionService).prepareSubmission(analysisSubmission);
	}

	/**
	 * Tests no analysis to prepare.
	 * 
	 * @throws ExecutionManagerException
	 * @throws IridaWorkflowNotFoundException
	 * @throws IOException
	 */
	@Test
	public void testPrepareAnalysesNoAnalysis() throws ExecutionManagerException, IridaWorkflowNotFoundException,
			IOException {
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
	 */
	@Test
	public void testExecuteAnalysesSuccess() throws ExecutionManagerException, IridaWorkflowException {
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
	 */
	@Test
	public void testExecuteAnalysesNoAnalyses() throws ExecutionManagerException, IridaWorkflowException {
		analysisSubmission.setAnalysisState(AnalysisState.PREPARED);

		when(analysisSubmissionRepository.findByAnalysisState(AnalysisState.PREPARED)).thenReturn(
				new ArrayList<AnalysisSubmission>());

		analysisExecutionScheduledTask.executeAnalyses();

		verify(analysisExecutionService, never()).executeAnalysis(analysisSubmission);
	}

	/**
	 * Tests successfully switching analysis state to
	 * {@link AnalysisState.FINISHED_RUNNING} on success in Galaxy.
	 * 
	 * @throws ExecutionManagerException
	 * @throws IridaWorkflowNotFoundException
	 */
	@Test
	public void testMonitorRunningAnalysesSuccessFinished() throws ExecutionManagerException,
			IridaWorkflowNotFoundException {
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
	}

	/**
	 * Tests successfully skipping over switching analysis state for a running
	 * analysis in Galaxy.
	 * 
	 * @throws ExecutionManagerException
	 * @throws IridaWorkflowNotFoundException
	 */
	@Test
	public void testMonitorRunningAnalysesSuccessRunning() throws ExecutionManagerException,
			IridaWorkflowNotFoundException {
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
	public void testMonitorRunningAnalysesSuccessQueued() throws ExecutionManagerException,
			IridaWorkflowNotFoundException {
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
	 * if there was an error Galaxy state.
	 * 
	 * @throws ExecutionManagerException
	 * @throws IridaWorkflowNotFoundException
	 */
	@Test
	public void testMonitorRunningAnalysesSuccessError() throws ExecutionManagerException,
			IridaWorkflowNotFoundException {
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
	}
	
	/**
	 * Tests successfully switching an analysis to {@link AnalysisState.ERROR}
	 * if there was an error building the workflow status.
	 * 
	 * @throws ExecutionManagerException
	 * @throws IridaWorkflowNotFoundException
	 */
	@Test
	public void testMonitorRunningAnalysesErrorWorkflowStatus() throws ExecutionManagerException,
			IridaWorkflowNotFoundException {
		analysisSubmission.setAnalysisState(AnalysisState.RUNNING);

		when(analysisSubmissionRepository.findByAnalysisState(AnalysisState.RUNNING)).thenReturn(
				Arrays.asList(analysisSubmission));
		when(analysisExecutionService.getWorkflowStatus(analysisSubmission)).thenThrow(new IllegalArgumentException());

		analysisExecutionScheduledTask.monitorRunningAnalyses();

		assertEquals(AnalysisState.ERROR, analysisSubmission.getAnalysisState());
		verify(analysisSubmissionRepository).save(analysisSubmission);
	}

	/**
	 * Tests successfully switching an analysis to {@link AnalysisState.ERROR}
	 * if there was an Galaxy job with an error, but still running.
	 * 
	 * @throws ExecutionManagerException
	 * @throws IridaWorkflowNotFoundException
	 */
	@Test
	public void testMonitorRunningAnalysesSuccessErrorStillRunning() throws ExecutionManagerException,
			IridaWorkflowNotFoundException {
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
	public void testTransferAnalysesResultsSuccess() throws ExecutionManagerException, IOException,
			IridaWorkflowNotFoundException, IridaWorkflowAnalysisTypeException {
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
	public void testTransferAnalysesResultsNoAnalyses() throws ExecutionManagerException, IOException,
			IridaWorkflowNotFoundException, IridaWorkflowAnalysisTypeException {
		when(analysisSubmissionRepository.findByAnalysisState(AnalysisState.FINISHED_RUNNING)).thenReturn(
				new ArrayList<AnalysisSubmission>());

		analysisExecutionScheduledTask.transferAnalysesResults();

		verify(analysisExecutionService, never()).transferAnalysisResults(analysisSubmission);
	}
}
