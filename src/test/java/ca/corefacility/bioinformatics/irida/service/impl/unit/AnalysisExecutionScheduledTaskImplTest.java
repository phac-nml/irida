package ca.corefacility.bioinformatics.irida.service.impl.unit;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import ca.corefacility.bioinformatics.irida.exceptions.ExecutionManagerException;
import ca.corefacility.bioinformatics.irida.model.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.enums.AnalysisState;
import ca.corefacility.bioinformatics.irida.model.project.ReferenceFile;
import ca.corefacility.bioinformatics.irida.model.workflow.galaxy.phylogenomics.RemoteWorkflowPhylogenomics;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.galaxy.phylogenomics.AnalysisSubmissionPhylogenomics;
import ca.corefacility.bioinformatics.irida.repositories.analysis.submission.AnalysisSubmissionRepository;
import ca.corefacility.bioinformatics.irida.service.AnalysisExecutionScheduledTask;
import ca.corefacility.bioinformatics.irida.service.AnalysisSubmissionService;
import ca.corefacility.bioinformatics.irida.service.analysis.execution.galaxy.phylogenomics.impl.AnalysisExecutionServicePhylogenomics;
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
	private AnalysisExecutionServicePhylogenomics analysisExecutionServicePhylogenomics;

	@Mock
	private RemoteWorkflowPhylogenomics remoteWorkflow;

	@Mock
	private Set<SequenceFile> sequenceFiles;

	@Mock
	private ReferenceFile referenceFile;

	private static final String ANALYSIS_ID = "1";
	private AnalysisSubmissionPhylogenomics analysisSubmission;

	private AnalysisExecutionScheduledTask analysisExecutionScheduledTask;

	/**
	 * Sets up variables for tests.
	 */
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);

		analysisExecutionScheduledTask = new AnalysisExecutionScheduledTaskImpl(
				analysisSubmissionService, analysisSubmissionRepository,
				analysisExecutionServicePhylogenomics);

		analysisSubmission = new AnalysisSubmissionPhylogenomics(sequenceFiles,
				referenceFile, remoteWorkflow);
		analysisSubmission.setAnalysisState(AnalysisState.SUBMITTED);
		analysisSubmission.setRemoteAnalysisId(ANALYSIS_ID);

		when(
				analysisSubmissionRepository.getByType(ANALYSIS_ID,
						AnalysisSubmissionPhylogenomics.class)).thenReturn(
				analysisSubmission);
	}

	/**
	 * Tests successfully executing submitted analyses.
	 * 
	 * @throws ExecutionManagerException
	 */
	@Test
	public void testExecuteAnalysesSuccess() throws ExecutionManagerException {
		when(
				analysisSubmissionRepository
						.findByAnalysisState(AnalysisState.SUBMITTED))
				.thenReturn(Arrays.asList(analysisSubmission));

		analysisExecutionScheduledTask.executeAnalyses();

		verify(analysisSubmissionRepository).findByAnalysisState(
				AnalysisState.SUBMITTED);
		verify(analysisExecutionServicePhylogenomics).executeAnalysis(
				analysisSubmission);
	}

	/**
	 * Tests successfully executing submitted analyses and moving it to an error
	 * state.
	 * 
	 * @throws ExecutionManagerException
	 */
	@Test
	public void testExecuteAnalysesAnalysisError()
			throws ExecutionManagerException {
		when(
				analysisSubmissionRepository
						.findByAnalysisState(AnalysisState.SUBMITTED))
				.thenReturn(Arrays.asList(analysisSubmission));

		when(
				analysisExecutionServicePhylogenomics
						.executeAnalysis(analysisSubmission)).thenThrow(
				new ExecutionManagerException());

		analysisExecutionScheduledTask.executeAnalyses();

		verify(analysisSubmissionRepository).findByAnalysisState(
				AnalysisState.SUBMITTED);
		verify(analysisExecutionServicePhylogenomics).executeAnalysis(
				analysisSubmission);
		verify(analysisSubmissionService).setStateForAnalysisSubmission(
				ANALYSIS_ID, AnalysisState.ERROR);
	}
}
