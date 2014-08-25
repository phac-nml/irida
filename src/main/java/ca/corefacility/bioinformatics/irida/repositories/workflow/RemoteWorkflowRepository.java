package ca.corefacility.bioinformatics.irida.repositories.workflow;

import org.springframework.data.jpa.repository.Query;

import ca.corefacility.bioinformatics.irida.model.workflow.RemoteWorkflow;
import ca.corefacility.bioinformatics.irida.repositories.IridaJpaRepository;

/**
 * A repository for storing {@link RemoteWorkflow} objects.
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 */
public interface RemoteWorkflowRepository extends IridaJpaRepository<RemoteWorkflow, String>  {

	/**
	 * Load up a @{link RemoteWorkflow} by an id for the passed type of RemoteWorkflow.
	 * @param workflowId  The id of the workflow to load.
	 * @param workflowType  The type of the workflow to load.
	 * @return  A RemoteWorkflow of the specified by with the given id.
	 */
	@Query("select w from RemoteWorkflow w where w.workflowId = ?1 and type(w) = ?2")
	public <T extends RemoteWorkflow> T getByType(String workflowId, Class<T> workflowType);
}
