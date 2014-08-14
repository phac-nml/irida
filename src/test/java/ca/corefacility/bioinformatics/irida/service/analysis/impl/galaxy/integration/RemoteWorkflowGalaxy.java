package ca.corefacility.bioinformatics.irida.service.analysis.impl.galaxy.integration;

import static com.google.common.base.Preconditions.checkNotNull;

public class RemoteWorkflowGalaxy implements RemoteWorkflow<ExecutionManagerGalaxy> {
	
	private String workflowId;
	private ExecutionManagerGalaxy executionManager;
	private String workflowChecksum;

	@Override
	public void setWorkflowId(String workflowId) {
		checkNotNull(workflowId, "workflowId is null");
		this.workflowId = workflowId;
	}

	@Override
	public void setExecutionManager(ExecutionManagerGalaxy executionManager) {
		checkNotNull(executionManager, "executionManager is null");
		this.executionManager = executionManager;
	}

	@Override
	public ExecutionManagerGalaxy getExecutionManager() {
		return executionManager;
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
		checkNotNull(workflowChecksum, "workflowChecksum is null");
		this.workflowChecksum = workflowChecksum;
	}
}
