package ca.corefacility.bioinformatics.irida.service.workflow.galaxy.phylogenomics.impl;

import ca.corefacility.bioinformatics.irida.model.workflow.galaxy.phylogenomics.RemoteWorkflowGalaxyPhylogenomics;
import ca.corefacility.bioinformatics.irida.service.workflow.galaxy.RemoteWorkflowServiceGalaxy;

/**
 * A service for obtaining remote workflows for phylogenomics analysis in Galaxy.
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 */
public class RemoteWorkflowServiceGalaxyPhylogenomics 
	extends RemoteWorkflowServiceGalaxy<RemoteWorkflowGalaxyPhylogenomics> {

	private RemoteWorkflowGalaxyPhylogenomics currentWorkflow;
	
	/**
	 * Builds a new RemoteWorkflowGalaxyPhylogenomics with the given current workflow.
	 * This is just temporary as I need to implement a way to get/set this information
	 * from the database.
	 * @param currentWorkflow  The currentWorkflow to use.
	 */
	public RemoteWorkflowServiceGalaxyPhylogenomics(
			RemoteWorkflowGalaxyPhylogenomics currentWorkflow) {
		this.currentWorkflow = currentWorkflow;
	}

	@Override
	public RemoteWorkflowGalaxyPhylogenomics getCurrentWorkflow() {
		return currentWorkflow;
	}
}
