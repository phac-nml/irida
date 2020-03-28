package ca.corefacility.bioinformatics.irida.model.workflow.submission.unit;

import static org.junit.Assert.*;

import java.util.Map;
import java.util.UUID;

import org.junit.Test;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;

import ca.corefacility.bioinformatics.irida.model.project.ReferenceFile;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.LocalSequenceFile;
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

	private static final SequenceFile sequenceFile = new LocalSequenceFile();
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
		assertEquals("analysis submission should have a reference to the specified named parameters.", inputParameters,
				submission.getInputParameters());
	}

	@Test(expected = UnsupportedOperationException.class)
	public void testBuildWithNamedParametersThenOthers() {
		// disallow adding or changing input parameters after we've elected to
		// use named parameters.
		AnalysisSubmission.builder(workflowId).withNamedParameters(namedParameters)
				.inputFiles(Sets.newHashSet(singleEndFile)).inputParameter("something", "crazy");
	}

	@Test(expected = UnsupportedOperationException.class)
	public void testBuildWithNamedParametersThenOthersMap() {
		AnalysisSubmission.builder(workflowId).withNamedParameters(namedParameters)
				.inputFiles(Sets.newHashSet(singleEndFile)).inputParameters(inputParameters);
	}

	@Test
	public void testBuildSuccess() {
		assertNotNull(
				"analysis submission is null",
				AnalysisSubmission.builder(workflowId).name("name").referenceFile(referenceFile)
						.inputFiles(Sets.newHashSet(singleEndFile)).inputParameters(inputParameters).build());
	}

	@Test
	public void testIndividualInputParameter() {
		AnalysisSubmission submission = AnalysisSubmission.builder(workflowId).name("name")
				.referenceFile(referenceFile).inputFiles(Sets.newHashSet(singleEndFile))
				.inputParameter("name", "value").inputParameter("name2", "value2").build();
		assertEquals("input parameter \"name\" not correct", "value", submission.getInputParameters().get("name"));
		assertEquals("input parameter \"name2\" not correct", "value2", submission.getInputParameters().get("name2"));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testIndividualInputParameterDuplicateFail() {
		AnalysisSubmission.builder(workflowId).name("name").referenceFile(referenceFile)
				.inputFiles(Sets.newHashSet(singleEndFile)).inputParameter("name", "value")
				.inputParameter("name", "value2").build();
	}

	@Test(expected = NullPointerException.class)
	public void testNullinputFilesSingle() {
		AnalysisSubmission.builder(workflowId).inputFiles(null).build();
	}

	@Test(expected = IllegalArgumentException.class)
	public void testEmptyinputFilesSingle() {
		AnalysisSubmission.builder(workflowId).inputFiles(Sets.newHashSet()).build();
	}

	@Test(expected = NullPointerException.class)
	public void testNullinputFilesPaired() {
		AnalysisSubmission.builder(workflowId).inputFiles(null).build();
	}

	@Test(expected = IllegalArgumentException.class)
	public void testEmptyinputFilesPaired() {
		AnalysisSubmission.builder(workflowId).inputFiles(Sets.newHashSet()).build();
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

	@Test(expected = NullPointerException.class)
	public void testNullInputParameters() {
		AnalysisSubmission.builder(workflowId).inputFiles(Sets.newHashSet(singleEndFile))
				.inputParameters(null).build();
	}

	@Test(expected = IllegalArgumentException.class)
	public void testEmptyInputParameters() {
		AnalysisSubmission.builder(workflowId).inputFiles(Sets.newHashSet(singleEndFile))
				.inputParameters(ImmutableMap.of()).build();
	}
}
