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
	private IridaWorkflowStructure workflowStructure;

	/**
	 * Defines a new {@link IridaWorkflow} with the given information.
	 * 
	 * @param workflowDescription
	 *            A description of the workflow.
	 * @param workflowStructure
	 *            The structure defining this workflow.
	 */
	public IridaWorkflow(IridaWorkflowDescription workflowDescription, IridaWorkflowStructure workflowStructure) {
		this.workflowDescription = workflowDescription;
		this.workflowStructure = workflowStructure;
	}

	public IridaWorkflowDescription getWorkflowDescription() {
		return workflowDescription;
	}

	public IridaWorkflowStructure getWorkflowStructure() {
		return workflowStructure;
	}

	/**
	 * Gets a unique identifier for this workflow.
	 * 
	 * @return An {@link IridaWorkflowIdentifier} for this workflow.
	 */
	public IridaWorkflowIdentifier getWorkflowIdentifier() {
		return new IridaWorkflowIdentifier(workflowDescription.getName(), workflowDescription.getVersion());
	}

	@Override
	public int hashCode() {
		return Objects.hash(workflowDescription, workflowStructure);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		else if (obj instanceof IridaWorkflow) {
			IridaWorkflow other = (IridaWorkflow) obj;

			return Objects.equals(workflowDescription, other.workflowDescription)
					&& Objects.equals(workflowStructure, other.workflowStructure);
		}

		return false;
	}
}
