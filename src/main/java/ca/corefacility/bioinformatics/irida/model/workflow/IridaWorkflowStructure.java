package ca.corefacility.bioinformatics.irida.model.workflow;

import java.nio.file.Path;
import java.util.Objects;

/**
 * Defines the structure of a workflow for IRIDA.
 * 
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 */
public class IridaWorkflowStructure {

	private Path workflowFile;

	public IridaWorkflowStructure(Path workflowFile) {
		this.workflowFile = workflowFile;
	}

	public Path getWorkflowFile() {
		return workflowFile;
	}

	public void setWorkflowFile(Path workflowFile) {
		this.workflowFile = workflowFile;
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
}
