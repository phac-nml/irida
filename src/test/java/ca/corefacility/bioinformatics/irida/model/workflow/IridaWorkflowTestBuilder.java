package ca.corefacility.bioinformatics.irida.model.workflow;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import com.google.common.collect.Lists;

import ca.corefacility.bioinformatics.irida.model.enums.AnalysisType;
import ca.corefacility.bioinformatics.irida.model.workflow.description.IridaToolParameter;
import ca.corefacility.bioinformatics.irida.model.workflow.description.IridaWorkflowDescription;
import ca.corefacility.bioinformatics.irida.model.workflow.description.IridaWorkflowInput;
import ca.corefacility.bioinformatics.irida.model.workflow.description.IridaWorkflowOutput;
import ca.corefacility.bioinformatics.irida.model.workflow.description.IridaWorkflowParameter;
import ca.corefacility.bioinformatics.irida.model.workflow.description.IridaWorkflowToolRepository;
import ca.corefacility.bioinformatics.irida.model.workflow.structure.IridaWorkflowStructure;

/**
 * Builds test {@link IridaWorkflow}s.
 * 
 *
 */
public class IridaWorkflowTestBuilder {
	public final static UUID DEFAULT_ID = UUID.fromString("739f29ea-ae82-48b9-8914-3d2931405db6");

	/**
	 * Builds a default test {@link IridaWorkflow} which accepts single input
	 * files.
	 * 
	 * @return A test workflow.
	 * @throws MalformedURLException
	 */
	public static IridaWorkflow buildTestWorkflowSingle() {
		return buildTestWorkflow(DEFAULT_ID, Input.SINGLE, "reference");
	}
	
	/**
	 * Builds a default test {@link IridaWorkflow} which accepts single input
	 * files and has no reference.
	 * 
	 * @return A test workflow.
	 * @throws MalformedURLException
	 */
	public static IridaWorkflow buildTestWorkflowSingleNoReference() {
		return buildTestWorkflow(DEFAULT_ID, Input.SINGLE, null);
	}

	/**
	 * Builds an {@link IridaWorkflow} which accepts paired input files.
	 * 
	 * @return A test workflow.
	 * @throws MalformedURLException
	 */
	public static IridaWorkflow buildTestWorkflowPaired() {
		return buildTestWorkflow(DEFAULT_ID, Input.PAIRED, "reference");
	}

	/**
	 * Builds an {@link IridaWorkflow} which accepts both single and paired
	 * input files.
	 * 
	 * @return A test workflow.
	 * @throws MalformedURLException
	 */
	public static IridaWorkflow buildTestWorkflowSinglePaired() {
		return buildTestWorkflow(DEFAULT_ID, Input.SINGLE_PAIRED, "reference");
	}

	/**
	 * Builds a test {@link IridaWorkflow} with the given id.
	 * 
	 * @param workflowId
	 *            The workflow id.
	 * @param input
	 *            The input type.
	 * @param reference
	 *            The reference label.
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
			return new IridaWorkflow(buildTestDescription(DEFAULT_ID, "TestWorkflow", "1.0", null, Input.SINGLE, "reference"),
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
		return buildTestDescription(workflowId, "TestWorkflow", "1.0", AnalysisType.DEFAULT, input, reference);
	}

	/**
	 * Builds a {@link IridaWorkflowDescription} with the following information.
	 * 
	 * @param id
	 *            The id of the workflow.
	 * @param name
	 *            The name of the workflow.
	 * @param version
	 *            The version of the workflow.
	 * @param analysisType
	 *            The {@link AnalysisType} of the workflow.
	 * @param reference
	 *            The reference label for the workflow.
	 * @return An {@link IridaWorkflowDescription} with the given information.
	 * @throws MalformedURLException
	 */
	public static IridaWorkflowDescription buildTestDescription(UUID id, String name, String version,
			AnalysisType analysisType, Input input, String reference) throws MalformedURLException {
		List<IridaWorkflowOutput> outputs = new LinkedList<>();
		outputs.add(new IridaWorkflowOutput("output1", "output1.txt"));
		outputs.add(new IridaWorkflowOutput("output2", "output2.txt"));

		List<IridaWorkflowToolRepository> tools = new LinkedList<>();
		IridaWorkflowToolRepository workflowTool = new IridaWorkflowToolRepository("sam_to_bam", "devteam", new URL(
				"http://toolshed.g2.bx.psu.edu/"), "8176b2575aa1");
		tools.add(workflowTool);

		IridaWorkflowInput workflowInput = null;
		switch (input) {
		case SINGLE:
			workflowInput = new IridaWorkflowInput("sequence_reads", null, reference, false);
			break;
		case PAIRED:
			workflowInput = new IridaWorkflowInput(null, "sequence_reads_paired", reference, false);
			break;
		case SINGLE_PAIRED:
			workflowInput = new IridaWorkflowInput("sequence_reads", "sequence_reads_paired", reference, false);
			break;
		}
		
		List<IridaWorkflowParameter> parameters = new LinkedList<>();
		IridaToolParameter tool1 = new IridaToolParameter("irida.corefacility.ca/galaxy-shed/repos/irida/test-tool/0.1", "a");
		IridaToolParameter tool2 = new IridaToolParameter("irida.corefacility.ca/galaxy-shed/repos/irida/test-tool/0.1", "b");
		IridaWorkflowParameter parameter1 = new IridaWorkflowParameter("test-parameter", "1", Lists.newArrayList(tool1, tool2));
		parameters.add(parameter1);

		IridaWorkflowDescription iridaWorkflow = new IridaWorkflowDescription(id, name, version,
				analysisType, workflowInput, outputs, tools, parameters);

		return iridaWorkflow;
	}

	public static enum Input {
		SINGLE, PAIRED, SINGLE_PAIRED
	}
}
