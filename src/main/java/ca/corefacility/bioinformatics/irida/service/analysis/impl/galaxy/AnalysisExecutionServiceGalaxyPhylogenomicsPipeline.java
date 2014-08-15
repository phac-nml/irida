package ca.corefacility.bioinformatics.irida.service.analysis.impl.galaxy;

import ca.corefacility.bioinformatics.irida.model.workflow.analysis.AnalysisPhylogenomicsPipeline;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.galaxy.AnalysisSubmissionGalaxyPhylogenomicsPipeline;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyHistoriesService;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyWorkflowService;
import ca.corefacility.bioinformatics.irida.service.galaxy.AnalysisExecutionServiceGalaxy;

/**
 * An execution service for performing a Phylogenomics Pipeline analysis.
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 */
public class AnalysisExecutionServiceGalaxyPhylogenomicsPipeline
	extends AnalysisExecutionServiceGalaxy
		<AnalysisPhylogenomicsPipeline,
		GalaxyWorkflowPreparationServicePhylogenomicsPipeline,
		AnalysisSubmissionGalaxyPhylogenomicsPipeline> {
		
	/**
	 * Builds a new Phylogenomis Pipeline analysis with the given service classes.
	 * @param galaxyWorkflowService  A GalaxyWorkflowService for interacting with Galaxy workflows.
	 * @param galaxyHistoriesService  A GalaxyHistoriesService for interacting with Galaxy Histories.
	 * @param preparationService  A PreparationService for preparing workflows.
	 */
	public AnalysisExecutionServiceGalaxyPhylogenomicsPipeline(GalaxyWorkflowService galaxyWorkflowService,
			GalaxyHistoriesService galaxyHistoriesService,
			GalaxyWorkflowPreparationServicePhylogenomicsPipeline preparationService) {
		super(galaxyWorkflowService, galaxyHistoriesService, preparationService);
	}
}
