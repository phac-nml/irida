package ca.corefacility.bioinformatics.irida.model.workflow.config;

import java.util.Set;
import java.util.UUID;

/**
 * A class wrapping around a {@link Set} of {@link UUID}s for IRIDA workflows.
 * 
 *
 */
public class IridaWorkflowIdSet {
	private Set<UUID> iridaWorkflowIds;

	/**
	 * Builds a new {@link IridaWorkflowIdSet} of workflow ids.
	 * 
	 * @param iridaWorkflowIds
	 *            The set of ids to build.
	 */
	public IridaWorkflowIdSet(Set<UUID> iridaWorkflowIds) {
		this.iridaWorkflowIds = iridaWorkflowIds;
	}

	public Set<UUID> getIridaWorkflowIds() {
		return iridaWorkflowIds;
	}
}
