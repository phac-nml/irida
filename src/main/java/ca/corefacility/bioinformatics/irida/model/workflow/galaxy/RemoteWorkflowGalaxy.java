package ca.corefacility.bioinformatics.irida.model.workflow.galaxy;

import ca.corefacility.bioinformatics.irida.model.workflow.RemoteWorkflow;

/**
 * A RemoteWorkflow that can be submitted to a Galaxy execution manager.
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 */
public abstract class RemoteWorkflowGalaxy extends RemoteWorkflow {
	
	protected RemoteWorkflowGalaxy() {
	}
	
	/**
	 * Builds a new RemoteWorkflowGalaxy.
	 * @param workflowId The id of the workflow in Galaxy.
	 * @param workflowChecksum  The checksum of the workflow in Galaxy.
	 */
	public RemoteWorkflowGalaxy(String workflowId, String workflowChecksum) {
		super(workflowId, workflowChecksum);
	}
}
