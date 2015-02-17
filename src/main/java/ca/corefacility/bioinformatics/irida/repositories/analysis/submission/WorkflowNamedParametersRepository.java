package ca.corefacility.bioinformatics.irida.repositories.analysis.submission;

import java.util.List;
import java.util.UUID;

import ca.corefacility.bioinformatics.irida.model.workflow.submission.IridaWorkflowNamedParameters;
import ca.corefacility.bioinformatics.irida.repositories.IridaJpaRepository;

/**
 * Repository for working with named parameter sets.
 * 
 * @author Franklin Bristow franklin.bristow@phac-aspc.gc.ca
 *
 */
public interface WorkflowNamedParametersRepository extends IridaJpaRepository<IridaWorkflowNamedParameters, Long> {

	/**
	 * Find all saved, named parameters for the specified workflow.
	 * 
	 * @param workflowId
	 *            the workflow ID to search for.
	 * @return the named parameters saved for the workflow.
	 */
	public List<IridaWorkflowNamedParameters> findByWorkflowId(final UUID workflowId);
}
