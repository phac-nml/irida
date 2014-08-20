package ca.corefacility.bioinformatics.irida.service.workflow.galaxy.phylogenomics.impl;

import ca.corefacility.bioinformatics.irida.model.workflow.galaxy.phylogenomics.RemoteWorkflowPhylogenomics;
import ca.corefacility.bioinformatics.irida.service.workflow.galaxy.RemoteWorkflowServiceGalaxy;

/**
 * A service for obtaining remote workflows for phylogenomics analysis in Galaxy.
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 */
public class RemoteWorkflowServicePhylogenomics 
	extends RemoteWorkflowServiceGalaxy<RemoteWorkflowPhylogenomics> {

	private RemoteWorkflowPhylogenomics currentWorkflow;
	
	/**
	 * Builds a new RemoteWorkflowServicePhylogenomics with the given current workflow.
	 * This is just temporary as I need to implement a way to get/set this information
	 * from the database.
	 * @param currentWorkflow  The currentWorkflow to use.
	 */
	public RemoteWorkflowServicePhylogenomics(
			RemoteWorkflowPhylogenomics currentWorkflow) {
		this.currentWorkflow = currentWorkflow;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public RemoteWorkflowPhylogenomics getCurrentWorkflow() {
		return currentWorkflow;
	}
}
