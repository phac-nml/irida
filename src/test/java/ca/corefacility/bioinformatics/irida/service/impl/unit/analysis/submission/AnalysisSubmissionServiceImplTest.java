package ca.corefacility.bioinformatics.irida.service.impl.unit.analysis.submission;

import javax.validation.Validator;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;
import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.exceptions.NoPercentageCompleteException;
import ca.corefacility.bioinformatics.irida.model.enums.AnalysisState;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;
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

	private AnalysisSubmissionServiceImpl analysisSubmissionServiceImpl;

	private static final Long ID = 1L;

	/**
	 * Setup for tests.
	 */
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);

		analysisSubmissionServiceImpl = new AnalysisSubmissionServiceImpl(analysisSubmissionRepository, userRepository,
				referenceFileRepository, sequenceFileService, sequenceFilePairService, validator);

		when(analysisSubmissionRepository.findOne(ID)).thenReturn(analysisSubmission);
	}

	/**
	 * Tests getting the percent complete in the new state.
	 * 
	 * @throws EntityNotFoundException
	 * @throws NoPercentageCompleteException
	 */
	@Test
	public void testGetPercentageCompleteStateNew() throws NoPercentageCompleteException, EntityNotFoundException {
		when(analysisSubmission.getAnalysisState()).thenReturn(AnalysisState.NEW);

		assertEquals("invalid percent complete", 0.0f,
				analysisSubmissionServiceImpl.getPercentCompleteForAnalysisSubmission(ID), DELTA);
	}

	/**
	 * Tests getting the percent complete in the preparing state.
	 * 
	 * @throws EntityNotFoundException
	 * @throws NoPercentageCompleteException
	 */
	@Test
	public void testGetPercentageCompleteStatePreparing() throws NoPercentageCompleteException, EntityNotFoundException {
		when(analysisSubmission.getAnalysisState()).thenReturn(AnalysisState.PREPARING);

		assertEquals("invalid percent complete", 0.0f,
				analysisSubmissionServiceImpl.getPercentCompleteForAnalysisSubmission(ID), DELTA);
	}

	/**
	 * Tests getting the percent complete in the prepared state.
	 * 
	 * @throws EntityNotFoundException
	 * @throws NoPercentageCompleteException
	 */
	@Test
	public void testGetPercentageCompleteStatePrepared() throws NoPercentageCompleteException, EntityNotFoundException {
		when(analysisSubmission.getAnalysisState()).thenReturn(AnalysisState.PREPARED);

		assertEquals("invalid percent complete", 1.0f,
				analysisSubmissionServiceImpl.getPercentCompleteForAnalysisSubmission(ID), DELTA);
	}

	/**
	 * Tests getting the percent complete in the submitting state.
	 * 
	 * @throws EntityNotFoundException
	 * @throws NoPercentageCompleteException
	 */
	@Test
	public void testGetPercentageCompleteStateSubmitting() throws NoPercentageCompleteException,
			EntityNotFoundException {
		when(analysisSubmission.getAnalysisState()).thenReturn(AnalysisState.SUBMITTING);

		assertEquals("invalid percent complete", 2.0f,
				analysisSubmissionServiceImpl.getPercentCompleteForAnalysisSubmission(ID), DELTA);
	}

	/**
	 * Tests getting the percent complete in the running state.
	 * 
	 * @throws EntityNotFoundException
	 * @throws NoPercentageCompleteException
	 */
	@Test
	public void testGetPercentageCompleteStateRunning() throws NoPercentageCompleteException, EntityNotFoundException {
		when(analysisSubmission.getAnalysisState()).thenReturn(AnalysisState.RUNNING);

		assertEquals("invalid percent complete", 10.0f,
				analysisSubmissionServiceImpl.getPercentCompleteForAnalysisSubmission(ID), DELTA);
	}

	/**
	 * Tests getting the percent complete in the finished running state.
	 * 
	 * @throws EntityNotFoundException
	 * @throws NoPercentageCompleteException
	 */
	@Test
	public void testGetPercentageCompleteStateFinishedRunning() throws NoPercentageCompleteException,
			EntityNotFoundException {
		when(analysisSubmission.getAnalysisState()).thenReturn(AnalysisState.FINISHED_RUNNING);

		assertEquals("invalid percent complete", 90.0f,
				analysisSubmissionServiceImpl.getPercentCompleteForAnalysisSubmission(ID), DELTA);
	}

	/**
	 * Tests getting the percent complete in the completing state.
	 * 
	 * @throws EntityNotFoundException
	 * @throws NoPercentageCompleteException
	 */
	@Test
	public void testGetPercentageCompleteStateCompleting() throws NoPercentageCompleteException,
			EntityNotFoundException {
		when(analysisSubmission.getAnalysisState()).thenReturn(AnalysisState.COMPLETING);

		assertEquals("invalid percent complete", 95.0f,
				analysisSubmissionServiceImpl.getPercentCompleteForAnalysisSubmission(ID), DELTA);
	}

	/**
	 * Tests getting the percent complete in the completed state.
	 * 
	 * @throws EntityNotFoundException
	 * @throws NoPercentageCompleteException
	 */
	@Test
	public void testGetPercentageCompleteStateCompleted() throws NoPercentageCompleteException, EntityNotFoundException {
		when(analysisSubmission.getAnalysisState()).thenReturn(AnalysisState.COMPLETED);

		assertEquals("invalid percent complete", 100.0f,
				analysisSubmissionServiceImpl.getPercentCompleteForAnalysisSubmission(ID), DELTA);
	}

	/**
	 * Tests getting the percent complete in the error.
	 * 
	 * @throws EntityNotFoundException
	 * @throws NoPercentageCompleteException
	 */
	@Test(expected = NoPercentageCompleteException.class)
	public void testGetPercentageCompleteError() throws NoPercentageCompleteException, EntityNotFoundException {
		when(analysisSubmission.getAnalysisState()).thenReturn(AnalysisState.ERROR);
		analysisSubmissionServiceImpl.getPercentCompleteForAnalysisSubmission(ID);
	}
}
