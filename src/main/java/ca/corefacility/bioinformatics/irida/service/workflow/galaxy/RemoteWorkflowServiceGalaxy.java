package ca.corefacility.bioinformatics.irida.service.workflow.galaxy;

import ca.corefacility.bioinformatics.irida.model.workflow.galaxy.RemoteWorkflowGalaxy;
import ca.corefacility.bioinformatics.irida.service.workflow.RemoteWorkflowService;

/**
 * A RemoteWorkflowService for getting Galaxy workflows.
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 * @param <R> The type of RemoteWorkflowGalaxy to interact with.
 */
public abstract class RemoteWorkflowServiceGalaxy<R extends RemoteWorkflowGalaxy> 
	implements RemoteWorkflowService<R> {

}
