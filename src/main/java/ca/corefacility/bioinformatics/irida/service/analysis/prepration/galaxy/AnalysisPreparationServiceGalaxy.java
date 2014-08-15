package ca.corefacility.bioinformatics.irida.service.analysis.prepration.galaxy;

import ca.corefacility.bioinformatics.irida.model.workflow.galaxy.RemoteWorkflowGalaxy;
import ca.corefacility.bioinformatics.irida.model.workflow.preparation.galaxy.PreparedWorkflowGalaxy;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.galaxy.AnalysisSubmissionGalaxy;
import ca.corefacility.bioinformatics.irida.service.analysis.prepration.AnalysisPreparationService;

/**
 * Prepares a Galaxy analysis for submission.
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 * @param <S>  The AnalysisSubmissionGalaxy to prepare.
 */
public abstract class AnalysisPreparationServiceGalaxy<R extends RemoteWorkflowGalaxy,
	S extends AnalysisSubmissionGalaxy<R>> 
	implements AnalysisPreparationService<S,PreparedWorkflowGalaxy> {

}