package ca.corefacility.bioinformatics.irida.service.impl.unit;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import ca.corefacility.bioinformatics.irida.exceptions.ExecutionManagerException;
import ca.corefacility.bioinformatics.irida.exceptions.IridaWorkflowNotFoundException;
import ca.corefacility.bioinformatics.irida.model.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.enums.AnalysisState;
import ca.corefacility.bioinformatics.irida.model.project.ReferenceFile;
import ca.corefacility.bioinformatics.irida.model.workflow.WorkflowState;
import ca.corefacility.bioinformatics.irida.model.workflow.WorkflowStatus;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.AnalysisPhylogenomicsPipeline;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;
import ca.corefacility.bioinformatics.irida.repositories.analysis.submission.AnalysisSubmissionRepository;
import ca.corefacility.bioinformatics.irida.service.AnalysisExecutionScheduledTask;
import ca.corefacility.bioinformatics.irida.service.AnalysisSubmissionService;
import ca.corefacility.bioinformatics.irida.service.analysis.execution.AnalysisExecutionService;
import ca.corefacility.bioinformatics.irida.service.impl.AnalysisExecutionScheduledTaskImpl;

/**
 * Tests out scheduling analysis tasks.
 * 
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
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
				analysisExecutionService);

		analysisSubmission = new AnalysisSubmission("my analysis", sequenceFiles, referenceFile,
				workflowId);
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
	 * @throws IridaWorkflowNotFoundException
	 */
	@Test
	public void testExecuteAnalysesSuccess() throws ExecutionManagerException, IridaWorkflowNotFoundException {
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
	 * @throws IridaWorkflowNotFoundException
	 */
	@Test
	public void testExecuteAnalysesNoAnalyses() throws ExecutionManagerException, IridaWorkflowNotFoundException {
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

		when(analysisSubmissionRepository.findByAnalysisState(AnalysisState.RUNNING)).thenReturn(
				Arrays.asList(analysisSubmission));
		when(analysisExecutionService.getWorkflowStatus(analysisSubmission)).thenReturn(
				new WorkflowStatus(WorkflowState.OK, 100.0f));

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

		when(analysisSubmissionRepository.findByAnalysisState(AnalysisState.RUNNING)).thenReturn(
				Arrays.asList(analysisSubmission));
		when(analysisExecutionService.getWorkflowStatus(analysisSubmission)).thenReturn(
				new WorkflowStatus(WorkflowState.RUNNING, 50.0f));

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

		when(analysisSubmissionRepository.findByAnalysisState(AnalysisState.RUNNING)).thenReturn(
				Arrays.asList(analysisSubmission));
		when(analysisExecutionService.getWorkflowStatus(analysisSubmission)).thenReturn(
				new WorkflowStatus(WorkflowState.ERROR, 50.0f));

		analysisExecutionScheduledTask.monitorRunningAnalyses();

		assertEquals(AnalysisState.ERROR, analysisSubmission.getAnalysisState());
		verify(analysisSubmissionRepository).save(analysisSubmission);
	}

	/**
	 * Tests successfully switching an analysis to {@link AnalysisState.ERROR}
	 * if there was an unknown Galaxy state.
	 * 
	 * @throws ExecutionManagerException
	 * @throws IridaWorkflowNotFoundException
	 */
	@Test
	public void testMonitorRunningAnalysesSuccessUnknown() throws ExecutionManagerException,
			IridaWorkflowNotFoundException {
		analysisSubmission.setAnalysisState(AnalysisState.RUNNING);

		when(analysisSubmissionRepository.findByAnalysisState(AnalysisState.RUNNING)).thenReturn(
				Arrays.asList(analysisSubmission));
		when(analysisExecutionService.getWorkflowStatus(analysisSubmission)).thenReturn(
				new WorkflowStatus(WorkflowState.UNKNOWN, 50.0f));

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
	 */
	@Test
	public void testTransferAnalysesResultsSuccess() throws ExecutionManagerException, IOException,
			IridaWorkflowNotFoundException {
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
	 */
	@Test
	public void testTransferAnalysesResultsNoAnalyses() throws ExecutionManagerException, IOException,
			IridaWorkflowNotFoundException {
		when(analysisSubmissionRepository.findByAnalysisState(AnalysisState.FINISHED_RUNNING)).thenReturn(
				new ArrayList<AnalysisSubmission>());

		analysisExecutionScheduledTask.transferAnalysesResults();

		verify(analysisExecutionService, never()).transferAnalysisResults(analysisSubmission);
	}
}
