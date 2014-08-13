package ca.corefacility.bioinformatics.irida.service.analysis.impl.galaxy.integration;

public class RemoteWorkflowGalaxy implements RemoteWorkflow<ExecutionManagerGalaxy> {

	@Override
	public void setWorkflowId(String workflowId) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setExecutionManager(ExecutionManagerGalaxy executionManager) {
		throw new UnsupportedOperationException();
	}

	@Override
	public ExecutionManagerGalaxy getExecutionManager() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getSequenceFileInputLabel() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getReferenceFileInputLabel() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getWorkflowId() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getWorkflowChecksum() {
		throw new UnsupportedOperationException();
	}
}
