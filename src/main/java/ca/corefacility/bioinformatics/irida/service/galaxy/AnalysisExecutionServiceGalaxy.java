package ca.corefacility.bioinformatics.irida.service.galaxy;

import ca.corefacility.bioinformatics.irida.model.workflow.analysis.Analysis;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.galaxy.AnalysisSubmissionGalaxy;
import ca.corefacility.bioinformatics.irida.service.AnalysisExecutionService;

/**
 * Service for performing analyses within a Galaxy execution manager.
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 * @param <A> The type of Analysis expected to be performed.
 * @param <S> The type of AnalysisSubmissionGalaxy to perform.
 */
public interface AnalysisExecutionServiceGalaxy<A extends Analysis, T extends AnalysisSubmissionGalaxy>
	extends AnalysisExecutionService<A,T> {
}
