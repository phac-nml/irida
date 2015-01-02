package ca.corefacility.bioinformatics.irida.model.workflow;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import ca.corefacility.bioinformatics.irida.model.enums.AnalysisType;
import ca.corefacility.bioinformatics.irida.model.workflow.description.IridaWorkflowDescription;
import ca.corefacility.bioinformatics.irida.model.workflow.description.IridaWorkflowInput;
import ca.corefacility.bioinformatics.irida.model.workflow.description.IridaWorkflowOutput;
import ca.corefacility.bioinformatics.irida.model.workflow.description.IridaWorkflowTool;
import ca.corefacility.bioinformatics.irida.model.workflow.structure.IridaWorkflowStructure;

/**
 * Builds test {@link IridaWorkflow}s.
 * 
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 */
public class IridaWorkflowTestBuilder {
	public final static UUID DEFAULT_ID = UUID.fromString("739f29ea-ae82-48b9-8914-3d2931405db6");

	/**
	 * Builds a default test {@link IridaWorkflow}.
	 * 
	 * @return A test workflow.
	 * @throws MalformedURLException
	 */
	public static IridaWorkflow buildTestWorkflow() {
		return buildTestWorkflow(DEFAULT_ID);
	}

	/**
	 * Builds a test {@link IridaWorkflow} with the given id.
	 * 
	 * @param workflowId
	 *            The workflow id.
	 * @return A test workflow.
	 * @throws MalformedURLException
	 */
	public static IridaWorkflow buildTestWorkflow(UUID workflowId) {
		try {
			return new IridaWorkflow(buildTestDescription(workflowId), buildTestStructure());
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
			return new IridaWorkflow(buildTestDescription(DEFAULT_ID, "TestWorkflow", "1.0", null),
					buildTestStructure());
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}

	private static IridaWorkflowStructure buildTestStructure() {
		return new IridaWorkflowStructure(Paths.get("/tmp"));
	}

	private static IridaWorkflowDescription buildTestDescription(UUID workflowId) throws MalformedURLException {
		return buildTestDescription(workflowId, "TestWorkflow", "1.0", AnalysisType.DEFAULT);
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
	 * @return An {@link IridaWorkflowDescription} with the given information.
	 * @throws MalformedURLException
	 */
	public static IridaWorkflowDescription buildTestDescription(UUID id, String name, String version,
			AnalysisType analysisType) throws MalformedURLException {
		List<IridaWorkflowOutput> outputs = new LinkedList<>();
		outputs.add(new IridaWorkflowOutput("output1", "output1.txt"));
		outputs.add(new IridaWorkflowOutput("output2", "output2.txt"));

		List<IridaWorkflowTool> tools = new LinkedList<>();
		IridaWorkflowTool workflowTool = new IridaWorkflowTool("sam_to_bam",
				"toolshed.g2.bx.psu.edu/repos/devteam/sam_to_bam/sam_to_bam/1.1.4", "1.1.4", "devteam", new URL(
						"http://toolshed.g2.bx.psu.edu/"), "8176b2575aa1");
		tools.add(workflowTool);

		IridaWorkflowDescription iridaWorkflow = new IridaWorkflowDescription(id, name, version, "Mr. Developer",
				"developer@example.com", analysisType, new IridaWorkflowInput("sequence_reads", "reference"), outputs,
				tools);

		return iridaWorkflow;
	}
}
