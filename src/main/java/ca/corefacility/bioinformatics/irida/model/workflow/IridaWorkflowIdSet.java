package ca.corefacility.bioinformatics.irida.model.workflow;

import java.util.Set;
import java.util.UUID;

/**
 * A class wrapping around a {@link Set} of {@link UUID}s for IRIDA workflows.
 * 
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
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
