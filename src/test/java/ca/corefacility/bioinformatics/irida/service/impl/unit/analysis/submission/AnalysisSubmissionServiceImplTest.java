package ca.corefacility.bioinformatics.irida.service.impl.unit.analysis.submission;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import javax.validation.Validator;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.exceptions.ExecutionManagerException;
import ca.corefacility.bioinformatics.irida.exceptions.NoPercentageCompleteException;
import ca.corefacility.bioinformatics.irida.model.enums.AnalysisState;
import ca.corefacility.bioinformatics.irida.model.workflow.execution.galaxy.GalaxyWorkflowStatus;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyHistoriesService;
import ca.corefacility.bioinformatics.irida.repositories.analysis.submission.AnalysisSubmissionRepository;
import ca.corefacility.bioinformatics.irida.repositories.referencefile.ReferenceFileRepository;
import ca.corefacility.bioinformatics.irida.repositories.user.UserRepository;
import ca.corefacility.bioinformatics.irida.service.SequenceFilePairService;
import ca.corefacility.bioinformatics.irida.service.SequenceFileService;
import ca.corefacility.bioinformatics.irida.service.impl.analysis.submission.AnalysisSubmissionServiceImpl;

/**
 * Unit tests for {@link AnalysisSubmissinServiceImpl}.
 */
public class AnalysisSubmissionServiceImplTest {

	private static final float DELTA = 0.000001f;

	@Mock
	private AnalysisSubmission analysisSubmission;

	@Mock
	private AnalysisSubmissionRepository analysisSubmissionRepository;
	@Mock
	private UserRepository userRepository;
	@Mock
	private ReferenceFileRepository referenceFileRepository;
	@Mock
	private SequenceFileService sequenceFileService;
	@Mock
	private SequenceFilePairService sequenceFilePairService;
	@Mock
	private Validator validator;
	@Mock
	private GalaxyHistoriesService galaxyHistoriesService;
	@Mock
	private GalaxyWorkflowStatus galaxyWorkflowStatus;

	private AnalysisSubmissionServiceImpl analysisSubmissionServiceImpl;

	private static final Long ID = 1L;
	private static final String HISTORY_ID = "1";

	/**
	 * Setup for tests.
	 */
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);

		analysisSubmissionServiceImpl = new AnalysisSubmissionServiceImpl(analysisSubmissionRepository, userRepository,
				referenceFileRepository, sequenceFileService, sequenceFilePairService, galaxyHistoriesService,
				validator);

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

		assertEquals("invalid percent complete", 0.0f,
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

		assertEquals("invalid percent complete", 1.0f,
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

		assertEquals("invalid percent complete", 5.0f,
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

		assertEquals("invalid percent complete", 10.0f,
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

		assertEquals("invalid percent complete", 50.0f, // half-way between 10
														// and 90
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

		assertEquals("invalid percent complete", 95.0f,
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
}
