package ca.corefacility.bioinformatics.irida.service.workflow;

import ca.corefacility.bioinformatics.irida.model.workflow.RemoteWorkflow;

/**
 * Service class for getting remote workflows.
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 * @param <R> A type of RemoteWorkflow.
 */
public interface RemoteWorkflowService<R extends RemoteWorkflow> {
	
	/**
	 * Gets the current implementation of this RemoteWorkflow.
	 * @return The current implementation of this RemoteWorkflow.
	 */
	R getCurrentWorkflow();
}
