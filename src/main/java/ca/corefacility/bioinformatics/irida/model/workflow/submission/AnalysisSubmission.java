package ca.corefacility.bioinformatics.irida.model.workflow.submission;

import java.util.Set;

import ca.corefacility.bioinformatics.irida.model.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.workflow.RemoteWorkflow;

public interface AnalysisSubmission<T extends RemoteWorkflow> {

	public T getRemoteWorkflow();
	public Set<SequenceFile> getInputFiles();
}
