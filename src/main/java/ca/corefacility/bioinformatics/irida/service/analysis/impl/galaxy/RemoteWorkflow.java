package ca.corefacility.bioinformatics.irida.service.analysis.impl.galaxy;

/**
 * A reference to a workflow in a remote execution manager.
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 */
public interface RemoteWorkflow {

	public void setWorkflowId(String workflowId);
		
	public String getWorkflowId();
		
	public String getWorkflowChecksum();

	public void setWorkflowChecksum(String workflowChecksum);
}
