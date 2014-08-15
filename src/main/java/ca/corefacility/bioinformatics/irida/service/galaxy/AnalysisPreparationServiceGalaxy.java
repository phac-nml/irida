package ca.corefacility.bioinformatics.irida.service.galaxy;

import ca.corefacility.bioinformatics.irida.model.workflow.submission.galaxy.AnalysisSubmissionGalaxy;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.galaxy.PreparedWorkflowGalaxy;
import ca.corefacility.bioinformatics.irida.service.AnalysisPreparationService;

public abstract class AnalysisPreparationServiceGalaxy<S extends AnalysisSubmissionGalaxy> 
	implements AnalysisPreparationService<S,PreparedWorkflowGalaxy> {

}