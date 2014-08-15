package ca.corefacility.bioinformatics.irida.service.galaxy;

import ca.corefacility.bioinformatics.irida.model.workflow.analysis.Analysis;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.galaxy.AnalysisSubmissionGalaxy;
import ca.corefacility.bioinformatics.irida.service.AnalysisExecutionService;

public interface AnalysisExecutionServiceGalaxy<A extends Analysis, T extends AnalysisSubmissionGalaxy>
	extends AnalysisExecutionService<A,T> {
}
