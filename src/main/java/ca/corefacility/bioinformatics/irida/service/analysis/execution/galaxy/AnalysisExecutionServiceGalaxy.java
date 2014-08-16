package ca.corefacility.bioinformatics.irida.service.analysis.execution.galaxy;

import ca.corefacility.bioinformatics.irida.model.workflow.galaxy.PreparedWorkflowGalaxy;
import ca.corefacility.bioinformatics.irida.model.workflow.galaxy.RemoteWorkflowGalaxy;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.galaxy.AnalysisSubmissionGalaxy;
import ca.corefacility.bioinformatics.irida.service.analysis.execution.AnalysisExecutionService;

/**
 * Executes a Galaxy analysis.
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 * @param <R> The type of RemoteWorkflow to use.
 * @param <S> The AnalysisSubmissionGalaxy to prepare and execute.
 */
public abstract class AnalysisExecutionServiceGalaxy<R extends RemoteWorkflowGalaxy,
	S extends AnalysisSubmissionGalaxy<R>> 
	implements AnalysisExecutionService<S,PreparedWorkflowGalaxy> {

}