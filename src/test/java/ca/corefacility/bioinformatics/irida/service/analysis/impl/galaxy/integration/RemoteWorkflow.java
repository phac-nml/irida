package ca.corefacility.bioinformatics.irida.service.analysis.impl.galaxy.integration;

import ca.corefacility.bioinformatics.irida.service.analysis.ExecutionManager;

/**
 * A reference to a workflow in a remote execution manager.
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 */
public interface RemoteWorkflow<T extends ExecutionManager> {

	public void setWorkflowId(String workflowId);

	public void setExecutionManager(T executionManager);
		
	public String getWorkflowId();
		
	public T getExecutionManager();

	public String getWorkflowChecksum();

	public void setWorkflowChecksum(String workflowChecksum);
}
