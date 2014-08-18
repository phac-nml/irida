package ca.corefacility.bioinformatics.irida.service.analysis.submission.galaxy.phylogenomics.impl;

import ca.corefacility.bioinformatics.irida.model.workflow.analysis.AnalysisPhylogenomicsPipeline;
import ca.corefacility.bioinformatics.irida.model.workflow.galaxy.phylogenomics.RemoteWorkflowGalaxyPhylogenomics;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.galaxy.phylogenomics.AnalysisSubmissionGalaxyPhylogenomicsPipeline;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyHistoriesService;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyWorkflowService;
import ca.corefacility.bioinformatics.irida.service.analysis.submission.galaxy.AnalysisSubmissionServiceGalaxy;
import ca.corefacility.bioinformatics.irida.service.analysis.workspace.galaxy.phylogenomics.impl.WorkspaceServicePhylogenomics;

/**
 * An execution service for performing a Phylogenomics Pipeline analysis.
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 */
public class AnalysisSubmissionServiceGalaxyPhylogenomicsPipeline
	extends AnalysisSubmissionServiceGalaxy
		<AnalysisPhylogenomicsPipeline,
		WorkspaceServicePhylogenomics,
		RemoteWorkflowGalaxyPhylogenomics,
		AnalysisSubmissionGalaxyPhylogenomicsPipeline> {
		
	/**
	 * Builds a new Phylogenomis Pipeline analysis with the given service classes.
	 * @param galaxyWorkflowService  A GalaxyWorkflowService for interacting with Galaxy workflows.
	 * @param galaxyHistoriesService  A GalaxyHistoriesService for interacting with Galaxy Histories.
	 * @param preparationService  A PreparationService for preparing workflows.
	 */
	public AnalysisSubmissionServiceGalaxyPhylogenomicsPipeline(GalaxyWorkflowService galaxyWorkflowService,
			GalaxyHistoriesService galaxyHistoriesService,
			WorkspaceServicePhylogenomics preparationService) {
		super(galaxyWorkflowService, galaxyHistoriesService, preparationService);
	}
}
