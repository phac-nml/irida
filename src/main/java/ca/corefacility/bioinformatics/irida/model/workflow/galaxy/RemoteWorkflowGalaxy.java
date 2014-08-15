package ca.corefacility.bioinformatics.irida.model.workflow.galaxy;

import ca.corefacility.bioinformatics.irida.model.workflow.RemoteWorkflow;

public abstract class RemoteWorkflowGalaxy implements RemoteWorkflow {
	
	private String workflowId;
	private String workflowChecksum;
	
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
