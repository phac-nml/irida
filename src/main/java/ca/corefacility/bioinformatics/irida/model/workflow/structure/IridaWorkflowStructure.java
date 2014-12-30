package ca.corefacility.bioinformatics.irida.model.workflow.structure;

import java.nio.file.Path;
import java.util.Objects;

/**
 * Defines the structure of a workflow for IRIDA. The structure consists of a
 * JSON-formatted file that has been exported from Galaxy and which defines the
 * set and execution plan for all tools in the workflow. An example of this file
 * is given below.
 * 
 * <pre>
 * {
 *   "a_galaxy_workflow": "true", 
 *   "annotation": "", 
 *   "format-version": "0.1", 
 *   "name": "TestWorkflow1", 
 *   "steps": {
 *   ...
 *   }
 * }
 * </pre>
 * 
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 */
public class IridaWorkflowStructure {

	private Path workflowFile;

	/**
	 * Builds a new {@link IridaWorkflowStructure} with the given information.
	 * 
	 * @param workflowFile
	 *            The file defining the structure of this workflow.
	 */
	public IridaWorkflowStructure(Path workflowFile) {
		this.workflowFile = workflowFile;
	}

	public Path getWorkflowFile() {
		return workflowFile;
	}

	@Override
	public int hashCode() {
		return Objects.hash(workflowFile);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		else if (obj instanceof IridaWorkflowStructure) {
			IridaWorkflowStructure other = (IridaWorkflowStructure) obj;

			return Objects.equals(workflowFile, other.workflowFile);
		}

		return false;
	}

	@Override
	public String toString() {
		return "IridaWorkflowStructure [workflowFile=" + workflowFile + "]";
	}
}
