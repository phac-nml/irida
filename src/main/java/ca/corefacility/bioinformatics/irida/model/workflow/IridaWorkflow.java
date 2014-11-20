package ca.corefacility.bioinformatics.irida.model.workflow;

import java.util.Objects;

/**
 * Describes a workflow for IRIDA.
 * 
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 */
public class IridaWorkflow {
	private IridaWorkflowDescription workflowDescription;

	public IridaWorkflow(IridaWorkflowDescription workflowDescription) {
		this.workflowDescription = workflowDescription;
	}

	public IridaWorkflowDescription getWorkflowDescription() {
		return workflowDescription;
	}

	public void setWorkflowDescription(IridaWorkflowDescription workflowDescription) {
		this.workflowDescription = workflowDescription;
	}

	@Override
	public int hashCode() {
		return Objects.hash(workflowDescription);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		else if (obj instanceof IridaWorkflow) {
			IridaWorkflow other = (IridaWorkflow) obj;

			return Objects.equals(workflowDescription, other.workflowDescription);
		}

		return false;
	}
}
