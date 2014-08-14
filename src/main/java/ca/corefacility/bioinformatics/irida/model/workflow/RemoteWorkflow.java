package ca.corefacility.bioinformatics.irida.model.workflow;

/**
 * A reference to a workflow in a remote execution manager.
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 */
public interface RemoteWorkflow {
		
	public String getWorkflowId();
	public String getWorkflowChecksum();
}
