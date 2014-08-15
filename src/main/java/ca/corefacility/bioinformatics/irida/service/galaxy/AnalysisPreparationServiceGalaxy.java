package ca.corefacility.bioinformatics.irida.service.galaxy;

import ca.corefacility.bioinformatics.irida.model.workflow.submission.PreparedWorkflowGalaxy;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.galaxy.AnalysisSubmissionGalaxy;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyHistoriesService;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyWorkflowService;
import ca.corefacility.bioinformatics.irida.service.AnalysisPreparationService;

public abstract class AnalysisPreparationServiceGalaxy<S extends AnalysisSubmissionGalaxy, 
	P extends PreparedWorkflowGalaxy> 
	implements AnalysisPreparationService<S,P> {
	
	protected GalaxyHistoriesService galaxyHistoriesService;
	protected GalaxyWorkflowService galaxyWorkflowService;
	
	public AnalysisPreparationServiceGalaxy(GalaxyHistoriesService galaxyHistoriesService,
			GalaxyWorkflowService galaxyWorkflowService) {
		this.galaxyHistoriesService = galaxyHistoriesService;
		this.galaxyWorkflowService = galaxyWorkflowService;
	}

	@Override
	public P prepareAnalysisWorkspace(S analysisSubmission) {
		// TODO Auto-generated method stub
		return null;
	}
}
