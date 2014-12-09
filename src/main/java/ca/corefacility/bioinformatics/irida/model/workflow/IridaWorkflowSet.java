package ca.corefacility.bioinformatics.irida.model.workflow;

import java.util.Set;

/**
 * Wraps around a {@link Set} of {@IridaWorkflow}s to allow it
 * to be handled as a spring managed bean.
 * 
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 */
public class IridaWorkflowSet {
	private Set<IridaWorkflow> iridaWorkflows;

	/**
	 * Builds a new {@link IridaWorkflowSet} of workflows.
	 * 
	 * @param iridaWorkflows
	 *            The set of workflows to build.
	 */
	public IridaWorkflowSet(Set<IridaWorkflow> iridaWorkflows) {
		this.iridaWorkflows = iridaWorkflows;
	}

	public Set<IridaWorkflow> getIridaWorkflows() {
		return iridaWorkflows;
	}
}
