package ca.corefacility.bioinformatics.irida.model.workflow.submission;

import ca.corefacility.bioinformatics.irida.model.project.ReferenceFile;
import ca.corefacility.bioinformatics.irida.model.workflow.RemoteWorkflow;

public interface AnalysisSubmissionPhylogenomicsPipeline<T extends RemoteWorkflow> 
	extends AnalysisSubmission<T> {

	public ReferenceFile getReferenceFile();
}
