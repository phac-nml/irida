package ca.corefacility.bioinformatics.irida.service.analysis.impl.galaxy;

import com.github.jmchilton.blend4j.galaxy.beans.WorkflowOutputs;

import ca.corefacility.bioinformatics.irida.model.workflow.RemoteAnalysisId;

public interface SubmittedAnalysis<T extends RemoteAnalysisId> {
	public T getRemoteAnalysisId();
	public WorkflowOutputs getOutputIds();
}
