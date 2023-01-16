package ca.corefacility.bioinformatics.irida.model.workflow;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import ca.corefacility.bioinformatics.irida.model.workflow.analysis.type.AnalysisType;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.type.BuiltInAnalysisTypes;
import ca.corefacility.bioinformatics.irida.model.workflow.description.*;
import ca.corefacility.bioinformatics.irida.model.workflow.structure.IridaWorkflowStructure;

import com.google.common.collect.Lists;

/**
 * Builds test {@link IridaWorkflow}s.
 */
public class IridaWorkflowTestBuilder {
	public static final UUID DEFAULT_ID = UUID.fromString("739f29ea-ae82-48b9-8914-3d2931405db6");
	public static final UUID MULTI_SAMPLES_ID = UUID.fromString("a8a573ef-b51e-409a-9a26-3fb79a6b894e");

	/**
	 * Builds a default test {@link IridaWorkflow} which accepts single input files.
	 * 
	 * @return A test workflow.
	 * @throws MalformedURLException
	 */
	public static IridaWorkflow buildTestWorkflowSingle() {
		return buildTestWorkflow(DEFAULT_ID, Input.SINGLE, "reference", true);
	}

	/**
	 * Builds a default test {@link IridaWorkflow} which accepts single input files and has no reference.
	 * 
	 * @return A test workflow.
	 * @throws MalformedURLException
	 */
	public static IridaWorkflow buildTestWorkflowSingleNoReference() {
		return buildTestWorkflow(DEFAULT_ID, Input.SINGLE, null, true);
	}

	/**
	 * Builds an {@link IridaWorkflow} which accepts paired input files.
	 * 
	 * @return A test workflow.
	 * @throws MalformedURLException
	 */
	public static IridaWorkflow buildTestWorkflowPaired() {
		return buildTestWorkflow(DEFAULT_ID, Input.PAIRED, "reference", true);
	}

	/**
	 * Builds an {@link IridaWorkflow} which accepts both single and paired input files.
	 * 
	 * @return A test workflow.
	 * @throws MalformedURLException
	 */
	public static IridaWorkflow buildTestWorkflowSinglePaired() {
		return buildTestWorkflow(DEFAULT_ID, Input.SINGLE_PAIRED, "reference", true);
	}

	/**
	 * Builds a test {@link IridaWorkflow} with the given id.
	 * 
	 * @param workflowId           The workflow id.
	 * @param input                The input type.
	 * @param reference            The reference label.
	 * @param requiresSingleSample Whether or not this workflow requires a single sample.
	 * @return A test workflow.
	 * @throws MalformedURLException
	 */
	public static IridaWorkflow buildTestWorkflow(UUID workflowId, Input input, String reference,
			boolean requiresSingleSample) {
		IridaWorkflow workflow = null;
		try {
			workflow = new IridaWorkflow(buildTestDescription(workflowId, input, reference, requiresSingleSample),
					buildTestStructure());
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		return workflow;
	}

	private static IridaWorkflowDescription buildTestDescription(UUID workflowId, Input input, String reference,
			boolean requiresSingleSample) throws MalformedURLException {
		return buildTestDescription(workflowId, "TestWorkflow", "1.0", BuiltInAnalysisTypes.DEFAULT, input, reference,
				requiresSingleSample);
	}

	/**
	 * Builds a test {@link IridaWorkflow} with the given id.
	 * 
	 * @param workflowId The workflow id.
	 * @param input      The input type.
	 * @param reference  The reference label.
	 * @return A test workflow.
	 * @throws MalformedURLException
	 */
	public static IridaWorkflow buildTestWorkflow(UUID workflowId, Input input, String reference) {
		try {
			return new IridaWorkflow(buildTestDescription(workflowId, input, reference), buildTestStructure());
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Builds a default test {@link IridaWorkflow} with a null analysis type.
	 * 
	 * @return A test workflow.
	 * @throws MalformedURLException
	 */
	public static IridaWorkflow buildTestWorkflowNullAnalysisType() {
		try {
			return new IridaWorkflow(
					buildTestDescription(DEFAULT_ID, "TestWorkflow", "1.0", null, Input.SINGLE, "reference", true),
					buildTestStructure());
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}

	private static IridaWorkflowStructure buildTestStructure() {
		return new IridaWorkflowStructure(Paths.get("/tmp"));
	}

	private static IridaWorkflowDescription buildTestDescription(UUID workflowId, Input input, String reference)
			throws MalformedURLException {
		return buildTestDescription(workflowId, "TestWorkflow", "1.0", BuiltInAnalysisTypes.DEFAULT, input, reference,
				true);
	}

	/**
	 * Builds a {@link IridaWorkflowDescription} with the following information.
	 * 
	 * @param id                   The id of the workflow.
	 * @param name                 The name of the workflow.
	 * @param version              The version of the workflow.
	 * @param analysisType         The {@link AnalysisTypeOld} of the workflow.
	 * @param input                The input type of the workflow
	 * @param reference            The reference label for the workflow.
	 * @param requiresSingleSample Whether or not this workflow requires a single sample.
	 * @return An {@link IridaWorkflowDescription} with the given information.
	 * @throws MalformedURLException
	 */
	public static IridaWorkflowDescription buildTestDescription(UUID id, String name, String version,
			AnalysisType analysisType, Input input, String reference, boolean requiresSingleSample)
			throws MalformedURLException {
		List<IridaWorkflowOutput> outputs = new LinkedList<>();
		outputs.add(new IridaWorkflowOutput("output1", "output1.txt"));
		outputs.add(new IridaWorkflowOutput("output2", "output2.txt"));

		List<IridaWorkflowToolRepository> tools = new LinkedList<>();
		IridaWorkflowToolRepository workflowTool = new IridaWorkflowToolRepository("sam_to_bam", "devteam",
				new URL("http://toolshed.g2.bx.psu.edu/"), "8176b2575aa1");
		tools.add(workflowTool);

		IridaWorkflowInput workflowInput = null;
		switch (input) {
		case SINGLE:
			workflowInput = new IridaWorkflowInput("sequence_reads", null, reference, requiresSingleSample);
			break;
		case PAIRED:
			workflowInput = new IridaWorkflowInput(null, "sequence_reads_paired", reference, requiresSingleSample);
			break;
		case SINGLE_PAIRED:
			workflowInput = new IridaWorkflowInput("sequence_reads", "sequence_reads_paired", reference,
					requiresSingleSample);
			break;
		}

		List<IridaWorkflowParameter> parameters = new LinkedList<>();
		IridaToolParameter tool1 = new IridaToolParameter("irida.corefacility.ca/galaxy-shed/repos/irida/test-tool/0.1",
				"a");
		IridaToolParameter tool2 = new IridaToolParameter("irida.corefacility.ca/galaxy-shed/repos/irida/test-tool/0.1",
				"b");
		IridaWorkflowParameter parameter1 = new IridaWorkflowParameter("test-parameter", "1",
				Lists.newArrayList(tool1, tool2));
		parameters.add(parameter1);

		IridaWorkflowDescription iridaWorkflow = new IridaWorkflowDescription(id, name, version, analysisType,
				workflowInput, outputs, tools, parameters);

		return iridaWorkflow;
	}

	public static enum Input {
		SINGLE,
		PAIRED,
		SINGLE_PAIRED
	}

	public static IridaWorkflow buildTestWorkflowSinglePairedMultipleSamples() {
		return buildTestWorkflow(MULTI_SAMPLES_ID, Input.SINGLE_PAIRED, "reference", false);
	}
}
