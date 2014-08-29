package ca.corefacility.bioinformatics.irida.ria.unit.components;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;

import ca.corefacility.bioinformatics.irida.ria.components.PipelineSubmission;
import ca.corefacility.bioinformatics.irida.ria.unit.TestDataFactory;
import ca.corefacility.bioinformatics.irida.service.ReferenceFileService;
import ca.corefacility.bioinformatics.irida.service.SequenceFileService;
import ca.corefacility.bioinformatics.irida.service.analysis.execution.galaxy.phylogenomics.impl.AnalysisExecutionServicePhylogenomics;
import ca.corefacility.bioinformatics.irida.service.workflow.galaxy.phylogenomics.impl.RemoteWorkflowServicePhylogenomics;

import com.google.common.collect.ImmutableList;

/**
 * Unit Test for {@link ca.corefacility.bioinformatics.irida.ria.components.PipelineSubmission}
 *
 * @author Josh Adam <josh.adam@phac-aspc.gc.ca>
 */
public class PipelineSubmissionTest {
	public static final long REFERENCE_FILE_ID = 1L;
	/*
	 * SERVICES
	 */
	private ReferenceFileService referenceFileService;
	private SequenceFileService sequenceFileService;
	private RemoteWorkflowServicePhylogenomics remoteWorkflowServicePhylogenomics;
	private AnalysisExecutionServicePhylogenomics analysisExecutionServicePhylogenomics;

	/*
	 * COMPONENT
	 */
	private PipelineSubmission pipelineSubmission;

	@Before
	public void init() {
		referenceFileService = Mockito.mock(ReferenceFileService.class);
		sequenceFileService = Mockito.mock(SequenceFileService.class);
		remoteWorkflowServicePhylogenomics = Mockito.mock(RemoteWorkflowServicePhylogenomics.class);
		analysisExecutionServicePhylogenomics = Mockito.mock(AnalysisExecutionServicePhylogenomics.class);

		pipelineSubmission = new PipelineSubmission(referenceFileService, sequenceFileService,
				remoteWorkflowServicePhylogenomics, analysisExecutionServicePhylogenomics);
	}

	@Test
	public void testSetSequenceFiles() {
		Mockito.when(sequenceFileService.read(Matchers.anyLong())).thenReturn(TestDataFactory.constructSequenceFile());
		pipelineSubmission.setSequenceFiles(getFileIds());
		Mockito.verify(sequenceFileService, Mockito.times(10)).read(Matchers.anyLong());
	}

	@Test
	public void testSetReferenceFiles() {
		Mockito.when(referenceFileService.read(REFERENCE_FILE_ID))
				.thenReturn(TestDataFactory.constructReferenceFile());
		pipelineSubmission.setReferenceFile(REFERENCE_FILE_ID);
		Mockito.verify(referenceFileService, Mockito.times(1)).read(REFERENCE_FILE_ID);
	}

	private List<Long> getFileIds() {
		return ImmutableList.of(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L);
	}
}
