package ca.corefacility.bioinformatics.irida.model.workflow.submission.unit;

import static org.junit.Assert.*;

import java.util.UUID;

import org.junit.Test;

import com.google.common.collect.Sets;

import ca.corefacility.bioinformatics.irida.model.project.ReferenceFile;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;

/**
 * Tests out constructing {@link AnalysisSubmission} objects.
 * 
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 */
public class AnalysisSubmissionTest {

	private static final SequenceFile sequenceFile = new SequenceFile();
	private static final ReferenceFile referenceFile = new ReferenceFile();

	private static final UUID workflowId = UUID.randomUUID();

	@Test
	public void testBuildSuccess() {
		assertNotNull(
				"analysis submission is null",
				AnalysisSubmission.builder(workflowId).name("name").referenceFile(referenceFile)
						.inputFilesSingle(Sets.newHashSet(sequenceFile)).build());
	}

	@Test(expected = NullPointerException.class)
	public void testNullinputFilesSingle() {
		AnalysisSubmission.builder(workflowId).inputFilesSingle(null).build();
	}

	@Test(expected = IllegalArgumentException.class)
	public void testEmptyinputFilesSingle() {
		AnalysisSubmission.builder(workflowId).inputFilesSingle(Sets.newHashSet()).build();
	}

	@Test(expected = NullPointerException.class)
	public void testNullinputFilesPaired() {
		AnalysisSubmission.builder(workflowId).inputFilesPaired(null).build();
	}

	@Test(expected = IllegalArgumentException.class)
	public void testEmptyinputFilesPaired() {
		AnalysisSubmission.builder(workflowId).inputFilesPaired(Sets.newHashSet()).build();
	}

	@Test(expected = IllegalArgumentException.class)
	public void testNoInputFiles() {
		AnalysisSubmission.builder(workflowId).build();
	}

	@Test(expected = NullPointerException.class)
	public void testNullReferenceFile() {
		AnalysisSubmission.builder(workflowId).referenceFile(null).build();
	}

	@Test(expected = NullPointerException.class)
	public void testNullWorkflowId() {
		AnalysisSubmission.builder(null).build();
	}

	@Test(expected = NullPointerException.class)
	public void testNullName() {
		AnalysisSubmission.builder(workflowId).name(null).build();
	}
}
