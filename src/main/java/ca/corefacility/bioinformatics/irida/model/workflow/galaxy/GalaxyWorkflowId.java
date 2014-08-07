package ca.corefacility.bioinformatics.irida.model.workflow.galaxy;

import static com.google.common.base.Preconditions.*;
import ca.corefacility.bioinformatics.irida.model.workflow.RemoteWorkflowId;

/**
 * An ID for a workflow in Galaxy.
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 */
public class GalaxyWorkflowId implements RemoteWorkflowId {

	private String id;
	
	/**
	 * Builds a new Galaxy workflow id given a String representation of this id.
	 * @param id  The String representation of this id.
	 */
	public GalaxyWorkflowId(String id) {
		checkNotNull(id, "id is null");
		this.id = id;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getValue() {
		return id;
	}
}
