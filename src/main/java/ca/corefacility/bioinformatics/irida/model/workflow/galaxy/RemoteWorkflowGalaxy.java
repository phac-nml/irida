package ca.corefacility.bioinformatics.irida.model.workflow.galaxy;

import ca.corefacility.bioinformatics.irida.model.workflow.RemoteWorkflow;

/**
 * A RemoteWorkflow that can be submitted to a Galaxy execution manager.
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 */
public abstract class RemoteWorkflowGalaxy implements RemoteWorkflow {
	
	private String workflowId;
	private String workflowChecksum;
	
	/**
	 * Builds a new RemoteWorkflowGalaxy.
	 * @param workflowId The id of the workflow in Galaxy.
	 * @param workflowChecksum  The checksum of the workflow in Galaxy.
	 */
	public RemoteWorkflowGalaxy(String workflowId, String workflowChecksum) {
		this.workflowId = workflowId;
		this.workflowChecksum = workflowChecksum;
	}

	public void setWorkflowId(String workflowId) {
		this.workflowId = workflowId;
	}

	@Override
	public String getWorkflowId() {
		return workflowId;
	}

	@Override
	public String getWorkflowChecksum() {
		return workflowChecksum;
	}
	
	public void setWorkflowChecksum(String workflowChecksum) {
		this.workflowChecksum = workflowChecksum;
	}
}
