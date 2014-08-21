package ca.corefacility.bioinformatics.irida.repositories.workflow;

import ca.corefacility.bioinformatics.irida.model.workflow.RemoteWorkflow;
import ca.corefacility.bioinformatics.irida.repositories.IridaJpaRepository;

/**
 * A repository for storing {@link RemoteWorkflow} objects.
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 */
public interface RemoteWorkflowRepository extends IridaJpaRepository<RemoteWorkflow, String>  {

}
