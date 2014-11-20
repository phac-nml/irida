package ca.corefacility.bioinformatics.irida.model.workflow;

/**
 * Describes a workflow for IRIDA.
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 */
public class IridaWorkflow {
	private IridaWorkflowDescription workflowDescription;

	public IridaWorkflowDescription getWorkflowDescription() {
		return workflowDescription;
	}

	public void setWorkflowDescription(IridaWorkflowDescription workflowDescription) {
		this.workflowDescription = workflowDescription;
	}
}
