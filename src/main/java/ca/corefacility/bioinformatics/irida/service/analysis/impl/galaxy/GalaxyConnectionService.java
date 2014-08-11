package ca.corefacility.bioinformatics.irida.service.analysis.impl.galaxy;

import ca.corefacility.bioinformatics.irida.exceptions.ExecutionManagerException;
import ca.corefacility.bioinformatics.irida.model.workflow.galaxy.GalaxyAnalysisId;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyHistoriesService;

/**
 * Handles getting connections to difference services for Galaxy.
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 */
public class GalaxyConnectionService {

	/**
	 * Gets a GalaxyHistoriesService given an analysis id.
	 * @param workflowId The id of the workflow.
	 * @return  A GalaxyHistoriesService corresponding to this id.
	 * @throws ExecutionManagerException If there was an issue getting the galaxy histories service.
	 */
	public GalaxyHistoriesService getGalaxyHistoriesService(GalaxyAnalysisId workflowId) 
			throws ExecutionManagerException {
		throw new UnsupportedOperationException();
	}
}
