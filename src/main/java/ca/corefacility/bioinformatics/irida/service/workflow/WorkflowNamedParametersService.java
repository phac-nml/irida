package ca.corefacility.bioinformatics.irida.service.workflow;

import java.util.List;
import java.util.UUID;

import ca.corefacility.bioinformatics.irida.model.workflow.submission.IridaWorkflowNamedParameters;
import ca.corefacility.bioinformatics.irida.service.CRUDService;

/**
 * Service for interacting with named parameter sets.
 * 
 *
 */
public interface WorkflowNamedParametersService extends CRUDService<Long, IridaWorkflowNamedParameters> {

	/**
	 * Get the named parameters saved for the specified workflow.
	 * 
	 * @param workflowId
	 *            the workflow ID to load parameters for.
	 * @return the collection of named workflow parameter sets for the specified
	 *         workflow.
	 */
	public List<IridaWorkflowNamedParameters> findNamedParametersForWorkflow(final UUID workflowId);
}
