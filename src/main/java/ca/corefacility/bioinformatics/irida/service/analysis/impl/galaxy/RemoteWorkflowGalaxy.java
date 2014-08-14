package ca.corefacility.bioinformatics.irida.service.analysis.impl.galaxy;

public class RemoteWorkflowGalaxy implements RemoteWorkflow {
	
	private String workflowId;
	private String workflowChecksum;

	@Override
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
	
	@Override
	public void setWorkflowChecksum(String workflowChecksum) {
		this.workflowChecksum = workflowChecksum;
	}
}
