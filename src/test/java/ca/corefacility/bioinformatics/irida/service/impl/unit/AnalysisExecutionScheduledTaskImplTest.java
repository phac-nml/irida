package ca.corefacility.bioinformatics.irida.service.impl.unit;

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
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.AnalysisPhylogenomicsPipeline;
import ca.corefacility.bioinformatics.irida.model.workflow.galaxy.phylogenomics.RemoteWorkflowPhylogenomics;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.galaxy.phylogenomics.AnalysisSubmissionPhylogenomics;
import ca.corefacility.bioinformatics.irida.repositories.analysis.submission.AnalysisSubmissionRepository;
import ca.corefacility.bioinformatics.irida.service.AnalysisExecutionScheduledTask;
import ca.corefacility.bioinformatics.irida.service.AnalysisSubmissionService;
import ca.corefacility.bioinformatics.irida.service.analysis.execution.AnalysisExecutionServiceSimplified;
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
	private AnalysisExecutionServiceSimplified analysisExecutionServiceSimplified;

	@Mock
	private RemoteWorkflowPhylogenomics remoteWorkflow;

	@Mock
	private Set<SequenceFile> sequenceFiles;

	@Mock
	private ReferenceFile referenceFile;

	@Mock
	private AnalysisPhylogenomicsPipeline analysis;

	private static final String ANALYSIS_ID = "1";
	private static final Long INTERNAL_ID = 1L;
	private AnalysisSubmissionPhylogenomics analysisSubmission;

	private AnalysisExecutionScheduledTask analysisExecutionScheduledTask;

	private UUID workflowId = UUID.randomUUID();

	/**
	 * Sets up variables for tests.
	 */
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);

		analysisExecutionScheduledTask = new AnalysisExecutionScheduledTaskImpl(analysisSubmissionRepository,
				analysisExecutionServiceSimplified);

		analysisSubmission = new AnalysisSubmissionPhylogenomics("my analysis", sequenceFiles, referenceFile,
				remoteWorkflow, workflowId);
		analysisSubmission.setId(INTERNAL_ID);
		analysisSubmission.setRemoteAnalysisId(ANALYSIS_ID);

		when(analysisSubmissionRepository.getByType(INTERNAL_ID, AnalysisSubmissionPhylogenomics.class)).thenReturn(
				analysisSubmission);
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

		verify(analysisExecutionServiceSimplified).executeAnalysis(analysisSubmission);
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

		verify(analysisExecutionServiceSimplified, never()).executeAnalysis(analysisSubmission);
	}

	/**
	 * Tests successfully transfering results for a submitted analysis.
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

		verify(analysisExecutionServiceSimplified).transferAnalysisResults(analysisSubmission);
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

		verify(analysisExecutionServiceSimplified, never()).transferAnalysisResults(analysisSubmission);
	}
}
