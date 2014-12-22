package ca.corefacility.bioinformatics.irida.model.workflow;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import ca.corefacility.bioinformatics.irida.model.workflow.analysis.TestAnalysis;

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
		try {
			return new IridaWorkflow(buildTestDescription(), buildTestStructure());
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}

	private static IridaWorkflowStructure buildTestStructure() {
		return new IridaWorkflowStructure(Paths.get("/tmp"));
	}

	private static IridaWorkflowDescription buildTestDescription() throws MalformedURLException {
		return buildTestDescription(DEFAULT_ID, "TestWorkflow", "1.0");
	}

	private static IridaWorkflowDescription buildTestDescription(UUID id, String name, String version)
			throws MalformedURLException {
		List<IridaWorkflowOutput> outputs = new LinkedList<>();
		outputs.add(new IridaWorkflowOutput("output1", "output1.txt"));
		outputs.add(new IridaWorkflowOutput("output2", "output2.txt"));

		List<IridaWorkflowTool> tools = new LinkedList<>();
		IridaWorkflowTool workflowTool = new IridaWorkflowTool("sam_to_bam",
				"toolshed.g2.bx.psu.edu/repos/devteam/sam_to_bam/sam_to_bam/1.1.4", "1.1.4", "devteam", new URL(
						"http://toolshed.g2.bx.psu.edu/"), "8176b2575aa1");
		tools.add(workflowTool);

		IridaWorkflowDescription iridaWorkflow = new IridaWorkflowDescription(id, name, version, "Mr. Developer",
				"developer@example.com", TestAnalysis.class, new IridaWorkflowInput("sequence_reads", "reference"), outputs,
				tools);

		return iridaWorkflow;
	}
}
