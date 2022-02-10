package ca.corefacility.bioinformatics.irida.model.workflow.submission.unit;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;

import ca.corefacility.bioinformatics.irida.model.project.ReferenceFile;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SingleEndSequenceFile;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.IridaWorkflowNamedParameters;

/**
 * Tests out constructing {@link AnalysisSubmission} objects.
 * 
 *
 */
public class AnalysisSubmissionTest {

	private static final SequenceFile sequenceFile = new SequenceFile();
	private static final SingleEndSequenceFile singleEndFile = new SingleEndSequenceFile(sequenceFile);
	private static final ReferenceFile referenceFile = new ReferenceFile();
	private static final Map<String, String> inputParameters = ImmutableMap.of("test", "test");

	private static final UUID workflowId = UUID.randomUUID();

	private static final IridaWorkflowNamedParameters namedParameters = new IridaWorkflowNamedParameters("name",
			workflowId, inputParameters);

	@Test
	public void testBuildWithNamedParameters() {
		final AnalysisSubmission submission = AnalysisSubmission.builder(workflowId)
				.withNamedParameters(namedParameters).inputFiles(Sets.newHashSet(singleEndFile)).build();
		assertEquals(inputParameters, submission.getInputParameters(),
				"analysis submission should have a reference to the specified named parameters.");
	}

	@Test
	public void testBuildWithNamedParametersThenOthers() {
		// disallow adding or changing input parameters after we've elected to
		// use named parameters.
		assertThrows(UnsupportedOperationException.class, () -> {
			AnalysisSubmission.builder(workflowId).withNamedParameters(namedParameters)
					.inputFiles(Sets.newHashSet(singleEndFile)).inputParameter("something", "crazy");
		});
	}

	@Test
	public void testBuildWithNamedParametersThenOthersMap() {
		assertThrows(UnsupportedOperationException.class, () -> {
			AnalysisSubmission.builder(workflowId).withNamedParameters(namedParameters)
					.inputFiles(Sets.newHashSet(singleEndFile)).inputParameters(inputParameters);
		});
	}

	@Test
	public void testBuildSuccess() {
		assertNotNull(AnalysisSubmission.builder(workflowId).name("name").referenceFile(referenceFile)
						.inputFiles(Sets.newHashSet(singleEndFile)).inputParameters(inputParameters).build(),
				"analysis submission is null");
	}

	@Test
	public void testIndividualInputParameter() {
		AnalysisSubmission submission = AnalysisSubmission.builder(workflowId).name("name")
				.referenceFile(referenceFile).inputFiles(Sets.newHashSet(singleEndFile))
				.inputParameter("name", "value").inputParameter("name2", "value2").build();
		assertEquals("value", submission.getInputParameters().get("name"), "input parameter \"name\" not correct");
		assertEquals("value2", submission.getInputParameters().get("name2"), "input parameter \"name2\" not correct");
	}

	@Test
	public void testIndividualInputParameterDuplicateFail() {
		assertThrows(IllegalArgumentException.class, () -> {
			AnalysisSubmission.builder(workflowId).name("name").referenceFile(referenceFile)
					.inputFiles(Sets.newHashSet(singleEndFile)).inputParameter("name", "value")
					.inputParameter("name", "value2").build();
		});
	}

	@Test
	public void testNullinputFilesSingle() {
		assertThrows(NullPointerException.class, () -> {
			AnalysisSubmission.builder(workflowId).inputFiles(null).build();
		});
	}

	@Test
	public void testEmptyinputFilesSingle() {
		assertThrows(IllegalArgumentException.class, () -> {
			AnalysisSubmission.builder(workflowId).inputFiles(Sets.newHashSet()).build();
		});
	}

	@Test
	public void testNullinputFilesPaired() {
		assertThrows(NullPointerException.class, () -> {
			AnalysisSubmission.builder(workflowId).inputFiles(null).build();
		});
	}

	@Test
	public void testEmptyinputFilesPaired() {
		assertThrows(IllegalArgumentException.class, () -> {
			AnalysisSubmission.builder(workflowId).inputFiles(Sets.newHashSet()).build();
		});
	}

	@Test
	public void testNoInputFiles() {
		assertThrows(IllegalArgumentException.class, () -> {
			AnalysisSubmission.builder(workflowId).build();
		});
	}

	@Test
	public void testNullReferenceFile() {
		assertThrows(NullPointerException.class, () -> {
			AnalysisSubmission.builder(workflowId).referenceFile(null).build();
		});
	}

	@Test
	public void testNullWorkflowId() {
		assertThrows(NullPointerException.class, () -> {
			AnalysisSubmission.builder(null).build();
		});
	}

	@Test
	public void testNullName() {
		assertThrows(NullPointerException.class, () -> {
			AnalysisSubmission.builder(workflowId).name(null).build();
		});
	}

	@Test
	public void testNullInputParameters() {
		assertThrows(NullPointerException.class, () -> {
			AnalysisSubmission.builder(workflowId).inputFiles(Sets.newHashSet(singleEndFile))
					.inputParameters(null).build();
		});
	}

	@Test
	public void testEmptyInputParameters() {
		assertThrows(IllegalArgumentException.class, () -> {
			AnalysisSubmission.builder(workflowId).inputFiles(Sets.newHashSet(singleEndFile))
					.inputParameters(ImmutableMap.of()).build();
		});
	}
}
