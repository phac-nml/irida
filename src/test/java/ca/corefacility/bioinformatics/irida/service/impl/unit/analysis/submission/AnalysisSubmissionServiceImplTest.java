package ca.corefacility.bioinformatics.irida.service.impl.unit.analysis.submission;

import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.exceptions.ExecutionManagerException;
import ca.corefacility.bioinformatics.irida.exceptions.NoPercentageCompleteException;
import ca.corefacility.bioinformatics.irida.model.enums.AnalysisCleanedState;
import ca.corefacility.bioinformatics.irida.model.enums.AnalysisState;
import ca.corefacility.bioinformatics.irida.model.workflow.execution.galaxy.GalaxyWorkflowStatus;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyHistoriesService;
import ca.corefacility.bioinformatics.irida.repositories.analysis.submission.AnalysisSubmissionRepository;
import ca.corefacility.bioinformatics.irida.repositories.analysis.submission.AnalysisSubmissionTemplateRepository;
import ca.corefacility.bioinformatics.irida.repositories.analysis.submission.JobErrorRepository;
import ca.corefacility.bioinformatics.irida.repositories.analysis.submission.ProjectAnalysisSubmissionJoinRepository;
import ca.corefacility.bioinformatics.irida.repositories.referencefile.ReferenceFileRepository;
import ca.corefacility.bioinformatics.irida.repositories.user.UserRepository;
import ca.corefacility.bioinformatics.irida.service.SequencingObjectService;
import ca.corefacility.bioinformatics.irida.service.analysis.execution.galaxy.AnalysisExecutionServiceGalaxyCleanupAsync;
import ca.corefacility.bioinformatics.irida.service.impl.analysis.submission.AnalysisSubmissionServiceImpl;
import ca.corefacility.bioinformatics.irida.service.workflow.IridaWorkflowsService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.validation.Validator;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link AnalysisSubmissionServiceImpl}.
 */
public class AnalysisSubmissionServiceImplTest {

	private static final float DELTA = 0.000001f;

	@Mock
	private AnalysisSubmission analysisSubmission;

	@Mock
	private AnalysisSubmissionRepository analysisSubmissionRepository;
	@Mock
	private AnalysisSubmissionTemplateRepository analysisTemplateRepository;
	@Mock
	private UserRepository userRepository;
	@Mock
	private ReferenceFileRepository referenceFileRepository;
	@Mock
	private SequencingObjectService sequencingObjectService;
	
	@Mock
	private ProjectAnalysisSubmissionJoinRepository pasRepository;
	
	@Mock
	private Validator validator;
	@Mock
	private GalaxyHistoriesService galaxyHistoriesService;
	@Mock
	private GalaxyWorkflowStatus galaxyWorkflowStatus;

	private AnalysisSubmissionServiceImpl analysisSubmissionServiceImpl;

	@Mock
	private IridaWorkflowsService iridaWorkflowsService;

	@Mock
	private JobErrorRepository jobErrorRepository;

	@Mock
	private AnalysisExecutionServiceGalaxyCleanupAsync analysisExecutionService;

	private static final Long ID = 1L;
	private static final String HISTORY_ID = "1";

	/**
	 * Setup for tests.
	 */
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);

		analysisSubmissionServiceImpl = new AnalysisSubmissionServiceImpl(analysisSubmissionRepository,
				analysisTemplateRepository, userRepository, referenceFileRepository, sequencingObjectService,
				galaxyHistoriesService, pasRepository, jobErrorRepository, iridaWorkflowsService, validator);
		analysisSubmissionServiceImpl.setAnalysisExecutionService(analysisExecutionService);

		when(analysisSubmissionRepository.findOne(ID)).thenReturn(analysisSubmission);
		when(analysisSubmission.getRemoteAnalysisId()).thenReturn(HISTORY_ID);
	}

	/**
	 * Tests getting the percent complete in the new state.
	 * 
	 * @throws EntityNotFoundException
	 * @throws ExecutionManagerException
	 */
	@Test
	public void testGetPercentageCompleteStateNew() throws EntityNotFoundException, ExecutionManagerException {
		when(analysisSubmission.getAnalysisState()).thenReturn(AnalysisState.NEW);

		assertEquals("invalid percent complete", 0.0f,
				analysisSubmissionServiceImpl.getPercentCompleteForAnalysisSubmission(ID), DELTA);
	}

	/**
	 * Tests getting the percent complete in the preparing state.
	 * 
	 * @throws EntityNotFoundException
	 * @throws ExecutionManagerException
	 */
	@Test
	public void testGetPercentageCompleteStatePreparing() throws EntityNotFoundException, ExecutionManagerException {
		when(analysisSubmission.getAnalysisState()).thenReturn(AnalysisState.PREPARING);

		assertEquals("invalid percent complete",
				AnalysisSubmissionServiceImpl.STATE_PERCENTAGE.get(AnalysisState.PREPARING),
				analysisSubmissionServiceImpl.getPercentCompleteForAnalysisSubmission(ID), DELTA);
	}

	/**
	 * Tests getting the percent complete in the prepared state.
	 * 
	 * @throws EntityNotFoundException
	 * @throws ExecutionManagerException
	 */
	@Test
	public void testGetPercentageCompleteStatePrepared() throws EntityNotFoundException, ExecutionManagerException {
		when(analysisSubmission.getAnalysisState()).thenReturn(AnalysisState.PREPARED);

		assertEquals("invalid percent complete",
				AnalysisSubmissionServiceImpl.STATE_PERCENTAGE.get(AnalysisState.PREPARED),
				analysisSubmissionServiceImpl.getPercentCompleteForAnalysisSubmission(ID), DELTA);
	}

	/**
	 * Tests getting the percent complete in the submitting state.
	 * 
	 * @throws EntityNotFoundException
	 * @throws ExecutionManagerException
	 */
	@Test
	public void testGetPercentageCompleteStateSubmitting() throws EntityNotFoundException, ExecutionManagerException {
		when(analysisSubmission.getAnalysisState()).thenReturn(AnalysisState.SUBMITTING);

		assertEquals("invalid percent complete",
				AnalysisSubmissionServiceImpl.STATE_PERCENTAGE.get(AnalysisState.SUBMITTING),
				analysisSubmissionServiceImpl.getPercentCompleteForAnalysisSubmission(ID), DELTA);
	}

	/**
	 * Tests getting the percent complete in the running state when the workflow
	 * has just started in Galaxy.
	 * 
	 * @throws EntityNotFoundException
	 * @throws ExecutionManagerException
	 */
	@Test
	public void testGetPercentageCompleteStateRunningJustStarted() throws EntityNotFoundException,
			ExecutionManagerException {
		when(analysisSubmission.getAnalysisState()).thenReturn(AnalysisState.RUNNING);
		when(galaxyHistoriesService.getStatusForHistory(HISTORY_ID)).thenReturn(galaxyWorkflowStatus);
		when(galaxyWorkflowStatus.getProportionComplete()).thenReturn(0.0f);

		assertEquals("invalid percent complete",
				AnalysisSubmissionServiceImpl.STATE_PERCENTAGE.get(AnalysisState.RUNNING),
				analysisSubmissionServiceImpl.getPercentCompleteForAnalysisSubmission(ID), DELTA);
	}

	/**
	 * Tests getting the percent complete in the running state when the workflow
	 * is halfway complete in Galaxy.
	 * 
	 * @throws EntityNotFoundException
	 * @throws ExecutionManagerException
	 */
	@Test
	public void testGetPercentageCompleteStateRunningHalfway() throws EntityNotFoundException,
			ExecutionManagerException {
		when(analysisSubmission.getAnalysisState()).thenReturn(AnalysisState.RUNNING);
		when(galaxyHistoriesService.getStatusForHistory(HISTORY_ID)).thenReturn(galaxyWorkflowStatus);
		when(galaxyWorkflowStatus.getProportionComplete()).thenReturn(0.5f);

		Float runningState = AnalysisSubmissionServiceImpl.STATE_PERCENTAGE.get(AnalysisState.RUNNING);
		Float finishedState = AnalysisSubmissionServiceImpl.STATE_PERCENTAGE.get(AnalysisState.FINISHED_RUNNING);
		// check that it's half done
		assertEquals("invalid percent complete", (runningState + finishedState) / 2,
				analysisSubmissionServiceImpl.getPercentCompleteForAnalysisSubmission(ID), DELTA);
	}

	/**
	 * Tests getting the percent complete in the running state when the workflow
	 * is 100% complete in Galaxy.
	 * 
	 * @throws EntityNotFoundException
	 * @throws ExecutionManagerException
	 */
	@Test
	public void testGetPercentageCompleteStateRunningFullyComplete() throws EntityNotFoundException,
			ExecutionManagerException {
		when(analysisSubmission.getAnalysisState()).thenReturn(AnalysisState.RUNNING);
		when(galaxyHistoriesService.getStatusForHistory(HISTORY_ID)).thenReturn(galaxyWorkflowStatus);
		when(galaxyWorkflowStatus.getProportionComplete()).thenReturn(1.0f);

		assertEquals("invalid percent complete", 90.0f,
				analysisSubmissionServiceImpl.getPercentCompleteForAnalysisSubmission(ID), DELTA);
	}

	/**
	 * Tests getting the percent complete in the finished running state.
	 * 
	 * @throws EntityNotFoundException
	 * @throws ExecutionManagerException
	 */
	@Test
	public void testGetPercentageCompleteStateFinishedRunning() throws EntityNotFoundException,
			ExecutionManagerException {
		when(analysisSubmission.getAnalysisState()).thenReturn(AnalysisState.FINISHED_RUNNING);

		assertEquals("invalid percent complete", 90.0f,
				analysisSubmissionServiceImpl.getPercentCompleteForAnalysisSubmission(ID), DELTA);
	}

	/**
	 * Tests getting the percent complete in the completing state.
	 * 
	 * @throws EntityNotFoundException
	 * @throws ExecutionManagerException
	 */
	@Test
	public void testGetPercentageCompleteStateCompleting() throws EntityNotFoundException, ExecutionManagerException {
		when(analysisSubmission.getAnalysisState()).thenReturn(AnalysisState.COMPLETING);

		assertEquals("invalid percent complete", 92.0f,
				analysisSubmissionServiceImpl.getPercentCompleteForAnalysisSubmission(ID), DELTA);
	}

	/**
	 * Tests getting the percent complete in the completed state.
	 * 
	 * @throws EntityNotFoundException
	 * @throws ExecutionManagerException
	 */
	@Test
	public void testGetPercentageCompleteStateCompleted() throws EntityNotFoundException, ExecutionManagerException {
		when(analysisSubmission.getAnalysisState()).thenReturn(AnalysisState.COMPLETED);

		assertEquals("invalid percent complete", 100.0f,
				analysisSubmissionServiceImpl.getPercentCompleteForAnalysisSubmission(ID), DELTA);
	}

	/**
	 * Tests getting the percent complete in the error.
	 * 
	 * @throws EntityNotFoundException
	 * @throws ExecutionManagerException
	 */
	@Test(expected = NoPercentageCompleteException.class)
	public void testGetPercentageCompleteError() throws EntityNotFoundException, ExecutionManagerException {
		when(analysisSubmission.getAnalysisState()).thenReturn(AnalysisState.ERROR);
		analysisSubmissionServiceImpl.getPercentCompleteForAnalysisSubmission(ID);
	}
	
	/**
	 * Tests that deleting a submission actually also tries to clean up the
	 * submission in galaxy.
	 * 
	 * @throws ExecutionManagerException
	 */
	@Test
	public void testDeleteSubmission() throws ExecutionManagerException {
		when(analysisSubmissionRepository.findOne(ID)).thenReturn(analysisSubmission);
		when(analysisSubmissionRepository.exists(ID)).thenReturn(true);
		when(analysisSubmission.getAnalysisCleanedState()).thenReturn(AnalysisCleanedState.NOT_CLEANED);
		analysisSubmissionServiceImpl.delete(ID);
		verify(analysisExecutionService).cleanupSubmission(analysisSubmission);
	}

	/**
	 * Tests that deleting a submission deletes the submission even if an
	 * execution manager exception is thrown.
	 * 
	 * @throws ExecutionManagerException
	 */
	@Test
	public void testDeleteSubmissionWorkflowError() throws ExecutionManagerException {
		when(analysisSubmissionRepository.findOne(ID)).thenReturn(analysisSubmission);
		when(analysisSubmissionRepository.exists(ID)).thenReturn(true);
		when(analysisExecutionService.cleanupSubmission(analysisSubmission)).thenThrow(new ExecutionManagerException());
		when(analysisSubmission.getAnalysisCleanedState()).thenReturn(AnalysisCleanedState.NOT_CLEANED);
		analysisSubmissionServiceImpl.delete(ID);
		verify(analysisExecutionService).cleanupSubmission(analysisSubmission);
		verify(analysisSubmissionRepository).delete(ID);
	}
}
